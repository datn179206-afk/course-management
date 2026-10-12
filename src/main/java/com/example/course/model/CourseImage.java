package com.example.course.model;

import jakarta.persistence.*;

/** Ảnh của khóa học. Mỗi khóa học có tối đa một ảnh đại diện (IsThumbnail = true). */
@Entity
@Table(name = "course_images")
public class CourseImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_url", nullable = false, length = 1000)
    private String imageUrl;

    @Column(name = "is_thumbnail", nullable = false)
    private boolean thumbnail;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    public CourseImage() {
    }

    public CourseImage(String imageUrl, boolean thumbnail) {
        this.imageUrl = imageUrl;
        this.thumbnail = thumbnail;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public boolean isThumbnail() { return thumbnail; }
    public void setThumbnail(boolean thumbnail) { this.thumbnail = thumbnail; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
}
