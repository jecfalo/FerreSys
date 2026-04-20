package com.jecfalo.FerreSys.modules.inventory.services;

import com.jecfalo.FerreSys.modules.inventory.models.Inventory;
import com.jecfalo.FerreSys.modules.inventory.records.inventory.ReferenceInventory;
import com.jecfalo.FerreSys.modules.inventory.records.inventory.RegisterInventory;
import com.jecfalo.FerreSys.modules.inventory.records.inventory.UpdateInventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IInventoryService {
    Page<ReferenceInventory> loadAllActive(Pageable page);
    ReferenceInventory create(RegisterInventory register);
    ReferenceInventory findById(Long id);
    ReferenceInventory update(Long id, UpdateInventory update);
    void delete(Long id);
    //reglas de negocio
    void eserveStock(Long productId, Long storeId, Integer quantity);
    void confirmSale(Long productId, Long storeId, Integer quantity);
    void releasedStock(Long productId, Long storeId, Integer quantity);
}
