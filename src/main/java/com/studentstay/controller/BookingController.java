package com.studentstay.controller;

import com.studentstay.domain.User;
import com.studentstay.dto.ApiDtos;
import com.studentstay.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/bookings/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<ApiDtos.BookingDto>> myBookings(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(bookingService.getStudentBookings(user));
    }

    @GetMapping("/properties/{propertyId}/bookings")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<List<ApiDtos.BookingDto>> propertyBookings(@AuthenticationPrincipal User user,
                                                                    @PathVariable Long propertyId) {
        return ResponseEntity.ok(bookingService.getPropertyBookings(user, propertyId));
    }

    @PatchMapping("/bookings/{id}/cancel")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<ApiDtos.BookingDto> cancel(@AuthenticationPrincipal User user,
                                                    @PathVariable Long id,
                                                    @Valid @RequestBody ApiDtos.CancelBookingRequest request) {
        return ResponseEntity.ok(bookingService.cancelBooking(user, id, request.getReason()));
    }
}
