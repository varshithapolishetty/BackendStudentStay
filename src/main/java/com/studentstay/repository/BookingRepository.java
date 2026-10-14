package com.studentstay.repository;

import com.studentstay.domain.Booking;
import com.studentstay.domain.BookingStatus;
import com.studentstay.domain.Bed;
import com.studentstay.domain.Property;
import com.studentstay.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByStudent(User student);
    List<Booking> findByProperty(Property property);
    Optional<Booking> findByBed(Bed bed);
    List<Booking> findByStatus(BookingStatus status);
    boolean existsByBedAndStatus(Bed bed, BookingStatus status);
    boolean existsByStudentAndStatus(User student, BookingStatus status);
}
