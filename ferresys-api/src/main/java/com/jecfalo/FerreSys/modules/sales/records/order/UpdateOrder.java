package com.jecfalo.FerreSys.modules.sales.records.order;

import com.jecfalo.FerreSys.modules.sales.models.PaidState;

public record UpdateOrder(
        Long id,
        PaidState state,
        Double amountPaid
) {
}
