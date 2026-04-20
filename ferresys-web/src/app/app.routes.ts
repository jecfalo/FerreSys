import { Routes } from '@angular/router';
import { LoginUser } from './features/users/pages/login-user/login-user';
import { RegisterUser } from './features/users/pages/register-user/register-user';
import { Home } from './features/landing/home/home';
import { CrudProducts } from './features/inventory/pages/crud-products/crud-products';
import { CrudStorageRoom } from './features/inventory/pages/crud-storage-room/crud-storage-room';
import { CrudIventory } from './features/inventory/pages/crud-iventory/crud-iventory';
import { ProcessOrder } from './features/sales/pages/process-order/process-order';
import { AuthGuard } from './core/guards/auth-guard';

export const routes: Routes = [
    { path: '', redirectTo: 'home', pathMatch: 'full' },
    { path: 'home', component: Home },
    { path: 'login-user', component: LoginUser },
    { path: 'register-user', component: RegisterUser },
    { path: 'crud-products', component: CrudProducts, canActivate: [AuthGuard], data: { roles: ['SELLER', 'ADMIN'] } },
    { path: 'crud-storage-room', component: CrudStorageRoom, canActivate: [AuthGuard], data: { roles: ['ADMIN'] } },
    { path: 'crud-inventory', component: CrudIventory, canActivate: [AuthGuard], data: { roles: ['SELLER', 'ADMIN'] } },
    { path: 'process-order', component: ProcessOrder, canActivate: [AuthGuard], data: { roles: ['SELLER', 'ADMIN'] } },
    { path: '**', redirectTo: 'home' }
];
