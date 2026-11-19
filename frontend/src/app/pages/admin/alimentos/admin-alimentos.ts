import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';

import { ApiService } from '../../../core/api.service';
import { aplicarErroresServidor, leerProblema, mensajeError } from '../../../core/errores';
import { Alimento, AlimentoRequest, CATEGORIAS, Categoria } from '../../../core/models';
import { NotificacionesService } from '../../../core/notificaciones.service';
import { ImagenAlimento, nombreArchivo } from '../../../shared/imagen-alimento';

@Component({
  selector: 'app-admin-alimentos',
  imports: [ReactiveFormsModule, FormsModule, ImagenAlimento],
  templateUrl: './admin-alimentos.html',
})
export class AdminAlimentos implements OnInit {
  private readonly api = inject(ApiService);
  private readonly avisos = inject(NotificacionesService);
  protected readonly categorias = CATEGORIAS;
  protected readonly mensaje = mensajeError;
  protected readonly archivo = nombreArchivo;

  protected readonly alimentos = signal<Alimento[]>([]);
  protected readonly editando = signal<Alimento | 'nuevo' | null>(null);
  protected readonly porEliminar = signal<number | null>(null);
  protected readonly errorForm = signal<string | null>(null);
  protected readonly errorLista = signal<string | null>(null);
  protected readonly guardando = signal(false);
  protected filtroCategoria: Categoria | '' = '';
  protected busqueda = '';

  // Mismas reglas que AlimentoRequest en el backend
  protected readonly form = inject(FormBuilder).group({
    nombre: ['', [Validators.required, Validators.maxLength(80)]],
    categoria: ['' as Categoria | '', Validators.required],
    descripcion: ['', Validators.maxLength(500)],
    imagenUrl: ['', [Validators.maxLength(500), Validators.pattern(/^https?:\/\/.+/)]],
  });

  ngOnInit(): void {
    this.cargar();
  }

  protected cargar(): void {
    this.api.alimentos(this.filtroCategoria || null, this.busqueda.trim() || null).subscribe({
      next: (lista) => { this.alimentos.set(lista); this.errorLista.set(null); },
      error: (e) => this.errorLista.set(leerProblema(e).mensaje),
    });
  }

  protected nuevo(): void {
    this.form.reset({ nombre: '', categoria: '', descripcion: '', imagenUrl: '' });
    this.errorForm.set(null);
    this.editando.set('nuevo');
  }

  protected editar(a: Alimento): void {
    this.form.reset({ nombre: a.nombre, categoria: a.categoria, descripcion: a.descripcion ?? '', imagenUrl: a.imagenUrl ?? '' });
    this.errorForm.set(null);
    this.editando.set(a);
  }

  protected cancelar(): void {
    this.editando.set(null);
  }

  protected guardar(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid) return;
    const v = this.form.getRawValue();
    const req: AlimentoRequest = {
      nombre: (v.nombre ?? '').trim(),
      categoria: v.categoria as Categoria,
      descripcion: v.descripcion?.trim() || null,
      imagenUrl: v.imagenUrl?.trim() || null,
    };
    const actual = this.editando();
    const peticion = actual === 'nuevo' || actual === null
      ? this.api.crearAlimento(req)
      : this.api.actualizarAlimento(actual.id, req);
    this.guardando.set(true);
    this.errorForm.set(null);
    peticion.subscribe({
      next: (a) => {
        this.avisos.exito(actual === 'nuevo' ? `Alimento "${a.nombre}" creado.` : `Alimento "${a.nombre}" actualizado.`);
        this.guardando.set(false);
        this.editando.set(null);
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

  protected eliminar(a: Alimento): void {
    this.api.eliminarAlimento(a.id).subscribe({
      next: () => {
        this.avisos.exito(`Alimento "${a.nombre}" eliminado.`);
        this.porEliminar.set(null);
        this.cargar();
      },
      error: (e) => {
        this.porEliminar.set(null);
        this.errorLista.set(leerProblema(e).mensaje);
      },
    });
  }

  protected tituloForm(): string {
    const e = this.editando();
    return e === 'nuevo' ? 'Nuevo alimento' : `Editar: ${e?.nombre ?? ''}`;
  }
}
