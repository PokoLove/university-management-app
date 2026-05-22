package kz.iitu.hello.service;

import kz.iitu.hello.domain.entity.Course;
import kz.iitu.hello.domain.entity.Student;
import kz.iitu.hello.domain.mapper.StudentsMyBatisMapper;
import kz.iitu.hello.domain.repository.CoursesRepository;
import kz.iitu.hello.domain.repository.StudentsRepository;
import kz.iitu.hello.domain.repository.UsersRepository;
import kz.iitu.hello.exception.EntityNotFoundException;
import kz.iitu.hello.web.converter.StudentConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentsRepository studentRepository;

    @Mock
    private UsersRepository userRepository;

    @Mock
    private CoursesRepository courseRepository;

    @Mock
    private StudentsMyBatisMapper studentsMyBatisMapper;

    @Mock
    private StudentConverter studentConverter;

    @InjectMocks
    private StudentService studentService;

    @Test
    void delete_removesCourseRelationshipsBeforeDeleting() {
        Course course = new Course();
        course.setId(10L);

        Student student = new Student();
        student.setId(1L);
        Set<Student> courseStudents = new HashSet<>();
        courseStudents.add(student);
        course.setStudents(courseStudents);
        student.setCourses(new HashSet<>(Set.of(course)));

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        studentService.delete(1L);

        assertThat(student.getCourses()).isEmpty();
        assertThat(course.getStudents()).doesNotContain(student);
        verify(studentRepository).delete(student);
    }

    @Test
    void findById_whenStudentDoesNotExist_throwsEntityNotFoundException() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.findById(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
