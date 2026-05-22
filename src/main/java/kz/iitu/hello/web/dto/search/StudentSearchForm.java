package kz.iitu.hello.web.dto.search;

import lombok.Data;
import org.springframework.data.domain.Sort;

@Data
public class StudentSearchForm {
    private String name;
    private String groupName;
    private Integer ageFrom;
    private Integer ageTo;
    private Double gpaFrom;
    private Double gpaTo;
    private String sortBy;
    private Sort.Direction sortDirection;
}
