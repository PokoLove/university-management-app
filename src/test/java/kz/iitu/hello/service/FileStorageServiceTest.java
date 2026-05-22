package kz.iitu.hello.service;

import kz.iitu.hello.domain.repository.UserFileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class FileStorageServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private UserFileRepository userFileRepository;

    @InjectMocks
    private FileStorageService fileStorageService;

    @Test
    void saveAvatar_withWrongContentType_throwsIllegalArgumentException() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "document.pdf", "application/pdf", new byte[1024]);

        assertThatThrownBy(() -> fileStorageService.saveAvatar(1L, file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Avatar must be a JPEG or PNG image");
    }

    @Test
    void saveAvatar_withFileSizeExceedingLimit_throwsIllegalArgumentException() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", new byte[6 * 1024 * 1024]);

        assertThatThrownBy(() -> fileStorageService.saveAvatar(1L, file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("File size exceeds 5MB limit");
    }

    @Test
    void saveDocuments_withEmptyList_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> fileStorageService.saveDocuments(1L, List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("At least one document file is required");
    }

    @Test
    void saveAvatar_withEmptyFile_throwsIllegalArgumentException() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", new byte[0]);

        assertThatThrownBy(() -> fileStorageService.saveAvatar(1L, emptyFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Avatar file is required");
    }
}
