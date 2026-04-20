package com.jecfalo.FerreSys.modules.sales.services;

import com.jecfalo.FerreSys.modules.sales.records.order.DataListOrder;
import com.jecfalo.FerreSys.modules.sales.records.order.ReferenceOrder;
import com.jecfalo.FerreSys.modules.sales.records.order.RegisterOrder;
import com.jecfalo.FerreSys.modules.sales.records.order.UpdateOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IOrderService {
    Page<DataListOrder> loadAllActive(Pageable page);
    ReferenceOrder create(RegisterOrder register);
    ReferenceOrder getOrderById(Long id);
    ReferenceOrder update(Long id, UpdateOrder update);
    void deleteOrder(Long id);
}
