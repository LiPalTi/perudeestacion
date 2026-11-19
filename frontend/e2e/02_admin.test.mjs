// Autenticacion (JWT) y CRUD completo de alimentos y temporadas desde el panel de administracion.
import { after, before, describe, it } from 'node:test';
import assert from 'node:assert/strict';
import { abrirNavegador, ADMIN_EMAIL, By, clic, elemento, escribir, esperarAusencia, esperarTexto, ingresar, ir, seleccionar } from './ayuda.mjs';

const NOMBRE = `Aguaymanto e2e ${Date.now() % 100000}`;
const fila = (nombre) => `tr[data-nombre="${nombre}"]`;

describe('Autenticacion y autorizacion', () => {
  let d;
  before(async () => { d = await abrirNavegador(); });
  after(async () => { await d?.quit(); });

  it('una ruta de administracion sin sesion redirige al login', async () => {
    await ir(d, '/admin/alimentos');
    await elemento(d, '#login-email');
    assert.match(await d.getCurrentUrl(), /\/login\?volver=/);
  });

  it('rechaza una contrasena incorrecta con el mensaje de la API (401)', async () => {
    await escribir(d, '#login-email', ADMIN_EMAIL);
    await escribir(d, '#login-password', 'ClaveIncorrecta');
    await clic(d, '#btn-ingresar');
    await elemento(d, '#error-login');
    assert.equal((await d.findElements(By.css('#nav-admin'))).length, 0);
  });

  it('con credenciales validas entra y vuelve a la pagina pedida', async () => {
    await escribir(d, '#login-password', process.env.ADMIN_PASSWORD ?? 'CambiaEstaClave2026');
    await clic(d, '#btn-ingresar');
    await elemento(d, '#nav-admin');
    await esperarTexto(d, 'h1', 'Alimentos');
    assert.match(await d.getCurrentUrl(), /\/admin\/alimentos$/);
    const token = await d.executeScript("return sessionStorage.getItem('pe-sesion')");
    assert.ok(token && token.includes('token'), 'el token JWT debe guardarse en sessionStorage');
  });

  it('al salir se borra la sesion', async () => {
    await clic(d, '#btn-salir');
    await elemento(d, '#nav-ingresar');
    assert.equal(await d.executeScript("return sessionStorage.getItem('pe-sesion')"), null);
  });
});

describe('CRUD de alimentos y temporadas', () => {
  let d;
  before(async () => { d = await abrirNavegador(); await ingresar(d); });
  after(async () => { await d?.quit(); });

  it('valida el formulario de alimento en el navegador', async () => {
    await ir(d, '/admin/alimentos');
    await clic(d, '#btn-nuevo-alimento');
    await escribir(d, '#ali-imagen', 'no-es-url');
    await clic(d, '#btn-guardar-alimento');
    await elemento(d, '#ali-nombre.is-invalid');
    await elemento(d, '#ali-categoria.is-invalid');
    await elemento(d, '#ali-imagen.is-invalid');
  });

  it('crea un alimento (POST 201) y aparece en la tabla', async () => {
    await escribir(d, '#ali-nombre', NOMBRE);
    await seleccionar(d, '#ali-categoria', 'Frutas');
    await escribir(d, '#ali-descripcion', 'Creado por la prueba de integracion');
    await (await elemento(d, '#ali-imagen')).clear();
    await clic(d, '#btn-guardar-alimento');
    await esperarAusencia(d, '#form-alimento');
    await elemento(d, fila(NOMBRE));
  });

  it('no permite un nombre repetido (409) y muestra el mensaje', async () => {
    await clic(d, '#btn-nuevo-alimento');
    await escribir(d, '#ali-nombre', NOMBRE);
    await seleccionar(d, '#ali-categoria', 'Frutas');
    await clic(d, '#btn-guardar-alimento');
    await esperarTexto(d, '#error-alimento', 'Ya existe');
  });

  it('edita el alimento (PUT 200)', async () => {
    await (await elemento(d, fila(NOMBRE))).findElement(By.css('.btn-editar')).click();
    await escribir(d, '#ali-descripcion', 'Descripcion editada');
    await clic(d, '#btn-guardar-alimento');
    await esperarAusencia(d, '#form-alimento');
    await esperarTexto(d, `${fila(NOMBRE)} .descripcion`, 'Descripcion editada');
  });

  it('crea una temporada para el alimento (POST 201)', async () => {
    await ir(d, '/admin/temporadas');
    await seleccionar(d, '#tf-dep', 'Lima');
    await seleccionar(d, '#tf-mes', 'Marzo');
    await clic(d, '#btn-nueva-temporada');
    await seleccionar(d, '#tem-alimento', NOMBRE);
    await seleccionar(d, '#tem-nivel', 'ALTA');
    await clic(d, '#btn-guardar-temporada');
    await esperarAusencia(d, '#form-temporada');
    await esperarTexto(d, fila(NOMBRE), 'Alta');
  });

  it('no permite la misma temporada dos veces (409)', async () => {
    await clic(d, '#btn-nueva-temporada');
    await seleccionar(d, '#tem-alimento', NOMBRE);
    await seleccionar(d, '#tem-nivel', 'BAJA');
    await clic(d, '#btn-guardar-temporada');
    await elemento(d, '#error-temporada');
  });

  it('edita el nivel de la temporada (PUT 200)', async () => {
    await (await elemento(d, fila(NOMBRE))).findElement(By.css('.btn-editar')).click();
    await seleccionar(d, '#tem-nivel', 'MEDIA');
    await clic(d, '#btn-guardar-temporada');
    await esperarAusencia(d, '#form-temporada');
    await esperarTexto(d, fila(NOMBRE), 'Media');
  });

  it('elimina la temporada con confirmacion (DELETE 204)', async () => {
    await (await elemento(d, fila(NOMBRE))).findElement(By.css('.btn-eliminar')).click();
    await (await elemento(d, fila(NOMBRE))).findElement(By.css('.btn-confirmar')).click();
    await esperarAusencia(d, fila(NOMBRE));
  });

  it('elimina el alimento con confirmacion (DELETE 204)', async () => {
    await ir(d, '/admin/alimentos');
    await (await elemento(d, fila(NOMBRE))).findElement(By.css('.btn-eliminar')).click();
    await (await elemento(d, fila(NOMBRE))).findElement(By.css('.btn-confirmar')).click();
    await esperarAusencia(d, fila(NOMBRE));
  });
});
