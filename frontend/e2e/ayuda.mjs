// Utilidades comunes de las pruebas de integracion (Angular + API Spring Boot reales).
// Variables: BASE_URL (por defecto http://localhost:4200), ADMIN_EMAIL, ADMIN_PASSWORD.
import { Builder, Browser, By, until } from 'selenium-webdriver';
import chrome from 'selenium-webdriver/chrome.js';

export const BASE_URL = process.env.BASE_URL ?? 'http://localhost:4200';
export const ADMIN_EMAIL = process.env.ADMIN_EMAIL ?? 'admin@perudeestacion.pe';
export const ADMIN_PASSWORD = process.env.ADMIN_PASSWORD ?? 'CambiaEstaClave2026';
const ESPERA = 15000;

export async function abrirNavegador() {
  const opciones = new chrome.Options().addArguments('--headless=new', '--window-size=1280,900', '--no-sandbox', '--disable-dev-shm-usage');
  // Opcional: rutas propias de Chrome y ChromeDriver; si no se indican, Selenium Manager los encuentra.
  if (process.env.CHROME_BIN) opciones.setChromeBinaryPath(process.env.CHROME_BIN);
  const constructor = new Builder().forBrowser(Browser.CHROME).setChromeOptions(opciones);
  if (process.env.CHROMEDRIVER) constructor.setChromeService(new chrome.ServiceBuilder(process.env.CHROMEDRIVER));
  return constructor.build();
}

export const ir = (d, ruta) => d.get(BASE_URL + ruta);

export async function elemento(d, css) {
  const el = await d.wait(until.elementLocated(By.css(css)), ESPERA, `No aparecio ${css}`);
  await d.wait(until.elementIsVisible(el), ESPERA, `No se ve ${css}`);
  return el;
}

/** Espera hasta que el texto del elemento contenga el valor indicado. */
export async function esperarTexto(d, css, texto) {
  await d.wait(async () => {
    const lista = await d.findElements(By.css(css));
    for (const el of lista) if ((await el.getText()).includes(texto)) return true;
    return false;
  }, ESPERA, `${css} no contiene "${texto}"`);
}

export async function esperarAusencia(d, css) {
  await d.wait(async () => (await d.findElements(By.css(css))).length === 0, ESPERA, `${css} sigue en la pagina`);
}

export async function escribir(d, css, valor) {
  const el = await elemento(d, css);
  await el.clear();
  await el.sendKeys(String(valor));
}

/** Elige en un <select> la opcion cuyo texto visible coincide. */
export async function seleccionar(d, css, texto) {
  const select = await elemento(d, css);
  await d.wait(async () => (await select.findElements(By.xpath(`.//option[normalize-space()="${texto}"]`))).length > 0,
    ESPERA, `${css} no tiene la opcion ${texto}`);
  await select.findElement(By.xpath(`.//option[normalize-space()="${texto}"]`)).click();
}

export async function clic(d, css) {
  await (await elemento(d, css)).click();
}

export async function ingresar(d) {
  await ir(d, '/login');
  await escribir(d, '#login-email', ADMIN_EMAIL);
  await escribir(d, '#login-password', ADMIN_PASSWORD);
  await clic(d, '#btn-ingresar');
  await elemento(d, '#nav-admin');
}

export { By, until };
