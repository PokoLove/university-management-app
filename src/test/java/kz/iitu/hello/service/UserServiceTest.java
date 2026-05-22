package kz.iitu.hello.service;

import kz.iitu.hello.domain.entity.User;
import kz.iitu.hello.domain.enums.UserRole;
import kz.iitu.hello.domain.repository.UsersRepository;
import kz.iitu.hello.exception.EntityNotFoundException;
import kz.iitu.hello.web.converter.UserConverter;
import kz.iitu.hello.web.dto.form.UserFormDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private UserConverter userConverter;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void create_encodesPasswordBeforeSaving() {
        UserFormDto form = new UserFormDto();
        form.setPassword("plaintext");
        when(passwordEncoder.encode("plaintext")).thenReturn("hashed");

        userService.create(form);

        verify(passwordEncoder).encode("plaintext");
        verify(usersRepository).save(any(User.class));
    }

    @Test
    void update_whenPasswordProvided_encodesNewPassword() {
        User existing = new User();
        existing.setId(1L);
        UserFormDto form = new UserFormDto();
        form.setPassword("newPassword");
        when(usersRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("newPassword")).thenReturn("hashedNew");

        userService.update(1L, form);

        verify(passwordEncoder).encode("newPassword");
        assertThat(existing.getPassword()).isEqualTo("hashedNew");
    }

    @Test
    void update_whenPasswordBlank_doesNotChangePassword() {
        User existing = new User();
        existing.setId(1L);
        existing.setPassword("existingHash");
        UserFormDto form = new UserFormDto();
        form.setPassword("");
        when(usersRepository.findById(1L)).thenReturn(Optional.of(existing));

        userService.update(1L, form);

        assertThat(existing.getPassword()).isEqualTo("existingHash");
    }

    @Test
    void findById_whenUserDoesNotExist_throwsEntityNotFoundException() {
        when(usersRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void findByUsername_whenUserDoesNotExist_throwsEntityNotFoundException() {
        when(usersRepository.findByUserName("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByUsername("ghost"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void delete_callsRepositoryDelete() {
        User user = new User();
        user.setId(1L);
        when(usersRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L);

        verify(usersRepository).delete(user);
    }
}
