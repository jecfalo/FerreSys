package com.jecfalo.FerreSys.modules.inventory.records.product;

public record RegisterProd(
        String SKU,
        String name,
        Double price,
        Integer reorderPoint
) {
}
