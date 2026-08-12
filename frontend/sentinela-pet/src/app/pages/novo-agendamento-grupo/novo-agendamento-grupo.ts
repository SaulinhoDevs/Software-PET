import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PacientePayload, PacienteService } from '../../services/paciente/paciente-service';
import {
  ProfissionalPayload,
  ProfissionalService,
} from '../../services/profissional/profissional-service';
import {
  CriarGrupoPayload,
  GrupoService,
  GrupoTerapeuticoDTO,
  NovaSessaoPayload,
  StandardError,
  ValidationError,
} from '../../services/grupo-service';

type Modo = 'NOVO_GRUPO' | 'GRUPO_EXISTENTE';

const RECORRENCIAS = [
  { valor: 'UNICA', label: 'Única (não se repete)' },
  { valor: 'SEMANAL', label: 'Semanal' },
  { valor: 'QUINZENAL', label: 'Quinzenal' },
  { valor: 'MENSAL', label: 'Mensal' },
];

const DELAY_NAVEGACAO_APOS_SUCESSO_MS = 1500;

@Component({
  selector: 'app-novo-agendamento-grupo',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './novo-agendamento-grupo.html',
  styleUrl: './novo-agendamento-grupo.css',
})
export class NovoAgendamentoGrupo implements OnInit {
  modo: Modo = 'NOVO_GRUPO';
  recorrencias = RECORRENCIAS;

  // Comuns
  coordenadores: ProfissionalPayload[] = [];
  carregandoCoordenadores = false;

  // Modo: novo grupo
  tema = '';
  coordenadorId: string | null = null;
  recorrencia: string | null = null;

  // Modo: grupo existente
  grupos: GrupoTerapeuticoDTO[] = [];
  carregandoGrupos = false;
  grupoSelecionadoId: number | null = null;
  grupoSelecionado: GrupoTerapeuticoDTO | null = null;
  carregandoSugestaoData = false;

  // Comum aos dois modos
  dataSessao = '';
  horario = '';

  // Participantes (sempre escolhidos do zero, nunca copiados)
  termoPesquisaPaciente = '';
  resultadosPacientes: PacientePayload[] = [];
  buscandoPaciente = false;
  participantesSelecionados: PacientePayload[] = [];

