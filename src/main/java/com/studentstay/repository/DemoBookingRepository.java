package com.studentstay.repository;

import com.studentstay.domain.DemoBooking;
import com.studentstay.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DemoBookingRepository extends JpaRepository<DemoBooking, Long> {
    List<DemoBooking> findByStudentOrderByCreatedAtDesc(User student);
    List<DemoBooking> findAllByOrderByCreatedAtDesc();
}
