package com.studentstay.repository;

import com.studentstay.domain.Application;
import com.studentstay.domain.ApplicationStatus;
import com.studentstay.domain.Property;
import com.studentstay.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByStudent(User student);
    List<Application> findByProperty(Property property);
    List<Application> findByPropertyAndStatus(Property property, ApplicationStatus status);
    List<Application> findByStudentAndStatus(User student, ApplicationStatus status);
}
