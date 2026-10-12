package com.example.course.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/** Không để lỗi hệ thống hiện thẳng ra màn hình: luôn trả về trang thông báo thân thiện. */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(ResourceNotFoundException ex, Model model) {
        return render(model, HttpStatus.NOT_FOUND, "Không tìm thấy dữ liệu", ex.getMessage());
    }

    /** Ví dụ: /courses/abc (id không phải số). */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String handleTypeMismatch(MethodArgumentTypeMismatchException ex, Model model) {
        return render(model, HttpStatus.NOT_FOUND, "Không tìm thấy dữ liệu",
                "Mã truy cập không hợp lệ.");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResource(NoResourceFoundException ex, Model model) {
        return render(model, HttpStatus.NOT_FOUND, "Không tìm thấy trang",
                "Trang bạn truy cập không tồn tại.");
    }

    @ExceptionHandler(Exception.class)
    public String handleOther(Exception ex, Model model) {
        log.error("Lỗi không mong muốn", ex);
        return render(model, HttpStatus.INTERNAL_SERVER_ERROR, "Đã xảy ra lỗi",
                "Hệ thống gặp sự cố. Vui lòng thử lại sau.");
    }

    private String render(Model model, HttpStatus status, String title, String message) {
        model.addAttribute("status", status.value());
        model.addAttribute("errorTitle", title);
        model.addAttribute("errorMessage", message);
        return "error-page";
    }
}
