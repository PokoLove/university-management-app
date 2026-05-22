package kz.iitu.hello.web.validations;

import kz.iitu.hello.domain.entity.User;
import kz.iitu.hello.domain.enums.UserRole;
import kz.iitu.hello.domain.repository.TeachersRepository;
import kz.iitu.hello.domain.repository.UsersRepository;
import kz.iitu.hello.web.dto.form.TeacherFormDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TeacherFormValidator {

    private final UsersRepository usersRepository;
    private final TeachersRepository teachersRepository;

    public void validate(TeacherFormDto form, BindingResult bindingResult, Long currentId) {
        if (form.getUserId() == null) {
            return;
        }

        Optional<User> userOpt = usersRepository.findById(form.getUserId());
        if (userOpt.isEmpty()) {
            bindingResult.rejectValue("userId", "userId.notFound", "Selected user does not exist");
            return;
        }

        if (userOpt.get().getRole() != UserRole.TEACHER) {
            bindingResult.rejectValue("userId", "userId.wrongRole", "Selected user must have TEACHER role");
        }

        boolean alreadyLinked = teachersRepository.findByUserId(form.getUserId())
                .map(existing -> !existing.getId().equals(currentId))
                .orElse(false);

        if (alreadyLinked) {
            bindingResult.rejectValue("userId", "userId.duplicate", "This user is already linked to another teacher");
        }
    }
}
