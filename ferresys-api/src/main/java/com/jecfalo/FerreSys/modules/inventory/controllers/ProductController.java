package com.jecfalo.FerreSys.modules.inventory.controllers;

import com.jecfalo.FerreSys.modules.inventory.records.product.DataProduct;
import com.jecfalo.FerreSys.modules.inventory.records.product.ReferenceProduct;
import com.jecfalo.FerreSys.modules.inventory.records.product.RegisterProd;
import com.jecfalo.FerreSys.modules.inventory.records.product.UpdateProd;
import com.jecfalo.FerreSys.modules.inventory.services.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private IProductService service;
    @GetMapping
    public ResponseEntity<Page<DataProduct>> getProducts(
            @PageableDefault(size=10, page=0, sort = "id", direction = Sort.Direction.DESC) Pageable page
    ){
        Page<DataProduct> products = service.loadAllActive(page);
        return ResponseEntity.ok(products);
    }
    @PostMapping
    public ResponseEntity<ReferenceProduct> createProduct(@RequestBody RegisterProd reg){
        ReferenceProduct reference = service.create(reg);
        return ResponseEntity.status(HttpStatus.CREATED).body(reference);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ReferenceProduct> updateProduct(@PathVariable Long id, @RequestBody UpdateProd upd){
        ReferenceProduct reference = service.update(id, upd);
        return ResponseEntity.ok(reference);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePeroduct(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
