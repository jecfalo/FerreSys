package com.jecfalo.FerreSys.modules.inventory.records.product;

import com.jecfalo.FerreSys.modules.inventory.models.Product;

public record DataProduct(
        Long id,
        String SKU,
        String name,
        Double price,
        Integer reorderPoint
) {
    public DataProduct(Product p){
        this(
                p.getId(),
                p.getSKU(),
                p.getName(),
                p.getPrice(),
                p.getReorderPoint()
        );
    }
}
