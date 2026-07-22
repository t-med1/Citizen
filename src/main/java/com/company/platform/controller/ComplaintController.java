package com.company.platform.controller;

import com.company.platform.dto.ComplaintDTO;
import com.company.platform.entity.ComplaintStatus;
import com.company.platform.entity.User;
import com.company.platform.service.interfaces.*;
import com.company.platform.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final CommentService commentService;
    private final AttachmentService attachmentService;
    private final SecurityUtil securityUtil;

    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                        @RequestParam(required = false) ComplaintStatus status,
                        Model model) {
        User currentUser = securityUtil.getCurrentUser();
        List<ComplaintDTO> complaints = complaintService.findForUser(currentUser);

        if (keyword != null && !keyword.isBlank()) {
            String lower = keyword.toLowerCase();
            complaints = complaints.stream()
                    .filter(c -> c.getTitle().toLowerCase().contains(lower))
                    .toList();
        }
        if (status != null) {
            complaints = complaints.stream().filter(c -> c.getStatus() == status).toList();
        }

        model.addAttribute("complaints", complaints);
        model.addAttribute("statuses", ComplaintStatus.values());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedStatus", status);
        return "complaints/list";
    }

    @PreAuthorize("hasRole('CITIZEN')")
    @GetMapping("/new")
    public String newForm(Model model) {
        if (!model.containsAttribute("complaintDTO")) {
            model.addAttribute("complaintDTO", ComplaintDTO.builder().build());
        }
        model.addAttribute("categories", categoryService.findAll());
        return "complaints/form";
    }

    @PreAuthorize("hasRole('CITIZEN')")
    @PostMapping
    public String create(@Valid @ModelAttribute("complaintDTO") ComplaintDTO dto,
                          BindingResult result,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            return "complaints/form";
        }
        User currentUser = securityUtil.getCurrentUser();
        ComplaintDTO created = complaintService.create(dto, currentUser);
        redirectAttributes.addFlashAttribute("successMessage", "Réclamation soumise avec succès");
        return "redirect:/complaints/" + created.getId();
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        User currentUser = securityUtil.getCurrentUser();
        if (!complaintService.canAccess(id, currentUser)) {
            throw new AccessDeniedException("Vous n'avez pas accès à cette réclamation");
        }
        model.addAttribute("complaint", complaintService.findById(id));
        model.addAttribute("comments", commentService.findByComplaint(id));
        model.addAttribute("attachments", attachmentService.findByComplaint(id));
        model.addAttribute("statuses", ComplaintStatus.values());
        model.addAttribute("agents", userService.findAgents());
        model.addAttribute("currentUser", currentUser);
        return "complaints/detail";
    }

    @PreAuthorize("hasAnyRole('AGENT','ADMIN')")
    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                                @RequestParam ComplaintStatus status,
                                @RequestParam(required = false) Long assignedAgentId,
                                RedirectAttributes redirectAttributes) {
        User currentUser = securityUtil.getCurrentUser();
        if (!complaintService.canAccess(id, currentUser)) {
            throw new AccessDeniedException("Vous n'avez pas accès à cette réclamation");
        }
        complaintService.updateStatus(id, status, assignedAgentId);
        redirectAttributes.addFlashAttribute("successMessage", "Statut mis à jour avec succès");
        return "redirect:/complaints/" + id;
    }

    @PreAuthorize("hasAnyRole('AGENT','ADMIN')")
    @PostMapping("/{id}/comments")
    public String addComment(@PathVariable Long id,
                              @RequestParam String content,
                              RedirectAttributes redirectAttributes) {
        User currentUser = securityUtil.getCurrentUser();
        if (!complaintService.canAccess(id, currentUser)) {
            throw new AccessDeniedException("Vous n'avez pas accès à cette réclamation");
        }
        commentService.addComment(id, content, currentUser);
        redirectAttributes.addFlashAttribute("successMessage", "Commentaire ajouté");
        return "redirect:/complaints/" + id;
    }

    @PostMapping("/{id}/attachments")
    public String uploadAttachment(@PathVariable Long id,
                                    @RequestParam("file") MultipartFile file,
                                    RedirectAttributes redirectAttributes) {
        User currentUser = securityUtil.getCurrentUser();
        if (!complaintService.canAccess(id, currentUser)) {
            throw new AccessDeniedException("Vous n'avez pas accès à cette réclamation");
        }
        try {
            attachmentService.upload(id, file);
            redirectAttributes.addFlashAttribute("successMessage", "Pièce jointe ajoutée");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/complaints/" + id;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        complaintService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Réclamation supprimée avec succès");
        return "redirect:/complaints";
    }
}
