import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  imports: [RouterModule, CommonModule],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css',
})
export class Sidebar {
  constructor(private router: Router) {}

  menuItems = [
    { label: 'Início', route: '/inicio', icon: 'home' },
  ];

  logout() {
    const confirmado = confirm('Deseja realmente sair?');
    if (confirmado) {
      localStorage.removeItem('token');
      this.router.navigate(['/login']);
    }
  }
}
