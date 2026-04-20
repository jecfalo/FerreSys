import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';

import { OrderService } from '../../services/order-service';
import { ProfileService } from '../../../users/services/profile-service';
import { CashRegisterService } from '../../services/cash-register-service';
import { ProductService } from '../../../inventory/services/product-service';
import { StorageRoomService } from '../../../inventory/services/storage-room-service';
import { InventoryService } from '../../../inventory/services/inventory-service';
import { AuthService } from '../../../users/services/auth-service';

import { RegisterOrder } from '../../models/orders/RegisterOrder';
import { Profile } from '../../../users/models/profile/profile';
import { ReferenceCashRegister } from '../../models/cash-register/referenceCashRegister';
import { DataProduct } from '../../../inventory/models/product/dataProduct';
import { ReferenceStorageRoom } from '../../../inventory/models/store/referenceSto';
import { ReferenceInv } from '../../../inventory/models/inventory/referenceInv';

@Component({
  selector: 'app-process-order',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './process-order.html',
  styleUrl: './process-order.css',
})
export class ProcessOrder implements OnInit {
  // Master data
  clientProfile: Profile | null = null;
  sellerProfile: Profile | null = null;
  cashRegisters: ReferenceCashRegister[] = [];

  // Order Details Shopping Cart
  cart: { product: DataProduct, storage: ReferenceStorageRoom, quantity: number, subtotal: number }[] = [];
  
  // Catalogs
  products: DataProduct[] = [];
  storages: ReferenceStorageRoom[] = [];
  inventories: ReferenceInv[] = [];

  // Forms
  masterForm!: FormGroup;
  detailForm!: FormGroup;

  // UI state
  clientNotFound: boolean = false;
  isProcessing: boolean = false;
  totalCalculated: number = 0;

  @ViewChild('barcodeInput') barcodeInput!: ElementRef;

  constructor(
    private fb: FormBuilder,
    private orderService: OrderService,
    private profileService: ProfileService,
    private cashRegisterService: CashRegisterService,
    private productService: ProductService,
    private storageService: StorageRoomService,
    private inventoryService: InventoryService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.masterForm = this.fb.group({
      document: ['', Validators.required],
      isConsumidorFinal: [false],
      cashRegisterId: [null, Validators.required]
    });

    this.detailForm = this.fb.group({
      barcodeOrName: ['', Validators.required],
      quantity: [1, [Validators.required, Validators.min(1)]]
    });

    this.loadInitialData();
    
    // Checkbox Consumidor final logic
    this.masterForm.get('isConsumidorFinal')?.valueChanges.subscribe(val => {
      if(val){
         this.masterForm.get('document')?.setValue('Consumidor Final');
         this.masterForm.get('document')?.disable();
         this.clientProfile = { id: 1, document: '9999999999', names: 'Consumidor', surnames: 'Final', email: '', typeUser: 'CLIENT', createdAt: null, updateAt: null, active: true };
         this.clientNotFound = false;
      } else {
         this.masterForm.get('document')?.setValue('');
         this.masterForm.get('document')?.enable();
         this.clientProfile = null;
      }
    });

  }

  loadInitialData() {
    // Load Seller using AuthService
    const loggedUsername = this.authService.getUsername();
    if(loggedUsername) {
       this.profileService.getProfileByUsername(loggedUsername).subscribe({
          next: (prof) => this.sellerProfile = prof,
          error: (err) => console.warn("No se pudo cargar el Perfil del Vendedor Autenticado")
       });
    }
    
    // Load Catalogs
    this.cashRegisterService.getCashRegisters().subscribe(res => this.cashRegisters = res);
    this.productService.getProducts().subscribe(res => this.products = res);
    this.storageService.getStorages().subscribe(res => this.storages = res);
    this.inventoryService.getInventories().subscribe(res => this.inventories = res);
  }

  searchClient() {
    const doc = this.masterForm.get('document')?.value;
    if(!doc) return;
    this.profileService.getProfileByDocument(doc).subscribe({
      next: (profile) => {
        this.clientProfile = profile;
        this.clientNotFound = false;
      },
      error: () => {
        this.clientProfile = null;
        this.clientNotFound = true;
      }
    });
  }

