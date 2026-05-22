package kz.iitu.hello.web.validations;

import kz.iitu.hello.domain.entity.Teacher;
import kz.iitu.hello.domain.entity.User;
import kz.iitu.hello.domain.enums.UserRole;
import kz.iitu.hello.domain.repository.TeachersRepository;
import kz.iitu.hello.domain.repository.UsersRepository;
import kz.iitu.hello.web.dto.form.TeacherFormDto;
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
class TeacherFormValidatorTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private TeachersRepository teachersRepository;

    @InjectMocks
    private TeacherFormValidator validator;

    @Test
    void validate_whenUserNotFound_addsError() {
        TeacherFormDto form = formWithUserId(99L);
        BeanPropertyBindingResult br = bindingResult(form);
        when(usersRepository.findById(99L)).thenReturn(Optional.empty());

        validator.validate(form, br, null);

        assertThat(br.hasFieldErrors("userId")).isTrue();
    }

    @Test
    void validate_whenUserHasWrongRole_addsError() {
        TeacherFormDto form = formWithUserId(1L);
        BeanPropertyBindingResult br = bindingResult(form);
        when(usersRepository.findById(1L)).thenReturn(Optional.of(userWithRole(UserRole.STUDENT)));
        when(teachersRepository.findByUserId(1L)).thenReturn(Optional.empty());

        validator.validate(form, br, null);

        assertThat(br.hasFieldErrors("userId")).isTrue();
    }

    @Test
    void validate_whenUserAlreadyLinkedToAnotherTeacher_addsError() {
        TeacherFormDto form = formWithUserId(1L);
        BeanPropertyBindingResult br = bindingResult(form);
        Teacher existingTeacher = new Teacher();
        existingTeacher.setId(99L);
        when(usersRepository.findById(1L)).thenReturn(Optional.of(userWithRole(UserRole.TEACHER)));
        when(teachersRepository.findByUserId(1L)).thenReturn(Optional.of(existingTeacher));

        validator.validate(form, br, null);

        assertThat(br.hasFieldErrors("userId")).isTrue();
    }

    @Test
    void validate_withValidForm_hasNoErrors() {
        TeacherFormDto form = formWithUserId(1L);
        BeanPropertyBindingResult br = bindingResult(form);
        when(usersRepository.findById(1L)).thenReturn(Optional.of(userWithRole(UserRole.TEACHER)));
        when(teachersRepository.findByUserId(1L)).thenReturn(Optional.empty());

        validator.validate(form, br, null);

        assertThat(br.hasErrors()).isFalse();
    }

    private TeacherFormDto formWithUserId(Long userId) {
        TeacherFormDto form = new TeacherFormDto();
        form.setUserId(userId);
        return form;
    }

    private User userWithRole(UserRole role) {
        User user = new User();
        user.setId(1L);
        user.setRole(role);
        return user;
    }

    private BeanPropertyBindingResult bindingResult(TeacherFormDto form) {
        return new BeanPropertyBindingResult(form, "form");
    }
}
