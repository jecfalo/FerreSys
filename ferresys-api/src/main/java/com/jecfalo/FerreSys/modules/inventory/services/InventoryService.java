package com.jecfalo.FerreSys.modules.inventory.services;

import com.jecfalo.FerreSys.modules.inventory.models.Inventory;
import com.jecfalo.FerreSys.modules.inventory.models.Product;
import com.jecfalo.FerreSys.modules.inventory.records.inventory.UpdateInventory;
import com.jecfalo.FerreSys.modules.location.models.StorageRoom;
import com.jecfalo.FerreSys.modules.inventory.records.inventory.ReferenceInventory;
import com.jecfalo.FerreSys.modules.inventory.records.inventory.RegisterInventory;
import com.jecfalo.FerreSys.modules.inventory.repositories.InventoryRepository;
import com.jecfalo.FerreSys.modules.inventory.repositories.ProductRepository;
import com.jecfalo.FerreSys.modules.location.repositories.StorageRoomRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class InventoryService implements IInventoryService{
    @Autowired
    private InventoryRepository repository;
    @Autowired
    private ProductRepository prodRepository;
    @Autowired
    private StorageRoomRepository storageRoomRepository;

    @Override
    @Transactional
    public Page<ReferenceInventory> loadAllActive(Pageable page) {
        Page<Inventory> invPage = repository.findByActiveTrue(page);
        return invPage.map(ReferenceInventory::new);
    }

    @Override
    @Transactional
    public ReferenceInventory create(RegisterInventory register) {
        Product product = prodRepository.findById(register.productId())
                .orElseThrow(()-> new RuntimeException("No se encuentra tal producto"));
        StorageRoom storage = storageRoomRepository.findById(register.storageId())
                .orElseThrow(()-> new RuntimeException("No existe esa bodega"));
        Inventory inv = new Inventory(register, product, storage);
        var saveInv = repository.save(inv);
        return new ReferenceInventory(saveInv);
    }

    @Override
    @Transactional
    public ReferenceInventory findById(Long id) {
        Inventory refId = repository.findById(id)
                .orElseThrow(()-> new RuntimeException("No se nada asociado a este registro"));
        return new ReferenceInventory(refId);
    }

    @Override
    @Transactional
    public ReferenceInventory update(Long id, UpdateInventory update) {
        Inventory invId = repository.findById(id)
                .orElseThrow(()-> new RuntimeException("No se encontra el registro"));
        invId.setPhysQuantity(update.physQuantity());
        var invSave = repository.save(invId);
        return new ReferenceInventory(invSave);
    }

    @Override
    public void delete(Long id) {
        Inventory inv = repository.findById(id)
                .orElseThrow(()-> new RuntimeException("No se encontra el registro"));
        inv.setActive(false);
        repository.save(inv);
    }


    @Override
    @Transactional
    public void eserveStock(Long productId, Long storeId, Integer quantity) {
        Inventory inv = repository.findByProductIdAndStorageId(productId, storeId)
                .orElseThrow(()-> new RuntimeException("No hay registro de este producto en bodega"));
        //calcular el stock disponible
        int avalaibleStock = inv.getPhysQuantity() -inv.getAmountReserved();
        //bloquear si no alcanza
        if(quantity > avalaibleStock) {
            throw new RuntimeException("Stock insuficiente. Disponible "+ avalaibleStock + ", solicitado: " + quantity);
        }
        //sumar a la reserva
        inv.setAmountReserved(inv.getAmountReserved() + quantity);
        repository.save(inv);
    }

    @Override
    @Transactional
    public void confirmSale(Long productId, Long storeId, Integer quantity) {
        Inventory inv = repository.findByProductIdAndStorageId(productId, storeId)
                .orElseThrow(()-> new RuntimeException("No se encontra el registro"));
        //cantidad vendida
        //si se paga se quita de la reserva y de lo fisico
        inv.setPhysQuantity(inv.getPhysQuantity() - quantity);
        inv.setAmountReserved(inv.getAmountReserved() - quantity);
        repository.save(inv);
        checkReorderPoint(inv);
    }

    @Override
    @Transactional
    public void releasedStock(Long productId, Long storeId, Integer quantity) {
        Inventory inv = repository.findByProductIdAndStorageId(productId, storeId)
                .orElseThrow(()-> new RuntimeException("No se encontra el registro"));
        //si se cancela quitar de la reserva
        inv.setAmountReserved(inv.getAmountReserved() - quantity);
        repository.save(inv);
    }
    //metodo privado para la regla 4
    private void checkReorderPoint(Inventory inv){
        //calcular la realidad actual
        int avalaibleStock = inv.getPhysQuantity() -inv.getAmountReserved();
        //compara contra el limite del producto
        if(avalaibleStock > inv.getProduct().getReorderPoint()) {
            //dispara la alarma
            System.err.println("¡ALERTA el producto " + inv.getProduct().getSKU()+ " en bodega bajo su punto de reorden. Stock actual: " + avalaibleStock);
        }
    }
}
