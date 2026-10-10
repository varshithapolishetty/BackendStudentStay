package com.studentstay.repository;

import com.studentstay.domain.Complaint;
import com.studentstay.domain.Property;
import com.studentstay.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByStudent(User student);
    List<Complaint> findByProperty(Property property);
}
