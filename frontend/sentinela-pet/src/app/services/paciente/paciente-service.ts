import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface EnderecoPayload {
  cidade: string;
  estado: string;
  bairro: string;
  logradouro: string;
  numero: string;
  complemento: string;
  cep: string;
}

export interface PacientePayload {
  nome: string;
  nomeMae: string;
  dataNascimento: string;
  sexo: string;
  racacor: string;
  cns: string;
  cpf: string;
  telefone: string;
  unidadeSaude: string;
  situacaoRua: boolean;
  tipoAcompanhamento: string;
  endereco: EnderecoPayload;
}

@Injectable({
  providedIn: 'root',
})
export class PacienteService {
  private readonly apiUrl = 'http://localhost:8080/pacientes';

  constructor(private http: HttpClient) {}

  cadastrarPaciente(paciente: PacientePayload): Observable<PacientePayload> {
    return this.http.post<PacientePayload>(this.apiUrl, paciente);
  }
}
