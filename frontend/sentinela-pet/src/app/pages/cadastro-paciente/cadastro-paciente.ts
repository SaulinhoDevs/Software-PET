import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { UnidadeSaude, UnidadeSaudeService } from '../../services/unidade-saude-service';
import { PacientePayload, PacienteService } from '../../services/paciente/paciente-service';

enum SexoEnum {
  MASCULINO = 'MASCULINO',
  FEMININO = 'FEMININO',
  OUTRO = 'OUTRO',
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
  USF = 'USF',
  CAPS = 'CAPS',
  CAPS_AD = 'CAPS_AD',
  CAPS_II = 'CAPS_II',
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

  pacienteForm = new FormGroup({
    nome: new FormControl('', Validators.required),
    nomeMae: new FormControl('', Validators.required),
    dataNascimento: new FormControl('', Validators.required),

    sexo: new FormControl('', Validators.required),
    racacor: new FormControl('', Validators.required),

    cns: new FormControl('', Validators.required),
    cpf: new FormControl('', Validators.required),
    telefone: new FormControl('', Validators.required),

    unidadeSaude: new FormControl('', Validators.required),

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
      error: () => {
        this.erroUnidades = true;
        this.carregandoUnidades = false;
      },
    });
  }

  salvarPaciente(): void {
    if (this.pacienteForm.invalid) {
      this.pacienteForm.markAllAsTouched();
      return;
    }

    this.salvando = true;

    const formValue = this.pacienteForm.getRawValue();

    const pacienteParaSalvar: PacientePayload = {
      nome: formValue.nome?.trim() ?? '',
      nomeMae: formValue.nomeMae?.trim() ?? '',
      dataNascimento: formValue.dataNascimento ?? '',

      sexo: formValue.sexo ?? '',
      racacor: formValue.racacor ?? '',

      cns: this.somenteNumeros(formValue.cns),
      cpf: this.somenteNumeros(formValue.cpf),
      telefone: this.somenteNumeros(formValue.telefone),

      unidadeSaude: formValue.unidadeSaude ?? '',

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

    this.pacienteService.cadastrarPaciente(pacienteParaSalvar).subscribe({
      next: () => {
        this.salvando = false;
        this.router.navigate(['/pacientes']);
      },
      error: (erro) => {
        this.salvando = false;
        console.error('Erro ao cadastrar paciente:', erro);
        alert('Erro ao cadastrar paciente.');
      },
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

    return !!control && control.invalid && (control.dirty || control.touched);
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
