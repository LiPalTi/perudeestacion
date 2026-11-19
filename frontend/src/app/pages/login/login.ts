import { Component, inject, input, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../core/auth.service';
import { leerProblema, mensajeError } from '../../core/errores';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.html',
})
export class Login {
  /** Parametros de la URL: ?volver=/admin/... y ?expirado=1 */
  readonly volver = input<string>();
  readonly expirado = input<string>();

  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  protected readonly error = signal<string | null>(null);
  protected readonly enviando = signal(false);
  protected readonly mensaje = mensajeError;

  protected readonly form = inject(FormBuilder).nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
  });

  protected ingresar(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid) return;
    this.enviando.set(true);
    this.error.set(null);
    const { email, password } = this.form.getRawValue();
    this.auth.login(email.trim(), password).subscribe({
      next: () => this.router.navigateByUrl(this.volver() || '/admin'),
      error: (e) => {
        this.error.set(leerProblema(e).mensaje);
        this.enviando.set(false);
      },
    });
  }
}