  salvando = false;
  erroGeral: string | null = null;
  mensagemSucesso: string | null = null;
  errosPorCampo: Record<string, string> = {};

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private pacienteService: PacienteService,
    private profissionalService: ProfissionalService,
    private grupoService: GrupoService,
  ) {}

  ngOnInit(): void {
    this.carregarCoordenadores();
    this.carregarGrupos();

    const grupoIdParam = this.route.snapshot.queryParamMap.get('grupoId');
    if (grupoIdParam) {
      this.modo = 'GRUPO_EXISTENTE';
      this.grupoSelecionadoId = Number(grupoIdParam);
      // A pré-seleção real acontece assim que carregarGrupos() terminar.
    }
  }

  carregarCoordenadores(): void {
    this.carregandoCoordenadores = true;

    this.profissionalService.listar().subscribe({
      next: (profissionais) => {
        this.coordenadores = profissionais.filter(
          (p) => p.tipoUsuario === 'PROFISSIONAL' || p.tipoUsuario === 'ADMINISTRADOR',
        );
        this.carregandoCoordenadores = false;
      },
      error: (erro) => {
        console.error('Erro ao carregar coordenadores', erro);
        this.erroGeral = 'Não foi possível carregar a lista de coordenadores.';
        this.carregandoCoordenadores = false;
      },
    });
  }

  carregarGrupos(): void {
    this.carregandoGrupos = true;

    this.grupoService.listarGrupos().subscribe({
      next: (grupos) => {
        this.grupos = grupos;
        this.carregandoGrupos = false;

        if (this.grupoSelecionadoId) {
          const grupo = this.grupos.find((g) => g.id === this.grupoSelecionadoId);
          if (grupo) {
            this.selecionarGrupo(grupo);
          }
        }
      },
      error: (erro) => {
        console.error('Erro ao carregar grupos', erro);
        this.erroGeral = 'Não foi possível carregar a lista de grupos.';
        this.carregandoGrupos = false;
      },
    });
  }

  trocarModo(novoModo: Modo): void {
    this.modo = novoModo;
    this.erroGeral = null;
    this.mensagemSucesso = null;
    this.errosPorCampo = {};
    this.limparDataHorario();

    if (novoModo === 'NOVO_GRUPO') {
      this.grupoSelecionadoId = null;
      this.grupoSelecionado = null;
    }
  }

  onGrupoSelecionado(): void {
    const grupo = this.grupos.find((g) => g.id === this.grupoSelecionadoId) ?? null;
    if (grupo) {
      this.selecionarGrupo(grupo);
    }
  }

  private selecionarGrupo(grupo: GrupoTerapeuticoDTO): void {
    this.grupoSelecionado = grupo;
    this.grupoSelecionadoId = grupo.id;
    this.horario = grupo.horarioPadrao?.slice(0, 5) ?? '';

    if (grupo.recorrencia === 'UNICA') {
      this.dataSessao = '';
      return;
    }

    this.carregandoSugestaoData = true;
    this.grupoService.sugerirProximaData(grupo.id).subscribe({
      next: (data) => {
        this.dataSessao = data;
        this.carregandoSugestaoData = false;
      },
      error: (erro) => {
        console.error('Erro ao sugerir próxima data', erro);
        this.carregandoSugestaoData = false;
      },
    });
  }

  private limparDataHorario(): void {
    this.dataSessao = '';
    this.horario = '';
  }

  pesquisarPaciente(): void {
    const termo = this.termoPesquisaPaciente.trim();

    if (termo.length < 2) {
      this.resultadosPacientes = [];
      return;
    }

    this.buscandoPaciente = true;

    this.pacienteService.buscarPorNome(termo).subscribe({
      next: (pacientes) => {
        const idsJaSelecionados = new Set(
          this.participantesSelecionados.map((p) => p.idPublico),
        );
        this.resultadosPacientes = pacientes.filter(
          (p) => !idsJaSelecionados.has(p.idPublico),
        );
        this.buscandoPaciente = false;
      },
      error: () => {
        this.resultadosPacientes = [];
        this.buscandoPaciente = false;
      },
    });
  }

  adicionarParticipante(paciente: PacientePayload): void {
    this.participantesSelecionados.push(paciente);
    this.resultadosPacientes = this.resultadosPacientes.filter(
      (p) => p.idPublico !== paciente.idPublico,
    );
    this.termoPesquisaPaciente = '';
  }

  removerParticipante(paciente: PacientePayload): void {
    this.participantesSelecionados = this.participantesSelecionados.filter(
      (p) => p.idPublico !== paciente.idPublico,
    );
  }

  get podeConfirmar(): boolean {
    if (this.salvando) return false;
    if (!this.dataSessao || !this.horario) return false;

    if (this.modo === 'NOVO_GRUPO') {
      return !!(this.tema.trim() && this.coordenadorId && this.recorrencia);
    }

    return !!this.grupoSelecionadoId;
  }

  confirmar(): void {
    if (!this.podeConfirmar) return;

    this.erroGeral = null;
    this.mensagemSucesso = null;
    this.errosPorCampo = {};
    this.salvando = true;

    const participantesIds = this.participantesSelecionados
      .map((p) => p.idPublico)
      .filter((id): id is string => !!id);

    if (this.modo === 'NOVO_GRUPO') {
      const payload: CriarGrupoPayload = {
        tema: this.tema.trim(),
        coordenadorId: this.coordenadorId!,
        recorrencia: this.recorrencia!,
        dataPrimeiraSessao: this.dataSessao,
        horario: this.horario,
        participantesIds,
      };

      this.grupoService.criarGrupo(payload).subscribe({
        next: () => this.tratarSucesso('Grupo criado com sucesso!'),
        error: (erro: HttpErrorResponse) => {
          this.salvando = false;
          this.tratarErro(erro);
        },
      });
      return;
    }

    const payload: NovaSessaoPayload = {
      grupoId: this.grupoSelecionadoId!,
      dataSessao: this.dataSessao,
      horario: this.horario,
      participantesIds,
    };

    this.grupoService.criarProximaSessao(payload).subscribe({
      next: () => this.tratarSucesso('Sessão agendada com sucesso!'),
      error: (erro: HttpErrorResponse) => {
        this.salvando = false;
        this.tratarErro(erro);
      },
    });
  }

  private tratarSucesso(mensagem: string): void {
    this.salvando = false;
    this.mensagemSucesso = mensagem;
    this.resetarFormulario();

    setTimeout(() => {
      this.router.navigate(['/grupos-terapeuticos']);
    }, DELAY_NAVEGACAO_APOS_SUCESSO_MS);
  }

  private resetarFormulario(): void {
    this.tema = '';
    this.coordenadorId = null;
    this.recorrencia = null;
    this.dataSessao = '';
    this.horario = '';
    this.grupoSelecionadoId = null;
    this.grupoSelecionado = null;
    this.participantesSelecionados = [];
    this.termoPesquisaPaciente = '';
    this.resultadosPacientes = [];
  }

  private tratarErro(erro: HttpErrorResponse): void {
    if (!erro.error) {
      this.erroGeral = 'Não foi possível conectar ao servidor. Tente novamente.';
      return;
    }

    if (erro.status === 422 && Array.isArray(erro.error.errors)) {
      const validationError = erro.error as ValidationError;
      validationError.errors.forEach((campo) => {
        this.errosPorCampo[campo.fieldName] = campo.message;
      });
      this.erroGeral = validationError.message;
      return;
    }

    if (erro.error.message) {
      const standardError = erro.error as StandardError;
      this.erroGeral = standardError.message;
      return;
    }

    this.erroGeral = 'Não foi possível salvar. Tente novamente.';
  }

  cancelar(): void {
    this.router.navigate(['/grupos-terapeuticos']);
  }

  labelEnum(valor: string | undefined): string {
    if (!valor) return '-';
    return valor
      .replaceAll('_', ' ')
      .toLowerCase()
      .replace(/\b\w/g, (letra) => letra.toUpperCase());
  }

  formatarCpf(cpf: string | undefined): string {
    if (!cpf) return '-';
    const numeros = cpf.replace(/\D/g, '');
    return numeros.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
  }
}
