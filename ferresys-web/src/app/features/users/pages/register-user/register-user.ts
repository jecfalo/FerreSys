import { Component } from '@angular/core';
import { ModelForms } from '../../../../shared/models/modelForms';
import { Validators } from '@angular/forms';
import { UserService } from '../../services/user-service';
import { Router } from '@angular/router';
import { RegisterUser as Register } from '../../models/users/dto/registerUser';
import { Forms } from "../../../../shared/components/forms/forms";

@Component({
  selector: 'app-register-user',
  imports: [Forms],
  templateUrl: './register-user.html',
  styleUrl: './register-user.css',
})
export class RegisterUser {
  fieldsRegister: ModelForms[] =[
    {
      name: 'username',
      label: 'Nombre de usurio',
      type: 'text',
      colClass: 'col-md-6',
      validators: [Validators.required]
    },
    {
      name: 'password',
      label: 'Contraseña',
      type: 'password',
      colClass: 'col-md-6',
      validators: [Validators.required]
    },
    {
      name: 'document',
      label: 'Cedula o Pasaporte',
      type: 'text',
      colClass: 'col-md-6',
      validators: [Validators.required]
    },
    {
      name: 'names',
      label: 'Nombres',
      type: 'text',
      colClass: 'col-md-6',
      validators: [Validators.required]
    },
    {
      name: 'surnames',
      label: 'Apellidos',
      type: 'text',
      colClass: 'col-md-6',
      validators: [Validators.required]
    },
    {
      name: 'email',
      label: 'Correo electrónico',
      type: 'email',
      colClass: 'col-md-6',
      validators: [Validators.required]
    }
  ]; 
  msg:string = '';
  constructor( private service: UserService, private router: Router){}
  registerUser(data: Register){
    this.service.addUser(data).subscribe({
      next:(res)=>{
        this.router.navigate(['/login-user'])
      },
      error: (err)=>{
        if(err.status == 400 || err.status == 409){
          this.msg = 'El usuario, documento o correo ya fueron registrados.';
        }else{
          this.msg = 'Ocurrió un error al registrar el usuario.';
      }
    }
    });
  }
  goToLogin(){
    this.router.navigate(['/login-user']);
  }
}
