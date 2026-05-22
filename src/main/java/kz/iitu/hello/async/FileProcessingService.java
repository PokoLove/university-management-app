package kz.iitu.hello.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Async process #3 — post-upload file processing (virus scan, metadata extraction).
 * Decoupled from the upload request so the HTTP response is returned immediately.
 */
@Slf4j
@Service
public class FileProcessingService {

    /**
     * Simulates an asynchronous virus/malware scan after a file is uploaded.
     *
     * @param fileId   DB id of the stored UserFile
     * @param fileName original file name for log context
     * @return CompletableFuture<Boolean> — true if file is clean
     */
    @Async("notificationExecutor")
    public CompletableFuture<Boolean> scanFileAsync(Long fileId, String fileName) {
        log.info("[ASYNC] Starting virus scan: fileId={}, fileName='{}', thread={}",
                fileId, fileName, Thread.currentThread().getName());
        try {
            // Simulates scan duration
            Thread.sleep(500);
            log.info("[ASYNC] Virus scan passed: fileId={}, fileName='{}'", fileId, fileName);
            return CompletableFuture.completedFuture(true);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("[ASYNC] Virus scan interrupted: fileId={}", fileId);
            return CompletableFuture.completedFuture(false);
        }
    }

    /**
     * Simulates extracting and logging metadata from an uploaded document.
     *
     * @param fileId   DB id of the stored UserFile
     * @param filePath absolute path to the stored file
     */
    @Async("notificationExecutor")
    public CompletableFuture<Void> extractMetadataAsync(Long fileId, String filePath) {
        log.info("[ASYNC] Extracting metadata: fileId={}, path='{}', thread={}",
                fileId, filePath, Thread.currentThread().getName());
        try {
            Thread.sleep(400);
            log.info("[ASYNC] Metadata extracted: fileId={}, path='{}'", fileId, filePath);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("[ASYNC] Metadata extraction interrupted: fileId={}", fileId);
        }
        return CompletableFuture.completedFuture(null);
    }
}
