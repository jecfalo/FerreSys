package com.jecfalo.FerreSys.modules.sales.records.order;

import com.jecfalo.FerreSys.modules.sales.models.Order;

public record DataListOrder(
        Long id,
        Double totalAmount,
        String documentClient,
        String cashRegisterName
) {
    public DataListOrder(Order order) {
        this(
                order.getId(),
                order.getTotalAmount(),
                order.getClient().getDocument(),
                order.getCashRegister().getName()
        );
    }
}
