package com.company.platform.controller;

import com.company.platform.dto.ProfileUpdateDTO;
import com.company.platform.entity.User;
import com.company.platform.service.interfaces.UserService;
import com.company.platform.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final SecurityUtil securityUtil;

    @GetMapping("/profile")
    public String profile(Model model) {
        User currentUser = securityUtil.getCurrentUser();
        model.addAttribute("currentUser", currentUser);
        if (!model.containsAttribute("profileUpdateDTO")) {
            model.addAttribute("profileUpdateDTO", ProfileUpdateDTO.builder()
                    .fullName(currentUser.getFullName())
                    .phone(currentUser.getPhone())
                    .address(currentUser.getAddress())
                    .build());
        }
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("profileUpdateDTO") ProfileUpdateDTO dto,
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        User currentUser = securityUtil.getCurrentUser();

        if (result.hasErrors()) {
            model.addAttribute("currentUser", currentUser);
            return "profile";
        }

        try {
            userService.updateProfile(currentUser.getId(), dto);
        } catch (IllegalArgumentException ex) {
            result.rejectValue("currentPassword", "invalid", ex.getMessage());
            model.addAttribute("currentUser", currentUser);
            return "profile";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Profil mis à jour avec succès");
        return "redirect:/profile";
    }
}
