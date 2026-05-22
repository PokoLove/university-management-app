package kz.iitu.hello.web.validations;

import kz.iitu.hello.web.dto.form.UserFormDto;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BindingResultValidationUtilsTest {

    @Test
    void validate_withNoErrors_doesNotThrow() {
        BeanPropertyBindingResult br = new BeanPropertyBindingResult(new UserFormDto(), "form");

        assertThatNoException().isThrownBy(() -> BindingResultValidationUtils.validate(br));
    }

    @Test
    void validate_withMultipleErrors_throwsWithAllMessagesConcatenated() {
        BeanPropertyBindingResult br = new BeanPropertyBindingResult(new UserFormDto(), "form");
        br.rejectValue("userName", "blank", "User name is required");
        br.rejectValue("email", "blank", "Email is required");

        assertThatThrownBy(() -> BindingResultValidationUtils.validate(br))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User name is required")
                .hasMessageContaining("Email is required");
    }
}
