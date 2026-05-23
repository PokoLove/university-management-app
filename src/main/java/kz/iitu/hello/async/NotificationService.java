package kz.iitu.hello.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Async process #1 — simulates sending notifications (email/push).
 * Uses @Async so the caller does not block waiting for delivery.
 */
@Slf4j
@Service
public class NotificationService {


    @Async("notificationExecutor")
    public CompletableFuture<Void> notifyStudentEnrolled(String studentName, String courseName) {
        log.info("[ASYNC] Sending enrollment notification to student='{}' for course='{}'",
                studentName, courseName);
        try {
            // Simulates I/O delay (e.g. SMTP call)
            Thread.sleep(300);
            log.info("[ASYNC] Enrollment notification delivered: student='{}', course='{}'",
                    studentName, courseName);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("[ASYNC] Enrollment notification interrupted for student='{}'", studentName);
        }
        return CompletableFuture.completedFuture(null);
    }


    @Async("notificationExecutor")
    public CompletableFuture<Void> notifyStudentRemoved(String studentName, String courseName) {
        log.info("[ASYNC] Sending removal notification to student='{}' for course='{}'",
                studentName, courseName);
        try {
            Thread.sleep(300);
            log.info("[ASYNC] Removal notification delivered: student='{}', course='{}'",
                    studentName, courseName);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("[ASYNC] Removal notification interrupted for student='{}'", studentName);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Async("notificationExecutor")
    public CompletableFuture<Void> notifyNewUserRegistered(String username, String email) {
        log.info("[ASYNC] Sending welcome notification to username='{}', email='{}'",
                username, email);
        try {
            Thread.sleep(200);
            log.info("[ASYNC] Welcome notification delivered to username='{}'", username);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("[ASYNC] Welcome notification interrupted for username='{}'", username);
        }
        return CompletableFuture.completedFuture(null);
    }
}
