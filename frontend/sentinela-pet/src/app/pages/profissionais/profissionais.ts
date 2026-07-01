import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

enum TipoUsuario {
  ADMINISTRADOR = 1,
  PROFISSIONAL = 2,
  RECEPCAO = 3,
}

interface Profissional {
  id: number;
  nome: string;
  tipoUsuario: TipoUsuario;
}

@Component({
  selector: 'app-profissionais',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './profissionais.html',
  styleUrl: './profissionais.css',
})
export class Profissionais {
  termoPesquisa = '';
  tipoUsuarioSelecionado: number | '' = '';

  tipoUsuarioOptions = Object.values(TipoUsuario).filter(
    (value) => typeof value === 'number',
  ) as number[];

  profissionais: Profissional[] = [
    {
      id: 1,
      nome: 'João Pereira',
      tipoUsuario: TipoUsuario.ADMINISTRADOR,
    },
    {
      id: 2,
      nome: 'Maria Oliveira',
      tipoUsuario: TipoUsuario.PROFISSIONAL,
    },
    {
      id: 3,
      nome: 'Carlos Souza',
      tipoUsuario: TipoUsuario.RECEPCAO,
    },
    {
      id: 4,
      nome: 'Ana Santos',
      tipoUsuario: TipoUsuario.PROFISSIONAL,
    },
  ];

  constructor(private router: Router) {}

  get profissionaisFiltrados(): Profissional[] {
    return this.profissionais.filter((profissional) => {
      const nomeOk = profissional.nome.toLowerCase().includes(this.termoPesquisa.toLowerCase());

      const tipoOk =
        this.tipoUsuarioSelecionado === '' ||
        profissional.tipoUsuario === this.tipoUsuarioSelecionado;

      return nomeOk && tipoOk;
    });
  }

  cadastrarNovoProfissional(): void {
    this.router.navigate(['/profissionais/novo']);
  }

  verDetalhes(id: number): void {
    this.router.navigate(['/profissionais', id]);
  }

  editarProfissional(id: number): void {
    this.router.navigate(['/profissionais', id, 'editar']);
  }

  labelTipoUsuario(tipo: number): string {
    switch (tipo) {
      case TipoUsuario.ADMINISTRADOR:
        return 'Administrador';

      case TipoUsuario.PROFISSIONAL:
        return 'Profissional';

      case TipoUsuario.RECEPCAO:
        return 'Recepção';

      default:
        return '';
    }
  }
}
