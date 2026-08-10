import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface GrupoTerapeuticoDTO {
  id: number;
  tema: string;
  coordenadorId: string;
  nomeCoordenador: string;
  recorrencia: string;
  horarioPadrao: string;
  ativo: boolean;
}

export interface CriarGrupoPayload {
  tema: string;
  coordenadorId: string;
  recorrencia: string;
  dataPrimeiraSessao: string;
  horario: string;
  participantesIds?: string[];
}

export interface NovaSessaoPayload {
  grupoId: number;
  dataSessao: string;
  horario?: string;
  participantesIds?: string[];
}

export interface ParticipanteSessaoDTO {
  pacienteId: string;
  nomePaciente: string;
}

export interface SessaoGrupoDTO {
  id: number;
  grupoId: number;
  temaGrupo: string;
  nomeCoordenador: string;
  dataSessao: string;
  horario: string;
  status: string;
  participantes: ParticipanteSessaoDTO[];
  quantidadeParticipantes: number;
  version: number;
}

export interface FieldMessage {
  fieldName: string;
  message: string;
}

export interface StandardError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
}

export interface ValidationError extends StandardError {
  errors: FieldMessage[];
}

@Injectable({
  providedIn: 'root',
})
export class GrupoService {
  private readonly apiUrl = '/api/grupos';

  constructor(private http: HttpClient) {}

  criarGrupo(payload: CriarGrupoPayload): Observable<GrupoTerapeuticoDTO> {
    return this.http.post<GrupoTerapeuticoDTO>(this.apiUrl, payload);
  }

  listarGrupos(): Observable<GrupoTerapeuticoDTO[]> {
    return this.http.get<GrupoTerapeuticoDTO[]>(this.apiUrl);
  }

  sugerirProximaData(grupoId: number): Observable<string> {
    return this.http.get(`${this.apiUrl}/${grupoId}/proxima-data-sugerida`, {
      responseType: 'text',
    }) as Observable<string>;
  }

  criarProximaSessao(payload: NovaSessaoPayload): Observable<SessaoGrupoDTO> {
    return this.http.post<SessaoGrupoDTO>(`${this.apiUrl}/sessoes`, payload);
  }

  listarSessoes(dataInicio: string, dataFim: string): Observable<SessaoGrupoDTO[]> {
    const params = new HttpParams().set('dataInicio', dataInicio).set('dataFim', dataFim);
    return this.http.get<SessaoGrupoDTO[]>(`${this.apiUrl}/sessoes`, { params });
  }

  adicionarParticipante(sessaoId: number, pacienteId: string): Observable<SessaoGrupoDTO> {
    return this.http.post<SessaoGrupoDTO>(`${this.apiUrl}/sessoes/${sessaoId}/participantes`, {
      pacienteId,
    });
  }

  removerParticipante(sessaoId: number, pacienteId: string): Observable<SessaoGrupoDTO> {
    return this.http.delete<SessaoGrupoDTO>(
      `${this.apiUrl}/sessoes/${sessaoId}/participantes/${pacienteId}`,
    );
  }

  atualizarStatus(
    sessaoId: number,
    novoStatus: string,
    version: number,
  ): Observable<SessaoGrupoDTO> {
    const params = new HttpParams().set('novoStatus', novoStatus).set('version', String(version));
    return this.http.patch<SessaoGrupoDTO>(`${this.apiUrl}/sessoes/${sessaoId}/status`, null, {
      params,
    });
  }
}
