package kz.iitu.hello.domain.repository;

import kz.iitu.hello.domain.entity.Teacher;
import kz.iitu.hello.domain.enums.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface TeachersRepository extends JpaRepository<Teacher, Long> {

    @Query("""
            SELECT t FROM Teacher t
            WHERE (:department IS NULL OR t.department = :department)
              AND (:name IS NULL OR LOWER(t.teacherName) LIKE CONCAT('%', LOWER(CAST(:name as string)), '%'))
            """)
    Page<Teacher> searchTeachers(@Param("department") Department department,
                                 @Param("name") String name,
                                 Pageable pageable);

    Optional<Teacher> findByUserId(Long userId);
}
