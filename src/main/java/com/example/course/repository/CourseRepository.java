package com.example.course.repository;

import com.example.course.model.Course;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {

    /** Lọc + sắp xếp ngay trong câu SQL (không lấy hết rồi lặp để lọc). */
    @Override
    @EntityGraph(attributePaths = "category")
    List<Course> findAll(Specification<Course> spec, Sort sort);

    long countByCategoryId(Long categoryId);

    boolean existsByCategoryIdAndNameIgnoreCase(Long categoryId, String name);

    boolean existsByCategoryIdAndNameIgnoreCaseAndIdNot(Long categoryId, String name, Long id);
}
