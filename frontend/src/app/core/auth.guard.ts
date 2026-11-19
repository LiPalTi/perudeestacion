import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { AuthService } from './auth.service';

/** Protege /admin: sin sesion vigente redirige al login y luego vuelve a la pagina pedida. */
export const authGuard: CanActivateFn = (_route, state) => {
  if (inject(AuthService).autenticado()) return true;
  return inject(Router).createUrlTree(['/login'], { queryParams: { volver: state.url } });
};
