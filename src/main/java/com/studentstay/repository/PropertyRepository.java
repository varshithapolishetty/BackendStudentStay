package com.studentstay.repository;

import com.studentstay.domain.Property;
import com.studentstay.domain.User;
import com.studentstay.domain.VerificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long>, JpaSpecificationExecutor<Property> {
    List<Property> findByOwner(User owner);
    Page<Property> findByVerificationStatusAndActive(VerificationStatus verificationStatus, boolean active, Pageable pageable);
    Page<Property> findByOwnerAndActive(User owner, boolean active, Pageable pageable);
}
