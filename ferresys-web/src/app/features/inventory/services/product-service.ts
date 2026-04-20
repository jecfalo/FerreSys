import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { DataProduct } from '../models/product/dataProduct';
import { RegisterProd } from '../models/product/registerProduct';
import { ReferenceProd } from '../models/product/referenceProd';
import { UpdateProd } from '../models/product/updateProd';
import { PageResponse } from '../../../shared/models/pageResponse';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  private apiUrl = "http://localhost:8080/api/products"
  constructor(private http:HttpClient){}
  getProducts():Observable<DataProduct[]>{
    //obtener el page que envia el backend
    return this.http.get<PageResponse<DataProduct>>(this.apiUrl).pipe(
      map(response => response.content)
    );
  }
  addProduct(prod: RegisterProd): Observable<ReferenceProd>{
    return this.http.post<ReferenceProd>(this.apiUrl, prod);
  }
  updateProduct(prod: UpdateProd): Observable<ReferenceProd>{
    const urlUpd = `${this.apiUrl}/${prod.id}`;
    return this.http.put<ReferenceProd>(urlUpd, prod);
  }
  deleteProduct(id: number):Observable<void>{
    const urlDelete = `${this.apiUrl}/${id}`;
    return this.http.delete<void>(urlDelete);
  }
}
