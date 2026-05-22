package kz.iitu.hello.domain.repository;

import kz.iitu.hello.domain.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentsRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUserId(Long userId);
}
