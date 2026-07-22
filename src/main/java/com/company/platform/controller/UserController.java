package com.company.platform.controller;

import com.company.platform.dto.UserDTO;
import com.company.platform.service.interfaces.RoleService;
import com.company.platform.service.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        if (!model.containsAttribute("userDTO")) {
            model.addAttribute("userDTO", UserDTO.builder().enabled(true).build());
        }
        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("isEdit", false);
        return "users/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("userDTO") UserDTO dto,
                          BindingResult result,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            result.rejectValue("password", "required", "Le mot de passe est obligatoire à la création");
        } else if (dto.getPassword().length() < 6) {
            result.rejectValue("password", "size", "Le mot de passe doit contenir au moins 6 caractères");
        }
        if (result.hasErrors()) {
            model.addAttribute("roles", roleService.findAll());
            model.addAttribute("isEdit", false);
            return "users/form";
        }
        try {
            userService.create(dto);
        } catch (RuntimeException ex) {
            result.rejectValue("email", "duplicate", ex.getMessage());
            model.addAttribute("roles", roleService.findAll());
            model.addAttribute("isEdit", false);
            return "users/form";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Utilisateur créé avec succès");
        return "redirect:/users";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        if (!model.containsAttribute("userDTO")) {
            model.addAttribute("userDTO", userService.findById(id));
        }
        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("isEdit", true);
        model.addAttribute("userId", id);
        return "users/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                          @Valid @ModelAttribute("userDTO") UserDTO dto,
                          BindingResult result,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        if (dto.getPassword() != null && !dto.getPassword().isBlank() && dto.getPassword().length() < 6) {
            result.rejectValue("password", "size", "Le mot de passe doit contenir au moins 6 caractères");
        }
        if (result.hasErrors()) {
            model.addAttribute("roles", roleService.findAll());
            model.addAttribute("isEdit", true);
            model.addAttribute("userId", id);
            return "users/form";
        }
        try {
            userService.update(id, dto);
        } catch (RuntimeException ex) {
            result.rejectValue("email", "duplicate", ex.getMessage());
            model.addAttribute("roles", roleService.findAll());
            model.addAttribute("isEdit", true);
            model.addAttribute("userId", id);
            return "users/form";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Utilisateur mis à jour avec succès");
        return "redirect:/users";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Utilisateur supprimé avec succès");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/users";
    }
}
