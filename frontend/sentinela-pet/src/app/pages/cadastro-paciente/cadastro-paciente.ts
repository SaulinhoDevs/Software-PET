import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

import { UnidadeSaude, UnidadeSaudeService } from '../../services/unidade-saude-service';

import {
  PacientePayload,
  PacienteService,
  ValidationError,
  StandardError,
} from '../../services/paciente/paciente-service';

enum SexoEnum {
  MASCULINO = 'MASCULINO',
  FEMININO = 'FEMININO',
  INTERSEXO = 'INTERSEXO',
  OUTRO = 'OUTRO',
  NAO_INFORMADO = 'NAO_INFORMADO',
}

enum RacaCorEnum {
  BRANCA = 'BRANCA',
  PRETA = 'PRETA',
  PARDA = 'PARDA',
  AMARELA = 'AMARELA',
  INDIGENA = 'INDIGENA',
  NAO_INFORMADO = 'NAO_INFORMADO',
}

enum TipoAcompanhamento {
  GRUPO_TERAPEUTICO = 'GRUPO_TERAPEUTICO',
  INDIVIDUAL = 'INDIVIDUAL',
  AMBOS = 'AMBOS',
}

@Component({
  selector: 'app-cadastro-paciente',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './cadastro-paciente.html',
  styleUrl: './cadastro-paciente.css',
})
export class CadastroPaciente implements OnInit {
  sexoOptions = Object.values(SexoEnum);
  racaCorOptions = Object.values(RacaCorEnum);
  tipoAcompanhamentoOptions = Object.values(TipoAcompanhamento);

  unidadesSaude: UnidadeSaude[] = [];

  carregandoUnidades = false;
  erroUnidades = false;
  salvando = false;

  // NOVO: erro geral para exibir num banner no topo do formulário
  erroGeral: string | null = null;

  // NOVO: mapa de erros por campo, vindo do backend
  errosPorCampo: Record<string, string> = {};

  pacienteForm = new FormGroup({
    nome: new FormControl('', Validators.required),
    nomeMae: new FormControl('', Validators.required),
    dataNascimento: new FormControl('', Validators.required),
    sexo: new FormControl('', Validators.required),
    racacor: new FormControl('', Validators.required),
    cns: new FormControl('', Validators.required),
    cpf: new FormControl('', Validators.required),
    telefone: new FormControl('', Validators.required),
    usfReferencia: new FormControl<UnidadeSaude | null>(null, Validators.required),
    situacaoRua: new FormControl(false, Validators.required),
    tipoAcompanhamento: new FormControl('', Validators.required),

    endereco: new FormGroup({
      cidade: new FormControl('', Validators.required),
      estado: new FormControl('', Validators.required),
      bairro: new FormControl('', Validators.required),
      logradouro: new FormControl('', Validators.required),
      numero: new FormControl('', Validators.required),
      complemento: new FormControl(''),
      cep: new FormControl('', Validators.required),
    }),
  });

  constructor(
    private router: Router,
    private unidadeSaudeService: UnidadeSaudeService,
    private pacienteService: PacienteService,
  ) {}

  ngOnInit(): void {
    this.carregarUnidadesSaude();
  }

  carregarUnidadesSaude(): void {
    this.carregandoUnidades = true;
    this.erroUnidades = false;

    this.unidadeSaudeService.listarUnidades().subscribe({
      next: (unidades) => {
        this.unidadesSaude = unidades;
        this.carregandoUnidades = false;
      },
      error: (erro) => {
        console.error(erro);
        this.erroUnidades = true;
        this.carregandoUnidades = false;
      },
    });
  }

  salvarPaciente(): void {
    // Limpa erros anteriores a cada nova tentativa
    this.erroGeral = null;
    this.errosPorCampo = {};

    if (this.pacienteForm.invalid) {
      this.pacienteForm.markAllAsTouched();
      return;
    }

    this.salvando = true;

    const formValue = this.pacienteForm.getRawValue();

    const paciente: PacientePayload = {
      nome: formValue.nome?.trim() ?? '',
      nomeMae: formValue.nomeMae?.trim() ?? '',
      dataNascimento: formValue.dataNascimento ?? '',
      sexo: formValue.sexo ?? '',
      racacor: formValue.racacor ?? '',
      cns: this.somenteNumeros(formValue.cns),
      cpf: this.somenteNumeros(formValue.cpf),
      telefone: this.somenteNumeros(formValue.telefone),
      usfReferencia: formValue.usfReferencia!,
      situacaoRua: formValue.situacaoRua ?? false,
      tipoAcompanhamento: formValue.tipoAcompanhamento ?? '',

      endereco: {
        cidade: formValue.endereco?.cidade?.trim() ?? '',
        estado: formValue.endereco?.estado?.trim().toUpperCase() ?? '',
        bairro: formValue.endereco?.bairro?.trim() ?? '',
        logradouro: formValue.endereco?.logradouro?.trim() ?? '',
        numero: formValue.endereco?.numero?.trim() ?? '',
        complemento: formValue.endereco?.complemento?.trim() ?? '',
        cep: this.somenteNumeros(formValue.endereco?.cep),
      },
    };

    this.pacienteService.cadastrarPaciente(paciente).subscribe({
      next: () => {
        this.salvando = false;
        this.router.navigate(['/pacientes']);
      },
      error: (erro: HttpErrorResponse) => {
        this.salvando = false;
        this.tratarErro(erro);
      },
    });
  }

