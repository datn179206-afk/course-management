package com.example.course.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Khóa học. Thuộc một CourseCategory, có nhiều CourseImage. */
@Entity
@Table(name = "courses",
        uniqueConstraints = @UniqueConstraint(name = "uk_course_name_category",
                columnNames = {"name", "category_id"}))
public class Course {

    /** Ngưỡng phân loại thời lượng (giờ): <= 30 là ngắn hạn, > 30 là dài hạn. */
    public static final int SHORT_TERM_MAX_HOURS = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "tuition_fee", nullable = false, precision = 15, scale = 2)
    private BigDecimal tuitionFee;

    /** Thời lượng tính theo số giờ. */
    @Column(nullable = false)
    private int duration;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(length = 2000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private CourseCategory category;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<CourseImage> images = new ArrayList<>();

    public Course() {
    }

    // ----- Giá trị tính khi hiển thị, KHÔNG lưu xuống database -----

    /** Ngắn hạn (Duration <= 30) hoặc Dài hạn (Duration > 30). */
    @Transient
    public String getDurationType() {
        return duration <= SHORT_TERM_MAX_HOURS ? "Ngắn hạn" : "Dài hạn";
    }

    @Transient
    public String getStatusText() {
        return active ? "Đang tuyển sinh" : "Ngừng tuyển sinh";
    }

    /** Ảnh đại diện; null nếu khóa học chưa có ảnh nào. */
    @Transient
    public CourseImage getThumbnail() {
        return images.stream().filter(CourseImage::isThumbnail).findFirst().orElse(null);
    }

    public void addImage(CourseImage image) {
        image.setCourse(this);
        images.add(image);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getTuitionFee() { return tuitionFee; }
    public void setTuitionFee(BigDecimal tuitionFee) { this.tuitionFee = tuitionFee; }
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public CourseCategory getCategory() { return category; }
    public void setCategory(CourseCategory category) { this.category = category; }
    public List<CourseImage> getImages() { return images; }
    public void setImages(List<CourseImage> images) { this.images = images; }
}
