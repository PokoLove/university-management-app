package kz.iitu.hello.config;

import kz.iitu.hello.domain.entity.Course;
import kz.iitu.hello.domain.entity.Student;
import kz.iitu.hello.domain.entity.Teacher;
import kz.iitu.hello.domain.entity.User;
import kz.iitu.hello.domain.enums.Department;
import kz.iitu.hello.domain.enums.UserRole;
import kz.iitu.hello.domain.repository.CoursesRepository;
import kz.iitu.hello.domain.repository.StudentsRepository;
import kz.iitu.hello.domain.repository.TeachersRepository;
import kz.iitu.hello.domain.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsersRepository usersRepository;
    private final StudentsRepository studentsRepository;
    private final TeachersRepository teachersRepository;
    private final CoursesRepository coursesRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        ensureLabAdmin();

        if (usersRepository.count() > 1) {
            return; // только если нет записи
        }

        String encoded = passwordEncoder.encode("test1234");

        // ── Admin ─────────────────────────
        User adminUser = new User();
        adminUser.setUserName("test_admin");
        adminUser.setEmail("test_admin@test.com");
        adminUser.setPassword(encoded);
        adminUser.setRole(UserRole.ADMIN);
        usersRepository.save(adminUser);

        // ── all ───────────────────────────
        Department[] departments = {Department.IT, Department.MATHEMATICS, Department.PHYSICS};
        int[] experiences = {10, 7, 5};
        Teacher[] teachers = new Teacher[3];

        for (int i = 1; i <= 3; i++) {
            User user = new User();
            user.setUserName("test_teacher_" + i);
            user.setEmail("test_teacher_" + i + "@test.com");
            user.setPassword(encoded);
            user.setRole(UserRole.TEACHER);
            usersRepository.save(user);

            Teacher teacher = new Teacher();
            teacher.setTeacherName("Test Teacher " + i);
            teacher.setExperienceYears(experiences[i - 1]);
            teacher.setDepartment(departments[i - 1]);
            teacher.setUser(user);
            teachers[i - 1] = teachersRepository.save(teacher);
        }

        Course course1 = new Course();
        course1.setCourseName("Test Course 1");
        course1.setCredits(4);
        course1.setMaxStudents(30);
        course1.setTeacher(teachers[0]);
        course1 = coursesRepository.save(course1);

        Course course2 = new Course();
        course2.setCourseName("Test Course 2");
        course2.setCredits(3);
        course2.setMaxStudents(30);
        course2.setTeacher(teachers[0]);
        course2 = coursesRepository.save(course2);

        double[] gpas = {3.8, 3.5, 3.2, 3.9, 2.7, 3.1, 2.9, 3.6, 3.3};
        int[] ages    = { 20,  21,  19,  22,  20,  21,  19,  23,  20};

        for (int i = 1; i <= 9; i++) {
            User user = new User();
            user.setUserName("test_student_" + i);
            user.setEmail("test_student_" + i + "@test.com");
            user.setPassword(encoded);
            user.setRole(UserRole.STUDENT);
            usersRepository.save(user);

            String group = "test_group_" + ((i - 1) / 3 + 1);

            Student student = new Student();
            student.setStudentName("Test Student " + i);
            student.setAge(ages[i - 1]);
            student.setGroupName(group);
            student.setGpa(gpas[i - 1]);
            student.setUser(user);

            if (i <= 6) student.getCourses().add(course1);
            if (i >= 4) student.getCourses().add(course2);

            studentsRepository.save(student);
        }
    }

    private void ensureLabAdmin() { //labka admin (если запистер бар но недоступно)
        if (usersRepository.existsByUserNameIgnoreCase("lab_admin")) {
            return;
        }
        User admin = new User();
        admin.setUserName("lab_admin");
        admin.setEmail("lab_admin@test.com");
        admin.setPassword(passwordEncoder.encode("test1234"));
        admin.setRole(UserRole.ADMIN);
        usersRepository.save(admin);
    }
}
