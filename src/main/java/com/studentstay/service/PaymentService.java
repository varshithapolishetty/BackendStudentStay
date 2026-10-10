package com.studentstay.service;

import com.studentstay.domain.*;
import com.studentstay.dto.ApiDtos;
import com.studentstay.exception.ConflictException;
import com.studentstay.exception.ResourceNotFoundException;
import com.studentstay.repository.BookingRepository;
import com.studentstay.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    public PaymentService(PaymentRepository paymentRepository, BookingRepository bookingRepository) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public ApiDtos.PaymentDto createMockPayment(User actingUser, ApiDtos.PaymentRequest request) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (actingUser.getRole() != Role.ADMIN && !booking.getStudent().getId().equals(actingUser.getId())) {
            throw new ConflictException("You cannot manage this payment");
        }
        if (request.getAmount().compareTo(booking.getMonthlyRent()) > 0) {
            throw new ConflictException("Payment amount cannot exceed the monthly rent");
        }
        Payment payment = new Payment(booking, booking.getStudent(), request.getAmount(), request.getCurrency(), request.getPurpose());
        payment.setPaymentReference("PAY-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
        payment.setStatus(PaymentStatus.CONFIRMED);
        return new ApiDtos.PaymentDto(paymentRepository.save(payment));
    }

    public List<ApiDtos.PaymentDto> getStudentPayments(User student) {
        return paymentRepository.findByStudent(student).stream().map(ApiDtos.PaymentDto::new).toList();
    }

    public List<ApiDtos.PaymentDto> getBookingPayments(User actingUser, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (actingUser.getRole() != Role.ADMIN && !booking.getStudent().getId().equals(actingUser.getId())) {
            throw new ConflictException("You cannot view these payment records");
        }
        return paymentRepository.findByBooking(booking).stream().map(ApiDtos.PaymentDto::new).toList();
    }
}
