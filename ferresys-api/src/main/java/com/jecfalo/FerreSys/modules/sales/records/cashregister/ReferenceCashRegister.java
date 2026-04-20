package com.jecfalo.FerreSys.modules.sales.records.cashregister;

import com.jecfalo.FerreSys.modules.sales.models.CashRegister;

public record ReferenceCashRegister(
        Long id,
        String name,
        String branch,
        Boolean active
) {
    public ReferenceCashRegister(CashRegister cashRegister) {
        this(
                cashRegister.getId(),
                cashRegister.getName(),
                cashRegister.getBranch().getName(),
                cashRegister.getActive()
        );
    }
}
