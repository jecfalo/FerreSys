package com.jecfalo.FerreSys.modules.sales.records.order;

import com.jecfalo.FerreSys.modules.sales.models.OrderDetail;

public record OrderDetailItem(
        Long id,
        String productName,
        Double productPrice,
        Integer quantity
) {
    public OrderDetailItem(OrderDetail detail){
        this(
                detail.getId(),
                detail.getProduct().getName(),
                detail.getProduct().getPrice(),
                detail.getQuantity()
        );
    }
}
