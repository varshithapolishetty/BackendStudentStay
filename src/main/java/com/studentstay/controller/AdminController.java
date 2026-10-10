package com.studentstay.controller;

import com.studentstay.domain.User;
import com.studentstay.dto.ApiDtos;
import com.studentstay.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AdminService adminService;
    private final com.studentstay.service.PropertyService propertyService;

    public AdminController(AdminService adminService, com.studentstay.service.PropertyService propertyService) {
        this.adminService = adminService;
        this.propertyService = propertyService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<ApiDtos.UserSummaryDto>> users() {
        return ResponseEntity.ok(adminService.getUsers());
    }

    @PatchMapping("/users/{id}/role")
    public ResponseEntity<ApiDtos.UserSummaryDto> updateUserRole(@PathVariable Long id,
                                                                @RequestParam String role) {
        return ResponseEntity.ok(adminService.updateUserRole(id, role));
    }

    @GetMapping("/reports/summary")
    public ResponseEntity<ApiDtos.AdminReportDto> summary() {
        return ResponseEntity.ok(adminService.getReport());
    }

    @GetMapping("/properties/pending")
    public ResponseEntity<List<ApiDtos.PropertySummaryDto>> pendingProperties() {
        return ResponseEntity.ok(adminService.getPendingProperties());
    }

    @PatchMapping("/properties/{id}/verify")
    public ResponseEntity<ApiDtos.PropertyDetailDto> verifyProperty(@AuthenticationPrincipal User user,
                                                                  @PathVariable Long id,
                                                                  @RequestParam String status,
                                                                  @RequestParam(required = false) String notes) {
        var property = propertyService.verifyProperty(user, id, status, notes == null ? "" : notes);
        return ResponseEntity.ok(propertyService.getPropertyDetail(property.getId(), user, true));
    }
}
