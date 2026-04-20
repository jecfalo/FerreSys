import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ReferenceCashRegister } from '../models/cash-register/referenceCashRegister';

@Injectable({
  providedIn: 'root',
})
export class CashRegisterService {
  private apiUrl = "http://localhost:8080/api/cash-registers";

  constructor(private http: HttpClient) { }

  getCashRegisters(): Observable<ReferenceCashRegister[]> {
    return this.http.get<ReferenceCashRegister[]>(this.apiUrl);
  }
}
