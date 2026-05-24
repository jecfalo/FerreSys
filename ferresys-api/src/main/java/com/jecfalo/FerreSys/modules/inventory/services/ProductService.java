package com.jecfalo.FerreSys.modules.inventory.services;

import com.jecfalo.FerreSys.modules.inventory.models.Product;
import com.jecfalo.FerreSys.modules.inventory.records.product.DataProduct;
import com.jecfalo.FerreSys.modules.inventory.records.product.ReferenceProduct;
import com.jecfalo.FerreSys.modules.inventory.records.product.RegisterProd;
import com.jecfalo.FerreSys.modules.inventory.records.product.UpdateProd;
import com.jecfalo.FerreSys.modules.inventory.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService implements IProductService{
    @Autowired
    private ProductRepository repository;
    @Override
    @Transactional
    public Page<DataProduct> loadAllActive(Pageable page) {
        Page<Product> productActive = repository.findByActiveTrue(page);
        return productActive.map(DataProduct::new);
    }

    @Override
    @Transactional
    public ReferenceProduct create(RegisterProd register) {
        Product create = new Product(register);
        Product savedProduct = repository.save(create);
        return new ReferenceProduct(savedProduct);
    }

    @Override
    @Transactional
    public ReferenceProduct update(Long id, UpdateProd update) {
        Product product = repository.findById(id)
                .orElseThrow(()-> new RuntimeException("No se encuentra el id asociado"));
        product.setPrice(update.price());
        product.setReorderPoint(update.reorderPoint());
        return new ReferenceProduct(product);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Product product = repository.findById(id)
                .orElseThrow(()-> new RuntimeException("No se encuentra el id"));
        product.setActive(false);
        repository.save(product);
    }
}
