import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-user-edit',
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './user-edit.component.html',
  styleUrl: './user-edit.component.css'
})
export class UserEditComponent implements OnInit {
  userForm: FormGroup;
  userId!: number; // ! is used to tell TypeScript that the variable will be initialized later
  availablePermissions = [
    'can_read_users',
    'can_create_users',
    'can_update_users',
    'can_delete_users'
  ];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private userService: UserService
  ) {
    this.userForm = this.fb.group({
      firstName: [''],
      lastName: [''],
      email: ['', [Validators.email]],
      password: [''],
      permissions: [[]]
    });
  }

  async ngOnInit() {
    this.userId = +this.route.snapshot.params['id'];
    try {
      const user = await this.userService.getUserById(this.userId);
      this.userForm.patchValue({ // pathcValue is used to update the form with the user data
        firstName: user.firstName,
        lastName: user.lastName,
        email: user.email,
        password: user.password,
        permissions: user.permissions
      });
    } catch (error) {
      console.error('Error loading user:', error);
    }
  }

  async onSubmit() {
    if (this.userForm.valid) {
      try {
        await this.userService.updateUser(this.userId, this.userForm.value);
        this.router.navigate(['/users']);
      } catch (error) {
        console.error('Error updating user:', error);
      }
    }
  }

  onPermissionChange(event: any, permission: string) {
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
