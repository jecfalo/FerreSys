import { Component, ElementRef, ViewChild, OnInit, AfterViewInit } from '@angular/core';
import { ReferenceInv } from '../../models/inventory/referenceInv';
import { InventoryService } from '../../services/inventory-service';
import { ModelTable } from '../../../../shared/models/modelTable';
import { ModelForms } from '../../../../shared/models/modelForms';
import { Validators } from '@angular/forms';
import { UpdateInventory } from '../../models/inventory/UpdateInventory';
import { RegisterInv } from '../../models/inventory/registerInventory';
import { CommonModule } from '@angular/common';
import { Forms } from '../../../../shared/components/forms/forms';
import { Tables } from '../../../../shared/components/tables/tables';
import * as bootstrap from 'bootstrap';
import { ProductService } from '../../services/product-service';
import { StorageRoomService } from '../../services/storage-room-service';
import { DataProduct } from '../../models/product/dataProduct';
import { ReferenceStorageRoom } from '../../models/store/referenceSto';

@Component({
  selector: 'app-crud-iventory',
  imports: [CommonModule, Forms, Tables],
  templateUrl: './crud-iventory.html',
  styleUrl: './crud-iventory.css',
})
export class CrudIventory implements OnInit, AfterViewInit {
  inventories: ReferenceInv[] = [];
  products: DataProduct[] = [];
  storages: ReferenceStorageRoom[] = [];

  isEditing: number | null = null;
  copyInventories: any = null;
  isSaving: boolean = false;
  modalRef: any;
  @ViewChild('inventoryModal') modalElement!: ElementRef;
  columnTables: ModelTable[] = [
    { header: 'Nombre de Producto', key: 'nameProduct', type: 'text' },
    { header: 'Almacen', key: 'storageName', type: 'text' },
    { header: 'Cantidad Fisica', key: 'physQuantity', type: 'number' },
    { header: 'Cantidad Reservada', key: 'amountReserved', type: 'number' }
  ];
  formInv: ModelForms[] = [
    { name: 'productId', label: 'Nombre del Producto', type: 'searchable-select', colClass: 'col-md-6', displayKey: 'name', valueKey: 'id', validators: [Validators.required] },
    { name: 'storageId', label: 'Almacen', type: 'searchable-select', colClass: 'col-md-6', displayKey: 'name', valueKey: 'id', validators: [Validators.required] },
    { name: 'physQuantity', label: 'Cantidad Fisica', type: 'number', colClass: 'col-md-6', validators: [Validators.required, Validators.min(0.01)] },
    { name: 'amountReserved', label: 'Cantidad Reservada', type: 'number', colClass: 'col-md-6', validators: [Validators.required, Validators.min(0.01)] }
  ];
  constructor(
    private inventoryService: InventoryService,
    private productService: ProductService,
    private storageService: StorageRoomService
  ) { }
  ngOnInit(): void {
    this.loadInventories();
    this.loadProducts();
    this.loadStorages();
  }
  ngAfterViewInit(): void {
    if (this.modalElement) {
      this.modalRef = new bootstrap.Modal(this.modalElement.nativeElement);
    }
  }
  loadInventories(): void {
    this.inventoryService.getInventories().subscribe({
      next: (data: ReferenceInv[]) => {
        this.inventories = data;
      },
      error: (err) => console.error('Error al cargar el inventario', err)
    });
  }
  loadProducts(): void {
    this.productService.getProducts().subscribe(data => {
      this.products = data;
      const field = this.formInv.find(f => f.name === 'productId');
      if (field) field.options = this.products;
    });
  }
  loadStorages(): void {
    this.storageService.getStorages().subscribe(data => {
      this.storages = data;
      const field = this.formInv.find(f => f.name === 'storageId');
      if (field) field.options = this.storages;
    });
  }
  openNew(): void {
    this.isEditing = null;
    this.copyInventories = null;
    this.formInv.forEach(field => field.readonly = false);
    this.modalRef?.show();
  }
  openEdit(inventory: ReferenceInv): void {
    const inventoryOriginal = this.inventories.find(i => i.id === inventory.id);
    this.isEditing = inventoryOriginal ? inventory.id : null;

    // Mapear los nombres de la tabla a los IDs para llenar el buscador del form
    const productOriginal = this.products.find(p => p.name === inventory.nameProduct);
    const storageOriginal = this.storages.find(s => s.name === inventory.storageName);

    this.copyInventories = {
      ...(inventoryOriginal ? inventoryOriginal : inventory),
      productId: productOriginal ? productOriginal.id : null,
      storageId: storageOriginal ? storageOriginal.id : null
    };

    this.formInv.forEach(field => {
      if (field.name === 'productId' || field.name === 'storageId') {
        field.readonly = true;
      } else {
        field.readonly = false;
      }
    });
    this.modalRef?.show();
  }
  closeModal(): void {
    this.modalRef?.hide();
  }
  saveInventory(formData: any): void {
    this.isSaving = true;
    if (this.isEditing) {
      const updateData: UpdateInventory = { ...formData, id: this.isEditing };
      this.inventoryService.updateInventory(updateData).subscribe({
        next: () => {
          this.closeModal();
          this.loadInventories();
          this.isSaving = false;
        },
        error: (err) => {
          console.error('Error al actualizar', err);
          this.isSaving = false;
        }
      });
    } else {
      const newData: RegisterInv = { ...formData };
      this.inventoryService.addInventory(newData).subscribe({
        next: () => {
          this.closeModal();
          this.loadInventories();
          this.isSaving = false;
        },
        error: (err) => {
          console.error('Error creando', err);
          this.isSaving = false;
        }
      });
    }
  }
  deleteInventory(inventory: ReferenceInv): void {
    const confirmDelete = confirm(`¿Estás seguro de eliminar el inventario: ${inventory.nameProduct}?`);
    if (confirmDelete && inventory.id) {
      this.inventoryService.deleteInventory(inventory.id).subscribe({
        next: () => {
          this.loadInventories();
        },
        error: (err) => {
          console.error(`Error eliminando: ${err}`)
        }
      });
    }
  }
}
