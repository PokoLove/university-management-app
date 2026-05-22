package kz.iitu.hello.web.validations;

import kz.iitu.hello.domain.entity.User;
import kz.iitu.hello.domain.enums.UserRole;
import kz.iitu.hello.domain.repository.StudentsRepository;
import kz.iitu.hello.domain.repository.UsersRepository;
import kz.iitu.hello.web.dto.form.StudentFormDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StudentFormValidator {

    private final UsersRepository usersRepository;
    private final StudentsRepository studentsRepository;

    public void validate(StudentFormDto form, BindingResult bindingResult, Long currentId) {
        if (form.getUserId() == null) {
            return;
        }

        Optional<User> userOpt = usersRepository.findById(form.getUserId());
        if (userOpt.isEmpty()) {
            bindingResult.rejectValue("userId", "userId.notFound", "Selected user does not exist");
            return;
        }

        if (userOpt.get().getRole() != UserRole.STUDENT) {
            bindingResult.rejectValue("userId", "userId.wrongRole", "Selected user must have STUDENT role");
        }

        boolean alreadyLinked = studentsRepository.findByUserId(form.getUserId())
                .map(existing -> !existing.getId().equals(currentId))
                .orElse(false);

        if (alreadyLinked) {
            bindingResult.rejectValue("userId", "userId.duplicate", "This user is already linked to another student");
        }
    }
}
