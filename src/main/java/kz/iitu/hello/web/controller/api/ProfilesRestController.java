package kz.iitu.hello.web.controller.api;

import kz.iitu.hello.domain.entity.User;
import kz.iitu.hello.domain.entity.UserFile;
import kz.iitu.hello.domain.enums.UserRole;
import kz.iitu.hello.service.FileStorageService;
import kz.iitu.hello.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
@Tag(name = "Profiles", description = "File upload/download for user profiles (authenticated)")
public class ProfilesRestController {
    private final FileStorageService fileStorageService;
    private final UserService userService;

    @PostMapping("/{userId}/avatar")
    @Operation(summary = "Upload avatar", description = "Upload an avatar image for a user (owner or admin)")
    public UserFile uploadAvatar(@PathVariable Long userId,
                                 @RequestParam("file") MultipartFile file,
                                 Authentication authentication) {
        verifyOwnerOrAdmin(userId, authentication);
        return fileStorageService.saveAvatar(userId, file);
    }

    @PostMapping("/{userId}/documents")
    @Operation(summary = "Upload documents", description = "Upload one or more documents for a user (owner or admin)")
    public List<UserFile> uploadDocuments(@PathVariable Long userId,
                                          @RequestParam("files") List<MultipartFile> files,
                                          Authentication authentication) {
        verifyOwnerOrAdmin(userId, authentication);
        return fileStorageService.saveDocuments(userId, files);
    }

    @GetMapping("/files/{fileId}")
    @Operation(summary = "Download file", description = "Download a previously uploaded file by ID")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        UserFile userFile = fileStorageService.getFile(fileId);
        Resource resource = fileStorageService.loadFileAsResource(fileId);

        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        if (userFile.getContentType() != null) {
            mediaType = MediaType.parseMediaType(userFile.getContentType());
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + userFile.getFileName() + "\"")
                .body(resource);
    }

    @DeleteMapping("/files/{fileId}")
    @Operation(summary = "Delete file", description = "Delete an uploaded file by ID (owner or admin)")
    public void deleteFile(@PathVariable Long fileId, Authentication authentication) {
        Long userId = fileStorageService.getFile(fileId).getUser().getId();
        verifyOwnerOrAdmin(userId, authentication);
        fileStorageService.deleteFile(fileId);
    }

    private void verifyOwnerOrAdmin(Long userId, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName());
        if (!currentUser.getId().equals(userId) && currentUser.getRole() != UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: you can only modify your own profile");
        }
    }
}
