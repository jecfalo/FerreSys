package com.jecfalo.FerreSys.modules.sales.records.order;

public record RegisterOrderDetail(
        Long productId,
        Long storageId,
        Integer quantity
) {
}
