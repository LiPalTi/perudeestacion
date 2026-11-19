import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';
import { NotificacionesService } from './notificaciones.service';

/** Agrega "Authorization: Bearer <token>" a las peticiones hacia la API cuando hay sesion. */
export const tokenInterceptor: HttpInterceptorFn = (req, next) => {
  const token = inject(AuthService).token();
  if (!token || !req.url.startsWith(environment.apiUrl)) return next(req);
  return next(req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }));
};

/**
 * Errores comunes a toda la app: 401 en una ruta protegida cierra la sesion y lleva al login;
 * sin conexion o 503 muestran un aviso. Los 400 y 409 los muestra cada formulario.
 */
export const erroresInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const avisos = inject(NotificacionesService);
  return next(req).pipe(
    catchError((err: unknown) => {
      if (err instanceof HttpErrorResponse) {
        const esLogin = req.url.endsWith('/auth/login');
        if (err.status === 401 && !esLogin) {
          auth.logout();
          router.navigate(['/login'], { queryParams: { expirado: 1 } });
        } else if (err.status === 0) {
          avisos.error('No se puede conectar con la API. Verifica que el backend esté encendido en el puerto 8080.');
        } else if (err.status === 503) {
          avisos.error('La base de datos no responde en este momento. Intenta en unos minutos.');
        }
      }
      return throwError(() => err);
    }),
  );
};
