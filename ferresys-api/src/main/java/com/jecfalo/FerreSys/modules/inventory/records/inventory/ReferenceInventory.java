package com.jecfalo.FerreSys.modules.inventory.records.inventory;

import com.jecfalo.FerreSys.modules.inventory.models.Inventory;

public record ReferenceInventory(
        Long id,
        String nameProduct,
        String storageName,
        Integer physQuantity,
        Integer amountReserved,
        Boolean active
) {
    public ReferenceInventory(Inventory inv){
        this(
                inv.getId(),
                inv.getProduct().getName(),
                inv.getStorage().getName(),
                inv.getPhysQuantity(),
                inv.getAmountReserved(),
                inv.getActive()
        );
    }
}
