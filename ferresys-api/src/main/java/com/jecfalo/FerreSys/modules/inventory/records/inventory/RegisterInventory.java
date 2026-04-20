package com.jecfalo.FerreSys.modules.inventory.records.inventory;

public record RegisterInventory(
        Long productId,
        Long storageId,
        Integer physQuantity,
        Integer amountReserved
) {
}
