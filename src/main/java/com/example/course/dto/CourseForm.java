package com.example.course.dto;

import com.example.course.model.Course;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** Dữ liệu form Create / Edit khóa học (kèm validation). */
public class CourseForm {

    @NotBlank(message = "Tên khóa học không được để trống")
    @Size(max = 200, message = "Tên khóa học tối đa 200 ký tự")
    private String name;

    @NotNull(message = "Học phí không được để trống")
    @DecimalMin(value = "0", inclusive = false, message = "Học phí phải lớn hơn 0")
    private BigDecimal tuitionFee;

    @NotNull(message = "Thời lượng không được để trống")
    @Min(value = 1, message = "Thời lượng phải lớn hơn 0")
    private Integer duration;

    private boolean active = true;

    @Size(max = 2000, message = "Mô tả tối đa 2000 ký tự")
    private String description;

    @NotNull(message = "Vui lòng chọn loại khóa học")
    private Long categoryId;

    public static CourseForm from(Course course) {
        CourseForm form = new CourseForm();
        form.name = course.getName();
        form.tuitionFee = course.getTuitionFee();
        form.duration = course.getDuration();
        form.active = course.isActive();
        form.description = course.getDescription();
        form.categoryId = course.getCategory().getId();
        return form;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getTuitionFee() { return tuitionFee; }
    public void setTuitionFee(BigDecimal tuitionFee) { this.tuitionFee = tuitionFee; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
}
