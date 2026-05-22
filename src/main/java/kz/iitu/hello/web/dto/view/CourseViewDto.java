package kz.iitu.hello.web.dto.view;

import kz.iitu.hello.web.dto.grid.StudentGridDto;
import kz.iitu.hello.web.dto.grid.TeacherGridDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseViewDto {

    private Long id;
    private String courseName;
    private Integer credits;
    private Integer maxStudents;
    private TeacherGridDto teacher;
    private List<StudentGridDto> students;
}
