package kz.iitu.hello.async;

import kz.iitu.hello.domain.repository.CoursesRepository;
import kz.iitu.hello.domain.repository.StudentsRepository;
import kz.iitu.hello.domain.repository.TeachersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Async process #2 — generates summary reports without blocking the HTTP thread.
 * Each count query runs in the "reportExecutor" pool, then results are combined
 * with CompletableFuture.allOf().
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final StudentsRepository studentsRepository;
    private final TeachersRepository teachersRepository;
    private final CoursesRepository coursesRepository;

    /**
     * Counts total students asynchronously.
     */
    @Async("reportExecutor")
    @Transactional(readOnly = true)
    public CompletableFuture<Long> countStudentsAsync() {
        log.info("[ASYNC] Counting students on thread={}", Thread.currentThread().getName());
        long count = studentsRepository.count();
        log.info("[ASYNC] Student count result: {}", count);
        return CompletableFuture.completedFuture(count);
    }


    @Async("reportExecutor")
    @Transactional(readOnly = true)
    public CompletableFuture<Long> countTeachersAsync() {
        log.info("[ASYNC] Counting teachers on thread={}", Thread.currentThread().getName());
        long count = teachersRepository.count();
        log.info("[ASYNC] Teacher count result: {}", count);
        return CompletableFuture.completedFuture(count);
    }

    /**
     * Counts total courses asynchronously.
     */
    @Async("reportExecutor")
    @Transactional(readOnly = true)
    public CompletableFuture<Long> countCoursesAsync() {
        log.info("[ASYNC] Counting courses on thread={}", Thread.currentThread().getName());
        long count = coursesRepository.count();
        log.info("[ASYNC] Course count result: {}", count);
        return CompletableFuture.completedFuture(count);
    }

    /**
     * Builds a summary report by running all three counts in parallel,
     * then combining results with CompletableFuture.allOf().
     */
    public Map<String, Long> buildSummaryReport() {
        log.info("[REPORT] Starting parallel summary report generation");

        CompletableFuture<Long> studentsFuture  = countStudentsAsync();
        CompletableFuture<Long> teachersFuture  = countTeachersAsync();
        CompletableFuture<Long> coursesFuture   = countCoursesAsync();

        CompletableFuture.allOf(studentsFuture, teachersFuture, coursesFuture).join();

        Map<String, Long> report = new LinkedHashMap<>();
        report.put("totalStudents",  studentsFuture.join());
        report.put("totalTeachers",  teachersFuture.join());
        report.put("totalCourses",   coursesFuture.join());

        log.info("[REPORT] Summary report ready: {}", report);
        return report;
    }
}
