import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { RegisterUser } from '../models/users/dto/registerUser';
import { ReferenceUser } from '../models/users/dto/referenceUser';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private apiUrl = "http://localhost:8080/api/users";
  constructor(private http:HttpClient){}
  addUser(user: RegisterUser):Observable<ReferenceUser>{
    return this.http.post<ReferenceUser>(this.apiUrl, user);
  }
}
