// Modelos con los mismos campos que los DTO del backend (paquete pe.upn.perudeestacion.dto).

export type Categoria = 'FRUTA' | 'VERDURA' | 'TUBERCULO' | 'PESCADO';
export type NivelTemporada = 'ALTA' | 'MEDIA' | 'BAJA';
export type NivelRiesgo = 'ALTO' | 'MEDIO' | 'BAJO';
export type TipoVivienda = 'MATERIAL_NOBLE' | 'ADOBE' | 'QUINCHA' | 'ESTERA' | 'MADERA';
export type CapacidadEconomica = 'BAJA' | 'MEDIA' | 'ALTA';

export interface Departamento {
  id: number;
  codigo: string;
  nombre: string;
  regionNatural: 'COSTA' | 'SIERRA' | 'SELVA';
}

export interface Alerta {
  id: number;
  comunicado: string;
  fecha: string;
  estado: string;
  magnitud: string;
  vigente: boolean;
}

export interface AlimentoAfectado {
  alimentoId: number;
  alimento: string;
  riesgoAlza: NivelRiesgo;
}

export interface Impacto {
  id: number;
  alertaId: number;
  departamento: string;
  sector: string;
  nivelRiesgo: NivelRiesgo;
  descripcion: string;
  alimentos: AlimentoAfectado[];
}

export interface DepartamentoDetalle {
  departamento: Departamento;
  alertaVigente: Alerta | null;
  impactos: Impacto[];
}

export interface Alimento {
  id: number;
  nombre: string;
  categoria: Categoria;
  descripcion: string | null;
  imagenUrl: string | null;
}

export interface AlimentoRequest {
  nombre: string;
  categoria: Categoria;
  descripcion: string | null;
  imagenUrl: string | null;
}

export interface Consejo {
  metodo: string;
  descripcion: string;
  duracionDias: number | null;
}

export interface Precio {
  id: number;
  alimentoId: number;
  alimento: string;
  departamento: string;
  precioKg: number;
  fecha: string;
  fuente: string;
}

export interface FichaAlimento {
  alimento: Alimento;
  consejos: Consejo[];
  preciosRecientes: Precio[];
}

export interface Calendario {
  alimentoId: number;
  alimento: string;
  departamento: string;
  meses: { mes: number; nivel: NivelTemporada | null }[];
}

export interface ItemTemporada {
  temporadaId: number;
  alimentoId: number;
  nombre: string;
  categoria: Categoria;
  imagenUrl: string | null;
  nivelTemporada: NivelTemporada;
  riesgoAlza: NivelRiesgo | null;
  precioRefKg: number | null;
  fuentePrecio: string | null;
  fechaPrecio: string | null;
}

export interface ConsultaTemporada {
  departamento: Departamento;
  mes: number;
  alertaVigente: Alerta | null;
  resultados: ItemTemporada[];
}

export interface Temporada {
  id: number;
  alimentoId: number;
  alimento: string;
  categoria: Categoria;
  departamento: string;
  mes: number;
  nivel: NivelTemporada;
}

export interface TemporadaRequest {
  alimentoId: number;
  departamentoId: number;
  mes: number;
  nivel: NivelTemporada;
}

export interface Programa {
  id: number;
  nombre: string;
  entidad: string;
  urlOficial: string | null;
  fechaRevision: string | null;
  requisitos: string[];
}

export interface Centro {
  id: number;
  programaId: number;
  programa: string;
  departamento: string;
  nombre: string;
  direccion: string | null;
  distrito: string | null;
  telefono: string | null;
}

export interface RacionRequest {
  departamento: string;
  adultos: number;
  ninos: number;
  adultosMayores: number;
  dias: number;
  tipoVivienda: TipoVivienda;
  capacidadEconomica: CapacidadEconomica;
}

export interface RacionResponse {
  simulacionId: number;
  departamento: string;
  kcalDiariasHogar: number;
  kcalTotal: number;
  dias: number;
  lista: { grupo: string; kg: number; costoEstimado: number }[];
  costoTotalEstimado: number;
  recomendaciones: string[];
  apoyo: { programaId: number; programa: string; urlOficial: string | null; centrosEnDepartamento: number }[];
}

export interface LoginResponse {
  token: string;
  tipo: string;
  expiraEnSegundos: number;
}

/** Formato de error de la API (ProblemDetail, RFC 9457). */
export interface Problema {
  title?: string;
  status?: number;
  detail?: string;
  errores?: Record<string, string>;
}

export const CATEGORIAS: { valor: Categoria; texto: string }[] = [
  { valor: 'FRUTA', texto: 'Frutas' },
  { valor: 'VERDURA', texto: 'Verduras' },
  { valor: 'TUBERCULO', texto: 'Tubérculos' },
  { valor: 'PESCADO', texto: 'Pescados' },
];

export const MESES = ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto',
  'Setiembre', 'Octubre', 'Noviembre', 'Diciembre'];

export const VIVIENDAS: { valor: TipoVivienda; texto: string }[] = [
  { valor: 'MATERIAL_NOBLE', texto: 'Material noble' },
  { valor: 'ADOBE', texto: 'Adobe' },
  { valor: 'QUINCHA', texto: 'Quincha' },
  { valor: 'ESTERA', texto: 'Estera' },
  { valor: 'MADERA', texto: 'Madera' },
];
