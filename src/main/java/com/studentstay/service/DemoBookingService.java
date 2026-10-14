package com.studentstay.service;

import com.studentstay.domain.BookingStatus;
import com.studentstay.domain.DemoBooking;
import com.studentstay.domain.Role;
import com.studentstay.domain.User;
import com.studentstay.dto.ApiDtos;
import com.studentstay.exception.ConflictException;
import com.studentstay.exception.ResourceNotFoundException;
import com.studentstay.repository.DemoBookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DemoBookingService {
    private final DemoBookingRepository demoBookingRepository;

    public DemoBookingService(DemoBookingRepository demoBookingRepository) {
        this.demoBookingRepository = demoBookingRepository;
    }

    @Transactional
    public ApiDtos.DemoBookingDto create(User student, ApiDtos.DemoBookingRequest request) {
        if (student.getRole() != Role.STUDENT) {
            throw new ConflictException("Only students can create a room booking");
        }
        DemoBooking booking = new DemoBooking(
                student,
                request.getPgId(),
                request.getPgName().trim(),
                request.getLocation().trim(),
                request.getCategory().trim(),
                request.getSharing().trim(),
                request.getRent(),
                request.getStudentName().trim(),
                request.getPlace().trim(),
                request.getPhone().trim(),
                request.getMoveInDate()
        );
        return new ApiDtos.DemoBookingDto(demoBookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    public List<ApiDtos.DemoBookingDto> getStudentBookings(User student) {
        return demoBookingRepository.findByStudentOrderByCreatedAtDesc(student).stream()
                .map(ApiDtos.DemoBookingDto::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ApiDtos.DemoBookingDto> getAllBookings() {
        return demoBookingRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(ApiDtos.DemoBookingDto::new)
                .toList();
    }

    @Transactional
    public ApiDtos.DemoBookingDto cancel(User student, Long id) {
        DemoBooking booking = demoBookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (!booking.getStudent().getId().equals(student.getId())) {
            throw new ResourceNotFoundException("Booking not found");
        }
        if (booking.getStatus() != BookingStatus.ACTIVE) {
            throw new ConflictException("Only active bookings can be cancelled");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        return new ApiDtos.DemoBookingDto(demoBookingRepository.save(booking));
    }
}
