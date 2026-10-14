package com.studentstay.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "demo_bookings")
public class DemoBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(nullable = false)
    private Long pgId;

    @Column(nullable = false, length = 160)
    private String pgName;

    @Column(nullable = false, length = 160)
    private String location;

    @Column(nullable = false, length = 40)
    private String category;

    @Column(nullable = false, length = 40)
    private String sharing;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal rent;

    @Column(nullable = false, length = 120)
    private String studentName;

    @Column(nullable = false, length = 120)
    private String place;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false)
    private LocalDate moveInDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.ACTIVE;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }

    protected DemoBooking() {
    }

    public DemoBooking(User student, Long pgId, String pgName, String location, String category,
                       String sharing, BigDecimal rent, String studentName, String place,
                       String phone, LocalDate moveInDate) {
        this.student = student;
        this.pgId = pgId;
        this.pgName = pgName;
        this.location = location;
        this.category = category;
        this.sharing = sharing;
        this.rent = rent;
        this.studentName = studentName;
        this.place = place;
        this.phone = phone;
        this.moveInDate = moveInDate;
    }

    public Long getId() { return id; }
    public User getStudent() { return student; }
    public Long getPgId() { return pgId; }
    public String getPgName() { return pgName; }
    public String getLocation() { return location; }
    public String getCategory() { return category; }
    public String getSharing() { return sharing; }
    public BigDecimal getRent() { return rent; }
    public String getStudentName() { return studentName; }
    public String getPlace() { return place; }
    public String getPhone() { return phone; }
    public LocalDate getMoveInDate() { return moveInDate; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
