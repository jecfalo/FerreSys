import { Component, ElementRef, ViewChild } from '@angular/core';
import { ReferenceStorageRoom } from '../../models/store/referenceSto';
import { StorageRoomService } from '../../services/storage-room-service';
import * as bootstrap from 'bootstrap';
import { UpdateStorageRoome } from '../../models/store/updateSto';
import { RegisterSto } from '../../models/store/registerStore';
import { ModelForms } from '../../../../shared/models/modelForms';
import { ModelTable } from '../../../../shared/models/modelTable';
import { Validators } from '@angular/forms';
import { Forms } from '../../../../shared/components/forms/forms';
import { Tables } from '../../../../shared/components/tables/tables';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-crud-storage-room',
  imports: [Forms, Tables, CommonModule],
  templateUrl: './crud-storage-room.html',
  styleUrl: './crud-storage-room.css',
})
export class CrudStorageRoom {
  storages  : ReferenceStorageRoom[] = [];
  isEditing: number | null = null;
  copyStorages: any = null;
  isSaving: boolean = false;
  //referencia al modal
  modalRef: any;
  @ViewChild('storagesModal') modalElement!: ElementRef;
  columnTables: ModelTable[]=[
    {header: 'Nombre de bodega', key: 'name', type: 'text'},
    {header: 'Ubicación', key: 'location', type: 'text'}
  ];
  formStorages: ModelForms[]=[
    { name: 'name', label:'Nombre de la bodega', type: 'text', validators: [Validators.required, Validators.minLength(3)]},
    {name: 'location', label: 'Ubicación o estante', type: 'text', validators: [Validators.required, Validators.minLength(3)]}
  ];
    constructor(private service: StorageRoomService) { }
    ngOnInit(): void {
      this.loadstorages();
    }
    ngAfterViewInit(): void {
      if(this.modalElement){
        this.modalRef = new bootstrap.Modal(this.modalElement.nativeElement);
      }
    }
    loadstorages(): void {
      this.service.getStorages().subscribe({
        next: (data: ReferenceStorageRoom[]) => {
          this.storages = data;
        },
        error: (err) => console.error('Error al cargar el storage o', err)
      })
    }
    //logica del modal
    openNew(): void {
      this.isEditing = null;
      this.copyStorages = null;
      //desbloqueamos todos los campos para crear uno nuevo
      this.formStorages.forEach(field => field.readonly = false);
      this.modalRef?.show();
    }
    openEdit(storage: ReferenceStorageRoom): void {
      /*
      ESTO SE HACE YA QUE NO INSERTAMOS EL ID EN EL FORMS
      */
      //buscamos el storage o de nuestra lista cargada
      const original = this.storages.find(p => p.name === storage.name);
      //se recupera el id que vido del backend
      this.isEditing = original ? storage.id : null;
      //pasamos el storage  o completo
      this.copyStorages = original ? original: storage;
      //bloqueamos el SKU y name porque no se puede bloquear
      this.formStorages.forEach(field =>{
        if(field.name === 'name'){
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
    saveStorage (formData: any): void {
      this.isSaving = true; //activar el spinner
      if (this.isEditing) {
        //flujo de actualizacion
        const updateData: UpdateStorageRoome = { ...formData, id: this.isEditing };
        this.service.updateStorage(updateData).subscribe({
          next: () => {
            this.closeModal();
            this.loadstorages();
            this.isSaving = false;
          },
          error: (err) => {
            console.error('Error al actualizar', err);
            this.isSaving = false;
          }
        });
      } else {
        //flujo de creacion
        const newData: RegisterSto = { ...formData };
        this.service.addStorage(newData).subscribe({
          next: () => {
            this.closeModal();
            this.loadstorages();
            this.isSaving = false;
          },
          error: (err) => {
            console.error('Error creando', err);
            this.isSaving = false;
          }
        });
      }
    }
    deleteStorage(storage: ReferenceStorageRoom): void {
      const confirmDelete = confirm(`¿Estás seguro de eliminar la bodega: ${storage .name}?`);
      if (confirmDelete && storage  .id) {
        this.service.deleteStorage(storage.id).subscribe({
          next: () => {
            this.loadstorages();
          },
          error: (err) => {
            console.error(`Error eliminando: ${err}`)
          }
        });
      }
    }
  
}
