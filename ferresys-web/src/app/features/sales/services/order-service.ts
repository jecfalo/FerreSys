import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { DataListOrder } from '../models/orders/DataListOrder';
import { PageResponse } from '../../../shared/models/pageResponse';
import { map, Observable } from 'rxjs';
import { RegisterOrder } from '../models/orders/RegisterOrder';
import { ReferenceOrder } from '../models/orders/ReferenceOrder';
import { UpdateOrder } from '../models/orders/UpdateOrder';
import { environment } from '../../../../environments/environment.development';

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  private apiUrl = environment.apiUrl+"/orders";
  constructor(private http: HttpClient) { }
  getOrders(): Observable<DataListOrder[]> {
    //obtener el page que envia el backend
    return this.http.get<PageResponse<DataListOrder>>(`${this.apiUrl}/list-orders`).pipe(
      map(response => response.content)
    );
  }
  getOrderById(id: number): Observable<ReferenceOrder> {
    const urlGet = `${this.apiUrl}/${id}`;
    return this.http.get<ReferenceOrder>(urlGet);
  }
  addOrder(prod: RegisterOrder): Observable<ReferenceOrder> {
    return this.http.post<ReferenceOrder>(`${this.apiUrl}/process-sale`, prod);
  }
  updateOrder(prod: UpdateOrder): Observable<ReferenceOrder> {
    const urlUpd = `${this.apiUrl}/${prod.id}`;
    return this.http.put<ReferenceOrder>(urlUpd, prod);
  }
  deleteOrder(id: number): Observable<void> {
    const urlDelete = `${this.apiUrl}/${id}`;
    return this.http.delete<void>(urlDelete);
  }
}
