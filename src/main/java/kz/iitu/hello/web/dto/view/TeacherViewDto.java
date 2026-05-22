package kz.iitu.hello.web.dto.view;

import kz.iitu.hello.domain.enums.Department;
import kz.iitu.hello.web.dto.grid.CourseGridDto;
import kz.iitu.hello.web.dto.grid.UserGridDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherViewDto {

    private Long id;
    private String teacherName;
    private Integer experienceYears;
    private Department department;
    private UserGridDto user;
    private List<CourseGridDto> courses;
}
