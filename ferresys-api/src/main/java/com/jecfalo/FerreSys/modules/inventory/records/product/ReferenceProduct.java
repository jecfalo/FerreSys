package com.jecfalo.FerreSys.modules.inventory.records.product;

import com.jecfalo.FerreSys.modules.inventory.models.Product;

import java.time.LocalDateTime;

public record ReferenceProduct(
        Long id,
        String SKU,
        String name,
        Double price,
        Integer reorderPoint,
        LocalDateTime createdAt,
        LocalDateTime updateAt,
        Boolean active
) {
    public ReferenceProduct(Product p){
        this(
                p.getId(),
                p.getSKU(),
                p.getName(),
                p.getPrice(),
                p.getReorderPoint(),
                p.getCreatedAt(),
                p.getUpdateAt(),
                p.isActive()
        );
    }
}
