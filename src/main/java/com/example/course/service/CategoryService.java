package com.example.course.service;

import com.example.course.exception.ResourceNotFoundException;
import com.example.course.model.CourseCategory;
import com.example.course.repository.CourseCategoryRepository;
import com.example.course.repository.CourseRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryService {

    private final CourseCategoryRepository categoryRepository;
    private final CourseRepository courseRepository;

    public CategoryService(CourseCategoryRepository categoryRepository, CourseRepository courseRepository) {
        this.categoryRepository = categoryRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional(readOnly = true)
    public List<CourseCategory> findAll() {
        return categoryRepository.findAll(Sort.by("name"));
    }

    @Transactional(readOnly = true)
    public CourseCategory getById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Loại khóa học có mã " + id + " không tồn tại."));
    }

    @Transactional(readOnly = true)
    public boolean exists(Long id) {
        return id != null && categoryRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public long countCourses(Long categoryId) {
        return courseRepository.countByCategoryId(categoryId);
    }

    public CourseCategory create(CourseCategory category) {
        category.setId(null);
        category.setName(category.getName().trim());
        return categoryRepository.save(category);
    }

    public CourseCategory update(Long id, CourseCategory data) {
        CourseCategory category = getById(id);
        category.setName(data.getName().trim());
        category.setDescription(data.getDescription());
        return category;
    }

    /**
     * Ràng buộc 1: không cho xóa loại khóa học nếu vẫn còn khóa học thuộc loại đó.
     *
     * @throws IllegalStateException nếu loại còn khóa học
     */
    public void delete(Long id) {
        CourseCategory category = getById(id);
        long count = courseRepository.countByCategoryId(id);
        if (count > 0) {
            throw new IllegalStateException("Không thể xóa loại khóa học \"" + category.getName()
                    + "\" vì vẫn còn " + count + " khóa học thuộc loại này.");
        }
        categoryRepository.delete(category);
    }
}
