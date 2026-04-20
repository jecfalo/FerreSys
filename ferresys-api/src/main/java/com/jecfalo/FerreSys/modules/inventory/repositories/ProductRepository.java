package com.jecfalo.FerreSys.modules.inventory.repositories;

import com.jecfalo.FerreSys.modules.inventory.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByActiveTrue(Pageable page);
    Page<Product> findByActiveFalse(Pageable page);
}