  private tratarErro(erro: HttpErrorResponse): void {
    // Sem resposta do servidor (rede caiu, backend fora do ar, etc.)
    if (!erro.error) {
      this.erroGeral = 'Não foi possível conectar ao servidor. Tente novamente.';
      return;
    }

    // 422: erro de validação de campos (formato ValidationError)
    if (erro.status === 422 && Array.isArray(erro.error.errors)) {
      const validationError = erro.error as ValidationError;

      for (const campo of validationError.errors) {
        this.errosPorCampo[campo.fieldName] = campo.message;
      }

      this.erroGeral = validationError.message;
      this.marcarCamposComoTocados();
      return;
    }

    // 409: CPF/CNS duplicado
    if (erro.status === 409) {
      const standardError = erro.error as StandardError;
      this.erroGeral = standardError.message ?? 'Já existe um paciente com esses dados.';
      return;
    }

    // 400/404/403 e outros formatos StandardError conhecidos
    if (erro.error.message) {
      const standardError = erro.error as StandardError;
      this.erroGeral = standardError.message;
      return;
    }

    // Fallback genérico
    this.erroGeral = 'Não foi possível salvar o paciente. Tente novamente.';
  }

  private marcarCamposComoTocados(): void {
    Object.keys(this.errosPorCampo).forEach((campo) => {
      const control = this.pacienteForm.get(campo);
      control?.markAsTouched();
    });
  }

  cancelar(): void {
    this.router.navigate(['/pacientes']);
  }

  somenteNumerosCampo(campo: string, limite?: number): void {
    const control = this.pacienteForm.get(campo);
    if (!control) return;

    let valor = this.somenteNumeros(control.value);
    if (limite) {
      valor = valor.slice(0, limite);
    }
    control.setValue(valor, { emitEvent: false });
  }

  aplicarMascaraCpf(): void {
    const control = this.pacienteForm.get('cpf');
    if (!control) return;

    let valor = this.somenteNumeros(control.value).slice(0, 11);
    valor = valor.replace(/^(\d{3})(\d)/, '$1.$2');
    valor = valor.replace(/^(\d{3})\.(\d{3})(\d)/, '$1.$2.$3');
    valor = valor.replace(/^(\d{3})\.(\d{3})\.(\d{3})(\d)/, '$1.$2.$3-$4');
    control.setValue(valor, { emitEvent: false });
  }

  aplicarMascaraTelefone(): void {
    const control = this.pacienteForm.get('telefone');
    if (!control) return;

    let valor = this.somenteNumeros(control.value).slice(0, 11);
    if (valor.length <= 10) {
      valor = valor.replace(/^(\d{2})(\d)/, '($1) $2');
      valor = valor.replace(/(\d{4})(\d)/, '$1-$2');
    } else {
      valor = valor.replace(/^(\d{2})(\d)/, '($1) $2');
      valor = valor.replace(/(\d{5})(\d)/, '$1-$2');
    }
    control.setValue(valor, { emitEvent: false });
  }

  aplicarMascaraCep(): void {
    const control = this.pacienteForm.get('endereco.cep');
    if (!control) return;

    let valor = this.somenteNumeros(control.value).slice(0, 8);
    valor = valor.replace(/^(\d{5})(\d)/, '$1-$2');
    control.setValue(valor, { emitEvent: false });
  }

  campoInvalido(campo: string): boolean {
    const control = this.pacienteForm.get(campo);
    return !!control && control.invalid && (control.touched || control.dirty);
  }

  // NOVO: retorna a mensagem de erro certa para um campo —
  // prioriza o erro vindo do backend; senão cai no erro padrão do Angular
  mensagemErro(campo: string, mensagemPadrao: string): string {
    return this.errosPorCampo[campo] ?? mensagemPadrao;
  }

  labelEnum(valor: string): string {
    return valor
      .replaceAll('_', ' ')
      .toLowerCase()
      .replace(/\b\w/g, (letra) => letra.toUpperCase());
  }

  private somenteNumeros(valor: unknown): string {
    return String(valor ?? '').replace(/\D/g, '');
  }
}
