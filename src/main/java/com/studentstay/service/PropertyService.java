package com.studentstay.service;

import com.studentstay.domain.*;
import com.studentstay.dto.ApiDtos;
import com.studentstay.exception.ConflictException;
import com.studentstay.exception.ResourceNotFoundException;
import com.studentstay.repository.PropertyRepository;
import com.studentstay.repository.RoomRepository;
import com.studentstay.repository.BedRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class PropertyService {
    private final PropertyRepository propertyRepository;
    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;

    public PropertyService(PropertyRepository propertyRepository, RoomRepository roomRepository, BedRepository bedRepository) {
        this.propertyRepository = propertyRepository;
        this.roomRepository = roomRepository;
        this.bedRepository = bedRepository;
    }

    @Transactional(readOnly = true)
    public Page<ApiDtos.PropertySummaryDto> searchProperties(String city, String locality, String type, BigDecimal minRent,
                                                            BigDecimal maxRent, String roomType, List<String> facilities,
                                                            boolean includeUnverified, User currentUser, Pageable pageable) {
        if (includeUnverified && (currentUser == null
                || (currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.OWNER))) {
            throw new ConflictException("Only owners and administrators can view unverified hostels");
        }
        Specification<Property> spec = Specification.where(PropertySpecifications.cityEquals(city))
                .and(PropertySpecifications.localityEquals(locality))
                .and(PropertySpecifications.typeEquals(type))
                .and(PropertySpecifications.priceRange(minRent, maxRent))
                .and(PropertySpecifications.roomTypeEquals(roomType))
                .and(PropertySpecifications.facilitiesMatch(facilities))
                .and(PropertySpecifications.isActive())
                .and(PropertySpecifications.verifiedOrAccessible(includeUnverified && currentUser != null && currentUser.getRole() == Role.ADMIN));
        if (includeUnverified && currentUser.getRole() == Role.OWNER) {
            spec = spec.and(PropertySpecifications.ownerEquals(currentUser));
        }
        return propertyRepository.findAll(spec, pageable).map(ApiDtos.PropertySummaryDto::new);
    }

    @Transactional(readOnly = true)
    public ApiDtos.PropertyDetailDto getPropertyDetail(Long id, User currentUser, boolean includeUnverified) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        if (currentUser != null && currentUser.getRole() == Role.ADMIN) {
            return buildDetail(property);
        }
        if (currentUser != null && currentUser.getRole() == Role.OWNER) {
            if (!property.getOwner().getId().equals(currentUser.getId())) {
                throw new ResourceNotFoundException("Property is not available for this owner");
            }
            return buildDetail(property);
        }
        if (includeUnverified || property.getVerificationStatus() != VerificationStatus.VERIFIED || !property.isActive()) {
            throw new ResourceNotFoundException("Property is not available for public view");
        }
        return buildDetail(property);
    }

    @Transactional
    public Property createProperty(User owner, ApiDtos.PropertyRequest request) {
        Property property = new Property(
                owner,
                request.getTitle().trim(),
                request.getDescription(),
                PropertyType.valueOf(request.getType().trim().toUpperCase()),
                request.getCity().trim(),
                request.getLocality().trim(),
                request.getAddress().trim(),
                request.getFacilities() == null ? Set.of() : request.getFacilities(),
                request.getRentFrom(),
                request.getImageUrl()
        );
        property.setVerificationStatus(VerificationStatus.PENDING);
        property.setActive(true);
        return propertyRepository.save(property);
    }

    @Transactional
    public Property updateProperty(User actingUser, Long propertyId, ApiDtos.PropertyRequest request) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        if (!actingUser.getRole().equals(Role.ADMIN) && !property.getOwner().getId().equals(actingUser.getId())) {
            throw new ResourceNotFoundException("You do not own this property");
        }
        property.setTitle(request.getTitle().trim());
        property.setDescription(request.getDescription());
        property.setType(PropertyType.valueOf(request.getType().trim().toUpperCase()));
        property.setCity(request.getCity().trim());
        property.setLocality(request.getLocality().trim());
        property.setAddress(request.getAddress().trim());
        property.setFacilities(request.getFacilities() == null ? Set.of() : request.getFacilities());
        property.setRentFrom(request.getRentFrom());
        property.setImageUrl(request.getImageUrl());
        return propertyRepository.save(property);
    }

    @Transactional
    public Property verifyProperty(User actingUser, Long propertyId, String status, String notes) {
        if (actingUser.getRole() != Role.ADMIN) {
            throw new ConflictException("Only admins can verify properties");
        }
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        property.setVerificationStatus(VerificationStatus.valueOf(status.trim().toUpperCase()));
        property.setVerificationNotes(notes);
        property.setActive(property.getVerificationStatus() == VerificationStatus.VERIFIED);
        return propertyRepository.save(property);
    }

    @Transactional
    public Room createRoom(User actingUser, Long propertyId, ApiDtos.RoomRequest request) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        if (actingUser.getRole() != Role.ADMIN && !property.getOwner().getId().equals(actingUser.getId())) {
            throw new ConflictException("You cannot manage this property");
        }
        if (request.getCapacity() <= 0) {
            throw new IllegalArgumentException("Room capacity must be positive");
        }
        Room room = new Room(property, request.getName().trim(), RoomType.valueOf(request.getRoomType().trim().toUpperCase()), request.getCapacity(), request.getRentPerMonth());
        Room saved = roomRepository.save(room);
        for (int idx = 1; idx <= request.getCapacity(); idx++) {
            saved.getBeds().add(new Bed(saved, "B" + idx));
        }
        return roomRepository.save(saved);
    }

    @Transactional
    public Bed createBed(User actingUser, Long roomId, ApiDtos.BedRequest request) {
        Room existingRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        Property property = propertyRepository.findByIdForUpdate(existingRoom.getProperty().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        Room room = roomRepository.findByIdForUpdate(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        if (actingUser.getRole() != Role.ADMIN && !property.getOwner().getId().equals(actingUser.getId())) {
            throw new ConflictException("You cannot manage this property");
        }
        if (room.getBeds().size() >= room.getCapacity()) {
            throw new ConflictException("Room is already at full bed capacity");
        }
        Bed bed = new Bed(room, request.getBedNumber());
        room.getBeds().add(bed);
        roomRepository.save(room);
        return bed;
    }

    @Transactional
    public Room updateRoom(User actingUser, Long roomId, ApiDtos.RoomRequest request) {
        Room existingRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        Property property = propertyRepository.findByIdForUpdate(existingRoom.getProperty().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        Room room = roomRepository.findByIdForUpdate(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        if (actingUser.getRole() != Role.ADMIN && !property.getOwner().getId().equals(actingUser.getId())) {
            throw new ConflictException("You cannot manage this property");
        }
        if (request.getCapacity() < room.getBeds().size()) {
            throw new ConflictException("Room capacity cannot be less than its existing bed records");
        }
        room.setName(request.getName().trim());
        room.setRoomType(RoomType.valueOf(request.getRoomType().trim().toUpperCase()));
        room.setRentPerMonth(request.getRentPerMonth());
        for (int idx = room.getBeds().size() + 1; idx <= request.getCapacity(); idx++) {
            room.getBeds().add(new Bed(room, "B" + idx));
        }
        room.setCapacity(request.getCapacity());
        return roomRepository.save(room);
    }

    @Transactional
    public Bed updateBedAvailability(User actingUser, Long bedId, String requestedStatus) {
        Bed existingBed = bedRepository.findById(bedId)
                .orElseThrow(() -> new ResourceNotFoundException("Bed not found"));
        Room existingRoom = roomRepository.findById(existingBed.getRoom().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        Property property = propertyRepository.findByIdForUpdate(existingRoom.getProperty().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        Room room = roomRepository.findByIdForUpdate(existingRoom.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        Bed bed = bedRepository.findById(bedId)
                .orElseThrow(() -> new ResourceNotFoundException("Bed not found"));
        if (actingUser.getRole() != Role.ADMIN && !property.getOwner().getId().equals(actingUser.getId())) {
            throw new ConflictException("You cannot manage this property");
        }
        BedStatus status = BedStatus.valueOf(requestedStatus.trim().toUpperCase());
        if (status == BedStatus.ALLOCATED || bed.getStatus() == BedStatus.ALLOCATED) {
            throw new ConflictException("Allocated beds cannot be manually changed");
        }
        bed.setStatus(status);
        return bedRepository.save(bed);
    }

    @Transactional(readOnly = true)
    public List<ApiDtos.PropertySummaryDto> getOwnerProperties(User owner) {
        if (owner.getRole() == Role.ADMIN) {
            return propertyRepository.findAll().stream().map(ApiDtos.PropertySummaryDto::new).toList();
        }
        return propertyRepository.findByOwner(owner).stream().map(ApiDtos.PropertySummaryDto::new).toList();
    }

    private ApiDtos.PropertyDetailDto buildDetail(Property property) {
        List<ApiDtos.RoomSummaryDto> roomDtos = new ArrayList<>();
        for (Room room : property.getRooms()) {
            long totalBeds = room.getBeds().size();
            long availableBeds = room.getBeds().stream().filter(b -> b.getStatus() == BedStatus.AVAILABLE).count();
            roomDtos.add(new ApiDtos.RoomSummaryDto(room, availableBeds, totalBeds));
        }
        return new ApiDtos.PropertyDetailDto(property, roomDtos);
    }
}

class PropertySpecifications {
    public static Specification<Property> cityEquals(String city) {
        return (root, query, cb) -> city == null || city.isBlank() ? cb.conjunction() : cb.equal(cb.lower(root.get("city")), city.trim().toLowerCase());
    }

    public static Specification<Property> localityEquals(String locality) {
        return (root, query, cb) -> locality == null || locality.isBlank() ? cb.conjunction() : cb.equal(cb.lower(root.get("locality")), locality.trim().toLowerCase());
    }

    public static Specification<Property> typeEquals(String type) {
        return (root, query, cb) -> type == null || type.isBlank() ? cb.conjunction() : cb.equal(root.get("type"), PropertyType.valueOf(type.trim().toUpperCase()));
    }

    public static Specification<Property> priceRange(BigDecimal minRent, BigDecimal maxRent) {
        return (root, query, cb) -> {
            if (minRent == null && maxRent == null) return cb.conjunction();
            if (minRent != null && maxRent != null) return cb.between(root.get("rentFrom"), minRent, maxRent);
            if (minRent != null) return cb.greaterThanOrEqualTo(root.get("rentFrom"), minRent);
            return cb.lessThanOrEqualTo(root.get("rentFrom"), maxRent);
        };
    }

    public static Specification<Property> roomTypeEquals(String roomType) {
        return (root, query, cb) -> {
            if (roomType == null || roomType.isBlank()) return cb.conjunction();
            query.distinct(true);
            return cb.equal(root.join("rooms").get("roomType"), RoomType.valueOf(roomType.trim().toUpperCase()));
        };
    }

    public static Specification<Property> facilitiesMatch(List<String> facilities) {
        return (root, query, cb) -> {
            if (facilities == null || facilities.isEmpty()) return cb.conjunction();
            query.distinct(true);
            var facilityJoin = root.join("facilities", jakarta.persistence.criteria.JoinType.INNER);
            return facilityJoin.in(facilities);
        };
    }

    public static Specification<Property> isActive() {
        return (root, query, cb) -> cb.isTrue(root.get("active"));
    }

    public static Specification<Property> verifiedOrAccessible(boolean includeUnverified) {
        return (root, query, cb) -> includeUnverified ? cb.conjunction() : cb.equal(root.get("verificationStatus"), VerificationStatus.VERIFIED);
    }

    public static Specification<Property> ownerEquals(User owner) {
        return (root, query, cb) -> cb.equal(root.get("owner"), owner);
    }
}
