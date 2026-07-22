package com.company.platform.controller;

import com.company.platform.dto.CategoryDTO;
import com.company.platform.service.interfaces.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "categories/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        if (!model.containsAttribute("categoryDTO")) {
            model.addAttribute("categoryDTO", CategoryDTO.builder().build());
        }
        model.addAttribute("isEdit", false);
        return "categories/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("categoryDTO") CategoryDTO dto,
                          BindingResult result,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "categories/form";
        }
        try {
            categoryService.create(dto);
        } catch (RuntimeException ex) {
            result.rejectValue("name", "duplicate", ex.getMessage());
            model.addAttribute("isEdit", false);
            return "categories/form";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Catégorie créée avec succès");
        return "redirect:/categories";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        if (!model.containsAttribute("categoryDTO")) {
            model.addAttribute("categoryDTO", categoryService.findById(id));
        }
        model.addAttribute("isEdit", true);
        model.addAttribute("categoryId", id);
        return "categories/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                          @Valid @ModelAttribute("categoryDTO") CategoryDTO dto,
                          BindingResult result,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("categoryId", id);
            return "categories/form";
        }
        try {
            categoryService.update(id, dto);
        } catch (RuntimeException ex) {
            result.rejectValue("name", "duplicate", ex.getMessage());
            model.addAttribute("isEdit", true);
            model.addAttribute("categoryId", id);
            return "categories/form";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Catégorie mise à jour avec succès");
        return "redirect:/categories";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Catégorie supprimée avec succès");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/categories";
    }
}
