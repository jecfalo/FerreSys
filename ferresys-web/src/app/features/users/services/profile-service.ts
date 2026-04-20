import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Profile } from '../models/profile/profile';

@Injectable({
  providedIn: 'root',
})
export class ProfileService {
  private apiUrl = "http://localhost:8080/api/profiles";

  constructor(private http: HttpClient) { }

  // Buscar un perfil (cliente) por su documento de identidad
  getProfileByDocument(document: string): Observable<Profile> {
    return this.http.get<Profile>(`${this.apiUrl}/document/${document}`);
  }

  // Obtener al vendedor logueado a traves del token/username
  getProfileByUsername(username: string): Observable<Profile> {
    return this.http.get<Profile>(`${this.apiUrl}/username/${username}`);
  }
}
