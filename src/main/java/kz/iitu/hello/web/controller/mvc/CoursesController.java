package kz.iitu.hello.web.controller.mvc;

import jakarta.validation.Valid;
import kz.iitu.hello.service.CourseService;
import kz.iitu.hello.web.dto.form.CourseFormDto;
import kz.iitu.hello.web.dto.search.CourseSearchForm;
import kz.iitu.hello.web.validations.BindingResultValidationUtils;
import kz.iitu.hello.web.validations.CourseFormValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/courses")
public class CoursesController {
    private final CourseService courseService;
    private final CourseFormValidator courseFormValidator;

    @GetMapping
    public String read(@RequestParam(name = "id", required = false) Long id,
                       @ModelAttribute("searchForm") CourseSearchForm searchForm,
                       @PageableDefault(size = 10) Pageable pageable,
                       Model model) {
        model.addAttribute("editMode", id != null);
        model.addAttribute("form", courseService.getForm(id));
        fillCommonAttributes(model, searchForm, pageable);
        return "courses";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") CourseFormDto form, BindingResult bindingResult, Model model) {
        courseFormValidator.validate(form, bindingResult, null);
        if (BindingResultValidationUtils.hasErrors(bindingResult)) {
            return renderFormWithErrors(model, form, false);
        }
        courseService.create(form);
        return "redirect:/courses";
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("form") CourseFormDto form, BindingResult bindingResult, Model model) {
        courseFormValidator.validate(form, bindingResult, form.getId());
        if (BindingResultValidationUtils.hasErrors(bindingResult)) {
            form.setId(id);
            return renderFormWithErrors(model, form, true);
        }
        courseService.update(id, form);
        return "redirect:/courses";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        courseService.delete(id);
        return "redirect:/courses";
    }

    private String renderFormWithErrors(Model model, CourseFormDto form, boolean editMode) {
        model.addAttribute("editMode", editMode);
        model.addAttribute("form", form);
        fillCommonAttributes(model, new CourseSearchForm(), Pageable.ofSize(10));
        return "courses";
    }

    private void fillCommonAttributes(Model model, CourseSearchForm form, Pageable pageable) {
        model.addAttribute("page", courseService.search(form, pageable));
        model.addAttribute("searchForm", form);
        model.addAttribute("teachers", courseService.findAllTeachers());
        model.addAttribute("students", courseService.findAllStudents());
    }
}
