package com.jecfalo.FerreSys.modules.sales.models;

import com.jecfalo.FerreSys.modules.inventory.models.Product;
import com.jecfalo.FerreSys.modules.location.models.StorageRoom;
import com.jecfalo.FerreSys.modules.sales.records.order.RegisterOrderDetail;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId")
    private Order order;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId")
    private Product product;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storageId")
    private StorageRoom storage;
    private Integer quantity;
    private Boolean active;
    public OrderDetail(RegisterOrderDetail rOD, Order order, Product product, StorageRoom storage) {
        this.id = null;
        this.order = order;
        this.product = product;
        this.storage = storage;
        this.quantity =rOD.quantity();
        this.active = true;
    }
}
