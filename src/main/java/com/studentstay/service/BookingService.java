package com.studentstay.service;

import com.studentstay.domain.*;
import com.studentstay.dto.ApiDtos;
import com.studentstay.exception.ConflictException;
import com.studentstay.exception.ResourceNotFoundException;
import com.studentstay.repository.BedRepository;
import com.studentstay.repository.BookingRepository;
import com.studentstay.repository.PropertyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BedRepository bedRepository;
    private final PropertyRepository propertyRepository;

    public BookingService(BookingRepository bookingRepository, BedRepository bedRepository, PropertyRepository propertyRepository) {
        this.bookingRepository = bookingRepository;
        this.bedRepository = bedRepository;
        this.propertyRepository = propertyRepository;
    }

    @Transactional
    public Booking createFromApplication(Application application) {
        if (application.getStatus() != ApplicationStatus.ACCEPTED) {
            throw new ConflictException("Application must be accepted before creating a booking");
        }
        Room room = application.getRoom();
        if (room == null) {
            throw new ConflictException("Application does not include a room");
        }

        Bed bed = room.getBeds().stream()
                .filter(candidate -> candidate.getStatus() == BedStatus.AVAILABLE)
                .findFirst()
                .orElseThrow(() -> new ConflictException("No beds are available for this room"));

        if (bookingRepository.existsByBedAndStatus(bed, BookingStatus.ACTIVE)) {
            throw new ConflictException("Selected bed is already allocated");
        }

        bed.setStatus(BedStatus.ALLOCATED);
        Booking booking = new Booking(
                application.getStudent(),
                application.getProperty(),
                room,
                bed,
                application,
                room.getRentPerMonth(),
                application.getProposedMoveInDate()
        );
        Booking saved = bookingRepository.save(booking);
        application.setBed(bed);
        application.setBooking(saved);
        return saved;
    }

    public List<ApiDtos.BookingDto> getStudentBookings(User student) {
        return bookingRepository.findByStudent(student).stream().map(ApiDtos.BookingDto::new).toList();
    }

    public List<ApiDtos.BookingDto> getPropertyBookings(User actingUser, Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        if (actingUser.getRole() != Role.ADMIN && !property.getOwner().getId().equals(actingUser.getId())) {
            throw new ConflictException("You cannot access bookings for this property");
        }
        return bookingRepository.findByProperty(property).stream().map(ApiDtos.BookingDto::new).toList();
    }

    @Transactional
    public ApiDtos.BookingDto cancelBooking(User actingUser, Long bookingId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (actingUser.getRole() != Role.ADMIN && !booking.getStudent().getId().equals(actingUser.getId())) {
            throw new ConflictException("You cannot cancel this booking");
        }
        if (booking.getStatus() != BookingStatus.ACTIVE) {
            throw new ConflictException("Only active bookings can be cancelled");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancellationReason(reason);
        if (booking.getBed() != null) {
            booking.getBed().setStatus(BedStatus.AVAILABLE);
        }
        return new ApiDtos.BookingDto(bookingRepository.save(booking));
    }
}
