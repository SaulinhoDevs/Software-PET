import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';

import {
  ProfissionalPayload,
  ProfissionalService,
  ValidationError,
  StandardError,
} from '../../services/profissional/profissional-service';

enum TipoUsuario {
  ADMINISTRADOR = 'ADMINISTRADOR',
  PROFISSIONAL = 'PROFISSIONAL',
  RECEPCAO = 'RECEPCAO',
}

enum UnidadeAtuacao {
  USF = 'USF',
  CAPS_AD = 'CAPS_AD',
  CAPS_I = 'CAPS_I',
  CAPS_II = 'CAPS_II',
}

@Component({
  selector: 'app-cadastro-profissional',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './cadastro-profissional.html',
  styleUrl: './cadastro-profissional.css',
})
export class CadastroProfissional {
  tipoUsuarioOptions = Object.values(TipoUsuario);
  unidadeAtuacaoOptions = Object.values(UnidadeAtuacao);

  salvando = false;

  erroGeral: string | null = null;

  errosPorCampo: Record<string, string> = {};

  profissionalForm = new FormGroup(
    {
      nome: new FormControl('', Validators.required),

      email: new FormControl('', [Validators.required, Validators.email]),

      senha: new FormControl('', [Validators.required, Validators.minLength(6)]),

      confirmarSenha: new FormControl('', Validators.required),

      tipoUsuario: new FormControl<string | null>(null, Validators.required),

      unidadeAtuacao: new FormControl<string | null>(null, Validators.required),
    },
    {
      validators: CadastroProfissional.validarSenhas,
    },
  );

  constructor(
    private router: Router,
    private profissionalService: ProfissionalService,
  ) {}
  salvarProfissional(): void {
    this.erroGeral = null;
    this.errosPorCampo = {};

    if (this.profissionalForm.invalid) {
      this.profissionalForm.markAllAsTouched();
      return;
    }

    this.salvando = true;

    const form = this.profissionalForm.getRawValue();

    const profissional: ProfissionalPayload = {
      nome: form.nome?.trim() ?? '',
      email: form.email?.trim().toLowerCase() ?? '',
      senha: form.senha ?? '',
      tipoUsuario: form.tipoUsuario ?? '',
      unidadeAtuacao: form.unidadeAtuacao ?? '',
    };

    this.profissionalService.cadastrar(profissional).subscribe({
      next: () => {
        this.salvando = false;
        this.router.navigate(['/profissionais']);
      },
      error: (erro: HttpErrorResponse) => {
        this.salvando = false;
        this.tratarErro(erro);
      },
    });
  }

  private tratarErro(erro: HttpErrorResponse): void {
    if (!erro.error) {
      this.erroGeral = 'Não foi possível conectar ao servidor. Tente novamente.';
      return;
    }

    // Erros de validação (@Valid)
    if (erro.status === 422 && Array.isArray(erro.error.errors)) {
      const validationError = erro.error as ValidationError;

      validationError.errors.forEach((campo) => {
        this.errosPorCampo[campo.fieldName] = campo.message;
      });

      this.erroGeral = validationError.message;
      this.marcarCamposComoTocados();
      return;
    }

    // Conflitos (email já cadastrado, etc.)
    if (erro.status === 409) {
      const standardError = erro.error as StandardError;

      this.erroGeral = standardError.message ?? 'Já existe um profissional com esses dados.';
      return;
    }

    // Outros erros conhecidos
    if (erro.error.message) {
      const standardError = erro.error as StandardError;
      this.erroGeral = standardError.message;
      return;
    }

    this.erroGeral = 'Não foi possível cadastrar o profissional.';
  }

  private marcarCamposComoTocados(): void {
    Object.keys(this.errosPorCampo).forEach((campo) => {
      this.profissionalForm.get(campo)?.markAsTouched();
    });
  }
  cancelar(): void {
    this.router.navigate(['/profissionais']);
  }

  campoInvalido(campo: string): boolean {
    const control = this.profissionalForm.get(campo);

    return !!control && control.invalid && (control.touched || control.dirty);
  }

  mensagemErro(campo: string, mensagemPadrao: string): string {
    return this.errosPorCampo[campo] ?? mensagemPadrao;
  }

  labelEnum(valor: string): string {
    return valor
      .replaceAll('_', ' ')
      .toLowerCase()
      .replace(/\b\w/g, (letra) => letra.toUpperCase());
  }

  static validarSenhas(form: AbstractControl): ValidationErrors | null {
    const senha = form.get('senha')?.value;
    const confirmarSenha = form.get('confirmarSenha')?.value;

    if (senha !== confirmarSenha) {
      return {
        senhasDiferentes: true,
      };
    }

    return null;
  }
}
