import { Component, computed, input, linkedSignal } from '@angular/core';

/** Convierte "Cebolla roja" en "cebolla-roja" (sin tildes ni espacios), el nombre esperado del archivo. */
export function nombreArchivo(nombre: string): string {
  return nombre
    .normalize('NFD').replace(/[̀-ͯ]/g, '')
    .toLowerCase().trim()
    .replace(/[^a-z0-9]+/g, '-').replace(/^-|-$/g, '');
}

/**
 * Imagen de un alimento. Prueba en este orden y usa la primera que exista:
 *  1. la URL guardada en el alimento (campo imagenUrl, editable desde el panel de administracion);
 *  2. un archivo en public/img/alimentos/ con el nombre del alimento: mango.webp, mango.jpg o mango.png;
 *  3. la imagen por defecto public/img/sin-imagen.svg.
 */
@Component({
  selector: 'app-imagen-alimento',
  template: `<img [src]="actual()" [alt]="nombre()" class="img-alimento" [class.sin-imagen]="esDefecto()"
                  loading="lazy" (error)="siguiente()" />`,
})
export class ImagenAlimento {
  readonly nombre = input.required<string>();
  readonly imagenUrl = input<string | null>(null);

  private readonly candidatas = computed(() => {
    const archivo = `/img/alimentos/${nombreArchivo(this.nombre())}`;
    const lista = [`${archivo}.webp`, `${archivo}.jpg`, `${archivo}.png`, '/img/sin-imagen.svg'];
    const url = this.imagenUrl()?.trim();
    return url ? [url, ...lista] : lista;
  });

  // Vuelve a la primera opcion cuando cambia el alimento.
  private readonly indice = linkedSignal({ source: this.candidatas, computation: () => 0 });

  protected readonly actual = computed(() => this.candidatas()[this.indice()]);
  protected readonly esDefecto = computed(() => this.indice() === this.candidatas().length - 1);

  protected siguiente(): void {
    if (this.indice() < this.candidatas().length - 1) this.indice.update((i) => i + 1);
  }
}
