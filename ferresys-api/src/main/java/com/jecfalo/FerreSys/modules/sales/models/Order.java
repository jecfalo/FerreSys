package com.jecfalo.FerreSys.modules.sales.models;

import com.jecfalo.FerreSys.modules.sales.records.order.RegisterOrder;
import com.jecfalo.FerreSys.modules.users.models.Profile;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private PaidState paidState;
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
    private Double totalAmount;
    private Double amountPaid;
    private Double changeDue;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clientId")
    private Profile client;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sellerId")
    private Profile seller;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashId")
    private CashRegister cashRegister;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OrderDetail> orderDetail;
    private Boolean active;
    public Order(RegisterOrder register) {
        this.id = null;
        this.paidState = PaidState.PENDING;
        this.amountPaid = 0.0;
        this.changeDue = 0.0;
        this.orderDetail = new ArrayList<>();
        this.active = true;
    }
}
