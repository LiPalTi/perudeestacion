import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

import { AuthService } from './core/auth.service';
import { NotificacionesService } from './core/notificaciones.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.html',
})
export class App {
  protected readonly auth = inject(AuthService);
  protected readonly avisos = inject(NotificacionesService);
  private readonly router = inject(Router);
  protected readonly menuAbierto = signal(false);

  protected salir(): void {
    this.auth.logout();
    this.router.navigateByUrl('/');
  }
}
