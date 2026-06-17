import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface UnidadeSaude {
  nome: string;
}

@Injectable({
  providedIn: 'root',
})
export class UnidadeSaudeService {
  private readonly apiUrl = 'http://localhost:8080/unidades-saude';

  constructor(private http: HttpClient) {}

  listarUnidades(): Observable<UnidadeSaude[]> {
    return this.http.get<UnidadeSaude[]>(this.apiUrl);
  }
}
