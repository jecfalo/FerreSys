import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ReferenceCashRegister } from '../models/cash-register/referenceCashRegister';
import { environment } from '../../../../environments/environment.development';

@Injectable({
  providedIn: 'root',
})
export class CashRegisterService {
  private apiUrl = environment.apiUrl+"/cash-registers";

  constructor(private http: HttpClient) { }

  getCashRegisters(): Observable<ReferenceCashRegister[]> {
    return this.http.get<ReferenceCashRegister[]>(this.apiUrl);
  }
}
