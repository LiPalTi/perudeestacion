import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, computed, effect, inject, input, signal, untracked } from '@angular/core';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';

import { ApiService } from '../../core/api.service';
import { leerProblema } from '../../core/errores';
import { Calendario, FichaAlimento, MESES } from '../../core/models';
import { SeleccionService } from '../../core/seleccion.service';
import { ImagenAlimento } from '../../shared/imagen-alimento';
import { Nivel } from '../../shared/nivel';

@Component({
  selector: 'app-ficha',
  imports: [RouterLink, CurrencyPipe, DatePipe, ImagenAlimento, Nivel],
  templateUrl: './ficha.html',
})
export class Ficha {
  /** Viene de la ruta /alimentos/:id */
  readonly id = input.required<string>();

  private readonly api = inject(ApiService);
  protected readonly seleccion = inject(SeleccionService);
  protected readonly meses = MESES;
  protected readonly ficha = signal<FichaAlimento | null>(null);
  protected readonly calendario = signal<Calendario | null>(null);
  protected readonly error = signal<string | null>(null);

  /** Precio mas reciente del departamento elegido; si no hay, el mas reciente de cualquier departamento. */
  protected readonly precio = computed(() => {
    const precios = this.ficha()?.preciosRecientes ?? [];
    return precios.find((p) => p.departamento === this.seleccion.departamento()) ?? precios[0] ?? null;
  });
  protected readonly nivelActual = computed(() =>
    this.calendario()?.meses.find((m) => m.mes === this.seleccion.mes())?.nivel ?? null);

  constructor() {
    effect(() => {
      const id = Number(this.id());
      untracked(() => this.cargar(id));
    });
  }

  private cargar(id: number): void {
    this.error.set(null);
    forkJoin({
      ficha: this.api.ficha(id),
      calendario: this.api.calendario(id, this.seleccion.departamento()),
    }).subscribe({
      next: (r) => { this.ficha.set(r.ficha); this.calendario.set(r.calendario); },
      error: (e) => this.error.set(leerProblema(e).mensaje),
    });
  }
}
