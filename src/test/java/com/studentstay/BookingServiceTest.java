package com.studentstay;

import com.studentstay.domain.Application;
import com.studentstay.domain.ApplicationStatus;
import com.studentstay.domain.Property;
import com.studentstay.domain.Role;
import com.studentstay.domain.Room;
import com.studentstay.domain.RoomType;
import com.studentstay.domain.User;
import com.studentstay.exception.ConflictException;
import com.studentstay.repository.BedRepository;
import com.studentstay.repository.BookingRepository;
import com.studentstay.repository.PropertyRepository;
import com.studentstay.repository.RoomRepository;
import com.studentstay.repository.UserRepository;
import com.studentstay.service.BookingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock private BookingRepository bookingRepository;
    @Mock private BedRepository bedRepository;
    @Mock private PropertyRepository propertyRepository;
    @Mock private RoomRepository roomRepository;
    @Mock private UserRepository userRepository;

    @Test
    void rejectsAllocationWhenRoomHasNoAvailableBeds() {
        User student = new User("Student", "student@example.com", "student", "password", null, Role.STUDENT);
        User owner = new User("Owner", "owner@example.com", "owner", "password", null, Role.OWNER);
        Property property = new Property(owner, "Hostel", "", com.studentstay.domain.PropertyType.HOSTEL,
                "City", "Locality", "Address", Set.of(), BigDecimal.TEN, null);
        Room room = new Room(property, "Room 1", RoomType.SINGLE, 1, BigDecimal.TEN);
        Application application = new Application(student, property, room, null, LocalDate.now(), null);
        application.setStatus(ApplicationStatus.ACCEPTED);

        ReflectionTestUtils.setField(student, "id", 1L);
        ReflectionTestUtils.setField(owner, "id", 2L);
        ReflectionTestUtils.setField(property, "id", 3L);
        ReflectionTestUtils.setField(room, "id", 4L);
        when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(student));
        when(propertyRepository.findByIdForUpdate(3L)).thenReturn(Optional.of(property));
        when(roomRepository.findByIdForUpdate(4L)).thenReturn(Optional.of(room));
        when(bookingRepository.existsByStudentAndStatus(student, com.studentstay.domain.BookingStatus.ACTIVE))
                .thenReturn(false);
        when(bedRepository.findByRoomAndStatusOrderByIdAsc(room, com.studentstay.domain.BedStatus.AVAILABLE))
                .thenReturn(List.of());

        BookingService service = new BookingService(bookingRepository, bedRepository, propertyRepository, roomRepository, userRepository);
        ConflictException error = assertThrows(ConflictException.class, () -> service.createFromApplication(application));

        assertEquals("Sorry, this hostel room is fully booked. Please choose another available room.", error.getMessage());
        verify(bookingRepository, never()).save(any());
    }
}
