import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, GuardResult, MaybeAsync, Router, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../../features/users/services/auth-service';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate{
  constructor(private auth: AuthService, private router: Router){  }
  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): MaybeAsync<GuardResult> {
    if(!this.auth.isLoggedIn() || !this.auth.isTokenValid()){
      this.auth.logout('/home');
      return false;
    }
    const allowedRoles = route.data?.['roles'] as String[] | undefined;
    const userRole = this.auth.getUserRole();
    if(allowedRoles && (!userRole || !allowedRoles.includes(userRole))){
      console.warn('Acceso denegado: tu rol no tiene permiso para esta ruta');
      this.router.navigate(['/home']);
      return false;
    } 
    return true;
  }  
}
