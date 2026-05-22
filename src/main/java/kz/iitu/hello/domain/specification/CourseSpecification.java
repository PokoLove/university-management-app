package kz.iitu.hello.domain.specification;

import kz.iitu.hello.domain.entity.Course;
import kz.iitu.hello.web.dto.search.CourseSearchForm;
import org.springframework.data.jpa.domain.Specification;

public final class CourseSpecification {

    private CourseSpecification() {
    }

    public static Specification<Course> withFilters(CourseSearchForm form) {
        return (root, query, criteriaBuilder) -> {
            var predicate = criteriaBuilder.conjunction();

            if (form.getCourseName() != null && !form.getCourseName().isBlank()) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("courseName")),
                                "%" + form.getCourseName().toLowerCase() + "%"
                        )
                );
            }

            if (form.getCreditsFrom() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("credits"), form.getCreditsFrom()));
            }

            if (form.getCreditsTo() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get("credits"), form.getCreditsTo()));
            }

            if (form.getMaxStudentsFrom() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("maxStudents"), form.getMaxStudentsFrom()));
            }

            if (form.getMaxStudentsTo() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get("maxStudents"), form.getMaxStudentsTo()));
            }

            if (form.getTeacherId() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("teacher").get("id"), form.getTeacherId()));
            }

            return predicate;
        };
    }
}
