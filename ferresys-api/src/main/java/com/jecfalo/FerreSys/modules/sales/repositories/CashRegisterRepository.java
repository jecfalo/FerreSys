package com.jecfalo.FerreSys.modules.sales.repositories;

import com.jecfalo.FerreSys.modules.sales.models.CashRegister;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CashRegisterRepository extends JpaRepository<CashRegister, Long> {
}
