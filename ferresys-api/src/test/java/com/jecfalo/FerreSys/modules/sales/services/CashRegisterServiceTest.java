package com.jecfalo.FerreSys.modules.sales.services;

import com.jecfalo.FerreSys.modules.location.models.Branch;
import com.jecfalo.FerreSys.modules.location.repositories.BranchRepository;
import com.jecfalo.FerreSys.modules.sales.models.CashRegister;
import com.jecfalo.FerreSys.modules.sales.records.cashregister.ReferenceCashRegister;
import com.jecfalo.FerreSys.modules.sales.records.cashregister.RegisterCashRegister;
import com.jecfalo.FerreSys.modules.sales.repositories.CashRegisterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Pruebas unitarias de CashRegisterService (cajas registradoras por sucursal). */
@ExtendWith(MockitoExtension.class)
class CashRegisterServiceTest {

    @Mock
    private CashRegisterRepository repository;
    @Mock
    private BranchRepository branchRepository;

    @InjectMocks
    private CashRegisterService service;

    private Branch sucursal() {
        Branch b = new Branch();
        b.setId(1L);
        b.setName("Matriz");
        b.setActive(true);
        return b;
    }

    private CashRegister caja(long id, boolean activa) {
        CashRegister c = new CashRegister(new RegisterCashRegister(null, "Caja " + id, 1L), sucursal());
        c.setId(id);
        c.setActive(activa);
        return c;
    }

    @Test
    void getAllActiveFiltraLasCajasInactivas() {
        when(repository.findAll()).thenReturn(List.of(caja(1, true), caja(2, false), caja(3, true)));

        List<ReferenceCashRegister> activas = service.getAllActiveCashRegisters();

        assertThat(activas).extracting(ReferenceCashRegister::id).containsExactly(1L, 3L);
    }

    @Test
    void registerCashAsociaLaCajaALaSucursal() {
        when(branchRepository.findById(1L)).thenReturn(Optional.of(sucursal()));
        when(repository.save(any(CashRegister.class))).thenAnswer(inv -> inv.getArgument(0));

        ReferenceCashRegister ref = service.registerCash(new RegisterCashRegister(null, "Caja 1", 1L));

        assertThat(ref.name()).isEqualTo("Caja 1");
        assertThat(ref.branch()).isEqualTo("Matriz");
        assertThat(ref.active()).isTrue();
    }

    @Test
    void registerCashConSucursalInexistenteLanzaExcepcion() {
        when(branchRepository.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.registerCash(new RegisterCashRegister(null, "Caja X", 7L)))
                .hasMessage("Branch not found");
        verify(repository, never()).save(any());
    }

    @Test
    void deleteCashDesactivaLaCaja() {
        CashRegister c = caja(5, true);
        when(repository.findById(5L)).thenReturn(Optional.of(c));

        service.deleteCash(5L);

        assertThat(c.getActive()).isFalse();
        verify(repository).save(c);
    }
}
