package com.jecfalo.FerreSys.modules.inventory.repositories;

import com.jecfalo.FerreSys.modules.inventory.models.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Page<Inventory> findByActiveTrue(Pageable page);
    Optional<Inventory> findByProductIdAndStorageId(Long productId, Long storeId);
}
