import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { RouterModule } from '@angular/router';
import { NavbarComponent } from '../navbar/navbar.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  standalone: true, // this means that this component is not part of any module
  imports: [RouterOutlet, RouterModule, NavbarComponent]
})
export class AppComponent {
  title = 'user-management-app';
}