package kz.iitu.hello.web.validations;

import kz.iitu.hello.web.dto.form.CourseFormDto;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CourseFormValidatorTest {

    private final CourseFormValidator validator = new CourseFormValidator();

    @Test
    void validate_withBlankCourseName_addsError() {
        CourseFormDto form = validForm();
        form.setCourseName("  ");
        BeanPropertyBindingResult br = bindingResult(form);

        validator.validate(form, br, null);

        assertThat(br.hasFieldErrors("courseName")).isTrue();
    }

    @Test
    void validate_whenStudentCountExceedsMaxStudents_addsError() {
        CourseFormDto form = validForm();
        form.setMaxStudents(2);
        form.setStudentIds(List.of(1L, 2L, 3L));
        BeanPropertyBindingResult br = bindingResult(form);

        validator.validate(form, br, null);

        assertThat(br.hasFieldErrors("studentIds")).isTrue();
    }

    @Test
    void validate_withNullTeacherId_addsError() {
        CourseFormDto form = validForm();
        form.setTeacherId(null);
        BeanPropertyBindingResult br = bindingResult(form);

        validator.validate(form, br, null);

        assertThat(br.hasFieldErrors("teacherId")).isTrue();
    }

    @Test
    void validate_withValidForm_hasNoErrors() {
        CourseFormDto form = validForm();
        BeanPropertyBindingResult br = bindingResult(form);

        validator.validate(form, br, null);

        assertThat(br.hasErrors()).isFalse();
    }

    private CourseFormDto validForm() {
        CourseFormDto form = new CourseFormDto();
        form.setCourseName("Algorithms");
        form.setCredits(3);
        form.setMaxStudents(30);
        form.setTeacherId(1L);
        return form;
    }

    private BeanPropertyBindingResult bindingResult(CourseFormDto form) {
        return new BeanPropertyBindingResult(form, "form");
    }
}
