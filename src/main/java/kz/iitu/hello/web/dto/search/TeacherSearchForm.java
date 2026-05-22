package kz.iitu.hello.web.dto.search;

import kz.iitu.hello.domain.enums.Department;
import lombok.Data;
import org.springframework.data.domain.Sort;

@Data
public class TeacherSearchForm {
    private String name;
    private Department department;
    private Integer experienceYearsFrom;
    private Integer experienceYearsTo;
    private String sortBy;
    private Sort.Direction sortDirection;
}
