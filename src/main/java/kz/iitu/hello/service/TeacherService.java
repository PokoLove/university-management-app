package kz.iitu.hello.service;

import kz.iitu.hello.domain.entity.Course;
import kz.iitu.hello.domain.entity.Teacher;
import kz.iitu.hello.domain.entity.User;
import kz.iitu.hello.domain.enums.Department;
import kz.iitu.hello.domain.repository.CoursesRepository;
import kz.iitu.hello.domain.repository.TeachersRepository;
import kz.iitu.hello.domain.repository.UsersRepository;
import kz.iitu.hello.exception.BusinessException;
import kz.iitu.hello.exception.EntityNotFoundException;
import kz.iitu.hello.web.converter.TeacherConverter;
import kz.iitu.hello.web.dto.form.TeacherFormDto;
import kz.iitu.hello.web.dto.grid.CourseGridDto;
import kz.iitu.hello.web.dto.grid.UserGridDto;
import kz.iitu.hello.web.dto.search.TeacherSearchForm;
import kz.iitu.hello.web.dto.view.TeacherViewDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TeacherService {
    private static final String DEFAULT_SORT = "teacherName";
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "teacherName", "experienceYears", "department");

    private final TeachersRepository teachersRepository;
    private final UsersRepository usersRepository;
    private final CoursesRepository coursesRepository;
    private final TeacherConverter teacherConverter;

    @Transactional(readOnly = true)
    public Page<TeacherViewDto> search(TeacherSearchForm form, Pageable pageable) {
        Sort.Direction direction = form.getSortDirection() == null ? Sort.Direction.ASC : form.getSortDirection();
        String requestedSortBy = form.getSortBy() == null ? DEFAULT_SORT : form.getSortBy();
        String sortBy = ALLOWED_SORT_FIELDS.contains(requestedSortBy) ? requestedSortBy : DEFAULT_SORT;

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(direction, sortBy));

        return teachersRepository.searchTeachers(form.getDepartment(), form.getName(), sortedPageable)
                .map(teacherConverter::toViewDto);
    }

    @Transactional(readOnly = true)
    public List<UserGridDto> findAllUsers() {
        return usersRepository.findAll().stream().map(teacherConverter::toUserGridDto).toList();
    }

    @Transactional(readOnly = true)
    public List<CourseGridDto> findAllCourses() {
        return coursesRepository.findAll().stream().map(teacherConverter::toCourseGridDto).toList();
    }

    @Transactional(readOnly = true)
    public TeacherFormDto getForm(Long id) {
        return id == null ? new TeacherFormDto() : teacherConverter.toFormDto(findById(id));
    }

    public void create(TeacherFormDto form) {
        log.info("Creating teacher: name='{}'", form.getTeacherName());
        Teacher teacher = new Teacher();
        applyForm(form, teacher);
        teachersRepository.save(teacher);
        log.info("Teacher created with id={}", teacher.getId());
    }

    public void update(Long id, TeacherFormDto form) {
        log.info("Updating teacher id={}", id);
        Teacher teacher = findById(id);
        applyForm(form, teacher);
        teachersRepository.save(teacher);
        log.info("Teacher id={} updated", id);
    }

    public void delete(Long id) {
        log.info("Deleting teacher id={}", id);
        Teacher teacher = findById(id);
        if (teacher.getCourses() != null && !teacher.getCourses().isEmpty()) {
            throw new BusinessException("Cannot delete teacher with assigned courses");
        }
        teachersRepository.delete(teacher);
        log.info("Teacher id={} deleted", id);
    }

    @Transactional(readOnly = true)
    public Teacher findById(Long id) {
        return teachersRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Teacher not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Teacher findByUserId(Long userId) {
        return teachersRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found for user id: " + userId));
    }

    private void applyForm(TeacherFormDto form, Teacher teacher) {
        User user = usersRepository.findById(form.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + form.getUserId()));
        List<Long> courseIds = form.getCourseIds();
        List<Course> courses = courseIds == null ? new ArrayList<>() : new ArrayList<>(coursesRepository.findAllById(courseIds));
        teacherConverter.applyFormToEntity(form, teacher, user, courses);
    }
}
