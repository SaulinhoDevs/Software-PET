import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-header',
  imports: [],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header implements OnInit {
  userName = 'Saulo Melo'; // futuramente virá do serviço de auth
  userInitials = '';
  avatarColor = '';

  // Paleta de cores para o avatar
  private colors = [
    '#7c3aed',
    '#5b21b6',
    '#4f46e5',
    '#0891b2',
    '#059669',
    '#d97706',
    '#dc2626',
    '#db2777',
  ];

  ngOnInit() {
    this.userInitials = this.getInitials(this.userName);
    this.avatarColor = this.getColor(this.userName);
  }

  private getInitials(name: string): string {
    return name
      .split(' ')
      .filter((n) => n.length > 0)
      .slice(0, 2)
      .map((n) => n[0].toUpperCase())
      .join('');
  }

  private getColor(name: string): string {
    const index = name.split('').reduce((acc, c) => acc + c.charCodeAt(0), 0) % this.colors.length;
    return this.colors[index];
  }
}
