package com.studentstay.service;

import com.studentstay.domain.*;
import com.studentstay.dto.ApiDtos;
import com.studentstay.exception.ConflictException;
import com.studentstay.exception.ResourceNotFoundException;
import com.studentstay.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AdminService {
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final BookingRepository bookingRepository;
    private final ComplaintRepository complaintRepository;
    private final ApplicationRepository applicationRepository;
    private final PaymentRepository paymentRepository;
    private final BedRepository bedRepository;

    public AdminService(UserRepository userRepository,
                        PropertyRepository propertyRepository,
                        BookingRepository bookingRepository,
                        ComplaintRepository complaintRepository,
                        ApplicationRepository applicationRepository,
                        PaymentRepository paymentRepository,
                        BedRepository bedRepository) {
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
        this.bookingRepository = bookingRepository;
        this.complaintRepository = complaintRepository;
        this.applicationRepository = applicationRepository;
        this.paymentRepository = paymentRepository;
        this.bedRepository = bedRepository;
    }

    public List<ApiDtos.UserSummaryDto> getUsers() {
        return userRepository.findAll().stream().map(ApiDtos.UserSummaryDto::new).toList();
    }

    @Transactional(readOnly = true)
    public List<ApiDtos.PropertySummaryDto> getProperties() {
        return propertyRepository.findAll().stream().map(ApiDtos.PropertySummaryDto::new).toList();
    }

    @Transactional(readOnly = true)
    public List<ApiDtos.ComplaintDto> getComplaints() {
        return complaintRepository.findAll().stream().map(ApiDtos.ComplaintDto::new).toList();
    }

    @Transactional
    public ApiDtos.UserSummaryDto updateUserRole(Long userId, String role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setRole(Role.valueOf(role.trim().toUpperCase()));
        return new ApiDtos.UserSummaryDto(userRepository.save(user));
    }

    public ApiDtos.AdminReportDto getReport() {
        long totalUsers = userRepository.count();
        long totalProperties = propertyRepository.count();
        long verifiedProperties = propertyRepository.findAll().stream().filter(p -> p.getVerificationStatus() == VerificationStatus.VERIFIED).count();
        long pendingProperties = propertyRepository.findAll().stream().filter(p -> p.getVerificationStatus() == VerificationStatus.PENDING).count();
        long activeBookings = bookingRepository.findByStatus(BookingStatus.ACTIVE).size();
        long openComplaints = complaintRepository.findAll().stream().filter(c -> c.getStatus() == ComplaintStatus.OPEN || c.getStatus() == ComplaintStatus.IN_PROGRESS).count();
        BigDecimal revenue = paymentRepository.findAll().stream()
                .filter(p -> p.getStatus() == PaymentStatus.CONFIRMED)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long totalApplications = applicationRepository.count();
        return new ApiDtos.AdminReportDto(
                totalUsers,
                totalProperties,
                verifiedProperties,
                pendingProperties,
                activeBookings,
                openComplaints,
                revenue,
                totalApplications,
                bedRepository.countByStatus(BedStatus.ALLOCATED),
                bedRepository.countByStatus(BedStatus.AVAILABLE)
        );
    }

    @Transactional(readOnly = true)
    public List<ApiDtos.PropertySummaryDto> getPendingProperties() {
        return propertyRepository.findAll().stream()
                .filter(p -> p.getVerificationStatus() == VerificationStatus.PENDING)
                .map(ApiDtos.PropertySummaryDto::new)
                .toList();
    }
}
