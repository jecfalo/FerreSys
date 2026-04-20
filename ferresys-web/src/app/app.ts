import { Component, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterModule, RouterOutlet } from '@angular/router';
import { Forms } from './shared/components/forms/forms';
import { AuthService } from './features/users/services/auth-service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, RouterModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('ferresys-web');
  //inyectar el servicio auth
  constructor(public auth: AuthService){}
  //crear un metodo para cerrar sesion
  onLogout(){
    this.auth.logout();
  }
}
