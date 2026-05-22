package kz.iitu.hello.web.dto.grid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentGridDto {

    private Long id;
    private String studentName;
    private Integer age;
    private String groupName;
    private Double gpa;
}
