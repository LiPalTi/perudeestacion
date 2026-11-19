import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import {
  Alerta, Alimento, AlimentoRequest, Calendario, Categoria, Centro, ConsultaTemporada, Departamento,
  DepartamentoDetalle, FichaAlimento, Programa, RacionRequest, RacionResponse, Temporada, TemporadaRequest,
} from './models';

/** Unico punto de acceso a la API REST de Spring Boot. */
@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);
  private readonly url = environment.apiUrl;

  // ---- Consultas publicas ----
  departamentos(): Observable<Departamento[]> {
    return this.http.get<Departamento[]>(`${this.url}/departamentos`);
  }

  departamento(codigo: string): Observable<DepartamentoDetalle> {
    return this.http.get<DepartamentoDetalle>(`${this.url}/departamentos/${codigo}`);
  }

  temporadas(departamento: string, mes: number, categoria?: Categoria | null): Observable<ConsultaTemporada> {
    let params = new HttpParams().set('departamento', departamento).set('mes', mes);
    if (categoria) params = params.set('categoria', categoria);
    return this.http.get<ConsultaTemporada>(`${this.url}/temporadas`, { params });
  }

  alimentos(categoria?: Categoria | null, q?: string | null): Observable<Alimento[]> {
    let params = new HttpParams();
    if (categoria) params = params.set('categoria', categoria);
    if (q) params = params.set('q', q);
    return this.http.get<Alimento[]>(`${this.url}/alimentos`, { params });
  }

  ficha(id: number): Observable<FichaAlimento> {
    return this.http.get<FichaAlimento>(`${this.url}/alimentos/${id}`);
  }

  calendario(id: number, departamento: string): Observable<Calendario> {
    return this.http.get<Calendario>(`${this.url}/alimentos/${id}/calendario`, { params: { departamento } });
  }

  alertasVigentes(): Observable<Alerta[]> {
    return this.http.get<Alerta[]>(`${this.url}/alertas`, { params: { vigente: true } });
  }

  programas(): Observable<Programa[]> {
    return this.http.get<Programa[]>(`${this.url}/programas-apoyo`);
  }

  centros(departamento: string): Observable<Centro[]> {
    return this.http.get<Centro[]>(`${this.url}/centros-apoyo`, { params: { departamento } });
  }

  simular(req: RacionRequest): Observable<RacionResponse> {
    return this.http.post<RacionResponse>(`${this.url}/simulaciones/racion`, req);
  }

  // ---- Administracion (el interceptor agrega el token) ----
  crearAlimento(req: AlimentoRequest): Observable<Alimento> {
    return this.http.post<Alimento>(`${this.url}/alimentos`, req);
  }

  actualizarAlimento(id: number, req: AlimentoRequest): Observable<Alimento> {
    return this.http.put<Alimento>(`${this.url}/alimentos/${id}`, req);
  }

  eliminarAlimento(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/alimentos/${id}`);
  }

  crearTemporada(req: TemporadaRequest): Observable<Temporada> {
    return this.http.post<Temporada>(`${this.url}/temporadas`, req);
  }

  actualizarTemporada(id: number, req: TemporadaRequest): Observable<Temporada> {
    return this.http.put<Temporada>(`${this.url}/temporadas/${id}`, req);
  }

  eliminarTemporada(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/temporadas/${id}`);
  }
}
