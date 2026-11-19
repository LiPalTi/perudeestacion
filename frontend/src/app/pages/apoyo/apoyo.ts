import { DatePipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { ApiService } from '../../core/api.service';
import { leerProblema } from '../../core/errores';
import { Centro, Departamento, Programa } from '../../core/models';
import { SeleccionService } from '../../core/seleccion.service';

@Component({
  selector: 'app-apoyo',
  imports: [FormsModule, DatePipe],
  templateUrl: './apoyo.html',
})
export class Apoyo implements OnInit {
  private readonly api = inject(ApiService);
  private readonly seleccion = inject(SeleccionService);
  protected readonly programas = signal<Programa[]>([]);
  protected readonly centros = signal<Centro[]>([]);
  protected readonly departamentos = signal<Departamento[]>([]);
  protected readonly error = signal<string | null>(null);
  protected departamento = this.seleccion.departamento();

  ngOnInit(): void {
    this.api.programas().subscribe({
      next: (p) => this.programas.set(p),
      error: (e) => this.error.set(leerProblema(e).mensaje),
    });
    this.api.departamentos().subscribe((d) => this.departamentos.set(d));
    this.cargarCentros();
  }

  protected cargarCentros(): void {
    this.api.centros(this.departamento).subscribe({
      next: (c) => this.centros.set(c),
      error: (e) => this.error.set(leerProblema(e).mensaje),
    });
  }
}
