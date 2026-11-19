import { CurrencyPipe, DecimalPipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { ApiService } from '../../core/api.service';
import { aplicarErroresServidor, leerProblema, mensajeError } from '../../core/errores';
import { CapacidadEconomica, Departamento, RacionRequest, RacionResponse, TipoVivienda, VIVIENDAS } from '../../core/models';
import { SeleccionService } from '../../core/seleccion.service';

/** Mismas reglas que RacionRequest en el backend: al menos una persona en el hogar. */
function alMenosUnaPersona(grupo: AbstractControl): ValidationErrors | null {
  const v = grupo.value as { adultos: number; ninos: number; adultosMayores: number };
  return (Number(v.adultos) || 0) + (Number(v.ninos) || 0) + (Number(v.adultosMayores) || 0) > 0
    ? null : { sinPersonas: true };
}

const PERSONAS = [Validators.required, Validators.min(0), Validators.max(20)];

@Component({
  selector: 'app-simulador',
  imports: [ReactiveFormsModule, RouterLink, CurrencyPipe, DecimalPipe],
  templateUrl: './simulador.html',
})
export class Simulador implements OnInit {
  private readonly api = inject(ApiService);
  private readonly seleccion = inject(SeleccionService);
  protected readonly viviendas = VIVIENDAS;
  protected readonly capacidades: CapacidadEconomica[] = ['BAJA', 'MEDIA', 'ALTA'];
  protected readonly departamentos = signal<Departamento[]>([]);
  protected readonly resultado = signal<RacionResponse | null>(null);
  protected readonly errorGeneral = signal<string | null>(null);
  protected readonly enviando = signal(false);
  protected readonly mensaje = mensajeError;

  protected readonly form = inject(FormBuilder).nonNullable.group({
    departamento: [this.seleccion.departamento(), [Validators.required, Validators.pattern(/^[A-Za-z]{3}$/)]],
    adultos: [2, PERSONAS],
    ninos: [0, PERSONAS],
    adultosMayores: [0, PERSONAS],
    dias: [7, [Validators.required, Validators.min(3), Validators.max(30)]],
    tipoVivienda: ['ADOBE' as TipoVivienda, Validators.required],
    capacidadEconomica: ['BAJA' as CapacidadEconomica, Validators.required],
  }, { validators: alMenosUnaPersona });

  ngOnInit(): void {
    this.api.departamentos().subscribe((lista) => this.departamentos.set(lista));
  }

  protected calcular(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid) return;
    this.enviando.set(true);
    this.errorGeneral.set(null);
    this.api.simular(this.form.getRawValue() as RacionRequest).subscribe({
      next: (r) => { this.resultado.set(r); this.enviando.set(false); },
      error: (e) => {
        const p = leerProblema(e);
        aplicarErroresServidor(this.form, p.errores);
        this.errorGeneral.set(p.mensaje);
        this.resultado.set(null);
        this.enviando.set(false);
      },
    });
  }

  protected nombreGrupo(grupo: string): string {
    return grupo.charAt(0) + grupo.slice(1).toLowerCase().replaceAll('_', ' y ');
  }
}
