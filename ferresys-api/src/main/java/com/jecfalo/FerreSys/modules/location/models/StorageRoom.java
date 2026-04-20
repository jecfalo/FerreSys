package com.jecfalo.FerreSys.modules.location.models;

import com.jecfalo.FerreSys.modules.inventory.models.Inventory;
import com.jecfalo.FerreSys.modules.location.records.store.RegisterStorageRoom;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "storages")
@EntityListeners(AuditingEntityListener.class)
public class StorageRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String location;
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
    @OneToMany(mappedBy = "storage", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Inventory> inventoryList;
    @ManyToOne(fetch = FetchType.LAZY)
    private Branch branch;
    private Boolean active;
    public StorageRoom(RegisterStorageRoom rST){
        this.id = null;
        this.name = rST.name();
        this.location = rST.location();
        this.active = true;
    }
}
