package kz.iitu.hello.web.controller.api;

import kz.iitu.hello.service.StudentService;
import kz.iitu.hello.web.dto.form.StudentFormDto;
import kz.iitu.hello.web.dto.search.StudentSearchForm;
import kz.iitu.hello.web.dto.view.StudentViewDto;
import kz.iitu.hello.web.validations.BindingResultValidationUtils;
import kz.iitu.hello.web.validations.StudentFormValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Students", description = "CRUD operations for students (ADMIN only)")
public class StudentsRestController {
    private final StudentService studentService;
    private final StudentFormValidator studentFormValidator;

    @GetMapping
    @Operation(summary = "Search students", description = "Search and paginate students with optional filters")
    public Page<StudentViewDto> read(StudentSearchForm form,
                                     @PageableDefault(size = 10) Pageable pageable) {
        return studentService.search(form, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create student", description = "Create a new student linked to an existing user")
    public void create(@Valid @RequestBody StudentFormDto form) {
        BeanPropertyBindingResult br = new BeanPropertyBindingResult(form, "form");
        studentFormValidator.validate(form, br, null);
        BindingResultValidationUtils.validate(br);
        studentService.create(form);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update student", description = "Update an existing student by ID")
    public void update(@PathVariable Long id, @Valid @RequestBody StudentFormDto form) {
        BeanPropertyBindingResult br = new BeanPropertyBindingResult(form, "form");
        studentFormValidator.validate(form, br, id);
        BindingResultValidationUtils.validate(br);
        studentService.update(id, form);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete student", description = "Delete a student by ID")
    public void delete(@PathVariable Long id) {
        studentService.delete(id);
    }
}
