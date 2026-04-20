package com.jecfalo.FerreSys.modules.inventory.records.product;

public record UpdateProd(
        Long id,
        Double price,
        Integer reorderPoint
) {
}
