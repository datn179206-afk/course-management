package com.example.course.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/** Loại khóa học. Một CourseCategory có nhiều Course. */
@Entity
@Table(name = "course_categories")
public class CourseCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên loại khóa học không được để trống")
    @Size(max = 150, message = "Tên loại khóa học tối đa 150 ký tự")
    @Column(nullable = false, length = 150)
    private String name;

    @Size(max = 1000, message = "Mô tả tối đa 1000 ký tự")
    @Column(length = 1000)
    private String description;

    // Không dùng cascade REMOVE: việc xóa loại được kiểm soát bằng ràng buộc nghiệp vụ.
    @OneToMany(mappedBy = "category")
    private List<Course> courses = new ArrayList<>();

    public CourseCategory() {
    }

    public CourseCategory(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<Course> getCourses() { return courses; }
    public void setCourses(List<Course> courses) { this.courses = courses; }
}
