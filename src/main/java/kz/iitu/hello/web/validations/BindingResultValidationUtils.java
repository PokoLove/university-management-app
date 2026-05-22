package kz.iitu.hello.web.validations;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.stream.Collectors;

public final class BindingResultValidationUtils {

    private BindingResultValidationUtils() {
    }

    public static void validate(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String messages = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("; "));
            throw new IllegalArgumentException(messages);
        }
    }

    public static boolean hasErrors(BindingResult bindingResult) {
        return bindingResult.hasErrors();
    }
}
