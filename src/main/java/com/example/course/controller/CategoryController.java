package com.example.course.controller;

import com.example.course.model.CourseCategory;
import com.example.course.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String list(Model model) {
        var categories = categoryService.findAll();
        Map<Long, Long> courseCounts = new LinkedHashMap<>();
        categories.forEach(c -> courseCounts.put(c.getId(), categoryService.countCourses(c.getId())));
        model.addAttribute("categories", categories);
        model.addAttribute("courseCounts", courseCounts);
        return "categories/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("category", new CourseCategory());
        model.addAttribute("formTitle", "Thêm loại khóa học");
        model.addAttribute("action", "/categories/create");
        return "categories/form";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("category") CourseCategory category, BindingResult result,
                         Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("formTitle", "Thêm loại khóa học");
            model.addAttribute("action", "/categories/create");
            return "categories/form";
        }
        categoryService.create(category);
        redirect.addFlashAttribute("success", "Đã thêm loại khóa học.");
        return "redirect:/categories";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("category", categoryService.getById(id));
        model.addAttribute("formTitle", "Sửa loại khóa học");
        model.addAttribute("action", "/categories/" + id + "/edit");
        return "categories/form";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id, @Valid @ModelAttribute("category") CourseCategory category,
                       BindingResult result, Model model, RedirectAttributes redirect) {
        categoryService.getById(id);
        if (result.hasErrors()) {
            model.addAttribute("formTitle", "Sửa loại khóa học");
            model.addAttribute("action", "/categories/" + id + "/edit");
            return "categories/form";
        }
        categoryService.update(id, category);
        redirect.addFlashAttribute("success", "Đã cập nhật loại khóa học.");
        return "redirect:/categories";
    }

    /** Ràng buộc 1: loại còn khóa học thì không được xóa. */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            categoryService.delete(id);
            redirect.addFlashAttribute("success", "Đã xóa loại khóa học.");
        } catch (IllegalStateException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/categories";
    }
}
