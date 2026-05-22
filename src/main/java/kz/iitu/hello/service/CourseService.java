package kz.iitu.hello.service;

import kz.iitu.hello.async.NotificationService;
import kz.iitu.hello.domain.entity.Course;
import kz.iitu.hello.domain.entity.Student;
import kz.iitu.hello.domain.entity.Teacher;
import kz.iitu.hello.domain.repository.CoursesRepository;
import kz.iitu.hello.domain.repository.StudentsRepository;
import kz.iitu.hello.domain.repository.TeachersRepository;
import kz.iitu.hello.domain.specification.CourseSpecification;
import kz.iitu.hello.exception.CourseLimitExceededException;
import kz.iitu.hello.exception.EntityNotFoundException;
import kz.iitu.hello.web.converter.CourseConverter;
import kz.iitu.hello.web.dto.form.CourseFormDto;
import kz.iitu.hello.web.dto.grid.StudentGridDto;
import kz.iitu.hello.web.dto.grid.TeacherGridDto;
import kz.iitu.hello.web.dto.search.CourseSearchForm;
import kz.iitu.hello.web.dto.view.CourseViewDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CourseService {
    private static final String DEFAULT_SORT = "courseName";
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("courseName", "maxStudents", "teacher");

    private final CoursesRepository coursesRepository;
    private final TeachersRepository teachersRepository;
    private final StudentsRepository studentsRepository;
    private final CourseConverter courseConverter;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public Page<CourseViewDto> search(CourseSearchForm form, Pageable pageable) {
        Sort sort = pageable.getSort().isSorted() ? pageable.getSort() : Sort.by(Sort.Direction.ASC, DEFAULT_SORT);

        if (form.getSortBy() != null && !form.getSortBy().isBlank()) {
            String requestedSortBy = form.getSortBy();
            if (!ALLOWED_SORT_FIELDS.contains(requestedSortBy)) {
                throw new IllegalArgumentException("Invalid sort field: " + requestedSortBy);
            }

            Sort.Direction direction = form.getSortDirection() == null ? Sort.Direction.ASC : form.getSortDirection();
            String sortProperty = "teacher".equals(requestedSortBy) ? "teacher.teacherName" : requestedSortBy;
            sort = Sort.by(direction, sortProperty);
        }

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return coursesRepository.findAll(CourseSpecification.withFilters(form), sortedPageable)
                .map(courseConverter::toViewDto);
    }

    @Transactional(readOnly = true)
    public List<TeacherGridDto> findAllTeachers() {
        return teachersRepository.findAll().stream().map(courseConverter::toTeacherGridDto).toList();
    }

    @Transactional(readOnly = true)
    public List<StudentGridDto> findAllStudents() {
        return studentsRepository.findAll().stream().map(courseConverter::toStudentGridDto).toList();
    }

    @Transactional(readOnly = true)
    public CourseFormDto getForm(Long id) {
        return id == null ? new CourseFormDto() : courseConverter.toFormDto(findById(id));
    }

    public void create(CourseFormDto form) {
        log.info("Creating course: name='{}'", form.getCourseName());
        Course course = new Course();
        applyForm(form, course);
        coursesRepository.save(course);
        log.info("Course created with id={}", course.getId());
        // Async: notify enrolled students
        if (form.getStudentIds() != null) {
            form.getStudentIds().forEach(sid ->
                notificationService.notifyStudentEnrolled("Student#" + sid, form.getCourseName())
            );
        }
    }

    public void update(Long id, CourseFormDto form) {
        log.info("Updating course id={}", id);
        Course course = findById(id);
        Set<Student> oldStudents = new HashSet<>(course.getStudents());
        applyForm(form, course);
        coursesRepository.save(course);
        log.info("Course id={} updated", id);
        // Async: notify newly added students
        Set<Student> newStudents = course.getStudents();
        newStudents.stream()
            .filter(s -> oldStudents.stream().noneMatch(os -> os.getId().equals(s.getId())))
            .forEach(s -> notificationService.notifyStudentEnrolled(s.getStudentName(), form.getCourseName()));
        // Async: notify removed students
        oldStudents.stream()
            .filter(s -> newStudents.stream().noneMatch(ns -> ns.getId().equals(s.getId())))
            .forEach(s -> notificationService.notifyStudentRemoved(s.getStudentName(), form.getCourseName()));
    }

    public void delete(Long id) {
        log.info("Deleting course id={}", id);
        Course course = findById(id);
        Set<Student> assignedStudents = new HashSet<>(course.getStudents());
        for (Student student : assignedStudents) {
            student.getCourses().remove(course);
        }
        course.getStudents().clear();
        coursesRepository.delete(course);
        log.info("Course id={} deleted", id);
    }

    @Transactional(readOnly = true)
    public Course findById(Long id) {
        return coursesRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + id));
    }

    private void applyForm(CourseFormDto form, Course course) {
        Teacher teacher = teachersRepository.findById(form.getTeacherId())
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found with id: " + form.getTeacherId()));

        List<Long> studentIds = form.getStudentIds() == null ? List.of() : form.getStudentIds();
        Set<Student> students = new HashSet<>(studentsRepository.findAllById(studentIds));

        if (students.size() != new HashSet<>(studentIds).size()) {
            throw new EntityNotFoundException("One or more students not found");
        }

        if (students.size() > form.getMaxStudents()) {
            throw new CourseLimitExceededException("Student count exceeds maximum allowed for this course");
        }

        Set<Student> oldStudents = new HashSet<>(course.getStudents());
        for (Student oldStudent : oldStudents) {
            oldStudent.getCourses().remove(course);
        }

        courseConverter.applyFormToEntity(form, course, teacher, new HashSet<>());

        for (Student student : students) {
            student.getCourses().add(course);
        }

        course.setStudents(students);
    }
}
