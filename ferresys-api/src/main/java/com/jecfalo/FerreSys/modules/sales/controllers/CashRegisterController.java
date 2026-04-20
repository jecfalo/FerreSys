package com.jecfalo.FerreSys.modules.sales.controllers;

import com.jecfalo.FerreSys.modules.sales.records.cashregister.ReferenceCashRegister;
import com.jecfalo.FerreSys.modules.sales.records.cashregister.RegisterCashRegister;
import com.jecfalo.FerreSys.modules.sales.services.ICashRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador de Cajas Registradoras (Cash Registers).
 * Sirve para proveer la información de las cajas en la sucursal activa.
 */
@RestController
@RequestMapping("/api/cash-registers")
public class CashRegisterController {

    @Autowired
    private ICashRegisterService service;

    /**
     * Devuelve una lista de todas las cajas registradoras que se encuentran activas.
     * Este endpoint es vital para que al iniciar una venta, el vendedor o el sistema Frontend
     * sepa a qué caja puede amarrar el pago.
     */
    @GetMapping
    public ResponseEntity<List<ReferenceCashRegister>> getAll() {
        List<ReferenceCashRegister> cashRegisters = service.getAllActiveCashRegisters();
        return ResponseEntity.ok(cashRegisters);
    }

    @PostMapping
    public ResponseEntity<ReferenceCashRegister> create(@RequestBody RegisterCashRegister register) {
        ReferenceCashRegister create = service.registerCash(register);
        return ResponseEntity.status(HttpStatus.CREATED).body(create);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ReferenceCashRegister> update(@RequestBody RegisterCashRegister updateCash, @PathVariable Long id) {
        ReferenceCashRegister update = service.updateCash(updateCash, id);
        return ResponseEntity.ok(update);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ReferenceCashRegister> delete(@PathVariable Long id) {
        service.deleteCash(id);
        return ResponseEntity.noContent().build();
    }
}
