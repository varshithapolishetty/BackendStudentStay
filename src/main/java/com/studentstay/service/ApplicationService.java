package com.studentstay.service;

import com.studentstay.domain.*;
import com.studentstay.dto.ApiDtos;
import com.studentstay.exception.ConflictException;
import com.studentstay.exception.ResourceNotFoundException;
import com.studentstay.repository.ApplicationRepository;
import com.studentstay.repository.PropertyRepository;
import com.studentstay.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final PropertyRepository propertyRepository;
    private final RoomRepository roomRepository;
    private final BookingService bookingService;

    public ApplicationService(ApplicationRepository applicationRepository,
                             PropertyRepository propertyRepository,
                             RoomRepository roomRepository,
                             BookingService bookingService) {
        this.applicationRepository = applicationRepository;
        this.propertyRepository = propertyRepository;
        this.roomRepository = roomRepository;
        this.bookingService = bookingService;
    }

    @Transactional
    public ApiDtos.ApplicationDto createApplication(User student, ApiDtos.ApplicationRequest request) {
        if (student.getRole() != Role.STUDENT) {
            throw new ConflictException("Only students can submit applications");
        }
        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        if (property.getVerificationStatus() != VerificationStatus.VERIFIED || !property.isActive()) {
            throw new ConflictException("This property is not currently open for applications");
        }
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        if (!room.getProperty().getId().equals(property.getId())) {
            throw new ConflictException("Room does not belong to the selected property");
        }

        Application application = new Application(student, property, room, null, request.getProposedMoveInDate(), request.getNote());
        return new ApiDtos.ApplicationDto(applicationRepository.save(application));
    }

    public List<ApiDtos.ApplicationDto> getStudentApplications(User student) {
        return applicationRepository.findByStudent(student).stream().map(ApiDtos.ApplicationDto::new).toList();
    }

    public List<ApiDtos.ApplicationDto> getPropertyApplications(User actingUser, Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        if (actingUser.getRole() != Role.ADMIN && !property.getOwner().getId().equals(actingUser.getId())) {
            throw new ConflictException("You cannot view applications for this property");
        }
        return applicationRepository.findByProperty(property).stream().map(ApiDtos.ApplicationDto::new).toList();
    }

    @Transactional
    public ApiDtos.ApplicationDto updateApplicationStatus(User actingUser, Long applicationId, ApiDtos.ApplicationDecisionRequest request) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        if (actingUser.getRole() != Role.ADMIN && !application.getProperty().getOwner().getId().equals(actingUser.getId())) {
            throw new ConflictException("You cannot manage this application");
        }

        ApplicationStatus status = ApplicationStatus.valueOf(request.getStatus().trim().toUpperCase());
        application.setStatus(status);
        application.setOwnerDecisionReason(request.getReason());

        if (status == ApplicationStatus.ACCEPTED) {
            bookingService.createFromApplication(application);
        }

        return new ApiDtos.ApplicationDto(applicationRepository.save(application));
    }
}
