package kz.iitu.hello.web.controller.mvc;

import jakarta.validation.Valid;
import kz.iitu.hello.domain.enums.Department;
import kz.iitu.hello.service.TeacherService;
import kz.iitu.hello.web.dto.form.TeacherFormDto;
import kz.iitu.hello.web.dto.search.TeacherSearchForm;
import kz.iitu.hello.web.validations.BindingResultValidationUtils;
import kz.iitu.hello.web.validations.TeacherFormValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/teachers")
public class TeachersController {
    private final TeacherService teacherService;
    private final TeacherFormValidator teacherFormValidator;

    @GetMapping
    public String read(@RequestParam(name = "id", required = false) Long id,
                       @ModelAttribute("searchForm") TeacherSearchForm searchForm,
                       @PageableDefault(size = 10) Pageable pageable,
                       Model model) {
        model.addAttribute("editMode", id != null);
        model.addAttribute("form", teacherService.getForm(id));
        fillCommonAttributes(model, searchForm, pageable);
        return "teachers";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") TeacherFormDto form, BindingResult bindingResult, Model model) {
        teacherFormValidator.validate(form, bindingResult, form.getId());
        if (BindingResultValidationUtils.hasErrors(bindingResult)) {
            return renderFormWithErrors(model, form, false);
        }
        teacherService.create(form);
        return "redirect:/teachers";
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("form") TeacherFormDto form, BindingResult bindingResult, Model model) {
        teacherFormValidator.validate(form, bindingResult, form.getId());
        if (BindingResultValidationUtils.hasErrors(bindingResult)) {
            form.setId(id);
            return renderFormWithErrors(model, form, true);
        }
        teacherService.update(id, form);
        return "redirect:/teachers";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        teacherService.delete(id);
        return "redirect:/teachers";
    }

    private String renderFormWithErrors(Model model, TeacherFormDto form, boolean editMode) {
        model.addAttribute("editMode", editMode);
        model.addAttribute("form", form);
        fillCommonAttributes(model, new TeacherSearchForm(), Pageable.ofSize(10));
        return "teachers";
    }

    private void fillCommonAttributes(Model model, TeacherSearchForm form, Pageable pageable) {
        model.addAttribute("page", teacherService.search(form, pageable));
        model.addAttribute("searchForm", form);
        model.addAttribute("users", teacherService.findAllUsers());
        model.addAttribute("courses", teacherService.findAllCourses());
        model.addAttribute("departments", Department.values());
    }
}
