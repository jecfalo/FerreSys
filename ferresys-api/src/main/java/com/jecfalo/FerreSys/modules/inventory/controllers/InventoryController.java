package com.jecfalo.FerreSys.modules.inventory.controllers;

import com.jecfalo.FerreSys.modules.inventory.records.inventory.ReferenceInventory;
import com.jecfalo.FerreSys.modules.inventory.records.inventory.RegisterInventory;
import com.jecfalo.FerreSys.modules.inventory.records.inventory.UpdateInventory;
import com.jecfalo.FerreSys.modules.inventory.services.IInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de operaciones CRUD generales de las existencias (Inventario).
 * Permite registrar ingresos, hacer listados paginados de productos en bodega y actualizarlos.
 */
@RestController
@RequestMapping("/api/crud-inv")
public class InventoryController {
    @Autowired
    private IInventoryService service;
    /**
     * Endpoint para habilitar o registrar una nueva entrada en el sistema de inventarios.
     */
    @PostMapping
    public ResponseEntity<ReferenceInventory> createInv(@RequestBody RegisterInventory register){
        ReferenceInventory inv = service.create(register);
        return ResponseEntity.status(HttpStatus.CREATED).body(inv);
    }
    
    /**
     * Retorna el inventario consolidado y habilitado actual en formato paginado.
     * Mapea al sub-path '/list-inventories' que consume la tabla del Frontend.
     */
    @GetMapping("/list-inventories")
    public ResponseEntity<Page<ReferenceInventory>> getInv(
            @PageableDefault(size=10, page=0, sort = "id", direction = Sort.Direction.DESC) Pageable page){
        Page<ReferenceInventory> get = service.loadAllActive(page);
        return ResponseEntity.ok(get);
    }
    
    /**
     * Consulta una ficha u hoja de inventario específico a través de su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReferenceInventory> getInv(@PathVariable Long id){
        ReferenceInventory get = service.findById(id);
        return ResponseEntity.ok(get);
    }
    
    /**
     * Actualiza la ficha de un inventario en particular (por ejemplo, correcciones manuales de stock o estado).
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReferenceInventory> updateInv(@PathVariable Long id, @RequestBody UpdateInventory update){
        ReferenceInventory refUpdate = service.update(id,  update);
        return ResponseEntity.ok(refUpdate);
    }
    
    /**
     * Desactiva de forma lógica un registro particular de inventario.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ReferenceInventory> deleteInv(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
