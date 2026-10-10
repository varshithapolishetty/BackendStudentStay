package com.studentstay.repository;

import com.studentstay.domain.Booking;
import com.studentstay.domain.Payment;
import com.studentstay.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByStudent(User student);
    List<Payment> findByBooking(Booking booking);
}
