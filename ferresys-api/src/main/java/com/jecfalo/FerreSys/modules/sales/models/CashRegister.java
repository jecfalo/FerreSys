package com.jecfalo.FerreSys.modules.sales.models;

import com.jecfalo.FerreSys.modules.location.models.Branch;
import com.jecfalo.FerreSys.modules.sales.records.cashregister.ReferenceCashRegister;
import com.jecfalo.FerreSys.modules.sales.records.cashregister.RegisterCashRegister;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class CashRegister {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branchId")
    private Branch branch;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime creationDate;
    @LastModifiedDate
    private LocalDateTime modificationDate;
    private Boolean active;

    public CashRegister(RegisterCashRegister register, Branch branch) {
        this.id = null;
        this.name = register.name();
        this.branch = branch;
        this.active = true;
    }
}