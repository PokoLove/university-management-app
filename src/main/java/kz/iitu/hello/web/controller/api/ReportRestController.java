package kz.iitu.hello.web.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kz.iitu.hello.async.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Async summary reports (ADMIN only)")
public class ReportRestController {

    private final ReportService reportService;

    @GetMapping("/summary")
    @Operation(
        summary = "Get summary report",
        description = "Returns total counts of students, teachers and courses. " +
                      "Counts are fetched in parallel using CompletableFuture."
    )
    public Map<String, Long> getSummary() {
        return reportService.buildSummaryReport();
    }
}
