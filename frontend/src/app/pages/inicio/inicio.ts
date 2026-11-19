import { CurrencyPipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { ApiService } from '../../core/api.service';
import { leerProblema } from '../../core/errores';
import { CATEGORIAS, Categoria, ConsultaTemporada, Departamento, MESES } from '../../core/models';
import { SeleccionService } from '../../core/seleccion.service';
import { ImagenAlimento } from '../../shared/imagen-alimento';
import { Nivel } from '../../shared/nivel';

@Component({
  selector: 'app-inicio',
  imports: [FormsModule, RouterLink, CurrencyPipe, ImagenAlimento, Nivel],
  templateUrl: './inicio.html',
})
export class Inicio implements OnInit {
  private readonly api = inject(ApiService);
  protected readonly seleccion = inject(SeleccionService);

  protected readonly meses = MESES;
  protected readonly categorias = CATEGORIAS;
  protected readonly departamentos = signal<Departamento[]>([]);
  protected readonly consulta = signal<ConsultaTemporada | null>(null);
  protected readonly categoria = signal<Categoria | null>(null);
  protected readonly cargando = signal(false);
  protected readonly error = signal<string | null>(null);

  // Valores del formulario (se confirman al pulsar Buscar)
  protected departamento = this.seleccion.departamento();
  protected mes = this.seleccion.mes();

  ngOnInit(): void {
    this.api.departamentos().subscribe({
      next: (lista) => this.departamentos.set(lista),
      error: (e) => this.error.set(leerProblema(e).mensaje),
    });
    this.buscar();
  }

  protected buscar(): void {
    this.seleccion.elegir(this.departamento, Number(this.mes));
    this.cargar();
  }

  protected filtrar(categoria: Categoria | null): void {
    this.categoria.set(categoria);
    this.cargar();
  }

  private cargar(): void {
    this.cargando.set(true);
    this.error.set(null);
    this.api.temporadas(this.seleccion.departamento(), this.seleccion.mes(), this.categoria()).subscribe({
      next: (c) => { this.consulta.set(c); this.cargando.set(false); },
      error: (e) => { this.error.set(leerProblema(e).mensaje); this.consulta.set(null); this.cargando.set(false); },
    });
  }
}
