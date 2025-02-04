// app.routes.ts
import { Routes } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from './services/auth.service';
import { Router } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar.component';
import { PermissionGuard } from './guards/permission.guard';

export const routes: Routes = [
    {
        path: '',
        canActivate: [() => {
          const authService = inject(AuthService);
          const router = inject(Router);
          
          
          if (!authService.tokenExists()) {
            console.log('Token ne postoji');
            router.navigate(['/login']);
            return false;
          }
          
          router.navigate(['/welcome']);
          return true;
        }],
        component: NavbarComponent  // Dodajemo komponentu da izbegnemo grešku o invalid configuration
      },
      {
        path: 'welcome',
        loadComponent: () => import('./components/welcome/welcome.component').then(m => m.WelcomeComponent),
      },
  {
    path: 'login',
    loadComponent: () => import('./components/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'users',
    loadComponent: () => import('./components/user-list/user-list.component').then(m => m.UserListComponent),
    canActivate: [PermissionGuard],
    data: { requiredPermission: 'can_read_users' }
  },
  {
    path: 'users/create',
    loadComponent: () => import('./components/user-create/user-create.component').then(m => m.UserCreateComponent),
    canActivate: [PermissionGuard],
    data: { requiredPermission: 'can_create_users' }
  },
  {
    path: 'users/edit/:id',
    loadComponent: () => import('./components/user-edit/user-edit.component').then(m => m.UserEditComponent),
    canActivate: [PermissionGuard],
    data: { requiredPermission: 'can_update_users' }
  },
  {
    path: 'orders',
    loadComponent: () => import('./components/order-list/order-list.component').then(m => m.OrderListComponent),
    canActivate: [PermissionGuard],
    data: { requiredPermission: 'can_search_order' }
  },
  {
    path: 'orders/create',
    loadComponent: () => import('./components/order-create/order-create.component').then(m => m.OrderCreateComponent),
    canActivate: [PermissionGuard],
    data: { requiredPermission: 'can_place_order' }
  },
  {
    path: 'orders/:id',
    loadComponent: () => import('./components/order-details/order-details.component').then(m => m.OrderDetailsComponent),
    canActivate: [PermissionGuard],
    data: { requiredPermission: 'can_track_order' }
  },
  {
    path: 'errors',
    loadComponent: () => import('./components/error-history/error-history.component').then(m => m.ErrorHistoryComponent),
  },
  {
    path: 'schedule',
    loadComponent: () => import('./components/order-schedule/order-schedule.component').then(m => m.OrderScheduleComponent),
    canActivate: [PermissionGuard],
    data: { requiredPermission: 'can_schedule_order' }
  }
];