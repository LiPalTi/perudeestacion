import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-admin',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="container-xl py-4">
      <div class="row g-4">
        <aside class="col-12 col-md-3">
          <div class="admin-lateral p-3">
            <div class="fw-bold mb-1">Administración</div>
            <div class="small mb-3" style="color:#b9dcc6">{{ auth.email() }}</div>
            <nav class="admin-menu" aria-label="Recursos">
              <a routerLink="alimentos" routerLinkActive="activo" id="menu-alimentos">Alimentos</a>
              <a routerLink="temporadas" routerLinkActive="activo" id="menu-temporadas">Temporadas</a>
            </nav>
          </div>
        </aside>
        <section class="col-12 col-md-9"><router-outlet /></section>
      </div>
    </div>`,
})
export class Admin {
  protected readonly auth = inject(AuthService);
}
