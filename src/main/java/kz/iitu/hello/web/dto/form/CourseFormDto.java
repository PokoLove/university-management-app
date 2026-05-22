package kz.iitu.hello.web.dto.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseFormDto {

    private Long id;
    @NotBlank(message = "Course name is required")
    @Size(min = 2, max = 100, message = "Course name must be between 2 and 100 characters")
    private String courseName;
    @NotNull(message = "Credits is required")
    @Min(value = 1, message = "Credits must be at least 1")
    @Max(value = 10, message = "Credits must be at most 10")
    private Integer credits;
    @NotNull(message = "Max students is required")
    @Min(value = 1, message = "Max students must be positive")
    private Integer maxStudents;
    @NotNull(message = "Teacher is required")
    private Long teacherId;
    @Size(max = 1000, message = "Too many students selected")
    private List<Long> studentIds;
}
