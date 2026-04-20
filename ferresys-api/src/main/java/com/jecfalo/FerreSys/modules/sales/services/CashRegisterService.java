package com.jecfalo.FerreSys.modules.sales.services;

import com.jecfalo.FerreSys.modules.location.models.Branch;
import com.jecfalo.FerreSys.modules.location.repositories.BranchRepository;
import com.jecfalo.FerreSys.modules.sales.models.CashRegister;
import com.jecfalo.FerreSys.modules.sales.records.cashregister.ReferenceCashRegister;
import com.jecfalo.FerreSys.modules.sales.records.cashregister.RegisterCashRegister;
import com.jecfalo.FerreSys.modules.sales.repositories.CashRegisterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CashRegisterService implements ICashRegisterService {

    @Autowired
    private CashRegisterRepository repository;
    @Autowired
    private BranchRepository branchRepository;

    @Override
    public List<ReferenceCashRegister> getAllActiveCashRegisters() {
        return repository.findAll().stream()
                .filter(cashRegister -> Boolean.TRUE.equals(cashRegister.getActive()))
                .map(ReferenceCashRegister::new)
                .collect(Collectors.toList());
    }

    @Override
    public ReferenceCashRegister registerCash(RegisterCashRegister register) {
        Branch branchId = branchRepository.findById(register.branchId())
                .orElseThrow(()-> new RuntimeException("Branch not found"));
        CashRegister cashRegister = new CashRegister(register, branchId);
        CashRegister result = repository.save(cashRegister);
        return new ReferenceCashRegister(result);
    }

    @Override
    public ReferenceCashRegister updateCash(RegisterCashRegister register, Long id) {
        CashRegister cashId = repository.findById(id)
                .orElseThrow(()-> new RuntimeException("CashRegister not found"));
        cashId.setName(register.name());
        CashRegister result = repository.save(cashId);
        return new ReferenceCashRegister(result);
    }

    @Override
    public void deleteCash(Long id) {
        CashRegister cashRegister = repository.findById(id)
                .orElseThrow(()-> new RuntimeException("CashRegister not found"));
        cashRegister.setActive(false);
        repository.save(cashRegister);
    }

}
