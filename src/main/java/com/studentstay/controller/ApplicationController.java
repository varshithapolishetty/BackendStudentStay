package com.studentstay.controller;

import com.studentstay.domain.User;
import com.studentstay.dto.ApiDtos;
import com.studentstay.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ApplicationController {
    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/applications")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiDtos.ApplicationDto> create(@AuthenticationPrincipal User user,
                                                        @Valid @RequestBody ApiDtos.ApplicationRequest request) {
        return ResponseEntity.ok(applicationService.createApplication(user, request));
    }

    @GetMapping("/applications/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<ApiDtos.ApplicationDto>> myApplications(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(applicationService.getStudentApplications(user));
    }

    @GetMapping("/properties/{propertyId}/applications")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<List<ApiDtos.ApplicationDto>> propertyApplications(@AuthenticationPrincipal User user,
                                                                           @PathVariable Long propertyId) {
        return ResponseEntity.ok(applicationService.getPropertyApplications(user, propertyId));
    }

    @PatchMapping("/applications/{id}/status")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiDtos.ApplicationDto> updateStatus(@AuthenticationPrincipal User user,
                                                              @PathVariable Long id,
                                                              @Valid @RequestBody ApiDtos.ApplicationDecisionRequest request) {
        return ResponseEntity.ok(applicationService.updateApplicationStatus(user, id, request));
    }
}
