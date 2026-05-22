package kz.iitu.hello.web.validations;

import kz.iitu.hello.domain.enums.UserRole;
import kz.iitu.hello.domain.repository.UsersRepository;
import kz.iitu.hello.web.dto.form.UserFormDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserFormValidatorTest {

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private UserFormValidator validator;

    @Test
    void validate_withBlankUserName_addsError() {
        UserFormDto form = form("", "test@example.com", "password", UserRole.STUDENT);
        BeanPropertyBindingResult br = bindingResult(form);

        validator.validate(form, br, null);

        assertThat(br.hasFieldErrors("userName")).isTrue();
    }

    @Test
    void validate_withInvalidEmail_addsError() {
        UserFormDto form = form("alice", "not-an-email", "password", UserRole.STUDENT);
        BeanPropertyBindingResult br = bindingResult(form);

        validator.validate(form, br, null);

        assertThat(br.hasFieldErrors("email")).isTrue();
    }

    @Test
    void validate_withDuplicateUserName_addsError() {
        UserFormDto form = form("alice", "alice@mail.com", "password", UserRole.STUDENT);
        BeanPropertyBindingResult br = bindingResult(form);
        when(usersRepository.existsByUserNameIgnoreCase("alice")).thenReturn(true);

        validator.validate(form, br, null);

        assertThat(br.hasFieldErrors("userName")).isTrue();
    }

    @Test
    void validate_withDuplicateEmail_addsError() {
        UserFormDto form = form("alice", "alice@mail.com", "password", UserRole.STUDENT);
        BeanPropertyBindingResult br = bindingResult(form);
        when(usersRepository.existsByEmailIgnoreCase("alice@mail.com")).thenReturn(true);

        validator.validate(form, br, null);

        assertThat(br.hasFieldErrors("email")).isTrue();
    }

    @Test
    void validate_withValidForm_hasNoErrors() {
        UserFormDto form = form("alice", "alice@mail.com", "password", UserRole.STUDENT);
        BeanPropertyBindingResult br = bindingResult(form);
        when(usersRepository.existsByUserNameIgnoreCase("alice")).thenReturn(false);
        when(usersRepository.existsByEmailIgnoreCase("alice@mail.com")).thenReturn(false);

        validator.validate(form, br, null);

        assertThat(br.hasErrors()).isFalse();
    }

    private UserFormDto form(String username, String email, String password, UserRole role) {
        UserFormDto dto = new UserFormDto();
        dto.setUserName(username);
        dto.setEmail(email);
        dto.setPassword(password);
        dto.setRole(role);
        return dto;
    }

    private BeanPropertyBindingResult bindingResult(UserFormDto form) {
        return new BeanPropertyBindingResult(form, "form");
    }
}
