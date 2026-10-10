package com.studentstay.repository;

import com.studentstay.domain.Property;
import com.studentstay.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByProperty(Property property);
}
