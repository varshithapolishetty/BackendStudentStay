package com.studentstay.service;

import com.studentstay.domain.*;
import com.studentstay.dto.ApiDtos;
import com.studentstay.exception.ConflictException;
import com.studentstay.exception.ResourceNotFoundException;
import com.studentstay.repository.BedRepository;
import com.studentstay.repository.BookingRepository;
import com.studentstay.repository.PropertyRepository;
import com.studentstay.repository.RoomRepository;
import com.studentstay.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BedRepository bedRepository;
    private final PropertyRepository propertyRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository, BedRepository bedRepository,
                          PropertyRepository propertyRepository, RoomRepository roomRepository,
                          UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.bedRepository = bedRepository;
        this.propertyRepository = propertyRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Booking createFromApplication(Application application) {
        if (application.getStatus() != ApplicationStatus.ACCEPTED) {
            throw new ConflictException("Application must be accepted before creating a booking");
        }
        if (application.getRoom() == null) {
            throw new ConflictException("Application does not include a room");
        }

        User student = userRepository.findByIdForUpdate(application.getStudent().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        Property property = propertyRepository.findByIdForUpdate(application.getProperty().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        Room room = roomRepository.findByIdForUpdate(application.getRoom().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        if (!room.getProperty().getId().equals(property.getId())) {
            throw new ConflictException("Room does not belong to the selected property");
        }
        if (bookingRepository.existsByStudentAndStatus(student, BookingStatus.ACTIVE)) {
            throw new ConflictException("You already have an active room allocation");
        }

        Bed bed = bedRepository.findByRoomAndStatusOrderByIdAsc(room, BedStatus.AVAILABLE).stream()
                .findFirst()
                .orElseThrow(() -> new ConflictException(
                        "Sorry, this hostel room is fully booked. Please choose another available room."));

        bed.setStatus(BedStatus.ALLOCATED);
        Booking booking = new Booking(
                student,
                property,
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

    @Transactional(readOnly = true)
    public List<ApiDtos.BookingDto> getStudentBookings(User student) {
        return bookingRepository.findByStudent(student).stream().map(ApiDtos.BookingDto::new).toList();
    }

    @Transactional(readOnly = true)
    public List<ApiDtos.BookingDto> getPropertyBookings(User actingUser, Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        if (actingUser.getRole() != Role.ADMIN && !property.getOwner().getId().equals(actingUser.getId())) {
            throw new ConflictException("You cannot access bookings for this property");
        }
        return bookingRepository.findByProperty(property).stream().map(ApiDtos.BookingDto::new).toList();
    }

    @Transactional
    public ApiDtos.BookingDto recordAttendance(User actingUser, Long bookingId, String status) {
        Booking booking = findOwnedBooking(actingUser, bookingId);
        if (booking.getStatus() != BookingStatus.ACTIVE) {
            throw new ConflictException("Attendance can only be recorded for an active booking");
        }
        booking.setAttendanceStatus(AttendanceStatus.valueOf(status.trim().toUpperCase(Locale.ROOT)));
        return new ApiDtos.BookingDto(bookingRepository.save(booking));
    }

    @Transactional
    public ApiDtos.BookingDto checkIn(User actingUser, Long bookingId) {
        Booking booking = findOwnedBooking(actingUser, bookingId);
        if (booking.getStatus() != BookingStatus.ACTIVE || booking.getCheckedInAt() != null) {
            throw new ConflictException("This booking cannot be checked in");
        }
        booking.setCheckedInAt(LocalDateTime.now());
        return new ApiDtos.BookingDto(bookingRepository.save(booking));
    }

    @Transactional
    public ApiDtos.BookingDto checkOut(User actingUser, Long bookingId) {
        Booking booking = findOwnedBooking(actingUser, bookingId);
        if (booking.getStatus() != BookingStatus.ACTIVE || booking.getCheckedInAt() == null) {
            throw new ConflictException("Check-in is required before check-out");
        }
        booking.setCheckedOutAt(LocalDateTime.now());
        booking.setStatus(BookingStatus.COMPLETED);
        if (booking.getBed() != null) {
            booking.getBed().setStatus(BedStatus.AVAILABLE);
            booking.setBed(null);
        }
        return new ApiDtos.BookingDto(bookingRepository.save(booking));
    }

    private Booking findOwnedBooking(User actingUser, Long bookingId) {
        Booking existing = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        userRepository.findByIdForUpdate(existing.getStudent().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        Property property = propertyRepository.findByIdForUpdate(existing.getProperty().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        if (actingUser.getRole() != Role.ADMIN && !property.getOwner().getId().equals(actingUser.getId())) {
            throw new ConflictException("You cannot manage this booking");
        }
        if (existing.getRoom() != null) {
            roomRepository.findByIdForUpdate(existing.getRoom().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        }
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
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
        userRepository.findByIdForUpdate(booking.getStudent().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        propertyRepository.findByIdForUpdate(booking.getProperty().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        if (booking.getRoom() != null) {
            roomRepository.findByIdForUpdate(booking.getRoom().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        }
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancellationReason(reason);
        if (booking.getBed() != null) {
            booking.getBed().setStatus(BedStatus.AVAILABLE);
            booking.setBed(null);
        }
        return new ApiDtos.BookingDto(bookingRepository.save(booking));
    }
}
