import { Injectable, signal } from '@angular/core';

export interface Aviso {
  id: number;
  tipo: 'exito' | 'error';
  texto: string;
}

/** Avisos breves en la esquina de la pantalla (exito al guardar, errores de conexion). */
@Injectable({ providedIn: 'root' })
export class NotificacionesService {
  private siguiente = 1;
  readonly avisos = signal<Aviso[]>([]);

  exito(texto: string): void {
    this.agregar('exito', texto);
  }

  error(texto: string): void {
    this.agregar('error', texto);
  }

  cerrar(id: number): void {
    this.avisos.update((lista) => lista.filter((a) => a.id !== id));
  }

  private agregar(tipo: Aviso['tipo'], texto: string): void {
    const id = this.siguiente++;
    this.avisos.update((lista) => [...lista, { id, tipo, texto }]);
    setTimeout(() => this.cerrar(id), 5000);
  }
}
