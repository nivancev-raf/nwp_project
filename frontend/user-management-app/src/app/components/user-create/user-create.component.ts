import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { UserService } from '../../services/user.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-user-create',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './user-create.component.html',
  styleUrl: './user-create.component.css'
})
export class UserCreateComponent {
  userForm: FormGroup;
  error = '';
  loading = false;

  availablePermissions = [
    'can_read_users',
    'can_create_users',
    'can_update_users',
    'can_delete_users'
  ];

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private router: Router
  ) {
    this.userForm = this.fb.group({
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]],
      permissions: [[], [Validators.required]]
    });
  }

  async onSubmit() {
    if (this.userForm.valid) {
      try {
        this.loading = true;
        this.error = '';
        await this.userService.createUser(this.userForm.value);
        this.router.navigate(['/users']);
      } catch (error: any) {
        this.error = error.error?.message || 'Error creating user';
      } finally {
        this.loading = false;
      }
    } else {
      this.markFormGroupTouched(this.userForm);
    }
  }

  private markFormGroupTouched(formGroup: FormGroup) {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched(); // mark as touched so that validation messages are shown
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }

  onPermissionChange(permission: string, event: any) {
    const permissions = this.userForm.get('permissions')?.value || [];
    
    if (event.target.checked) {
      this.userForm.get('permissions')?.setValue([...permissions, permission]);
    } else {
      this.userForm.get('permissions')?.setValue(
        permissions.filter((p: string) => p !== permission)
      );
    }
  }
}