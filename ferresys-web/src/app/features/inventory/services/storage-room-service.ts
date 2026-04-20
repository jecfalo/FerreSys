import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { ReferenceStorageRoom } from '../models/store/referenceSto';
import { UpdateProd } from '../models/product/updateProd';
import { PageResponse } from '../../../shared/models/pageResponse';
import { RegisterSto } from '../models/store/registerStore';
import { UpdateStorageRoome } from '../models/store/updateSto';

@Injectable({
  providedIn: 'root',
})
export class StorageRoomService {
  private apiUrl = "http://localhost:8080/api/storages-room"
  constructor(private http:HttpClient){}
  getStorages():Observable<ReferenceStorageRoom[]>{
    //obtener el page que envia el backend
    return this.http.get<ReferenceStorageRoom[]>(this.apiUrl);
  }
  addStorage(sr: RegisterSto): Observable<ReferenceStorageRoom>{
    return this.http.post<ReferenceStorageRoom>(this.apiUrl, sr);
  }
  updateStorage(usr: UpdateStorageRoome): Observable<ReferenceStorageRoom>{
    const urlUpd = `${this.apiUrl}/${usr.id}`;
    return this.http.put<ReferenceStorageRoom>(urlUpd, usr);
  }
  deleteStorage(id: number):Observable<void>{
    const urlDelete = `${this.apiUrl}/${id}`;
    return this.http.delete<void>(urlDelete);
  }
}
