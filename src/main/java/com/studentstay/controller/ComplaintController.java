package com.studentstay.controller;

import com.studentstay.domain.User;
import com.studentstay.dto.ApiDtos;
import com.studentstay.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ComplaintController {
    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @PostMapping("/complaints")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiDtos.ComplaintDto> create(@AuthenticationPrincipal User user,
                                                      @Valid @RequestBody ApiDtos.ComplaintRequest request) {
        return ResponseEntity.ok(complaintService.createComplaint(user, request));
    }

    @GetMapping("/complaints/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<ApiDtos.ComplaintDto>> myComplaints(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(complaintService.getMyComplaints(user));
    }

    @GetMapping("/properties/{propertyId}/complaints")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<List<ApiDtos.ComplaintDto>> propertyComplaints(@AuthenticationPrincipal User user,
                                                                       @PathVariable Long propertyId) {
        return ResponseEntity.ok(complaintService.getPropertyComplaints(user, propertyId));
    }

    @PatchMapping("/complaints/{id}/status")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiDtos.ComplaintDto> updateStatus(@AuthenticationPrincipal User user,
                                                            @PathVariable Long id,
                                                            @Valid @RequestBody ApiDtos.ComplaintStatusUpdateRequest request) {
        return ResponseEntity.ok(complaintService.updateComplaintStatus(user, id, request));
    }
}
