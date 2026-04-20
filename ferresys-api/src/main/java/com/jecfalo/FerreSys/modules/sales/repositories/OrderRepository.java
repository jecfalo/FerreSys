package com.jecfalo.FerreSys.modules.sales.repositories;

import com.jecfalo.FerreSys.modules.sales.models.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {
    /*
    @Query("SELECT o FROM Order o WHERE o.client.user.username = :username")
    Page<Order> findAllClientUserUsername(@Param("username") String username, Pageable pageable);
     */
    Page<Order> findByClient_User_Username(String username, Pageable pageable);
}
