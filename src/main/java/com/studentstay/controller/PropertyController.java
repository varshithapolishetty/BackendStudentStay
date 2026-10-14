package com.studentstay.controller;

import com.studentstay.domain.Bed;
import com.studentstay.domain.Role;
import com.studentstay.domain.Room;
import com.studentstay.domain.User;
import com.studentstay.dto.ApiDtos;
import com.studentstay.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class PropertyController {
    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @GetMapping("/properties")
    public ResponseEntity<Page<ApiDtos.PropertySummaryDto>> list(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String locality,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) BigDecimal minRent,
            @RequestParam(required = false) BigDecimal maxRent,
            @RequestParam(required = false) String roomType,
            @RequestParam(required = false) List<String> facilities,
            @RequestParam(defaultValue = "false") boolean includeUnverified,
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        return ResponseEntity.ok(propertyService.searchProperties(city, locality, type, minRent, maxRent, roomType, facilities, includeUnverified, user, pageable));
    }

    @GetMapping("/properties/{id}")
    public ResponseEntity<ApiDtos.PropertyDetailDto> get(@PathVariable Long id,
                                                      @AuthenticationPrincipal User user,
                                                      @RequestParam(defaultValue = "false") boolean includeUnverified) {
        return ResponseEntity.ok(propertyService.getPropertyDetail(id, user, includeUnverified));
    }

    @GetMapping("/properties/owner")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<List<ApiDtos.PropertySummaryDto>> getOwnerProperties(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(propertyService.getOwnerProperties(user));
    }

    @PostMapping("/properties")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiDtos.PropertyDetailDto> create(@AuthenticationPrincipal User user,
                                                          @Valid @RequestBody ApiDtos.PropertyRequest request) {
        var property = propertyService.createProperty(user, request);
        return ResponseEntity.ok(propertyService.getPropertyDetail(property.getId(), user, true));
    }

    @PutMapping("/properties/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiDtos.PropertyDetailDto> update(@AuthenticationPrincipal User user,
                                                          @PathVariable Long id,
                                                          @Valid @RequestBody ApiDtos.PropertyRequest request) {
        var property = propertyService.updateProperty(user, id, request);
        return ResponseEntity.ok(propertyService.getPropertyDetail(property.getId(), user, true));
    }

    @PatchMapping("/properties/{id}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiDtos.PropertyDetailDto> verify(@AuthenticationPrincipal User user,
                                                           @PathVariable Long id,
                                                           @RequestParam String status,
                                                           @RequestParam(required = false) String notes) {
        var property = propertyService.verifyProperty(user, id, status, notes == null ? "" : notes);
        return ResponseEntity.ok(propertyService.getPropertyDetail(property.getId(), user, true));
    }

    @PostMapping("/properties/{id}/rooms")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiDtos.RoomSummaryDto> createRoom(@AuthenticationPrincipal User user,
                                                            @PathVariable Long id,
                                                            @Valid @RequestBody ApiDtos.RoomRequest request) {
        Room room = propertyService.createRoom(user, id, request);
        return ResponseEntity.ok(new ApiDtos.RoomSummaryDto(room, room.getBeds().stream().filter(b -> b.getStatus() == com.studentstay.domain.BedStatus.AVAILABLE).count(), room.getBeds().size()));
    }

    @PutMapping("/rooms/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiDtos.RoomSummaryDto> updateRoom(@AuthenticationPrincipal User user,
                                                            @PathVariable Long id,
                                                            @Valid @RequestBody ApiDtos.RoomRequest request) {
        Room room = propertyService.updateRoom(user, id, request);
        return ResponseEntity.ok(new ApiDtos.RoomSummaryDto(room,
                room.getBeds().stream().filter(bed -> bed.getStatus() == com.studentstay.domain.BedStatus.AVAILABLE).count(),
                room.getBeds().size()));
    }

    @PostMapping("/rooms/{id}/beds")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<Object> createBed(@AuthenticationPrincipal User user,
                                           @PathVariable Long id,
                                           @Valid @RequestBody ApiDtos.BedRequest request) {
        Bed bed = propertyService.createBed(user, id, request);
        return ResponseEntity.ok(bed);
    }

    @PatchMapping("/beds/{id}/availability")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<Object> updateBedAvailability(@AuthenticationPrincipal User user,
                                                       @PathVariable Long id,
                                                       @Valid @RequestBody ApiDtos.BedAvailabilityRequest request) {
        return ResponseEntity.ok(propertyService.updateBedAvailability(user, id, request.getStatus()));
    }
}
