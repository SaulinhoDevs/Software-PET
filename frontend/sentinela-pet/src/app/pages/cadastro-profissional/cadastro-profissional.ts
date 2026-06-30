import { CommonModule } from '@angular/common';
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

// import { ProfissionalPayload, ProfissionalService } from '../../services/profissional/profissional-service';

enum TipoUsuario {
  ADMINISTRADOR = 1,
  PROFISSIONAL = 2,
  RECEPCAO = 3,
}

enum UnidadeAtuacao {
  USF = 1,
  CAPS_AD = 2,
  CAPS_I = 3,
  CAPS_II = 4,
}

@Component({
  selector: 'app-cadastro-profissional',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './cadastro-profissional.html',
  styleUrl: './cadastro-profissional.css',
})
export class CadastroProfissional {
  tipoUsuarioOptions = Object.values(TipoUsuario).filter(
    (value) => typeof value === 'number',
  ) as number[];

  unidadeAtuacaoOptions = Object.values(UnidadeAtuacao).filter(
    (value) => typeof value === 'number',
  ) as number[];

  salvando = false;

  profissionalForm = new FormGroup(
    {
      nome: new FormControl('', Validators.required),

      email: new FormControl('', [Validators.required, Validators.email]),

      senha: new FormControl('', [Validators.required, Validators.minLength(6)]),

      confirmarSenha: new FormControl('', Validators.required),

      tipoUsuario: new FormControl<number | null>(null, Validators.required),

      unidadeAtuacao: new FormControl<number | null>(null, Validators.required),
    },
    {
      validators: CadastroProfissional.validarSenhas,
    },
  );

  constructor(
    private router: Router,
    // private profissionalService: ProfissionalService
  ) {}

  salvarProfissional(): void {
    if (this.profissionalForm.invalid) {
      this.profissionalForm.markAllAsTouched();
      return;
    }

    this.salvando = true;

    const form = this.profissionalForm.getRawValue();

    const payload = {
      nome: form.nome?.trim() ?? '',
      email: form.email?.trim() ?? '',
      senha: form.senha ?? '',
      tipoUsuario: form.tipoUsuario!,
      unidadeAtuacao: form.unidadeAtuacao!,
    };

    console.log(payload);

    /*
    this.profissionalService.cadastrar(payload).subscribe({
      next: () => {
        this.salvando = false;
        this.router.navigate(['/profissionais']);
      },
      error: (erro) => {
        this.salvando = false;
        console.error('Erro ao cadastrar profissional:', erro);
        alert('Erro ao cadastrar profissional.');
      },
    });
    */
  }

  cancelar(): void {
    this.router.navigate(['/profissionais']);
  }

  campoInvalido(campo: string): boolean {
    const control = this.profissionalForm.get(campo);

    return !!control && control.invalid && (control.dirty || control.touched);
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

  labelUnidadeAtuacao(unidade: number): string {
    switch (unidade) {
      case UnidadeAtuacao.USF:
        return 'USF';

      case UnidadeAtuacao.CAPS_AD:
        return 'CAPS AD';

      case UnidadeAtuacao.CAPS_I:
        return 'CAPS I';

      case UnidadeAtuacao.CAPS_II:
        return 'CAPS II';

      default:
        return '';
    }
  }
}
