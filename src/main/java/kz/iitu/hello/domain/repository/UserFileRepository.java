package kz.iitu.hello.domain.repository;

import kz.iitu.hello.domain.entity.UserFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserFileRepository extends JpaRepository<UserFile, Long> {
    List<UserFile> findByUserId(Long userId);

    List<UserFile> findByUserIdAndFileType(Long userId, String fileType);
}
