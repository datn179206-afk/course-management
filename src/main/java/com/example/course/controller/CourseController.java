package com.example.course.controller;

import com.example.course.dto.CourseFilter;
import com.example.course.dto.CourseForm;
import com.example.course.model.Course;
import com.example.course.service.CategoryService;
import com.example.course.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CourseController {

    private final CourseService courseService;
    private final CategoryService categoryService;

    public CourseController(CourseService courseService, CategoryService categoryService) {
        this.courseService = courseService;
        this.categoryService = categoryService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/courses";
    }

    // ================= Danh sách + tìm kiếm / lọc / sắp xếp =================

    @GetMapping("/courses")
    public String list(@ModelAttribute("filter") CourseFilter filter, BindingResult binding, Model model) {
        List<Course> courses = List.of();
        String filterError = null;

        if (binding.hasErrors()) {
            // Ví dụ: nhập chữ vào ô học phí hoặc loại khóa học là chuỗi bất kỳ
            filterError = "Điều kiện lọc không hợp lệ: học phí và loại khóa học phải là số.";
        } else if (filter.hasInvalidFeeRange()) {
            filterError = "Học phí tối thiểu không được lớn hơn học phí tối đa.";
        } else {
            courses = courseService.search(filter);
        }

        model.addAttribute("courses", courses);
        model.addAttribute("filterError", filterError);
        model.addAttribute("categories", categoryService.findAll());
        return "courses/list";
    }

    // ================= Chi tiết =================

    @GetMapping("/courses/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("course", courseService.getById(id));
        return "courses/detail";
    }

    // ================= Create =================

    @GetMapping("/courses/create")
    public String createForm(Model model) {
        model.addAttribute("courseForm", new CourseForm());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("formTitle", "Thêm khóa học");
        model.addAttribute("action", "/courses/create");
        return "courses/form";
    }

    @PostMapping("/courses/create")
    public String create(@Valid @ModelAttribute("courseForm") CourseForm form, BindingResult result,
                         Model model, RedirectAttributes redirect) {
        checkBusinessRules(form, null, result);
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("formTitle", "Thêm khóa học");
            model.addAttribute("action", "/courses/create");
            return "courses/form";
        }
        Course saved = courseService.create(form);
        redirect.addFlashAttribute("success", "Đã thêm khóa học \"" + saved.getName() + "\".");
        return "redirect:/courses/" + saved.getId();
    }

    // ================= Edit =================

    @GetMapping("/courses/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Course course = courseService.getById(id); // ném 404 nếu CourseId không tồn tại
        model.addAttribute("courseForm", CourseForm.from(course));
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("formTitle", "Sửa khóa học");
        model.addAttribute("action", "/courses/" + id + "/edit");
        return "courses/form";
    }

    @PostMapping("/courses/{id}/edit")
    public String edit(@PathVariable Long id, @Valid @ModelAttribute("courseForm") CourseForm form,
                       BindingResult result, Model model, RedirectAttributes redirect) {
        courseService.getById(id); // kiểm tra tồn tại trước
        checkBusinessRules(form, id, result);
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("formTitle", "Sửa khóa học");
            model.addAttribute("action", "/courses/" + id + "/edit");
            return "courses/form";
        }
        Course saved = courseService.update(id, form);
        redirect.addFlashAttribute("success", "Đã cập nhật khóa học \"" + saved.getName() + "\".");
        return "redirect:/courses/" + id;
    }

    // ================= Delete =================

    @PostMapping("/courses/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        Course course = courseService.getById(id);
        String name = course.getName();
        courseService.delete(id);
        redirect.addFlashAttribute("success", "Đã xóa khóa học \"" + name + "\".");
        return "redirect:/courses";
    }

    // ================= Quản lý ảnh =================

    @PostMapping("/courses/{id}/images")
    public String addImage(@PathVariable Long id,
                           @RequestParam(defaultValue = "") String imageUrl,
                           @RequestParam(defaultValue = "false") boolean thumbnail,
                           RedirectAttributes redirect) {
        courseService.getById(id);
        String url = imageUrl.trim();
        String lower = url.toLowerCase();
        if (url.isEmpty()) {
            redirect.addFlashAttribute("error", "Đường dẫn ảnh không được để trống.");
        } else if (url.length() > 1000 || !(lower.startsWith("http://") || lower.startsWith("https://"))) {
            redirect.addFlashAttribute("error", "Đường dẫn ảnh phải bắt đầu bằng http:// hoặc https:// (tối đa 1000 ký tự).");
        } else {
            courseService.addImage(id, url, thumbnail);
            redirect.addFlashAttribute("success", "Đã thêm ảnh cho khóa học.");
        }
        return "redirect:/courses/" + id;
    }

    @PostMapping("/courses/{id}/images/{imageId}/thumbnail")
    public String setThumbnail(@PathVariable Long id, @PathVariable Long imageId, RedirectAttributes redirect) {
        courseService.setThumbnail(id, imageId);
        redirect.addFlashAttribute("success", "Đã đổi ảnh đại diện.");
        return "redirect:/courses/" + id;
    }

    @PostMapping("/courses/{id}/images/{imageId}/delete")
    public String deleteImage(@PathVariable Long id, @PathVariable Long imageId, RedirectAttributes redirect) {
        courseService.deleteImage(id, imageId);
        redirect.addFlashAttribute("success", "Đã xóa ảnh.");
        return "redirect:/courses/" + id;
    }

    // ================= Kiểm tra nghiệp vụ cho Create / Edit =================

    private void checkBusinessRules(CourseForm form, Long courseId, BindingResult result) {
        if (form.getCategoryId() == null || result.hasFieldErrors("categoryId")) {
            return;
        }
        if (!categoryService.exists(form.getCategoryId())) {
            result.rejectValue("categoryId", "category.notFound", "Loại khóa học không tồn tại");
            return;
        }
        if (form.getName() != null && !form.getName().isBlank()
                && courseService.nameExists(form.getCategoryId(), form.getName(), courseId)) {
            result.rejectValue("name", "name.duplicate", "Tên khóa học đã tồn tại trong loại khóa học này");
        }
    }
}
