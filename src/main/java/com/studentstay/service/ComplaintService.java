package com.studentstay.service;

import com.studentstay.domain.Complaint;
import com.studentstay.domain.ComplaintStatus;
import com.studentstay.domain.Property;
import com.studentstay.domain.Role;
import com.studentstay.domain.User;
import com.studentstay.dto.ApiDtos;
import com.studentstay.exception.ConflictException;
import com.studentstay.exception.ResourceNotFoundException;
import com.studentstay.repository.ComplaintRepository;
import com.studentstay.repository.PropertyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ComplaintService {
    private final ComplaintRepository complaintRepository;
    private final PropertyRepository propertyRepository;

    public ComplaintService(ComplaintRepository complaintRepository, PropertyRepository propertyRepository) {
        this.complaintRepository = complaintRepository;
        this.propertyRepository = propertyRepository;
    }

    @Transactional
    public ApiDtos.ComplaintDto createComplaint(User student, ApiDtos.ComplaintRequest request) {
        if (student.getRole() != Role.STUDENT) {
            throw new ConflictException("Only students can create complaints");
        }
        Property property = null;
        if (request.getPropertyId() != null) {
            property = propertyRepository.findById(request.getPropertyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        }
        Complaint complaint = new Complaint(student, property, request.getSubject().trim(), request.getDescription().trim());
        return new ApiDtos.ComplaintDto(complaintRepository.save(complaint));
    }

    public List<ApiDtos.ComplaintDto> getMyComplaints(User student) {
        return complaintRepository.findByStudent(student).stream().map(ApiDtos.ComplaintDto::new).toList();
    }

    public List<ApiDtos.ComplaintDto> getPropertyComplaints(User actingUser, Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        if (actingUser.getRole() != Role.ADMIN && !property.getOwner().getId().equals(actingUser.getId())) {
            throw new ConflictException("You cannot access complaints for this property");
        }
        return complaintRepository.findByProperty(property).stream().map(ApiDtos.ComplaintDto::new).toList();
    }

    @Transactional
    public ApiDtos.ComplaintDto updateComplaintStatus(User actingUser, Long complaintId, ApiDtos.ComplaintStatusUpdateRequest request) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found"));
        if (actingUser.getRole() != Role.ADMIN && (complaint.getProperty() == null || !complaint.getProperty().getOwner().getId().equals(actingUser.getId()))) {
            throw new ConflictException("You cannot update this complaint");
        }
        ComplaintStatus status = ComplaintStatus.valueOf(request.getStatus().trim().toUpperCase());
        complaint.setStatus(status);
        complaint.setOwnerResponse(request.getResponse());
        return new ApiDtos.ComplaintDto(complaintRepository.save(complaint));
    }
}
