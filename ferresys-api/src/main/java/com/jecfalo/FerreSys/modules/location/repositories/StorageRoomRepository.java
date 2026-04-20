package com.jecfalo.FerreSys.modules.location.repositories;

import com.jecfalo.FerreSys.modules.location.models.StorageRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StorageRoomRepository extends JpaRepository<StorageRoom, Long> {
    List<StorageRoom> findByActiveTrue();
}
