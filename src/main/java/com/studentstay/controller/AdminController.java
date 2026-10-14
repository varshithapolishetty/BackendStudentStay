package com.studentstay.controller;

import com.studentstay.domain.User;
import com.studentstay.dto.ApiDtos;
import com.studentstay.service.AdminService;
import com.studentstay.service.DemoBookingService;
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
    private final DemoBookingService demoBookingService;

    public AdminController(AdminService adminService,
                           com.studentstay.service.PropertyService propertyService,
                           DemoBookingService demoBookingService) {
        this.adminService = adminService;
        this.propertyService = propertyService;
        this.demoBookingService = demoBookingService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<ApiDtos.UserSummaryDto>> users() {
        return ResponseEntity.ok(adminService.getUsers());
    }

    @GetMapping("/properties")
    public ResponseEntity<List<ApiDtos.PropertySummaryDto>> properties() {
        return ResponseEntity.ok(adminService.getProperties());
    }

    @GetMapping("/complaints")
    public ResponseEntity<List<ApiDtos.ComplaintDto>> complaints() {
        return ResponseEntity.ok(adminService.getComplaints());
    }

    @GetMapping("/demo-bookings")
    public ResponseEntity<List<ApiDtos.DemoBookingDto>> demoBookings() {
        return ResponseEntity.ok(demoBookingService.getAllBookings());
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
