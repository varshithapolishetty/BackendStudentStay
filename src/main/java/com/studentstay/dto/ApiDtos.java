package com.studentstay.dto;

import com.studentstay.domain.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class ApiDtos {

    public static class RegisterRequest {
        @NotBlank
        private String fullName;
        @NotBlank
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9]*[@#$_.-][A-Za-z0-9]*$")
        private String username;
        @NotBlank @Email
        private String email;
        @NotBlank
        private String password;
        @Pattern(regexp = "^(?:[6-9]\\d{9})?$")
        private String phone;
        private String role = "STUDENT";

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    public static class LoginRequest {
        @Email
        private String email;
        private String username;
        @NotBlank
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class AuthResponse {
        private String token;
        private String email;
        private String username;
        private String fullName;
        private String role;

        public AuthResponse() {}

        public AuthResponse(String token, String email, String username, String fullName, String role) {
            this.token = token;
            this.email = email;
            this.username = username;
            this.fullName = fullName;
            this.role = role;
        }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    public static class DemoBookingRequest {
        @NotNull
        private Long pgId;
        @NotBlank @Size(max = 160)
        private String pgName;
        @NotBlank @Size(max = 160)
        private String location;
        @NotBlank @Size(max = 40)
        private String category;
        @NotBlank @Size(max = 40)
        private String sharing;
        @NotNull @DecimalMin("0.00")
        private BigDecimal rent;
        @NotBlank @Size(max = 120)
        private String studentName;
        @NotBlank @Size(max = 120)
        private String place;
        @NotBlank @Pattern(regexp = "^[6-9]\\d{9}$")
        private String phone;
        @NotNull
        private LocalDate moveInDate;

        public Long getPgId() { return pgId; }
        public void setPgId(Long pgId) { this.pgId = pgId; }
        public String getPgName() { return pgName; }
        public void setPgName(String pgName) { this.pgName = pgName; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getSharing() { return sharing; }
        public void setSharing(String sharing) { this.sharing = sharing; }
        public BigDecimal getRent() { return rent; }
        public void setRent(BigDecimal rent) { this.rent = rent; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        public String getPlace() { return place; }
        public void setPlace(String place) { this.place = place; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public LocalDate getMoveInDate() { return moveInDate; }
        public void setMoveInDate(LocalDate moveInDate) { this.moveInDate = moveInDate; }
    }

    public static class DemoBookingDto {
        private Long id;
        private Long studentId;
        private String studentName;
        private String studentEmail;
        private String studentPhone;
        private String place;
        private Long pgId;
        private String pgName;
        private String location;
        private String category;
        private String sharing;
        private BigDecimal rent;
        private LocalDate moveInDate;
        private String status;

        public DemoBookingDto() {}
        public DemoBookingDto(com.studentstay.domain.DemoBooking booking) {
            this.id = booking.getId();
            this.studentId = booking.getStudent().getId();
            this.studentName = booking.getStudentName();
            this.studentEmail = booking.getStudent().getEmail();
            this.studentPhone = booking.getPhone();
            this.place = booking.getPlace();
            this.pgId = booking.getPgId();
            this.pgName = booking.getPgName();
            this.location = booking.getLocation();
            this.category = booking.getCategory();
            this.sharing = booking.getSharing();
            this.rent = booking.getRent();
            this.moveInDate = booking.getMoveInDate();
            this.status = booking.getStatus().name();
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        public String getStudentEmail() { return studentEmail; }
        public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }
        public String getStudentPhone() { return studentPhone; }
        public void setStudentPhone(String studentPhone) { this.studentPhone = studentPhone; }
        public String getPlace() { return place; }
        public void setPlace(String place) { this.place = place; }
        public Long getPgId() { return pgId; }
        public void setPgId(Long pgId) { this.pgId = pgId; }
        public String getPgName() { return pgName; }
        public void setPgName(String pgName) { this.pgName = pgName; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getSharing() { return sharing; }
        public void setSharing(String sharing) { this.sharing = sharing; }
        public BigDecimal getRent() { return rent; }
        public void setRent(BigDecimal rent) { this.rent = rent; }
        public LocalDate getMoveInDate() { return moveInDate; }
        public void setMoveInDate(LocalDate moveInDate) { this.moveInDate = moveInDate; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class UserSummaryDto {
        private Long id; private String email; private String username; private String fullName; private String role; private String phone;
        public UserSummaryDto() {}
        public UserSummaryDto(User user) {
            this.id = user.getId(); this.email = user.getEmail(); this.username = user.getLoginName(); this.fullName = user.getFullName(); this.role = user.getRole().name(); this.phone = user.getPhone();
        }
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }

    public static class PropertyRequest {
        @NotBlank
        private String title;
        @NotBlank
        private String description;
        @NotBlank
        private String type;
        @NotBlank
        private String city;
        @NotBlank
        private String locality;
        @NotBlank
        private String address;
        private Set<String> facilities = Set.of();
        @NotNull @DecimalMin("0.00")
        private BigDecimal rentFrom;
        private String imageUrl;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getLocality() { return locality; }
        public void setLocality(String locality) { this.locality = locality; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public Set<String> getFacilities() { return facilities; }
        public void setFacilities(Set<String> facilities) { this.facilities = facilities; }
        public BigDecimal getRentFrom() { return rentFrom; }
        public void setRentFrom(BigDecimal rentFrom) { this.rentFrom = rentFrom; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }

    public static class PropertySummaryDto {
        private Long id; private String title; private String city; private String locality; private String type; private String status; private boolean active; private BigDecimal rentFrom; private String imageUrl; private Set<String> facilities; private long totalBeds; private long availableBeds;
        public PropertySummaryDto() {}
        public PropertySummaryDto(Property property) {
            this.id = property.getId(); this.title = property.getTitle(); this.city = property.getCity(); this.locality = property.getLocality(); this.type = property.getType().name(); this.status = property.getVerificationStatus().name(); this.active = property.isActive(); this.rentFrom = property.getRentFrom(); this.imageUrl = property.getImageUrl(); this.facilities = property.getFacilities();
            this.totalBeds = property.getRooms().stream().flatMap(room -> room.getBeds().stream()).count();
            this.availableBeds = property.getRooms().stream().flatMap(room -> room.getBeds().stream()).filter(bed -> bed.getStatus() == BedStatus.AVAILABLE).count();
        }
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getLocality() { return locality; }
        public void setLocality(String locality) { this.locality = locality; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        public BigDecimal getRentFrom() { return rentFrom; }
        public void setRentFrom(BigDecimal rentFrom) { this.rentFrom = rentFrom; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public Set<String> getFacilities() { return facilities; }
        public void setFacilities(Set<String> facilities) { this.facilities = facilities; }
        public long getTotalBeds() { return totalBeds; }
        public void setTotalBeds(long totalBeds) { this.totalBeds = totalBeds; }
        public long getAvailableBeds() { return availableBeds; }
        public void setAvailableBeds(long availableBeds) { this.availableBeds = availableBeds; }
    }

    public static class PropertyDetailDto {
        private Long id; private String title; private String description; private String type; private String city; private String locality; private String address; private String status; private boolean active; private BigDecimal rentFrom; private String imageUrl; private Set<String> facilities; private List<RoomSummaryDto> rooms; private String ownerName; private String ownerEmail;
        public PropertyDetailDto() {}
        public PropertyDetailDto(Property property, List<RoomSummaryDto> rooms) {
            this.id = property.getId(); this.title = property.getTitle(); this.description = property.getDescription(); this.type = property.getType().name(); this.city = property.getCity(); this.locality = property.getLocality(); this.address = property.getAddress(); this.status = property.getVerificationStatus().name(); this.active = property.isActive(); this.rentFrom = property.getRentFrom(); this.imageUrl = property.getImageUrl(); this.facilities = property.getFacilities(); this.rooms = rooms; this.ownerName = property.getOwner().getFullName(); this.ownerEmail = property.getOwner().getEmail();
        }
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getLocality() { return locality; }
        public void setLocality(String locality) { this.locality = locality; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        public BigDecimal getRentFrom() { return rentFrom; }
        public void setRentFrom(BigDecimal rentFrom) { this.rentFrom = rentFrom; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public Set<String> getFacilities() { return facilities; }
        public void setFacilities(Set<String> facilities) { this.facilities = facilities; }
        public List<RoomSummaryDto> getRooms() { return rooms; }
        public void setRooms(List<RoomSummaryDto> rooms) { this.rooms = rooms; }
        public String getOwnerName() { return ownerName; }
        public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
        public String getOwnerEmail() { return ownerEmail; }
        public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }
    }

    public static class RoomRequest {
        @NotBlank
        private String name;
        @NotBlank
        private String roomType;
        @NotNull @Min(1)
        private Integer capacity;
        @NotNull @DecimalMin("0.00")
        private BigDecimal rentPerMonth;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getRoomType() { return roomType; }
        public void setRoomType(String roomType) { this.roomType = roomType; }
        public Integer getCapacity() { return capacity; }
        public void setCapacity(Integer capacity) { this.capacity = capacity; }
        public BigDecimal getRentPerMonth() { return rentPerMonth; }
        public void setRentPerMonth(BigDecimal rentPerMonth) { this.rentPerMonth = rentPerMonth; }
    }

    public static class RoomSummaryDto {
        private Long id; private String name; private String roomType; private Integer capacity; private BigDecimal rentPerMonth; private boolean active; private Integer availableBeds; private Integer totalBeds; private List<BedSummaryDto> beds;
        public RoomSummaryDto() {}
        public RoomSummaryDto(Room room, long availableBeds, long totalBeds) {
            this.id = room.getId(); this.name = room.getName(); this.roomType = room.getRoomType().name(); this.capacity = room.getCapacity(); this.rentPerMonth = room.getRentPerMonth(); this.active = room.isActive(); this.availableBeds = Math.toIntExact(availableBeds); this.totalBeds = Math.toIntExact(totalBeds);
            this.beds = room.getBeds().stream().map(BedSummaryDto::new).toList();
        }
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getRoomType() { return roomType; }
        public void setRoomType(String roomType) { this.roomType = roomType; }
        public Integer getCapacity() { return capacity; }
        public void setCapacity(Integer capacity) { this.capacity = capacity; }
        public BigDecimal getRentPerMonth() { return rentPerMonth; }
        public void setRentPerMonth(BigDecimal rentPerMonth) { this.rentPerMonth = rentPerMonth; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        public Integer getAvailableBeds() { return availableBeds; }
        public void setAvailableBeds(Integer availableBeds) { this.availableBeds = availableBeds; }
        public Integer getTotalBeds() { return totalBeds; }
        public void setTotalBeds(Integer totalBeds) { this.totalBeds = totalBeds; }
        public List<BedSummaryDto> getBeds() { return beds; }
        public void setBeds(List<BedSummaryDto> beds) { this.beds = beds; }
    }

    public static class BedSummaryDto {
        private Long id; private String bedNumber; private String status;
        public BedSummaryDto() {}
        public BedSummaryDto(Bed bed) {
            this.id = bed.getId(); this.bedNumber = bed.getBedNumber(); this.status = bed.getStatus().name();
        }
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getBedNumber() { return bedNumber; }
        public void setBedNumber(String bedNumber) { this.bedNumber = bedNumber; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class BedRequest {
        @NotBlank
        private String bedNumber;
        public String getBedNumber() { return bedNumber; }
        public void setBedNumber(String bedNumber) { this.bedNumber = bedNumber; }
    }

    public static class BedAvailabilityRequest {
        @NotBlank
        private String status;
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class ApplicationRequest {
        @NotNull
        private Long propertyId;
        @NotNull
        private Long roomId;
        @NotNull
        private LocalDate proposedMoveInDate;
        private String note;
        public Long getPropertyId() { return propertyId; }
        public void setPropertyId(Long propertyId) { this.propertyId = propertyId; }
        public Long getRoomId() { return roomId; }
        public void setRoomId(Long roomId) { this.roomId = roomId; }
        public LocalDate getProposedMoveInDate() { return proposedMoveInDate; }
        public void setProposedMoveInDate(LocalDate proposedMoveInDate) { this.proposedMoveInDate = proposedMoveInDate; }
        public String getNote() { return note; }
        public void setNote(String note) { this.note = note; }
    }

    public static class ApplicationDecisionRequest {
        private String status;
        private String reason;
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    public static class ApplicationDto {
        private Long id; private Long propertyId; private String propertyTitle; private Long roomId; private String roomName; private String roomType; private String status; private LocalDate proposedMoveInDate; private String note; private String ownerDecisionReason; private String studentName; private String studentEmail;
        public ApplicationDto() {}
        public ApplicationDto(Application application) {
            this.id = application.getId();
            this.propertyId = application.getProperty().getId();
            this.propertyTitle = application.getProperty().getTitle();
            this.roomId = application.getRoom() != null ? application.getRoom().getId() : null;
            this.roomName = application.getRoom() != null ? application.getRoom().getName() : null;
            this.roomType = application.getRoom() != null ? application.getRoom().getRoomType().name() : null;
            this.status = application.getStatus().name();
            this.proposedMoveInDate = application.getProposedMoveInDate();
            this.note = application.getNote();
            this.ownerDecisionReason = application.getOwnerDecisionReason();
            this.studentName = application.getStudent().getFullName();
            this.studentEmail = application.getStudent().getEmail();
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getPropertyId() { return propertyId; }
        public void setPropertyId(Long propertyId) { this.propertyId = propertyId; }
        public String getPropertyTitle() { return propertyTitle; }
        public void setPropertyTitle(String propertyTitle) { this.propertyTitle = propertyTitle; }
        public Long getRoomId() { return roomId; }
        public void setRoomId(Long roomId) { this.roomId = roomId; }
        public String getRoomName() { return roomName; }
        public void setRoomName(String roomName) { this.roomName = roomName; }
        public String getRoomType() { return roomType; }
        public void setRoomType(String roomType) { this.roomType = roomType; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public LocalDate getProposedMoveInDate() { return proposedMoveInDate; }
        public void setProposedMoveInDate(LocalDate proposedMoveInDate) { this.proposedMoveInDate = proposedMoveInDate; }
        public String getNote() { return note; }
        public void setNote(String note) { this.note = note; }
        public String getOwnerDecisionReason() { return ownerDecisionReason; }
        public void setOwnerDecisionReason(String ownerDecisionReason) { this.ownerDecisionReason = ownerDecisionReason; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        public String getStudentEmail() { return studentEmail; }
        public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }
    }

    public static class BookingDto {
        private Long id; private Long propertyId; private String propertyTitle; private Long roomId; private String roomName; private Long bedId; private String bedNumber; private String status; private BigDecimal monthlyRent; private LocalDate moveInDate; private String studentName; private String studentEmail; private String studentPhone; private String attendanceStatus; private java.time.LocalDateTime checkedInAt; private java.time.LocalDateTime checkedOutAt;
        public BookingDto() {}
        public BookingDto(Booking booking) {
            this.id = booking.getId();
            this.propertyId = booking.getProperty().getId();
            this.propertyTitle = booking.getProperty().getTitle();
            this.roomId = booking.getRoom() != null ? booking.getRoom().getId() : null;
            this.roomName = booking.getRoom() != null ? booking.getRoom().getName() : null;
            this.bedId = booking.getBed() != null ? booking.getBed().getId() : null;
            this.bedNumber = booking.getBed() != null ? booking.getBed().getBedNumber() : null;
            this.status = booking.getStatus().name();
            this.monthlyRent = booking.getMonthlyRent();
            this.moveInDate = booking.getMoveInDate();
            this.studentName = booking.getStudent().getFullName();
            this.studentEmail = booking.getStudent().getEmail();
            this.studentPhone = booking.getStudent().getPhone();
            this.attendanceStatus = booking.getAttendanceStatus() == null ? null : booking.getAttendanceStatus().name();
            this.checkedInAt = booking.getCheckedInAt();
            this.checkedOutAt = booking.getCheckedOutAt();
        }
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getPropertyId() { return propertyId; }
        public void setPropertyId(Long propertyId) { this.propertyId = propertyId; }
        public String getPropertyTitle() { return propertyTitle; }
        public void setPropertyTitle(String propertyTitle) { this.propertyTitle = propertyTitle; }
        public Long getRoomId() { return roomId; }
        public void setRoomId(Long roomId) { this.roomId = roomId; }
        public String getRoomName() { return roomName; }
        public void setRoomName(String roomName) { this.roomName = roomName; }
        public Long getBedId() { return bedId; }
        public void setBedId(Long bedId) { this.bedId = bedId; }
        public String getBedNumber() { return bedNumber; }
        public void setBedNumber(String bedNumber) { this.bedNumber = bedNumber; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public BigDecimal getMonthlyRent() { return monthlyRent; }
        public void setMonthlyRent(BigDecimal monthlyRent) { this.monthlyRent = monthlyRent; }
        public LocalDate getMoveInDate() { return moveInDate; }
        public void setMoveInDate(LocalDate moveInDate) { this.moveInDate = moveInDate; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        public String getStudentEmail() { return studentEmail; }
        public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }
        public String getStudentPhone() { return studentPhone; }
        public void setStudentPhone(String studentPhone) { this.studentPhone = studentPhone; }
        public String getAttendanceStatus() { return attendanceStatus; }
        public void setAttendanceStatus(String attendanceStatus) { this.attendanceStatus = attendanceStatus; }
        public java.time.LocalDateTime getCheckedInAt() { return checkedInAt; }
        public void setCheckedInAt(java.time.LocalDateTime checkedInAt) { this.checkedInAt = checkedInAt; }
        public java.time.LocalDateTime getCheckedOutAt() { return checkedOutAt; }
        public void setCheckedOutAt(java.time.LocalDateTime checkedOutAt) { this.checkedOutAt = checkedOutAt; }
    }

    public static class CancelBookingRequest {
        @NotBlank
        private String reason;
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    public static class AttendanceRequest {
        @NotBlank
        private String status;
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class PaymentRequest {
        @NotNull
        private Long bookingId;
        @NotNull @DecimalMin("0.01")
        private BigDecimal amount;
        @NotBlank
        private String currency = "INR";
        @NotBlank
        private String purpose;
        public Long getBookingId() { return bookingId; }
        public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        public String getPurpose() { return purpose; }
        public void setPurpose(String purpose) { this.purpose = purpose; }
    }

    public static class PaymentDto {
        private Long id; private Long bookingId; private BigDecimal amount; private String currency; private String purpose; private String status; private String paymentReference; private String studentName;
        public PaymentDto() {}
        public PaymentDto(Payment payment) {
            this.id = payment.getId(); this.bookingId = payment.getBooking().getId(); this.amount = payment.getAmount(); this.currency = payment.getCurrency(); this.purpose = payment.getPurpose(); this.status = payment.getStatus().name(); this.paymentReference = payment.getPaymentReference(); this.studentName = payment.getStudent().getFullName();
        }
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getBookingId() { return bookingId; }
        public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        public String getPurpose() { return purpose; }
        public void setPurpose(String purpose) { this.purpose = purpose; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getPaymentReference() { return paymentReference; }
        public void setPaymentReference(String paymentReference) { this.paymentReference = paymentReference; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
    }

    public static class ComplaintRequest {
        @NotBlank
        private String subject;
        @NotBlank
        private String description;
        private Long propertyId;
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Long getPropertyId() { return propertyId; }
        public void setPropertyId(Long propertyId) { this.propertyId = propertyId; }
    }

    public static class ComplaintDto {
        private Long id; private String subject; private String description; private Long propertyId; private String propertyName; private String status; private String ownerResponse; private String studentName; private String studentEmail;
        public ComplaintDto() {}
        public ComplaintDto(Complaint complaint) {
            this.id = complaint.getId(); this.subject = complaint.getSubject(); this.description = complaint.getDescription(); this.propertyId = complaint.getProperty() != null ? complaint.getProperty().getId() : null; this.propertyName = complaint.getProperty() != null ? complaint.getProperty().getTitle() : null; this.status = complaint.getStatus().name(); this.ownerResponse = complaint.getOwnerResponse(); this.studentName = complaint.getStudent().getFullName(); this.studentEmail = complaint.getStudent().getEmail();
        }
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Long getPropertyId() { return propertyId; }
        public void setPropertyId(Long propertyId) { this.propertyId = propertyId; }
        public String getPropertyName() { return propertyName; }
        public void setPropertyName(String propertyName) { this.propertyName = propertyName; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getOwnerResponse() { return ownerResponse; }
        public void setOwnerResponse(String ownerResponse) { this.ownerResponse = ownerResponse; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        public String getStudentEmail() { return studentEmail; }
        public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }
    }

    public static class ComplaintStatusUpdateRequest {
        @NotBlank
        private String status;
        private String response;
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getResponse() { return response; }
        public void setResponse(String response) { this.response = response; }
    }

    public static class AdminReportDto {
        private long totalUsers; private long totalProperties; private long verifiedProperties; private long pendingProperties; private long activeBookings; private long openComplaints; private BigDecimal revenue; private long totalApplications; private long occupiedBeds; private long vacantBeds;
        public AdminReportDto(long totalUsers, long totalProperties, long verifiedProperties, long pendingProperties, long activeBookings, long openComplaints, BigDecimal revenue, long totalApplications, long occupiedBeds, long vacantBeds) {
            this.totalUsers = totalUsers; this.totalProperties = totalProperties; this.verifiedProperties = verifiedProperties; this.pendingProperties = pendingProperties; this.activeBookings = activeBookings; this.openComplaints = openComplaints; this.revenue = revenue; this.totalApplications = totalApplications;
            this.occupiedBeds = occupiedBeds; this.vacantBeds = vacantBeds;
        }
        public long getTotalUsers() { return totalUsers; }
        public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
        public long getTotalProperties() { return totalProperties; }
        public void setTotalProperties(long totalProperties) { this.totalProperties = totalProperties; }
        public long getVerifiedProperties() { return verifiedProperties; }
        public void setVerifiedProperties(long verifiedProperties) { this.verifiedProperties = verifiedProperties; }
        public long getPendingProperties() { return pendingProperties; }
        public void setPendingProperties(long pendingProperties) { this.pendingProperties = pendingProperties; }
        public long getActiveBookings() { return activeBookings; }
        public void setActiveBookings(long activeBookings) { this.activeBookings = activeBookings; }
        public long getOpenComplaints() { return openComplaints; }
        public void setOpenComplaints(long openComplaints) { this.openComplaints = openComplaints; }
        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
        public long getTotalApplications() { return totalApplications; }
        public void setTotalApplications(long totalApplications) { this.totalApplications = totalApplications; }
        public long getOccupiedBeds() { return occupiedBeds; }
        public void setOccupiedBeds(long occupiedBeds) { this.occupiedBeds = occupiedBeds; }
        public long getVacantBeds() { return vacantBeds; }
        public void setVacantBeds(long vacantBeds) { this.vacantBeds = vacantBeds; }
    }
}
