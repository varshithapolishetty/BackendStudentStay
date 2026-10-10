package com.studentstay.controller;

import com.studentstay.domain.User;
import com.studentstay.dto.ApiDtos;
import com.studentstay.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payments/mock-confirm")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<ApiDtos.PaymentDto> createMockPayment(@AuthenticationPrincipal User user,
                                                               @Valid @RequestBody ApiDtos.PaymentRequest request) {
        return ResponseEntity.ok(paymentService.createMockPayment(user, request));
    }

    @GetMapping("/payments/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<ApiDtos.PaymentDto>> myPayments(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(paymentService.getStudentPayments(user));
    }

    @GetMapping("/bookings/{bookingId}/payments")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<List<ApiDtos.PaymentDto>> bookingPayments(@AuthenticationPrincipal User user,
                                                                 @PathVariable Long bookingId) {
        return ResponseEntity.ok(paymentService.getBookingPayments(user, bookingId));
    }
}
