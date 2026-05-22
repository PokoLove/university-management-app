package kz.iitu.hello.web.validations;

import kz.iitu.hello.domain.repository.UsersRepository;
import kz.iitu.hello.web.dto.form.UserFormDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
@RequiredArgsConstructor
public class UserFormValidator {
    private final UsersRepository usersRepository;

    public void validate(UserFormDto form, BindingResult bindingResult, Long currentId) {
        if (form.getUserName() == null || form.getUserName().isBlank()) {
            bindingResult.rejectValue("userName", "userName.blank", "User name is required");
        }
        if (form.getEmail() == null || form.getEmail().isBlank()) {
            bindingResult.rejectValue("email", "email.blank", "Email is required");
        } else if (!form.getEmail().contains("@")) {
            bindingResult.rejectValue("email", "email.invalid", "Email must contain @");
        }
        if (currentId == null && (form.getPassword() == null || form.getPassword().isBlank())) {
            bindingResult.rejectValue("password", "password.blank", "Password is required");
        } else if (currentId == null && form.getPassword() != null && !form.getPassword().isBlank() && form.getPassword().length() < 6) {
            bindingResult.rejectValue("password", "password.length", "Password must be at least 6 characters");
        }
        if (form.getRole() == null) {
            bindingResult.rejectValue("role", "role.blank", "Role is required");
        }

        if (form.getUserName() != null && !form.getUserName().isBlank()) {
            boolean userNameTaken = currentId == null
                    ? usersRepository.existsByUserNameIgnoreCase(form.getUserName())
                    : usersRepository.existsByUserNameIgnoreCaseAndIdNot(form.getUserName(), currentId);
            if (userNameTaken) {
                bindingResult.rejectValue("userName", "userName.duplicate", "User with this username already exists");
            }
        }

        if (form.getEmail() != null && form.getEmail().contains("@")) {
            boolean emailTaken = currentId == null
                    ? usersRepository.existsByEmailIgnoreCase(form.getEmail())
                    : usersRepository.existsByEmailIgnoreCaseAndIdNot(form.getEmail(), currentId);
            if (emailTaken) {
                bindingResult.rejectValue("email", "email.duplicate", "User with this email already exists");
            }
        }
    }
}
