package com.jecfalo.FerreSys.modules.inventory.records.inventory;

public record UpdateInventory(
        Long id,
        Integer physQuantity,
        Integer amountReserved
) {
}
