import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';

import { ApiService } from '../../../core/api.service';
import { aplicarErroresServidor, leerProblema, mensajeError } from '../../../core/errores';
import { Alimento, Departamento, ItemTemporada, MESES, NivelTemporada, TemporadaRequest } from '../../../core/models';
import { NotificacionesService } from '../../../core/notificaciones.service';
import { Nivel } from '../../../shared/nivel';

@Component({
  selector: 'app-admin-temporadas',
  imports: [ReactiveFormsModule, FormsModule, Nivel],
  templateUrl: './admin-temporadas.html',
})
export class AdminTemporadas implements OnInit {
  private readonly api = inject(ApiService);
  private readonly avisos = inject(NotificacionesService);
  protected readonly meses = MESES;
  protected readonly niveles: NivelTemporada[] = ['ALTA', 'MEDIA', 'BAJA'];
  protected readonly mensaje = mensajeError;

  protected readonly departamentos = signal<Departamento[]>([]);
  protected readonly alimentos = signal<Alimento[]>([]);
  protected readonly temporadas = signal<ItemTemporada[]>([]);
  protected readonly editandoId = signal<number | 'nuevo' | null>(null);
  protected readonly porEliminar = signal<number | null>(null);
  protected readonly errorForm = signal<string | null>(null);
  protected readonly errorLista = signal<string | null>(null);
  protected readonly guardando = signal(false);
  protected filtroDep = 'PIU';
  protected filtroMes = new Date().getMonth() + 1;

  // Mismas reglas que TemporadaRequest en el backend
  protected readonly form = inject(FormBuilder).group({
    alimentoId: [null as number | null, Validators.required],
    departamentoId: [null as number | null, Validators.required],
    mes: [1, [Validators.required, Validators.min(1), Validators.max(12)]],
    nivel: [null as NivelTemporada | null, Validators.required],
  });

  ngOnInit(): void {
    this.api.departamentos().subscribe((d) => this.departamentos.set(d));
    this.api.alimentos().subscribe((a) => this.alimentos.set(a));
    this.cargar();
  }

  protected cargar(): void {
    this.api.temporadas(this.filtroDep, Number(this.filtroMes)).subscribe({
      next: (c) => { this.temporadas.set(c.resultados); this.errorLista.set(null); },
      error: (e) => this.errorLista.set(leerProblema(e).mensaje),
    });
  }

  private idDepartamento(codigo: string): number | null {
    return this.departamentos().find((d) => d.codigo === codigo)?.id ?? null;
  }

  protected nuevo(): void {
    this.form.reset({ alimentoId: null, departamentoId: this.idDepartamento(this.filtroDep), mes: Number(this.filtroMes), nivel: null });
    this.errorForm.set(null);
    this.editandoId.set('nuevo');
  }

  protected editar(t: ItemTemporada): void {
    this.form.reset({ alimentoId: t.alimentoId, departamentoId: this.idDepartamento(this.filtroDep), mes: Number(this.filtroMes), nivel: t.nivelTemporada });
    this.errorForm.set(null);
    this.editandoId.set(t.temporadaId);
  }

  protected guardar(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid) return;
    const v = this.form.getRawValue();
    const req: TemporadaRequest = { alimentoId: Number(v.alimentoId), departamentoId: Number(v.departamentoId), mes: Number(v.mes), nivel: v.nivel! };
    const id = this.editandoId();
    const peticion = id === 'nuevo' || id === null ? this.api.crearTemporada(req) : this.api.actualizarTemporada(id, req);
    this.guardando.set(true);
    this.errorForm.set(null);
    peticion.subscribe({
      next: (t) => {
        this.avisos.exito(`Temporada de ${t.alimento} (${t.departamento}, mes ${t.mes}) guardada.`);
        this.guardando.set(false);
        this.editandoId.set(null);
        // Muestra el departamento y mes de lo que se acaba de guardar
        this.filtroDep = t.departamento;
        this.filtroMes = t.mes;
        this.cargar();
      },
      error: (e) => {
        const p = leerProblema(e);
        aplicarErroresServidor(this.form, p.errores);
        this.errorForm.set(p.mensaje);
        this.guardando.set(false);
      },
    });
  }

  protected eliminar(t: ItemTemporada): void {
    this.api.eliminarTemporada(t.temporadaId).subscribe({
      next: () => {
        this.avisos.exito(`Temporada de ${t.nombre} eliminada.`);
        this.porEliminar.set(null);
        this.cargar();
      },
      error: (e) => { this.porEliminar.set(null); this.errorLista.set(leerProblema(e).mensaje); },
    });
  }
}
