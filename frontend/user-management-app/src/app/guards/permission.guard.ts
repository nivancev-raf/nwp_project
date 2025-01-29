// guards/permission.guard.ts
import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class PermissionGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  async canActivate(route: ActivatedRouteSnapshot): Promise<boolean> {
    const requiredPermission = route.data['requiredPermission']; // requiredPermission je ključ u data objektu rute
    console.log('requiredPermission', requiredPermission);

    try {

      const userId = this.authService.getCurrentUserId();
      if (!userId) {
        return false;
      }
      const permissions = await this.authService.getUserPermissions(userId);
      
      const hasPermission = permissions.includes(requiredPermission);

      if (!hasPermission) {
        alert('Nemate dozvolu za pristup ovoj stranici');
        return false;
      }

      return true;
    } catch (error) {
      console.error('Error checking permissions:', error);
      return false;
    }
  }
}