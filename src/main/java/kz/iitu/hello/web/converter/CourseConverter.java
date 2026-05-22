package kz.iitu.hello.web.converter;

import kz.iitu.hello.domain.entity.Course;
import kz.iitu.hello.domain.entity.Student;
import kz.iitu.hello.domain.entity.Teacher;
import kz.iitu.hello.web.dto.form.CourseFormDto;
import kz.iitu.hello.web.dto.grid.StudentGridDto;
import kz.iitu.hello.web.dto.grid.TeacherGridDto;
import kz.iitu.hello.web.dto.view.CourseViewDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class CourseConverter {

    public void applyFormToEntity(CourseFormDto form, Course course, Teacher teacher, Set<Student> students) {
        course.setCourseName(form.getCourseName());
        course.setCredits(form.getCredits());
        course.setMaxStudents(form.getMaxStudents());
        course.setTeacher(teacher);
        course.setStudents(students);
    }

    public CourseFormDto toFormDto(Course course) {
        CourseFormDto dto = new CourseFormDto();
        dto.setId(course.getId());
        dto.setCourseName(course.getCourseName());
        dto.setCredits(course.getCredits());
        dto.setMaxStudents(course.getMaxStudents());
        if (course.getTeacher() != null) {
            dto.setTeacherId(course.getTeacher().getId());
        }
        if (course.getStudents() != null) {
            dto.setStudentIds(course.getStudents().stream().map(Student::getId).toList());
        }
        return dto;
    }

    public CourseViewDto toViewDto(Course course) {
        CourseViewDto dto = new CourseViewDto();
        dto.setId(course.getId());
        dto.setCourseName(course.getCourseName());
        dto.setCredits(course.getCredits());
        dto.setMaxStudents(course.getMaxStudents());
        if (course.getTeacher() != null) {
            dto.setTeacher(toTeacherGridDto(course.getTeacher()));
        }
        List<StudentGridDto> students = new ArrayList<>();
        if (course.getStudents() != null) {
            for (Student student : course.getStudents()) {
                students.add(toStudentGridDto(student));
            }
        }
        dto.setStudents(students);
        return dto;
    }

    public TeacherGridDto toTeacherGridDto(Teacher teacher) {
        if (teacher == null) {
            return null;
        }
        TeacherGridDto dto = new TeacherGridDto();
        dto.setId(teacher.getId());
        dto.setTeacherName(teacher.getTeacherName());
        dto.setDepartment(teacher.getDepartment());
        dto.setExperienceYears(teacher.getExperienceYears());
        return dto;
    }

    public StudentGridDto toStudentGridDto(Student student) {
        if (student == null) {
            return null;
        }
        StudentGridDto dto = new StudentGridDto();
        dto.setId(student.getId());
        dto.setStudentName(student.getStudentName());
        dto.setAge(student.getAge());
        dto.setGroupName(student.getGroupName());
        dto.setGpa(student.getGpa());
        return dto;
    }
}
