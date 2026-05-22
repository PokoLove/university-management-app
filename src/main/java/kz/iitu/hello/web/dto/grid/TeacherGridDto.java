package kz.iitu.hello.web.dto.grid;

import kz.iitu.hello.domain.enums.Department;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherGridDto {

    private Long id;
    private String teacherName;
    private Integer experienceYears;
    private Department department;
}
