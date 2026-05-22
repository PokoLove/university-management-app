package kz.iitu.hello.web.dto.search;

import lombok.Data;
import org.springframework.data.domain.Sort;

@Data
public class CourseSearchForm {
    private String courseName;
    private Integer creditsFrom;
    private Integer creditsTo;
    private Integer maxStudentsFrom;
    private Integer maxStudentsTo;
    private Long teacherId;
    private String sortBy;
    private Sort.Direction sortDirection;
}
