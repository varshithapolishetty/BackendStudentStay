package com.studentstay.controller;

import com.studentstay.domain.User;
import com.studentstay.dto.ApiDtos;
import com.studentstay.service.DemoBookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/demo-bookings")
@PreAuthorize("hasRole('STUDENT')")
public class DemoBookingController {
    private final DemoBookingService demoBookingService;

    public DemoBookingController(DemoBookingService demoBookingService) {
        this.demoBookingService = demoBookingService;
    }

    @PostMapping
    public ResponseEntity<ApiDtos.DemoBookingDto> create(
            @AuthenticationPrincipal User student,
            @Valid @RequestBody ApiDtos.DemoBookingRequest request) {
        return ResponseEntity.ok(demoBookingService.create(student, request));
    }

    @GetMapping("/my")
    public ResponseEntity<List<ApiDtos.DemoBookingDto>> myBookings(@AuthenticationPrincipal User student) {
        return ResponseEntity.ok(demoBookingService.getStudentBookings(student));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiDtos.DemoBookingDto> cancel(@AuthenticationPrincipal User student,
                                                         @PathVariable Long id) {
        return ResponseEntity.ok(demoBookingService.cancel(student, id));
    }
}
