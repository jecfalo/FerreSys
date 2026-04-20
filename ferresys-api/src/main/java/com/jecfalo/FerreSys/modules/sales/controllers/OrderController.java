package com.jecfalo.FerreSys.modules.sales.controllers;

import com.jecfalo.FerreSys.modules.sales.records.order.DataListOrder;
import com.jecfalo.FerreSys.modules.sales.records.order.ReferenceOrder;
import com.jecfalo.FerreSys.modules.sales.records.order.RegisterOrder;
import com.jecfalo.FerreSys.modules.sales.records.order.UpdateOrder;
import com.jecfalo.FerreSys.modules.sales.services.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para manejar todas las peticiones HTTP relacionadas a las Órdenes de Venta.
 * Permite listar, procesar, consultar y actualizar transacciones de clientes.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private IOrderService service;
    /**
     * Endpoint para obtener un registro histórico de ventas.
     * Retorna DataListOrder paginado dependiendo del nivel de acceso del usuario 
     * (los vendedores/admins ven todo, los clientes ven únicamente sus propias compras).
     */
    @GetMapping("/list-orders")
    public ResponseEntity<Page<DataListOrder>> getAllOrders(
            @PageableDefault(size=10, page=0, sort = "id", direction = Sort.Direction.DESC)Pageable page) {
        Page<DataListOrder> order = service.loadAllActive(page);
        return ResponseEntity.ok(order);
    }
    /**
     * Endpoint encargado de inicializar una nueva orden de venta (presupuesto).
     * Reserva el stock de los ítems en su bodega específica para evitar sobreventas.
     */
    @PostMapping("/process-sale")
    public ResponseEntity<ReferenceOrder> processSale(@RequestBody RegisterOrder register) {
        ReferenceOrder create = service.create(register);
        return ResponseEntity.status(HttpStatus.CREATED).body(create);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ReferenceOrder> getOrderById(@PathVariable Long id) {
        ReferenceOrder orderId = service.getOrderById(id);
        return ResponseEntity.ok(orderId);
    }
    /**
     * Procesa la liquidación o cancelación de la orden (Pagado o Cancelado).
     * Si la orden se marca como PAID, extrae la mercancía del inventario definitivamente.
     * Si se marca como CANCELLED, restaura el stock reservado para ventas futuras.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReferenceOrder> updateOrderById(@PathVariable Long id, @RequestBody UpdateOrder update) {
        ReferenceOrder orderUpdate  = service.update(id, update);
        return ResponseEntity.ok(orderUpdate);
    }

    /**
     * Da de baja lógica o cancela una orden de venta usando su ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderById(@PathVariable Long id) {
        service.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
