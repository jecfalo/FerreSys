package com.jecfalo.FerreSys.modules.inventory.models;

import com.jecfalo.FerreSys.modules.inventory.records.inventory.RegisterInventory;
import com.jecfalo.FerreSys.modules.location.models.StorageRoom;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;
    @ManyToOne(fetch = FetchType.LAZY)
    private StorageRoom storage;
    private Integer physQuantity;
    private Integer amountReserved;
    private Boolean active;
    public Inventory(RegisterInventory register, Product product, StorageRoom storage){
        this.id = null;
        this.product = product;
        this.storage = storage;
        this.physQuantity = register.physQuantity();
        this.amountReserved = register.amountReserved();
        this.active = true;
    }
}
