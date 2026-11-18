package com.jecfalo.FerreSys.modules.inventory.services;

import com.jecfalo.FerreSys.modules.inventory.models.Product;
import com.jecfalo.FerreSys.modules.inventory.records.product.DataProduct;
import com.jecfalo.FerreSys.modules.inventory.records.product.ReferenceProduct;
import com.jecfalo.FerreSys.modules.inventory.records.product.RegisterProd;
import com.jecfalo.FerreSys.modules.inventory.records.product.UpdateProd;
import com.jecfalo.FerreSys.modules.inventory.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de ProductService.
 * El repositorio se reemplaza por un mock de Mockito, así que no se necesita base de datos.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService service;

    private Product productoActivo() {
        Product p = new Product(new RegisterProd("MAR-001", "Martillo", 12.5, 5));
        p.setId(1L);
        return p;
    }

    @Test
    void createGuardaElProductoActivoConLosDatosDelRegistro() {
        when(repository.save(any(Product.class))).thenAnswer(inv -> {
            Product p = inv.getArgument(0);
            p.setId(10L);
            return p;
        });

        ReferenceProduct ref = service.create(new RegisterProd("TAL-002", "Taladro", 80.0, 3));

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().isActive()).isTrue();
        assertThat(ref.id()).isEqualTo(10L);
        assertThat(ref.SKU()).isEqualTo("TAL-002");
        assertThat(ref.price()).isEqualTo(80.0);
        assertThat(ref.active()).isTrue();
    }

    @Test
    void updateCambiaPrecioYPuntoDeReorden() {
        Product existente = productoActivo();
        when(repository.findById(1L)).thenReturn(Optional.of(existente));

        ReferenceProduct ref = service.update(1L, new UpdateProd(1L, 15.0, 8));

        assertThat(ref.price()).isEqualTo(15.0);
        assertThat(ref.reorderPoint()).isEqualTo(8);
        assertThat(ref.name()).isEqualTo("Martillo");
    }

    @Test
    void updateConIdInexistenteLanzaExcepcion() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L, new UpdateProd(99L, 1.0, 1)))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No se encuentra el id");
    }

    @Test
    void deleteEsUnBorradoLogico() {
        Product existente = productoActivo();
        when(repository.findById(1L)).thenReturn(Optional.of(existente));

        service.delete(1L);

        assertThat(existente.isActive()).isFalse();
        verify(repository).save(existente);
        verify(repository, never()).delete(any());
    }

    @Test
    void loadAllActiveDevuelveSoloLoQueEntregaElRepositorio() {
        Pageable page = PageRequest.of(0, 10);
        when(repository.findByActiveTrue(page)).thenReturn(new PageImpl<>(List.of(productoActivo())));

        Page<DataProduct> resultado = service.loadAllActive(page);

        assertThat(resultado.getTotalElements()).isEqualTo(1);
    }
}
