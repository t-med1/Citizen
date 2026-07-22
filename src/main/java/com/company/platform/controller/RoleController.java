package com.company.platform.controller;

import com.company.platform.dto.RoleDTO;
import com.company.platform.service.interfaces.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("roles", roleService.findAll());
        return "roles/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        if (!model.containsAttribute("roleDTO")) {
            model.addAttribute("roleDTO", RoleDTO.builder().build());
        }
        model.addAttribute("isEdit", false);
        return "roles/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("roleDTO") RoleDTO dto,
                          BindingResult result,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "roles/form";
        }
        try {
            roleService.create(dto);
        } catch (RuntimeException ex) {
            result.rejectValue("name", "duplicate", ex.getMessage());
            model.addAttribute("isEdit", false);
            return "roles/form";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Rôle créé avec succès");
        return "redirect:/roles";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        if (!model.containsAttribute("roleDTO")) {
            model.addAttribute("roleDTO", roleService.findById(id));
        }
        model.addAttribute("isEdit", true);
        model.addAttribute("roleId", id);
        return "roles/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                          @Valid @ModelAttribute("roleDTO") RoleDTO dto,
                          BindingResult result,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("roleId", id);
            return "roles/form";
        }
        try {
            roleService.update(id, dto);
        } catch (RuntimeException ex) {
            result.rejectValue("name", "duplicate", ex.getMessage());
            model.addAttribute("isEdit", true);
            model.addAttribute("roleId", id);
            return "roles/form";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Rôle mis à jour avec succès");
        return "redirect:/roles";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            roleService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Rôle supprimé avec succès");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/roles";
    }
}
