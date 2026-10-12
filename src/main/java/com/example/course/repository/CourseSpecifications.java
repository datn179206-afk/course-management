package com.example.course.repository;

import com.example.course.dto.CourseFilter;
import com.example.course.model.Course;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/** Ghép các điều kiện lọc thành một câu WHERE duy nhất, thực thi trong database. */
public final class CourseSpecifications {

    private CourseSpecifications() {
    }

    public static Specification<Course> byFilter(CourseFilter f) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (f.getKeyword() != null && !f.getKeyword().isBlank()) {
                String pattern = "%" + f.getKeyword().trim().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("name")), pattern));
            }
            if (f.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("category").get("id"), f.getCategoryId()));
            }
            if (CourseFilter.STATUS_ACTIVE.equals(f.getStatus())) {
                predicates.add(cb.isTrue(root.get("active")));
            } else if (CourseFilter.STATUS_INACTIVE.equals(f.getStatus())) {
                predicates.add(cb.isFalse(root.get("active")));
            }
            if (f.getMinFee() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("tuitionFee"), f.getMinFee()));
            }
            if (f.getMaxFee() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("tuitionFee"), f.getMaxFee()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
