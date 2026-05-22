package kz.iitu.hello.service;

import kz.iitu.hello.async.NotificationService;
import kz.iitu.hello.domain.entity.Course;
import kz.iitu.hello.domain.entity.Student;
import kz.iitu.hello.domain.entity.User;
import kz.iitu.hello.domain.mapper.StudentsMyBatisMapper;
import kz.iitu.hello.domain.repository.CoursesRepository;
import kz.iitu.hello.domain.repository.StudentsRepository;
import kz.iitu.hello.domain.repository.UsersRepository;
import kz.iitu.hello.exception.EntityNotFoundException;
import kz.iitu.hello.web.converter.StudentConverter;
import kz.iitu.hello.web.dto.form.StudentFormDto;
import kz.iitu.hello.web.dto.grid.CourseGridDto;
import kz.iitu.hello.web.dto.grid.UserGridDto;
import kz.iitu.hello.web.dto.search.StudentSearchForm;
import kz.iitu.hello.web.dto.view.StudentViewDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StudentService {
    private static final String DEFAULT_SORT = "studentName";
    private static final Map<String, String> SORT_COLUMN_MAPPING = Map.of(
            "id", "id",
            "studentName", "student_name",
            "age", "age",
            "groupName", "group_name",
            "gpa", "gpa"
    );

    private final StudentsRepository studentRepository;
    private final UsersRepository userRepository;
    private final CoursesRepository courseRepository;
    private final StudentsMyBatisMapper studentsMyBatisMapper;
    private final StudentConverter studentConverter;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public Page<StudentViewDto> search(StudentSearchForm form, Pageable pageable) {
        String requestedSortBy = form.getSortBy() == null ? DEFAULT_SORT : form.getSortBy();
        String sortBy = SORT_COLUMN_MAPPING.getOrDefault(requestedSortBy, SORT_COLUMN_MAPPING.get(DEFAULT_SORT));
        Sort.Direction direction = form.getSortDirection() == null ? Sort.Direction.ASC : form.getSortDirection();

        List<Student> students = studentsMyBatisMapper.findStudents(
                form.getName(),
                form.getGroupName(),
                form.getAgeFrom(),
                form.getAgeTo(),
                form.getGpaFrom(),
                form.getGpaTo(),
                sortBy,
                direction.name(),
                pageable.getPageSize(),
                pageable.getOffset()
        );

        long total = studentsMyBatisMapper.countStudents(
                form.getName(),
                form.getGroupName(),
                form.getAgeFrom(),
                form.getAgeTo(),
                form.getGpaFrom(),
                form.getGpaTo()
        );

        return new PageImpl<>(students, pageable, total)
                .map(studentConverter::toViewDto);
    }

    @Transactional(readOnly = true)
    public List<UserGridDto> findAllUsers() {
        return userRepository.findAll().stream().map(studentConverter::toUserGridDto).toList();
    }

    @Transactional(readOnly = true)
    public List<CourseGridDto> findAllCourses() {
        return courseRepository.findAll().stream().map(studentConverter::toCourseGridDto).toList();
    }

    @Transactional(readOnly = true)
    public StudentFormDto getForm(Long id) {
        return id == null ? new StudentFormDto() : studentConverter.toFormDto(findById(id));
    }

    public void create(StudentFormDto form) {
        log.info("Creating student: name='{}'", form.getStudentName());
        Student student = new Student();
        applyForm(form, student);
        studentRepository.save(student);
        log.info("Student created with id={}", student.getId());
    }

    public void update(Long id, StudentFormDto form) {
        log.info("Updating student id={}", id);
        Student student = findById(id);
        applyForm(form, student);
        studentRepository.save(student);
        log.info("Student id={} updated", id);
    }

    public void delete(Long id) {
        log.info("Deleting student id={}", id);
        Student student = findById(id);
        if (student.getCourses() != null) {
            for (Course course : student.getCourses()) {
                if (course.getStudents() != null) {
                    course.getStudents().remove(student);
                }
            }
            student.getCourses().clear();
        }
        studentRepository.delete(student);
        log.info("Student id={} deleted", id);
    }

    @Transactional(readOnly = true)
    public Student findById(Long id) {
        return studentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Student findByUserId(Long userId) {
        return studentRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found for user id: " + userId));
    }

    private void applyForm(StudentFormDto form, Student student) {
        User user = userRepository.findById(form.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + form.getUserId()));
        List<Long> courseIds = form.getCourseIds();
        Set<Course> courses = courseIds == null ? new HashSet<>() : new HashSet<>(courseRepository.findAllById(courseIds));
        studentConverter.applyFormToEntity(form, student, user, courses);
    }
}
