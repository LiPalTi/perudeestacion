import { Injectable, signal } from '@angular/core';

const CLAVE = 'pe-seleccion';

/** Recuerda el departamento y el mes elegidos para usarlos en todas las pantallas. */
@Injectable({ providedIn: 'root' })
export class SeleccionService {
  readonly departamento = signal<string>('PIU');
  readonly mes = signal<number>(new Date().getMonth() + 1);

  constructor() {
    try {
      const guardado = JSON.parse(localStorage.getItem(CLAVE) ?? 'null') as { departamento: string; mes: number } | null;
      if (guardado) {
        this.departamento.set(guardado.departamento);
        this.mes.set(guardado.mes);
      }
    } catch { /* sin almacenamiento: se usan los valores por defecto */ }
  }

  elegir(departamento: string, mes: number): void {
    this.departamento.set(departamento);
    this.mes.set(mes);
    try { localStorage.setItem(CLAVE, JSON.stringify({ departamento, mes })); } catch { /* opcional */ }
  }
}