  addProductToCart() {
    if(this.detailForm.invalid) {
      this.detailForm.markAllAsTouched();
      return;
    }
    const searchVal = this.detailForm.get('barcodeOrName')?.value?.toString().toLowerCase().trim();
    const qty = this.detailForm.get('quantity')?.value;

    // Busqueda flexible por Nombre O por SKU (Codigo de Barras)
    const product = this.products.find(p => p.SKU.toLowerCase() === searchVal || p.name.toLowerCase().includes(searchVal));
    if(!product) {
      alert("Producto no encontrado por SKU ni Nombre. Intente buscar de nuevo.");
      return;
    }

    // Calcular cantidad requerida total
    const existingIndex = this.cart.findIndex(c => c.product.id === product.id);
    const existingQty = existingIndex > -1 ? this.cart[existingIndex].quantity : 0;
    const requiredTotalQty = existingQty + qty;

    // Buscar inventario asociado y validar stock
    const inventory = this.inventories.find(i => i.nameProduct === product.name);
    if (!inventory) {
      alert("Este producto no posee inventario asociado. No se puede extraer stock.");
      return;
    }
    
    // El stock real disponible
    const currentAvailable = inventory.physQuantity - inventory.amountReserved;
    if (currentAvailable < requiredTotalQty) {
      alert(`No hay stock suficiente para este producto. Stock actualmente disponible: ${currentAvailable}`);
      return;
    }

    // Extraer bodega vinculada
    const storage = this.storages.find(s => s.name === inventory.storageName);
    if (!storage) {
       alert("Error de integridad: La bodega asociada a este inventario no está activa o no existe.");
       return;
    }

    // Verificar si el bloque (producto + bodega) ya existe en el carrito local
    if(existingIndex > -1) {
       this.cart[existingIndex].quantity += qty;
       this.cart[existingIndex].subtotal = this.cart[existingIndex].quantity * product.price;
    } else {
       this.cart.push({
         product,
         storage,
         quantity: qty,
         subtotal: product.price * qty
       });
    }
    this.calculateTotal();
    
    // Resetear form detalle y focus para pistoleo rápido de cajero
    this.detailForm.get('barcodeOrName')?.setValue('');
    this.detailForm.get('quantity')?.setValue(1);
    
    setTimeout(() => {
        if(this.barcodeInput) {
            this.barcodeInput.nativeElement.focus();
        }
    }, 50);
  }

  removeCartItem(index: number) {
    this.cart.splice(index, 1);
    this.calculateTotal();
  }

  calculateTotal() {
    this.totalCalculated = this.cart.reduce((acc, curr) => acc + curr.subtotal, 0);
  }

  processOrder() {
    if(this.masterForm.invalid || !this.clientProfile || this.cart.length === 0) {
      alert("Compruebe los datos del Cliente, seleccione la Caja y agregue algún producto.");
      this.masterForm.markAllAsTouched();
      return;
    }
    
    this.isProcessing = true;
    const registerPayload: RegisterOrder = {
       clientId: this.clientProfile.id!,
       sellerId: this.sellerProfile?.id || 0, // Fallback, aunque el Back end confíe en la Auth
       cashRegisterId: this.masterForm.get('cashRegisterId')?.value,
       details: this.cart.map(c => ({
          productId: c.product.id,
          storageId: c.storage.id,
          quantity: c.quantity
       }))
    };

    this.orderService.addOrder(registerPayload).subscribe({
       next: (res) => {
         alert("¡Venta procesada exitosamente! Orden N°" + res.id);
         this.cart = [];
         this.calculateTotal();
         this.masterForm.patchValue({document: '', isConsumidorFinal: false});
         this.clientProfile = null;
         this.isProcessing = false;
       },
       error: (err) => {
         console.error(err);
         alert("Ocurrió un error al procesar el pago o la reserva de stock.");
         this.isProcessing = false;
       }
    });

  }
}
