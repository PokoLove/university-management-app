package kz.iitu.hello.web.dto.view;

import kz.iitu.hello.web.dto.grid.CourseGridDto;
import kz.iitu.hello.web.dto.grid.UserGridDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentViewDto {

    private Long id;
    private String studentName;
    private Integer age;
    private String groupName;
    private Double gpa;
    private UserGridDto user;
    private List<CourseGridDto> courses;
}
