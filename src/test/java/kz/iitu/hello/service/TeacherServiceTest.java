package kz.iitu.hello.service;

import kz.iitu.hello.domain.entity.Course;
import kz.iitu.hello.domain.entity.Teacher;
import kz.iitu.hello.domain.repository.CoursesRepository;
import kz.iitu.hello.domain.repository.TeachersRepository;
import kz.iitu.hello.domain.repository.UsersRepository;
import kz.iitu.hello.exception.BusinessException;
import kz.iitu.hello.exception.EntityNotFoundException;
import kz.iitu.hello.web.converter.TeacherConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

    @Mock
    private TeachersRepository teachersRepository;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private CoursesRepository coursesRepository;

    @Mock
    private TeacherConverter teacherConverter;

    @InjectMocks
    private TeacherService teacherService;

    @Test
    void delete_whenTeacherHasCourses_throwsBusinessException() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setCourses(List.of(new Course()));
        when(teachersRepository.findById(1L)).thenReturn(Optional.of(teacher));

        assertThatThrownBy(() -> teacherService.delete(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Cannot delete teacher with assigned courses");
    }

    @Test
    void delete_whenTeacherHasNoCourses_deletesSuccessfully() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setCourses(List.of());
        when(teachersRepository.findById(1L)).thenReturn(Optional.of(teacher));

        teacherService.delete(1L);

        verify(teachersRepository).delete(teacher);
    }

    @Test
    void findById_whenTeacherDoesNotExist_throwsEntityNotFoundException() {
        when(teachersRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> teacherService.findById(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
