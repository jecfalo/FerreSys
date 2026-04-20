package com.jecfalo.FerreSys.modules.inventory.services;

import com.jecfalo.FerreSys.modules.inventory.records.product.DataProduct;
import com.jecfalo.FerreSys.modules.inventory.records.product.ReferenceProduct;
import com.jecfalo.FerreSys.modules.inventory.records.product.RegisterProd;
import com.jecfalo.FerreSys.modules.inventory.records.product.UpdateProd;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProductService {
    Page<DataProduct> loadAllActive(Pageable page);
    ReferenceProduct create(RegisterProd register);
    ReferenceProduct update(Long id, UpdateProd update);
    void delete(Long id);
}
