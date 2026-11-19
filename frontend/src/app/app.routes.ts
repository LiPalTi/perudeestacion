import { Routes } from '@angular/router';

import { authGuard } from './core/auth.guard';

export const routes: Routes = [
  { path: '', title: 'Perú de Estación', loadComponent: () => import('./pages/inicio/inicio').then((m) => m.Inicio) },
  { path: 'alimentos/:id', title: 'Ficha del alimento', loadComponent: () => import('./pages/ficha/ficha').then((m) => m.Ficha) },
  { path: 'el-nino', title: 'Módulo El Niño', loadComponent: () => import('./pages/el-nino/el-nino').then((m) => m.ElNino) },
  { path: 'simulador', title: 'Simulador de ración', loadComponent: () => import('./pages/simulador/simulador').then((m) => m.Simulador) },
  { path: 'apoyo', title: 'Programas de apoyo', loadComponent: () => import('./pages/apoyo/apoyo').then((m) => m.Apoyo) },
  { path: 'login', title: 'Ingresar', loadComponent: () => import('./pages/login/login').then((m) => m.Login) },
  {
    path: 'admin',
    canActivate: [authGuard],
    loadComponent: () => import('./pages/admin/admin').then((m) => m.Admin),
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'alimentos' },
      { path: 'alimentos', title: 'Administrar alimentos', loadComponent: () => import('./pages/admin/alimentos/admin-alimentos').then((m) => m.AdminAlimentos) },
      { path: 'temporadas', title: 'Administrar temporadas', loadComponent: () => import('./pages/admin/temporadas/admin-temporadas').then((m) => m.AdminTemporadas) },
    ],
  },
  { path: '**', redirectTo: '' },
];
