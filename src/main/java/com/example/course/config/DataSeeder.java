package com.example.course.config;

import com.example.course.model.Course;
import com.example.course.model.CourseCategory;
import com.example.course.model.CourseImage;
import com.example.course.repository.CourseCategoryRepository;
import com.example.course.repository.CourseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/** Dữ liệu mẫu: 3 loại khóa học, 7 khóa học (chỉ nạp khi database còn trống). */
@Component
public class DataSeeder implements CommandLineRunner {

    private final CourseCategoryRepository categoryRepository;
    private final CourseRepository courseRepository;

    public DataSeeder(CourseCategoryRepository categoryRepository, CourseRepository courseRepository) {
        this.categoryRepository = categoryRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            return;
        }

        CourseCategory web = categoryRepository.save(
                new CourseCategory("Lập trình Web", "Các khóa học phát triển ứng dụng web"));
        CourseCategory mobile = categoryRepository.save(
                new CourseCategory("Lập trình Di động", "Các khóa học phát triển ứng dụng di động"));
        CourseCategory db = categoryRepository.save(
                new CourseCategory("Cơ sở dữ liệu", "Các khóa học thiết kế và quản trị cơ sở dữ liệu"));

        course(web, "ASP.NET Core", "5000000", 60, true,
                "Xây dựng ứng dụng web với ASP.NET Core MVC và Entity Framework Core.",
                "https://picsum.photos/seed/aspnet/400/250", "https://picsum.photos/seed/aspnet2/400/250");
        course(web, "ReactJS", "4000000", 40, true,
                "Xây dựng giao diện người dùng với ReactJS và Hooks.",
                "https://picsum.photos/seed/react/400/250");
        course(web, "HTML & CSS cơ bản", "1500000", 20, true,
                "Nền tảng HTML5, CSS3 và responsive design.");
        course(mobile, "Flutter", "4500000", 50, true,
                "Lập trình ứng dụng di động đa nền tảng với Flutter.",
                "https://picsum.photos/seed/flutter/400/250");
        course(mobile, "Android Kotlin", "3800000", 30, false,
                "Lập trình Android với Kotlin (hiện ngừng tuyển sinh).",
                "https://picsum.photos/seed/kotlin/400/250");
        course(db, "SQL Server cơ bản", "1200000", 24, true,
                "Truy vấn và thiết kế cơ sở dữ liệu với SQL Server.");
        course(db, "MongoDB", "2500000", 36, false,
                "Cơ sở dữ liệu NoSQL với MongoDB (hiện ngừng tuyển sinh).");
    }

    /** Ảnh đầu tiên (nếu có) là ảnh đại diện. */
    private void course(CourseCategory category, String name, String fee, int duration, boolean active,
                        String description, String... imageUrls) {
        Course c = new Course();
        c.setName(name);
        c.setTuitionFee(new BigDecimal(fee));
        c.setDuration(duration);
        c.setActive(active);
        c.setDescription(description);
        c.setCategory(category);
        for (int i = 0; i < imageUrls.length; i++) {
            c.addImage(new CourseImage(imageUrls[i], i == 0));
        }
        courseRepository.save(c);
    }
}
