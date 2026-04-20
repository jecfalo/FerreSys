import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Forms } from '../../../../shared/components/forms/forms';
import { ModelForms } from '../../../../shared/models/modelForms';
import { Validators } from '@angular/forms';
import { AuthService } from '../../services/auth-service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login-user',
  imports: [CommonModule, Forms],
  templateUrl: './login-user.html',
  styleUrl: './login-user.css',
})
export class LoginUser {
  fieldsLogin: ModelForms[] = [
    {
      name: 'username',
      label: 'Usuario',
      type: 'text',
      validators: [Validators.required]
    },
    {
      name: 'password',
      label: 'Contraseña',
      type: 'password',
      validators: [Validators.required]
    }
  ];
  errorMsg: string = '';
  constructor(private router: Router, private auth: AuthService){}
  ngOnInit():void{}
  onLogin(data:any){
    this.auth.login(data.username, data.password).subscribe({
      next: (res)=>{
        this.auth.saveToken(res.token);
        this.auth.saveRefreshToken(res.refreshToken);
        const role = this.auth.getUserRole();
        console.log("Rol detectado", role);
        if(role && role.toLowerCase()=='client'){
          this.router.navigate(['/home']);
        }
        if(role && role.toLowerCase()=='admin'){
          this.router.navigate(['/admin'])
        }
      },
      error: err =>{
        if(err.status === 0){
          this.errorMsg = "Error de conexión al servidor";
        }else{
          this.errorMsg = "Usuario o contraseña incorrectos";
        }
      }
    })
  }
  goToRegister(){
    this.router.navigate(['/register-user']);
  }
}
