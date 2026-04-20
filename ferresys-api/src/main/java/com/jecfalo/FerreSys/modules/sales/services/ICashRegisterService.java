package com.jecfalo.FerreSys.modules.sales.services;

import com.jecfalo.FerreSys.modules.sales.records.cashregister.ReferenceCashRegister;
import com.jecfalo.FerreSys.modules.sales.records.cashregister.RegisterCashRegister;

import java.util.List;

public interface ICashRegisterService {
    List<ReferenceCashRegister> getAllActiveCashRegisters();
    ReferenceCashRegister registerCash(RegisterCashRegister register);
    ReferenceCashRegister updateCash(RegisterCashRegister register, Long id);
    void deleteCash(Long id);
}
