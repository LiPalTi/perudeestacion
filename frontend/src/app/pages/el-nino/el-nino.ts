import { DatePipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { ApiService } from '../../core/api.service';
import { leerProblema } from '../../core/errores';
import { Alerta, DepartamentoDetalle } from '../../core/models';
import { Nivel } from '../../shared/nivel';

@Component({
  selector: 'app-el-nino',
  imports: [RouterLink, DatePipe, Nivel],
  templateUrl: './el-nino.html',
})
export class ElNino implements OnInit {
  private readonly api = inject(ApiService);
  protected readonly piloto = [{ codigo: 'TUM', nombre: 'Tumbes' }, { codigo: 'PIU', nombre: 'Piura' }, { codigo: 'LAM', nombre: 'Lambayeque' }];
  protected readonly alerta = signal<Alerta | null>(null);
  protected readonly elegido = signal('PIU');
  protected readonly detalle = signal<DepartamentoDetalle | null>(null);
  protected readonly error = signal<string | null>(null);

  ngOnInit(): void {
    this.api.alertasVigentes().subscribe({
      next: (lista) => this.alerta.set(lista[0] ?? null),
      error: (e) => this.error.set(leerProblema(e).mensaje),
    });
    this.elegir('PIU');
  }

  protected elegir(codigo: string): void {
    this.elegido.set(codigo);
    this.api.departamento(codigo).subscribe({
      next: (d) => this.detalle.set(d),
      error: (e) => this.error.set(leerProblema(e).mensaje),
    });
  }
}
