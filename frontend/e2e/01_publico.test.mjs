// Paginas publicas: consulta de temporada, ficha, El Nino y simulador con validaciones.
import { after, before, describe, it } from 'node:test';
import assert from 'node:assert/strict';
import { abrirNavegador, By, clic, elemento, escribir, esperarAusencia, esperarTexto, ir, seleccionar } from './ayuda.mjs';

describe('Paginas publicas', () => {
  let d;
  before(async () => { d = await abrirNavegador(); });
  after(async () => { await d?.quit(); });

  it('consulta Piura en noviembre: muestra Mango, la alerta de El Nino y filtra por categoria', async () => {
    await ir(d, '/');
    await seleccionar(d, '#sel-departamento', 'Piura');
    await seleccionar(d, '#sel-mes', 'Noviembre');
    await clic(d, '#btn-buscar');
    await esperarTexto(d, '#titulo-consulta', 'Piura');
    await elemento(d, '[data-alimento="Mango"]');
    await esperarTexto(d, '#aviso-nino', 'El Niño');

    await clic(d, 'label[for="cat-VERDURA"]');
    await esperarAusencia(d, '[data-alimento="Mango"]');
    await elemento(d, '[data-alimento="Tomate"]');
    await clic(d, 'label[for="cat-todos"]');
    await elemento(d, '[data-alimento="Mango"]');
  });

  it('abre la ficha del alimento con su calendario de 12 meses', async () => {
    await clic(d, '[data-alimento="Mango"]');
    await esperarTexto(d, '#nombre-ficha', 'Mango');
    const meses = await (await elemento(d, '#calendario')).findElements(By.css('.mes'));
    assert.equal(meses.length, 12);
  });

  it('muestra los impactos de El Nino por departamento', async () => {
    await ir(d, '/el-nino');
    await clic(d, 'label[for="nino-TUM"]');
    await esperarTexto(d, 'main', 'Tumbes');
  });

  it('el simulador valida en el navegador antes de llamar a la API', async () => {
    await ir(d, '/simulador');
    await seleccionar(d, '#sim-departamento', 'Piura');
    await escribir(d, '#sim-adultos', 0);
    await escribir(d, '#sim-dias', 2);
    await clic(d, '#btn-calcular');
    await esperarTexto(d, '#error-dias', '3');
    await elemento(d, '#error-personas');
    assert.equal((await d.findElements(By.css('#resultado-simulacion'))).length, 0);
  });

  it('el simulador calcula 4 adultos por 7 dias en Piura: 58 800 kcal y S/ 108.83', async () => {
    await escribir(d, '#sim-adultos', 4);
    await escribir(d, '#sim-dias', 7);
    await seleccionar(d, '#sim-vivienda', 'Adobe');
    await clic(d, 'label[for="cap-BAJA"]');
    await clic(d, '#btn-calcular');
    await elemento(d, '#resultado-simulacion');
    const kcal = (await (await elemento(d, '#kcal-total')).getText()).replace(/\D/g, '');
    assert.equal(kcal, '58800');
    await esperarTexto(d, '#costo-total', '108.83');
  });
});
