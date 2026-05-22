package kz.iitu.hello.web.controller.api;

import kz.iitu.hello.service.CourseService;
import kz.iitu.hello.web.dto.form.CourseFormDto;
import kz.iitu.hello.web.dto.search.CourseSearchForm;
import kz.iitu.hello.web.dto.view.CourseViewDto;
import kz.iitu.hello.web.validations.BindingResultValidationUtils;
import kz.iitu.hello.web.validations.CourseFormValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "Courses", description = "CRUD operations for courses (ADMIN only)")
public class CoursesRestController {
    private final CourseService courseService;
    private final CourseFormValidator courseFormValidator;

    @GetMapping
    @Operation(summary = "Search courses", description = "Search and paginate courses with optional filters")
    public Page<CourseViewDto> read(CourseSearchForm form,
                                    @PageableDefault(size = 10) Pageable pageable) {
        return courseService.search(form, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create course", description = "Create a new course with teacher and optional students")
    public void create(@RequestBody CourseFormDto form) {
        BeanPropertyBindingResult br = new BeanPropertyBindingResult(form, "form");
        courseFormValidator.validate(form, br, null);
        BindingResultValidationUtils.validate(br);
        courseService.create(form);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update course", description = "Update an existing course by ID")
    public void update(@PathVariable Long id, @RequestBody CourseFormDto form) {
        BeanPropertyBindingResult br = new BeanPropertyBindingResult(form, "form");
        courseFormValidator.validate(form, br, id);
        BindingResultValidationUtils.validate(br);
        courseService.update(id, form);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete course", description = "Delete a course by ID")
    public void delete(@PathVariable Long id) {
        courseService.delete(id);
    }
}
