package com.company.platform.controller;

import com.company.platform.entity.Attachment;
import com.company.platform.entity.User;
import com.company.platform.service.interfaces.AttachmentService;
import com.company.platform.service.interfaces.ComplaintService;
import com.company.platform.util.FileStorageUtil;
import com.company.platform.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.MalformedURLException;

@Controller
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;
    private final ComplaintService complaintService;
    private final FileStorageUtil fileStorageUtil;
    private final SecurityUtil securityUtil;

    @GetMapping("/attachments/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        Attachment attachment = attachmentService.getEntityById(id);
        checkAccess(attachment);

        try {
            Resource resource = new UrlResource(fileStorageUtil.resolve(attachment.getFilePath()).toUri());
            if (!resource.exists()) {
                throw new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Fichier introuvable sur le serveur");
            }
            String contentType = attachment.getFileType() != null ? attachment.getFileType() : "application/octet-stream";
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, "Erreur de lecture du fichier");
        }
    }

    @PostMapping("/attachments/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Attachment attachment = attachmentService.getEntityById(id);
        checkAccess(attachment);
        Long complaintId = attachment.getComplaint().getId();
        attachmentService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Pièce jointe supprimée");
        return "redirect:/complaints/" + complaintId;
    }

    private void checkAccess(Attachment attachment) {
        User currentUser = securityUtil.getCurrentUser();
        Long complaintId = attachment.getComplaint().getId();
        if (!complaintService.canAccess(complaintId, currentUser)) {
            throw new AccessDeniedException("Vous n'avez pas accès à cette pièce jointe");
        }
    }
}
