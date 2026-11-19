import { Component, computed, input } from '@angular/core';

import { NivelRiesgo, NivelTemporada } from '../core/models';

/** Etiqueta de nivel de temporada (Alta, Media, Baja) o de riesgo de alza (Alto, Medio, Bajo). */
@Component({
  selector: 'app-nivel',
  template: `<span class="badge-nivel" [class]="clase()">{{ texto() }}</span>`,
})
export class Nivel {
  readonly valor = input.required<NivelTemporada | NivelRiesgo | null>();
  readonly prefijo = input('');

  protected readonly clase = computed(() => {
    const v = this.valor();
    if (v === 'ALTA') return 'badge-nivel nivel-alta';
    if (v === 'MEDIA') return 'badge-nivel nivel-media';
    if (v === 'ALTO') return 'badge-nivel riesgo-alto';
    if (v === 'MEDIO') return 'badge-nivel riesgo-medio';
    return 'badge-nivel nivel-baja';
  });

  protected readonly texto = computed(() => {
    const v = this.valor();
    const t = v ? v.charAt(0) + v.slice(1).toLowerCase() : 'Sin dato';
    return this.prefijo() ? `${this.prefijo()} ${t.toLowerCase()}` : t;
  });
}
