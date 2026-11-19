import { HttpErrorResponse } from '@angular/common/http';
import { AbstractControl } from '@angular/forms';

import { Problema } from './models';

/** Convierte cualquier error HTTP en un mensaje para la persona y los errores por campo del backend. */
export function leerProblema(err: unknown): { mensaje: string; errores: Record<string, string> } {
  if (!(err instanceof HttpErrorResponse)) {
    return { mensaje: 'Ocurrió un error inesperado.', errores: {} };
  }
  if (err.status === 0) {
    return { mensaje: 'No se puede conectar con la API. Verifica que el backend esté encendido.', errores: {} };
  }
  const p = (err.error ?? {}) as Problema;
  const porDefecto: Record<number, string> = {
    400: 'Revisa los datos ingresados.',
    401: 'Tu sesión venció o las credenciales no son correctas.',
    403: 'No tienes permiso para esta acción.',
    404: 'El registro no existe.',
    409: 'La operación entra en conflicto con datos existentes.',
    503: 'La base de datos no responde. Intenta en unos minutos.',
  };
  return {
    mensaje: p.detail ?? porDefecto[err.status] ?? 'Ocurrió un error en el servidor.',
    errores: p.errores ?? {},
  };
}

/** Marca en el formulario los errores por campo que devolvio el backend (400). */
export function aplicarErroresServidor(form: AbstractControl, errores: Record<string, string>): void {
  for (const [campo, texto] of Object.entries(errores)) {
    const control = form.get(campo);
    if (control) {
      control.setErrors({ servidor: texto });
      control.markAsTouched();
    }
  }
}

/** Texto del primer error de un control, con los mismos mensajes que usa el backend. */
export function mensajeError(control: AbstractControl | null): string | null {
  if (!control || !control.errors || !(control.touched || control.dirty)) return null;
  const e = control.errors;
  if (e['servidor']) return e['servidor'];
  if (e['required']) return 'Este campo es obligatorio.';
  if (e['email']) return 'Ingresa un correo válido.';
  if (e['min']) return `El valor mínimo es ${e['min'].min}.`;
  if (e['max']) return `El valor máximo es ${e['max'].max}.`;
  if (e['maxlength']) return `Máximo ${e['maxlength'].requiredLength} caracteres.`;
  if (e['pattern']) return 'El formato no es válido.';
  return 'Valor no válido.';
}
