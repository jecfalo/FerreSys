import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { ReferenceInv } from '../models/inventory/referenceInv';
import { PageResponse } from '../../../shared/models/pageResponse';
import { RegisterInv } from '../models/inventory/registerInventory';
import { UpdateInventory } from '../models/inventory/UpdateInventory';

@Injectable({
  providedIn: 'root',
})
export class InventoryService {
  private apiUrl = "http://localhost:8080/api/crud-inv";
  constructor(private http: HttpClient) { }
  getInventories(): Observable<ReferenceInv[]> {
    //obtener el page que envia el backend
    return this.http.get<PageResponse<ReferenceInv>>(`${this.apiUrl}/list-inventories`).pipe(
      map(response => response.content)
    );
  }
  getInventoryById(id: number): Observable<ReferenceInv> {
    const urlGet = `${this.apiUrl}/${id}`;
    return this.http.get<ReferenceInv>(urlGet);
  }
  addInventory(prod: RegisterInv): Observable<ReferenceInv> {
    return this.http.post<ReferenceInv>(this.apiUrl, prod);
  }
  updateInventory(prod: UpdateInventory): Observable<ReferenceInv> {
    const urlUpd = `${this.apiUrl}/${prod.id}`;
    return this.http.put<ReferenceInv>(urlUpd, prod);
  }
  deleteInventory(id: number): Observable<void> {
    const urlDelete = `${this.apiUrl}/${id}`;
    return this.http.delete<void>(urlDelete);
  }
}
