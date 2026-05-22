package kz.iitu.hello.service;

import kz.iitu.hello.domain.entity.Course;
import kz.iitu.hello.domain.entity.Student;
import kz.iitu.hello.domain.entity.Teacher;
import kz.iitu.hello.domain.repository.CoursesRepository;
import kz.iitu.hello.domain.repository.StudentsRepository;
import kz.iitu.hello.domain.repository.TeachersRepository;
import kz.iitu.hello.exception.CourseLimitExceededException;
import kz.iitu.hello.exception.EntityNotFoundException;
import kz.iitu.hello.web.converter.CourseConverter;
import kz.iitu.hello.web.dto.form.CourseFormDto;
import kz.iitu.hello.web.dto.search.CourseSearchForm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CoursesRepository coursesRepository;

    @Mock
    private TeachersRepository teachersRepository;

    @Mock
    private StudentsRepository studentsRepository;

    @Mock
    private CourseConverter courseConverter;

    @InjectMocks
    private CourseService courseService;

    @Test
    void search_withInvalidSortField_throwsIllegalArgumentException() {
        CourseSearchForm form = new CourseSearchForm();
        form.setSortBy("invalidField");

        assertThatThrownBy(() -> courseService.search(form, PageRequest.of(0, 10)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid sort field");
    }

    @Test
    void create_whenStudentCountExceedsMaxStudents_throwsCourseLimitExceededException() {
        CourseFormDto form = new CourseFormDto();
        form.setTeacherId(1L);
        form.setMaxStudents(2);
        form.setStudentIds(List.of(1L, 2L, 3L));

        when(teachersRepository.findById(1L)).thenReturn(Optional.of(new Teacher()));
        when(studentsRepository.findAllById(form.getStudentIds()))
                .thenReturn(List.of(new Student(), new Student(), new Student()));

        assertThatThrownBy(() -> courseService.create(form))
                .isInstanceOf(CourseLimitExceededException.class);
    }

    @Test
    void findById_whenCourseDoesNotExist_throwsEntityNotFoundException() {
        when(coursesRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.findById(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void delete_clearsBidirectionalRelationshipBeforeDeleting() {
        Student student = new Student();
        Course course = new Course();
        course.setId(1L);
        course.setStudents(new HashSet<>(List.of(student)));
        student.setCourses(new HashSet<>(List.of(course)));

        when(coursesRepository.findById(1L)).thenReturn(Optional.of(course));

        courseService.delete(1L);

        assertThat(student.getCourses()).isEmpty();
        assertThat(course.getStudents()).isEmpty();
    }
}
