package com.example.course.service;

import com.example.course.dto.CourseFilter;
import com.example.course.dto.CourseForm;
import com.example.course.exception.ResourceNotFoundException;
import com.example.course.model.Course;
import com.example.course.model.CourseCategory;
import com.example.course.model.CourseImage;
import com.example.course.repository.CourseCategoryRepository;
import com.example.course.repository.CourseRepository;
import com.example.course.repository.CourseSpecifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseCategoryRepository categoryRepository;

    public CourseService(CourseRepository courseRepository, CourseCategoryRepository categoryRepository) {
        this.courseRepository = courseRepository;
        this.categoryRepository = categoryRepository;
    }

    // ---------------- Tra cứu ----------------

    @Transactional(readOnly = true)
    public List<Course> search(CourseFilter filter) {
        return courseRepository.findAll(CourseSpecifications.byFilter(filter), filter.toSort());
    }

    @Transactional(readOnly = true)
    public Course getById(Long id) {
        return courseRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Khóa học có mã " + id + " không tồn tại."));
    }

    @Transactional(readOnly = true)
    public boolean nameExists(Long categoryId, String name, Long excludeCourseId) {
        String trimmed = name.trim();
        return excludeCourseId == null
                ? courseRepository.existsByCategoryIdAndNameIgnoreCase(categoryId, trimmed)
                : courseRepository.existsByCategoryIdAndNameIgnoreCaseAndIdNot(categoryId, trimmed, excludeCourseId);
    }

    // ---------------- Create / Edit / Delete ----------------

    public Course create(CourseForm form) {
        Course course = new Course();
        apply(course, form);
        return courseRepository.save(course);
    }

    public Course update(Long id, CourseForm form) {
        Course course = getById(id);
        apply(course, form);
        return course;
    }

    public void delete(Long id) {
        courseRepository.delete(getById(id));
    }

    private void apply(Course course, CourseForm form) {
        CourseCategory category = categoryRepository.findById(form.getCategoryId()).orElseThrow(() ->
                new ResourceNotFoundException("Loại khóa học có mã " + form.getCategoryId() + " không tồn tại."));
        course.setName(form.getName().trim());
        course.setTuitionFee(form.getTuitionFee());
        course.setDuration(form.getDuration());
        course.setActive(form.isActive());
        course.setDescription(form.getDescription());
        course.setCategory(category);
    }

    // ---------------- Quản lý ảnh ----------------

    /** Thêm ảnh bằng URL. Ảnh đầu tiên của khóa học tự động là ảnh đại diện. */
    public CourseImage addImage(Long courseId, String imageUrl, boolean makeThumbnail) {
        Course course = getById(courseId);
        boolean thumbnail = makeThumbnail || course.getImages().isEmpty();
        if (thumbnail) {
            clearThumbnails(course);
        }
        CourseImage image = new CourseImage(imageUrl.trim(), thumbnail);
        course.addImage(image);
        courseRepository.flush();
        return image;
    }

    /** Đổi ảnh đại diện: ảnh đại diện cũ tự động chuyển thành ảnh thường. */
    public void setThumbnail(Long courseId, Long imageId) {
        Course course = getById(courseId);
        CourseImage target = findImage(course, imageId);
        clearThumbnails(course);
        target.setThumbnail(true);
    }

    public void deleteImage(Long courseId, Long imageId) {
        Course course = getById(courseId);
        CourseImage target = findImage(course, imageId);
        boolean wasThumbnail = target.isThumbnail();
        course.getImages().remove(target);
        // Nếu vừa xóa ảnh đại diện thì chọn ảnh còn lại đầu tiên làm đại diện.
        if (wasThumbnail && !course.getImages().isEmpty()) {
            course.getImages().get(0).setThumbnail(true);
        }
    }

    private void clearThumbnails(Course course) {
        course.getImages().forEach(i -> i.setThumbnail(false));
    }

    private CourseImage findImage(Course course, Long imageId) {
        return course.getImages().stream()
                .filter(i -> i.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ảnh có mã " + imageId + " không tồn tại trong khóa học này."));
    }
}
