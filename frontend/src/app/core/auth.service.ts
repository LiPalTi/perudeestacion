import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';

import { environment } from '../../environments/environment';
import { LoginResponse } from './models';

interface Sesion {
  email: string;
  token: string;
  expira: number; // milisegundos desde 1970
}

const CLAVE = 'pe-sesion';

/** Login con JWT. La sesion se guarda en sessionStorage: se borra al cerrar la pestana. */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly sesion = signal<Sesion | null>(this.leer());

  readonly email = computed(() => this.sesion()?.email ?? null);
  readonly autenticado = computed(() => {
    const s = this.sesion();
    return s !== null && s.expira > Date.now();
  });

  login(email: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${environment.apiUrl}/auth/login`, { email, password }).pipe(
      tap((r) => this.guardar({ email, token: r.token, expira: Date.now() + r.expiraEnSegundos * 1000 })),
    );
  }

  /** Token vigente o null si no hay sesion o ya vencio. */
  token(): string | null {
    return this.autenticado() ? this.sesion()!.token : null;
  }

  logout(): void {
    try { sessionStorage.removeItem(CLAVE); } catch { /* almacenamiento no disponible */ }
    this.sesion.set(null);
  }

  private guardar(s: Sesion): void {
    try { sessionStorage.setItem(CLAVE, JSON.stringify(s)); } catch { /* almacenamiento no disponible */ }
    this.sesion.set(s);
  }

  private leer(): Sesion | null {
    try {
      const s = JSON.parse(sessionStorage.getItem(CLAVE) ?? 'null') as Sesion | null;
      return s && s.expira > Date.now() ? s : null;
    } catch {
      return null;
    }
  }
}
