package com.jecfalo.FerreSys.modules.sales.records.order;

import java.util.List;

public record RegisterOrder(
        Long clientId,
        Long sellerId,
        Long cashRegisterId,
        List<RegisterOrderDetail> details
) {
}
