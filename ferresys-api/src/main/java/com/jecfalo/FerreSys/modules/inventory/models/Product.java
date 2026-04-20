package com.jecfalo.FerreSys.modules.inventory.models;

import com.jecfalo.FerreSys.modules.inventory.records.product.RegisterProd;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "products")
@EntityListeners(AuditingEntityListener.class)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(unique = true)
    private String SKU;
    private String name;
    private Double price;
    private Integer reorderPoint;
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updateAt;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Inventory> inventoryList;
    private boolean active;
    public Product(RegisterProd register){
        this.id = null;
        this.SKU = register.SKU();
        this.name = register.name();
        this.price = register.price();
        this.reorderPoint = register.reorderPoint();
        this.active = true;
    }
}
