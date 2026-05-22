package kz.iitu.hello.web.validations;

import kz.iitu.hello.domain.entity.Student;
import kz.iitu.hello.domain.entity.User;
import kz.iitu.hello.domain.enums.UserRole;
import kz.iitu.hello.domain.repository.StudentsRepository;
import kz.iitu.hello.domain.repository.UsersRepository;
import kz.iitu.hello.web.dto.form.StudentFormDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentFormValidatorTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private StudentsRepository studentsRepository;

    @InjectMocks
    private StudentFormValidator validator;

    @Test
    void validate_whenUserNotFound_addsError() {
        StudentFormDto form = formWithUserId(99L);
        BeanPropertyBindingResult br = bindingResult(form);
        when(usersRepository.findById(99L)).thenReturn(Optional.empty());

        validator.validate(form, br, null);

        assertThat(br.hasFieldErrors("userId")).isTrue();
    }

    @Test
    void validate_whenUserHasWrongRole_addsError() {
        StudentFormDto form = formWithUserId(1L);
        BeanPropertyBindingResult br = bindingResult(form);
        when(usersRepository.findById(1L)).thenReturn(Optional.of(userWithRole(UserRole.TEACHER)));
        when(studentsRepository.findByUserId(1L)).thenReturn(Optional.empty());

        validator.validate(form, br, null);

        assertThat(br.hasFieldErrors("userId")).isTrue();
    }

    @Test
    void validate_whenUserAlreadyLinkedToAnotherStudent_addsError() {
        StudentFormDto form = formWithUserId(1L);
        BeanPropertyBindingResult br = bindingResult(form);
        Student existingStudent = new Student();
        existingStudent.setId(99L);
        when(usersRepository.findById(1L)).thenReturn(Optional.of(userWithRole(UserRole.STUDENT)));
        when(studentsRepository.findByUserId(1L)).thenReturn(Optional.of(existingStudent));

        validator.validate(form, br, null);

        assertThat(br.hasFieldErrors("userId")).isTrue();
    }

    @Test
    void validate_withValidForm_hasNoErrors() {
        StudentFormDto form = formWithUserId(1L);
        BeanPropertyBindingResult br = bindingResult(form);
        when(usersRepository.findById(1L)).thenReturn(Optional.of(userWithRole(UserRole.STUDENT)));
        when(studentsRepository.findByUserId(1L)).thenReturn(Optional.empty());

        validator.validate(form, br, null);

        assertThat(br.hasErrors()).isFalse();
    }

    private StudentFormDto formWithUserId(Long userId) {
        StudentFormDto form = new StudentFormDto();
        form.setUserId(userId);
        return form;
    }

    private User userWithRole(UserRole role) {
        User user = new User();
        user.setId(1L);
        user.setRole(role);
        return user;
    }

    private BeanPropertyBindingResult bindingResult(StudentFormDto form) {
        return new BeanPropertyBindingResult(form, "form");
    }
}
