package com.studentstay.repository;

import com.studentstay.domain.Bed;
import com.studentstay.domain.BedStatus;
import com.studentstay.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BedRepository extends JpaRepository<Bed, Long> {
    List<Bed> findByRoom(Room room);
    List<Bed> findByRoomAndStatusOrderByIdAsc(Room room, BedStatus status);
    long countByRoomAndStatus(Room room, BedStatus status);
    long countByStatus(BedStatus status);
}
