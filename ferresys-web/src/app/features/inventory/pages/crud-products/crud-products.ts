import { CommonModule } from '@angular/common';
import { AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Forms } from '../../../../shared/components/forms/forms';
import { Tables } from '../../../../shared/components/tables/tables';
import { DataProduct } from '../../models/product/dataProduct';
import { ModelTable } from '../../../../shared/models/modelTable';
import { ModelForms } from '../../../../shared/models/modelForms';
import { Validators } from '@angular/forms';
import { ProductService } from '../../services/product-service';
import { UpdateProd } from '../../models/product/updateProd';
import { RegisterProd } from '../../models/product/registerProduct';
import * as bootstrap from 'bootstrap';

@Component({
  selector: 'app-crud-products',
  imports: [CommonModule, Forms, Tables],
  templateUrl: './crud-products.html',
  styleUrl: './crud-products.css',
})
export class CrudProducts implements OnInit, AfterViewInit {
  products: DataProduct[] = [];
  isEditing: number | null = null;
  copyProduct: any = null;
  isSaving: boolean = false;
  //referencia al modal
  modalRef: any;
  @ViewChild('productsModal') modalElement!: ElementRef;
  //tabla
  tableColumns: ModelTable[] = [
    { header: 'SKU', key: 'SKU', type: 'text' },
    { header: 'Nombre', key: 'name', type: 'text' },
    { header: 'Precio ($)', key: 'price', type: 'number' },
    { header: 'Punto de Reorden', key: 'reorderPoint', type: 'text' }
  ];
  formProd: ModelForms[] = [
    { name: 'SKU', label: 'Código SKU', type: 'text', colClass: 'col-md-6', validators: [Validators.required, Validators.minLength(3), Validators.maxLength(20)] },
    { name: 'name', label: 'Nombre del producto', type: 'text', colClass: 'col-md-6', validators: [Validators.required, Validators.minLength(3), Validators.maxLength(100)] },
    { name: 'price', label: 'Precio', type: 'number', colClass: 'col-md-6', validators: [Validators.required, Validators.min(0.01)] },
    { name: 'reorderPoint', label: 'Stock mínimo', type: 'number', colClass: 'col-md-6', validators: [Validators.required, Validators.min(1)] }
  ];
  constructor(private service: ProductService) { }
  ngOnInit(): void {
    this.loadProducts();
  }
  ngAfterViewInit(): void {
    if(this.modalElement){
      this.modalRef = new bootstrap.Modal(this.modalElement.nativeElement);
    }
  }
  loadProducts(): void {
    this.service.getProducts().subscribe({
      next: (data: DataProduct[]) => {
        this.products = data;
      },
      error: (err) => console.error('Error al cargar el producto', err)
    })
  }
  //logica del modal
  openNew(): void {
    this.isEditing = null;
    this.copyProduct = null;
    //desbloqueamos todos los campos para crear uno nuevo
      this.formProd.forEach(field => field.readonly = false);
    this.modalRef?.show();
  }
  openEdit(product: DataProduct): void {
    /*
    ESTO SE HACE YA QUE NO INSERTAMOS EL ID EN EL FORMS
    */
    //buscamos el producto de nuestra lista cargada
    const productOriginal = this.products.find(p => p.SKU === product.SKU);
    //se recupera el id que vido del backend
    this.isEditing = productOriginal ? product.id : null;
    //pasamos el producto completo
    this.copyProduct = productOriginal ? productOriginal: product;
    //bloqueamos el SKU y name porque no se puede bloquear
    this.formProd.forEach(field =>{
      if(field.name === 'SKU' || field.name === 'name'){
        //se indica que se ponga en gris
        field.readonly =true;
      }else{
        //el resto queda libre
        field.readonly=false;
      }
    });
    /*
    OJO NO USAR disabled ya que guarda el valor del objeto al guardar,
    readonly los bloquea
    */
    this.modalRef?.show();
  }
  closeModal(): void {
    this.modalRef?.hide();
  }
  //crud
  saveProduct(formData: any): void {
    this.isSaving = true; //activar el spinner
    if (this.isEditing) {
      //flujo de actualizacion
      const updateData: UpdateProd = { ...formData, id: this.isEditing };
      this.service.updateProduct(updateData).subscribe({
        next: () => {
          this.closeModal();
          this.loadProducts();
          this.isSaving = false;
        },
        error: (err) => {
          console.error('Error al actualizar', err);
          this.isSaving = false;
        }
      });
    } else {
      //flujo de creacion
      const newData: RegisterProd = { ...formData };
      this.service.addProduct(newData).subscribe({
        next: () => {
          this.closeModal();
          this.loadProducts();
          this.isSaving = false;
        },
        error: (err) => {
          console.error('Error creando', err);
          this.isSaving = false;
        }
      });
    }
  }
  deleteProduct(product: DataProduct): void {
    const confirmDelete = confirm(`¿Estás seguro de eliminar el producto: ${product.name}?`);
    if (confirmDelete && product.id) {
      this.service.deleteProduct(product.id).subscribe({
        next: () => {
          this.loadProducts();
        },
        error: (err) => {
          console.error(`Error eliminando: ${err}`)
        }
      });
    }
  }
}
