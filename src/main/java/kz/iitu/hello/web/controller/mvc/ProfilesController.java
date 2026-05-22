package kz.iitu.hello.web.controller.mvc;

import kz.iitu.hello.domain.entity.User;
import kz.iitu.hello.domain.entity.UserFile;
import kz.iitu.hello.service.FileStorageService;
import kz.iitu.hello.service.StudentService;
import kz.iitu.hello.service.TeacherService;
import kz.iitu.hello.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/profiles")
public class ProfilesController {
    private static final String AVATAR_TYPE = "AVATAR";
    private static final String DOCUMENT_TYPE = "DOCUMENT";

    private final UserService userService;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final FileStorageService fileStorageService;

    @GetMapping("/{userId}")
    public String profile(@PathVariable Long userId, Model model) {
        User user = userService.findById(userId);
        model.addAttribute("user", user);

        switch (user.getRole()) {
            case STUDENT -> model.addAttribute("student", studentService.findByUserId(userId));
            case TEACHER -> model.addAttribute("teacher", teacherService.findByUserId(userId));
            default -> {
            }
        }

        List<UserFile> avatars = fileStorageService.findByUserAndFileType(userId, AVATAR_TYPE);
        model.addAttribute("avatar", avatars.isEmpty() ? null : avatars.get(0));
        model.addAttribute("documents", fileStorageService.findByUserAndFileType(userId, DOCUMENT_TYPE));
        return "profiles";
    }

    @PostMapping("/{userId}/avatar")
    public String uploadAvatar(@PathVariable Long userId, @RequestParam("file") MultipartFile file) {
        fileStorageService.saveAvatar(userId, file);
        return "redirect:/profiles/" + userId;
    }

    @PostMapping("/{userId}/documents")
    public String uploadDocuments(@PathVariable Long userId, @RequestParam("files") List<MultipartFile> files) {
        fileStorageService.saveDocuments(userId, files);
        return "redirect:/profiles/" + userId;
    }

    @PostMapping("/files/{fileId}/delete")
    public String deleteFile(@PathVariable Long fileId) {
        Long userId = fileStorageService.getFile(fileId).getUser().getId();
        fileStorageService.deleteFile(fileId);
        return "redirect:/profiles/" + userId;
    }
}
