package com.jecfalo.FerreSys.modules.sales.records.order;

import com.jecfalo.FerreSys.modules.sales.models.Order;
import com.jecfalo.FerreSys.modules.sales.models.PaidState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record ReferenceOrder(
        Long id,
        PaidState paidState,
        LocalDateTime creationDate,
        LocalDateTime updateDate,
        Double totalAmount,
        Double amountPaid,
        Double changeDue,
        String clientName,
        String documentClient,
        String sellerName,
        String cashRegisterName,
        List<OrderDetailItem> itemDetail,
        Boolean active
) {
    public ReferenceOrder(Order order){
        this(
                order.getId(),
                order.getPaidState(),
                order.getCreatedDate(),
                order.getLastModifiedDate(),
                order.getTotalAmount(),
                order.getAmountPaid(),
                order.getChangeDue(),
                //extraemos los dates del cliente
                order.getClient() !=null ? order.getClient().getNames() : "Consumidor final",
                order.getClient() != null ? order.getClient().getDocument() : "999999999",
                order.getSeller() != null ? order.getSeller().getNames() : "Compra web",
                order.getCashRegister() != null ? order.getCashRegister().getName() : "Compra web",

                //operator ternario para evitar que venga nula
                order.getOrderDetail() != null ?
                        order.getOrderDetail().stream()
                        .filter(detail -> detail.getActive())
                        .map(OrderDetailItem::new)
                        .collect(Collectors.toList())
                        :List.of(),

                order.getActive()
        );
    }
}
