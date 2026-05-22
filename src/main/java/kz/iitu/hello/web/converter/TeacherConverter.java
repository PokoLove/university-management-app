package kz.iitu.hello.web.converter;

import kz.iitu.hello.domain.entity.Course;
import kz.iitu.hello.domain.entity.Teacher;
import kz.iitu.hello.domain.entity.User;
import kz.iitu.hello.web.dto.form.TeacherFormDto;
import kz.iitu.hello.web.dto.grid.CourseGridDto;
import kz.iitu.hello.web.dto.grid.UserGridDto;
import kz.iitu.hello.web.dto.view.TeacherViewDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TeacherConverter {

    public void applyFormToEntity(TeacherFormDto form, Teacher teacher, User user, List<Course> courses) {
        teacher.setTeacherName(form.getTeacherName());
        teacher.setDepartment(form.getDepartment());
        teacher.setExperienceYears(form.getExperienceYears());
        teacher.setUser(user);
        teacher.setCourses(courses);
    }

    public TeacherFormDto toFormDto(Teacher teacher) {
        TeacherFormDto dto = new TeacherFormDto();
        dto.setId(teacher.getId());
        dto.setTeacherName(teacher.getTeacherName());
        dto.setDepartment(teacher.getDepartment());
        dto.setExperienceYears(teacher.getExperienceYears());
        if (teacher.getUser() != null) {
            dto.setUserId(teacher.getUser().getId());
        }
        if (teacher.getCourses() != null) {
            dto.setCourseIds(teacher.getCourses().stream().map(Course::getId).toList());
        }
        return dto;
    }

    public TeacherViewDto toViewDto(Teacher teacher) {
        TeacherViewDto dto = new TeacherViewDto();
        dto.setId(teacher.getId());
        dto.setTeacherName(teacher.getTeacherName());
        dto.setDepartment(teacher.getDepartment());
        dto.setExperienceYears(teacher.getExperienceYears());
        if (teacher.getUser() != null) {
            dto.setUser(toUserGridDto(teacher.getUser()));
        }
        List<CourseGridDto> courses = new ArrayList<>();
        if (teacher.getCourses() != null) {
            for (Course course : teacher.getCourses()) {
                courses.add(toCourseGridDto(course));
            }
        }
        dto.setCourses(courses);
        return dto;
    }

    public UserGridDto toUserGridDto(User user) {
        if (user == null) {
            return null;
        }
        UserGridDto dto = new UserGridDto();
        dto.setId(user.getId());
        dto.setUserName(user.getUserName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }

    public CourseGridDto toCourseGridDto(Course course) {
        if (course == null) {
            return null;
        }
        CourseGridDto dto = new CourseGridDto();
        dto.setId(course.getId());
        dto.setCourseName(course.getCourseName());
        dto.setCredits(course.getCredits());
        dto.setMaxStudents(course.getMaxStudents());
        return dto;
    }
}
