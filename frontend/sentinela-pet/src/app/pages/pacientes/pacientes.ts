import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

interface Paciente {
  id: number;
  nome: string;
  cns: string;
  cpf: string;
  countFaltas: number;
}

@Component({
  selector: 'app-pacientes',
  imports: [CommonModule, FormsModule],
  templateUrl: './pacientes.html',
  styleUrl: './pacientes.css',
})
export class Pacientes {
  termoPesquisa: string = '';

  pacientes: Paciente[] = [
    {
      id: 1,
      nome: 'Maria da Silva',
      cns: '123456789012345',
      cpf: '12345678900',
      countFaltas: 3,
    },
    {
      id: 2,
      nome: 'João Pereira',
      cns: '987654321098765',
      cpf: '98765432100',
      countFaltas: 1,
    },
    {
      id: 3,
      nome: 'Carlos Souza',
      cns: '456789123456789',
      cpf: '45678912300',
      countFaltas: 5,
    },
  ];

  constructor(private router: Router) {}

  get pacientesFiltrados(): Paciente[] {
    const termo = this.termoPesquisa.toLowerCase().trim();

    if (!termo) {
      return this.pacientes;
    }

    return this.pacientes.filter(
      (paciente) =>
        paciente.nome.toLowerCase().includes(termo) ||
        paciente.cns.includes(termo) ||
        paciente.cpf.includes(termo),
    );
  }

  cadastrarNovoPaciente(): void {
    this.router.navigate(['/pacientes/novo']);
  }

  verDetalhes(id: number): void {
    this.router.navigate(['/pacientes', id]);
  }

  editarPaciente(id: number): void {
    this.router.navigate(['/pacientes', id, 'editar']);
  }

  formatarCpf(cpf: string): string {
    if (!cpf) return '';

    return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
  }
}
