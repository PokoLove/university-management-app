package kz.iitu.hello.domain.repository;

import kz.iitu.hello.domain.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CoursesRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
}
