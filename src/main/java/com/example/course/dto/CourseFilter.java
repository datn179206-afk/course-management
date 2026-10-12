package com.example.course.dto;

import org.springframework.data.domain.Sort;

import java.math.BigDecimal;

/** Các điều kiện tìm kiếm / lọc / sắp xếp ở trang danh sách khóa học. */
public class CourseFilter {

    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_INACTIVE = "inactive";

    public static final String SORT_FEE_ASC = "fee_asc";
    public static final String SORT_FEE_DESC = "fee_desc";
    public static final String SORT_DURATION_ASC = "duration_asc";

    private String keyword;
    private Long categoryId;
    /** "" = tất cả, "active" = Đang tuyển sinh, "inactive" = Ngừng tuyển sinh. */
    private String status;
    private BigDecimal minFee;
    private BigDecimal maxFee;
    private String sort;

    public boolean hasInvalidFeeRange() {
        return minFee != null && maxFee != null && minFee.compareTo(maxFee) > 0;
    }

    public boolean isFiltering() {
        return (keyword != null && !keyword.isBlank()) || categoryId != null
                || (status != null && !status.isBlank()) || minFee != null || maxFee != null;
    }

    public Sort toSort() {
        if (SORT_FEE_ASC.equals(sort)) return Sort.by(Sort.Direction.ASC, "tuitionFee");
        if (SORT_FEE_DESC.equals(sort)) return Sort.by(Sort.Direction.DESC, "tuitionFee");
        if (SORT_DURATION_ASC.equals(sort)) return Sort.by(Sort.Direction.ASC, "duration");
        return Sort.by(Sort.Direction.ASC, "id");
    }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getMinFee() { return minFee; }
    public void setMinFee(BigDecimal minFee) { this.minFee = minFee; }
    public BigDecimal getMaxFee() { return maxFee; }
    public void setMaxFee(BigDecimal maxFee) { this.maxFee = maxFee; }
    public String getSort() { return sort; }
    public void setSort(String sort) { this.sort = sort; }
}
