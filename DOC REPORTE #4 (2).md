# 📈 Documentación: Reporte Histórico de Modalidad (Análisis Temporal)

## 📝 Descripción General

Este endpoint genera un **reporte histórico completo y analítico en formato PDF** sobre la evolución temporal de un tipo específico de modalidad de grado. Proporciona análisis profundo de tendencias, comparativas entre periodos, estadísticas de directores y estudiantes, evaluación de desempeño y proyecciones futuras. Es una herramienta estratégica para la planificación académica y la toma de decisiones basada en datos históricos.

**Generador**: `ModalityHistoricalPdfGenerator`

**Tipo de Análisis**: Longitudinal y retrospectivo

---

## 🔗 Endpoint

### **GET** `/reports/modalities/{modalityTypeId}/historical/pdf`

**Descripción**: Genera y descarga un reporte histórico en PDF que analiza la evolución de un tipo específico de modalidad de grado a lo largo de múltiples periodos académicos del programa del usuario autenticado.

### Autenticación
- **Requerida**: Sí
- **Tipo**: Bearer Token (JWT)
- **Permiso requerido**: `PERM_VIEW_REPORT`

---

## 📥 Request (Solicitud)

### Headers
```http
Authorization: Bearer <token_jwt>
```

### URL Parameters

| Parámetro | Tipo | Ubicación | Requerido | Descripción | Ejemplo |
|-----------|------|-----------|-----------|-------------|---------|
| `modalityTypeId` | `Long` | Path | ✅ Sí | ID del tipo de modalidad a analizar | `1` |
| `periods` | `Integer` | Query | ❌ No | Número de periodos históricos a incluir | `8` |

### Formato de URL

```
GET /reports/modalities/{modalityTypeId}/historical/pdf?periods={periods}
```

### Ejemplos de URLs

```http
# Análisis de 8 periodos (por defecto)
GET /reports/modalities/1/historical/pdf

# Análisis de 12 periodos (6 años)
GET /reports/modalities/1/historical/pdf?periods=12

# Análisis de 4 periodos (2 años)
GET /reports/modalities/5/historical/pdf?periods=4
```

### Notas Importantes

1. **`modalityTypeId` es OBLIGATORIO**: Debe existir en la base de datos.
2. **`periods` por defecto es 8**: Equivale a 4 años (8 semestres).
3. **Rango recomendado de periods**: 4-20 periodos.
4. **No requiere Request Body**: Todo se pasa por URL.

---

## 📤 Response (Respuesta)

### Respuesta Exitosa (200 OK)

**Content-Type**: `application/pdf`

**Headers de Respuesta**:
```http
Content-Type: application/pdf
Content-Disposition: attachment; filename=Reporte_Historico_PROYECTO_DE_GRADO_2026-02-18_143025.pdf
X-Report-Generated-At: 2026-02-18T14:30:25
X-Total-Records: 8
Content-Length: 345678
```

**Body**: Archivo PDF binario profesional con análisis histórico completo

### Respuestas de Error

#### Modalidad No Encontrada (400)
```json
{
  "success": false,
  "error": "Datos inválidos: Modalidad con ID 999 no encontrada",
  "timestamp": "2026-02-18T14:30:25"
}
```

#### Error al Generar PDF (500)
```json
{
  "success": false,
  "error": "Error al generar el PDF: <detalle>",
  "timestamp": "2026-02-18T14:30:25"
}
```

#### Sin Autorización (403)
```json
{
  "error": "Forbidden",
  "message": "Access Denied"
}
```

---

## 🎯 Casos de Uso

### Caso de Uso 1: Análisis de Proyectos de Grado (4 años)

**Escenario**: Jefatura quiere evaluar la evolución de los proyectos de grado en los últimos 4 años.

**Request**:
```http
GET /reports/modalities/1/historical/pdf?periods=8
```

**Resultado**: PDF con:
- 8 semestres analizados (2022-1 a 2025-2)
- Tendencias de crecimiento/declive
- Estadísticas de directores
- Tasas de completitud
- Proyecciones futuras

---

### Caso de Uso 2: Evaluación de Pasantías (2 años)

**Escenario**: Consejo necesita evaluar rápidamente las pasantías recientes.

**Request**:
```http
GET /reports/modalities/5/historical/pdf?periods=4
```

**Resultado**: PDF con análisis de los últimos 4 semestres de pasantías.

---

### Caso de Uso 3: Análisis Extenso de Seminarios (10 años)

**Escenario**: Comité académico revisa la historia completa del seminario de grado.

**Request**:
```http
GET /reports/modalities/8/historical/pdf?periods=20
```

**Resultado**: PDF extenso con 20 periodos (10 años) de evolución.

---

### Caso de Uso 4: Comparativa Actual vs Histórico

**Escenario**: Jefatura quiere comparar el semestre actual con el histórico.

**Request**:
```http
GET /reports/modalities/1/historical/pdf?periods=8
```

**Análisis en PDF**:
- Sección "Comparativa Actual vs Periodos Anteriores"
- Identificación de mejoras o declives
- Contexto histórico para decisiones

---

## 📄 Estructura Completa del PDF

### **PORTADA INSTITUCIONAL MEJORADA**

```
┌──────────────────────────────────────────────────────────────┐
│                                                              │
│            UNIVERSIDAD SURCOLOMBIANA                         │ ← Banda roja
│            Facultad de Ingeniería                            │   institucional
│                                                              │
└──────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────┐
│    INGENIERÍA DE SISTEMAS (IS-2020)         │ ← Caja dorada
└─────────────────────────────────────────────┘


        ANÁLISIS HISTÓRICO EVOLUTIVO
           PROYECTO DE GRADO                    ← Título principal
                                                   (rojo)

┌─────────────────────────────────────────────┐
│ PERIODO DE ANÁLISIS: 2022-1 a 2026-1       │ ← Caja dorada
│ 8 Periodos Académicos                       │   destacada
└─────────────────────────────────────────────┘


╔═══════════════════════════════════════════════════╗
║ Programa: Ingeniería de Sistemas                 ║
║ Código: IS-2020                                   ║
║ Fecha de Generación: 18/02/2026 - 14:30         ║ ← Tabla info
║ Generado por: Dr. Juan Pérez                     ║
║ Modalidad: PROYECTO DE GRADO                     ║
║ Periodos Analizados: 8 (4 años)                  ║
║ Total Instancias Históricas: 145                 ║
╚═══════════════════════════════════════════════════╝


┌──────────────────────────────────────────────────────┐
│  Sistema SIGMA - Análisis Histórico y Estratégico   │ ← Footer
│  Sistema Integral de Gestión de Modalidades         │   dorado
└──────────────────────────────────────────────────────┘
```

---

### **SECCIÓN 1: INFORMACIÓN DE LA MODALIDAD**

#### 1.1 Ficha Técnica de la Modalidad

```
┌─────────────────────────────────────────────────────┐
│ 📋 PROYECTO DE GRADO                                │ ← Encabezado rojo
├─────────────────────────────────────────────────────┤
│                                                     │
│ Código: MOD-001                                     │
│ Tipo: Individual/Grupal                             │
│ Requiere Director: Sí                               │
│ Estado: Activa                                      │
│ Años de Operación: 15 años                          │
│ Creada: 15/08/2010                                  │
│ Total Instancias Históricas: 145                    │
│                                                     │
│ Descripción:                                        │
│ Desarrollo de un proyecto de investigación         │
│ aplicado que contribuye a la solución de un        │
│ problema real...                                    │
│                                                     │
└─────────────────────────────────────────────────────┘
```

#### 1.2 Estado Actual (Snapshot del Periodo Vigente)

Tarjetas visuales (3×3):

**Fila 1**:
```
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│      15      │  │      18      │  │      12      │
│  Instancias  │  │  Estudiantes │  │  Directores  │
│   Activas    │  │  Inscritos   │  │  Asignados   │
└──────────────┘  └──────────────┘  └──────────────┘
```

**Fila 2**:
```
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│      12      │  │      2       │  │      1       │
│ Completadas  │  │ En Progreso  │  │ En Revisión  │
└──────────────┘  └──────────────┘  └──────────────┘
```

**Fila 3**:
```
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│    125.5     │  │    ALTA      │  │      2°      │
│ Días Promedio│  │ Popularidad  │  │  Ranking     │
│  Completitud │  │              │  │  Programa    │
└──────────────┘  └──────────────┘  └──────────────┘
```

---

### **SECCIÓN 2: RESUMEN EJECUTIVO HISTÓRICO**

#### 2.1 Indicadores Clave del Análisis

```
┌──────────────────────────────────────────────────────────┐
│ INDICADORES HISTÓRICOS (Últimos 8 periodos)             │
├──────────────────────────────────────────────────────────┤
│                                                          │
│ Total de Instancias: 145                                 │
│ Total de Estudiantes: 178                                │
│ Promedio por Periodo: 18.13 instancias                   │
│ Tasa de Completitud: 87.5%                               │
│ Tasa de Éxito: 92.3%                                     │
│ Tasa de Abandono: 7.7%                                   │
│ Tiempo Promedio: 145.8 días                              │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

#### 2.2 Puntos Críticos Históricos

```
📊 PERIODO PICO
┌─────────────────────────────────────────┐
│ 2024-2: 25 instancias (máximo histórico)│ ← Fondo verde claro
└─────────────────────────────────────────┘

📉 PERIODO MÍNIMO
┌─────────────────────────────────────────┐
│ 2022-1: 8 instancias (mínimo histórico) │ ← Fondo naranja claro
└─────────────────────────────────────────┘
```

---

### **SECCIÓN 3: ANÁLISIS HISTÓRICO POR PERIODO**

*Tabla detallada de todos los periodos analizados*

#### Tabla Completa de Evolución

| Periodo | Instancias | Estudiantes | Ind./Grupo | Completadas | Tasa Éxito | Tiempo Prom. | Directores | Top Director |
|---------|------------|-------------|------------|-------------|------------|--------------|------------|--------------|
| 2026-1 | 18 | 22 | 12/6 | 15 | 93.3% | 135 días | 14 | Dr. López (3) |
| 2025-2 | 20 | 25 | 14/6 | 18 | 90.0% | 142 días | 15 | Dr. García (4) |
| 2025-1 | 15 | 18 | 10/5 | 13 | 86.7% | 158 días | 12 | Dra. Martínez (3) |
| 2024-2 | 25 | 30 | 18/7 | 22 | 88.0% | 145 días | 18 | Dr. López (5) |
| ... | ... | ... | ... | ... | ... | ... | ... | ... |

**Características**:
- Encabezados rojos con texto blanco
- Filas alternadas (blanco/dorado claro)
- Resaltado del periodo pico (fondo verde)
- Resaltado del periodo mínimo (fondo naranja)
- Ordenamiento cronológico descendente (más reciente primero)

#### Detalles Expandidos por Periodo

Para cada periodo:

```
═══════════════════════════════════════════════════════
PERIODO 2025-2
═══════════════════════════════════════════════════════

Instancias Totales: 20
├─ Individuales: 14 (70%)
└─ Grupales: 6 (30%)

Estudiantes Participantes: 25

Resultados:
├─ Completadas Exitosamente: 18 (90.0%)
├─ Abandonadas: 1 (5.0%)
└─ Canceladas: 1 (5.0%)

Desempeño:
├─ Tasa de Completitud: 90.0%
├─ Tiempo Promedio: 142 días
└─ Calificación Promedio: 4.2/5.0

Directores Involucrados: 15
├─ Dr. Carlos García (4 instancias)
├─ Dra. María López (3 instancias)
└─ Dr. Pedro Martínez (3 instancias)

Distribución por Estado:
├─ Aprobado: 12 (60%)
├─ En Revisión: 5 (25%)
└─ Aprobado Consejo: 3 (15%)

Observaciones: Periodo con alto rendimiento, incremento
en modalidades grupales.
```

---

### **SECCIÓN 4: ANÁLISIS DE TENDENCIAS Y EVOLUCIÓN**

#### 4.1 Tendencia General

```
┌──────────────────────────────────────────────────────┐
│                                                      │
│  ↗ TENDENCIA GENERAL: EN CRECIMIENTO                │ ← Texto grande
│     Tasa: +15.8% en los últimos 4 años              │   (blanco)
│                                                      │
└──────────────────────────────────────────────────────┘
   ↑ Fondo: Verde (crecimiento), Rojo (declive), Dorado (estable)
```

#### 4.2 Gráfico de Evolución Temporal

```
EVOLUCIÓN DE INSTANCIAS POR PERIODO

25 │                           ★ PICO
   │                         ╱  ╲
20 │                       ╱      ╲
   │                     ╱          ╲
15 │       ╱╲          ╱              ╲    ╱
   │     ╱    ╲      ╱                  ╲╱
10 │   ╱        ╲  ╱
   │ ╱            ╲╱
 5 │╱               ★ MÍNIMO
   └─────────────────────────────────────────────
    2022 2022 2023 2023 2024 2024 2025 2025 2026
      -1   -2   -1   -2   -1   -2   -1   -2   -1

Leyenda:
  ↗ Crecimiento  ↘ Declive  → Estable
```

#### 4.3 Puntos de Evolución Detallados

```
PUNTOS DE CAMBIO SIGNIFICATIVOS

2022-1 → 2022-2   ↗ +37.5%   Crecimiento moderado
2022-2 → 2023-1   ↘ -25.0%   Declive temporal
2023-1 → 2023-2   ↗ +55.6%   Recuperación fuerte
2023-2 → 2024-1   ↘ -20.0%   Ajuste normal
2024-1 → 2024-2   ↗ +66.7%   BOOM - Pico histórico
2024-2 → 2025-1   ↘ -40.0%   Normalización post-pico
2025-1 → 2025-2   ↗ +33.3%   Crecimiento sostenido
2025-2 → 2026-1   ↘ -10.0%   Estabilización
```

#### 4.4 Patrones Identificados

```
🔍 PATRONES DETECTADOS AUTOMÁTICAMENTE

✓ Patrón 1: Crecimiento en segundos semestres
  Observado en: 2022-2, 2023-2, 2024-2, 2025-2
  Interpretación: Mayor demanda en segundo semestre

✓ Patrón 2: Pico cada 2 años
  Observado en: 2022-2, 2024-2
  Interpretación: Ciclo de promoción bienal

✓ Patrón 3: Preferencia por modalidades individuales
  Observado en: Todos los periodos (70% promedio)
  Interpretación: Estudiantes prefieren trabajo individual

⚠ Patrón 4: Aumento de abandonos en periodos largos
  Observado en: Periodos con >150 días promedio
  Interpretación: Requiere seguimiento más estricto
```

---

### **SECCIÓN 5: ANÁLISIS COMPARATIVO ENTRE PERIODOS**

#### 5.1 Actual vs Anterior

```
┌─────────────────────────────────────────────────────┐
│ COMPARATIVA: 2026-1 vs 2025-2                      │
├─────────────────────────────────────────────────────┤
│                                                     │
│              2026-1      2025-2      Cambio        │
│ Instancias:    18          20        -10.0% ↘     │
│ Estudiantes:   22          25        -12.0% ↘     │
│                                                     │
│ VEREDICTO: DECLIVE LEVE                            │
└─────────────────────────────────────────────────────┘
```

#### 5.2 Actual vs Mismo Semestre Año Anterior

```
┌─────────────────────────────────────────────────────┐
│ COMPARATIVA: 2026-1 vs 2025-1                      │
├─────────────────────────────────────────────────────┤
│                                                     │
│              2026-1      2025-1      Cambio        │
│ Instancias:    18          15        +20.0% ↗     │
│ Estudiantes:   22          18        +22.2% ↗     │
│                                                     │
│ VEREDICTO: MEJORA SIGNIFICATIVA                    │
└─────────────────────────────────────────────────────┘
```

#### 5.3 Mejor Periodo vs Peor Periodo

```
┌─────────────────────────────────────────────────────┐
│ COMPARATIVA: PICO (2024-2) vs MÍNIMO (2022-1)      │
├─────────────────────────────────────────────────────┤
│                                                     │
│              2024-2      2022-1      Diferencia    │
│ Instancias:    25           8        +212.5%       │
│ Estudiantes:   30          10        +200.0%       │
│ Tasa Éxito:   88.0%       75.0%      +13.0 pp     │
│                                                     │
│ ANÁLISIS: Crecimiento sostenido con mejora de      │
│ calidad. Pico en 2024-2 respaldado por incremento  │
│ en recursos docentes y mejora de procesos.         │
└─────────────────────────────────────────────────────┘
```

#### 5.4 Promedios Anuales

Tabla comparativa de promedios por año:

| Año | Prom. Instancias/Sem. | Prom. Estudiantes/Sem. | Tasa Completitud | Tiempo Promedio |
|-----|------------------------|------------------------|------------------|-----------------|
| 2026 | 18.0 | 22.0 | 90.0% | 135 días |
| 2025 | 17.5 | 21.5 | 88.3% | 150 días |
| 2024 | 20.0 | 24.0 | 87.5% | 145 días |
| 2023 | 13.5 | 16.5 | 85.0% | 165 días |
| 2022 | 11.0 | 13.5 | 82.5% | 170 días |

**Tendencia visible**: Mejora continua en todos los indicadores.

---

### **SECCIÓN 6: ESTADÍSTICAS DE DIRECTORES**

#### 6.1 Resumen de Participación Docente

```
PARTICIPACIÓN HISTÓRICA DE DIRECTORES

Total de Directores Únicos: 28 profesores
Directores Activos Actualmente: 12 profesores
Promedio de Instancias/Director: 5.18
Director Más Experimentado: Dr. Carlos López (15 instancias)
```

#### 6.2 Top 10 Directores de Todos los Tiempos

```
RANKING HISTÓRICO DE DIRECTORES

 1. Dr. Carlos López García          15 instancias │ 18 estudiantes │ 93.3% éxito
 2. Dra. María Rodríguez Pérez       12 instancias │ 14 estudiantes │ 91.7% éxito
 3. Dr. Pedro Martínez Torres        11 instancias │ 15 estudiantes │ 90.9% éxito
 4. Ing. Ana García Morales          10 instancias │ 12 estudiantes │ 90.0% éxito
 5. Dr. José Ramírez Luna             9 instancias │ 11 estudiantes │ 88.9% éxito
 6. Dra. Laura Fernández Ruiz         8 instancias │ 10 estudiantes │ 87.5% éxito
 7. Dr. David Gómez Castro            8 instancias │  9 estudiantes │ 87.5% éxito
 8. Ing. Sofía Herrera Díaz           7 instancias │  8 estudiantes │ 85.7% éxito
 9. Dr. Miguel Torres Vega            6 instancias │  7 estudiantes │ 83.3% éxito
10. Dra. Carmen Ortiz Silva           6 instancias │  7 estudiantes │ 83.3% éxito
```

#### 6.3 Top 5 Directores del Periodo Actual

```
DIRECTORES DESTACADOS EN 2026-1

 1. Dr. Carlos López         3 instancias │ 4 estudiantes  │ Periodos: 2026-1, 2025-2, 2025-1
 2. Dra. María Rodríguez     3 instancias │ 3 estudiantes  │ Periodos: 2026-1, 2025-2, 2024-2
 3. Dr. Pedro Martínez       2 instancias │ 3 estudiantes  │ Periodos: 2026-1, 2025-2
 4. Ing. Ana García          2 instancias │ 2 estudiantes  │ Periodos: 2026-1, 2025-1
 5. Dr. José Ramírez         2 instancias │ 2 estudiantes  │ Periodos: 2026-1, 2024-2
```

#### 6.4 Análisis de Experiencia

```
🎓 EXPERIENCIA Y ESPECIALIZACIÓN

• Dr. Carlos López: 15 instancias en 6 años
  Especialización: Sistemas de Información, Machine Learning
  Tasa de éxito histórica: 93.3%
  
• Dra. María Rodríguez: 12 instancias en 5 años
  Especialización: Desarrollo Web, Bases de Datos
  Tasa de éxito histórica: 91.7%
```

---

### **SECCIÓN 7: ESTADÍSTICAS DE ESTUDIANTES**

#### 7.1 Resumen de Participación Estudiantil

```
PARTICIPACIÓN HISTÓRICA DE ESTUDIANTES

Total Histórico de Estudiantes: 178
Estudiantes Actuales: 22
Promedio Estudiantes/Instancia: 1.23
Modalidad Más Grande (histórica): 4 estudiantes (grupo)
Modalidad Más Pequeña: 1 estudiante (individual)

Preferencia Histórica:
├─ Individual: 105 instancias (72.4%)
└─ Grupal: 40 instancias (27.6%)

VEREDICTO: Fuerte preferencia por trabajo individual
```

#### 7.2 Distribución de Estudiantes por Semestre

```
ESTUDIANTES POR PERIODO ACADÉMICO

2026-1 ████████████████░░░░  22 estudiantes
2025-2 ██████████████████░░  25 estudiantes (MÁXIMO)
2025-1 ███████████░░░░░░░░░  18 estudiantes
2024-2 ████████████████████  30 estudiantes
2024-1 ███████████░░░░░░░░░  18 estudiantes
2023-2 ████████████░░░░░░░░  20 estudiantes
2023-1 ████████░░░░░░░░░░░░  14 estudiantes
2022-2 ██████░░░░░░░░░░░░░░  12 estudiantes
2022-1 ████░░░░░░░░░░░░░░░░  10 estudiantes (MÍNIMO)
```

#### 7.3 Ratio Individual vs Grupal por Año

Tabla evolutiva:

| Año | Individual | Grupal | Ratio Ind:Grupo |
|-----|------------|--------|-----------------|
| 2026 | 12 (66.7%) | 6 (33.3%) | 2.0:1 |
| 2025 | 24 (68.6%) | 11 (31.4%) | 2.2:1 |
| 2024 | 32 (74.4%) | 11 (25.6%) | 2.9:1 |
| 2023 | 22 (73.3%) | 8 (26.7%) | 2.8:1 |
| 2022 | 15 (75.0%) | 5 (25.0%) | 3.0:1 |

**Tendencia**: Aumento gradual en modalidades grupales (+8% en 4 años).

---

### **SECCIÓN 8: ANÁLISIS DE DESEMPEÑO**

#### 8.1 Indicadores de Calidad

```
┌─────────────────────────────────────────────────────┐
│ EVALUACIÓN DE DESEMPEÑO: EXCELENTE                 │ ← Fondo verde
└─────────────────────────────────────────────────────┘

MÉTRICAS DE CALIDAD

✓ Tasa de Completitud General: 87.5%
  Benchmark: >80% = Excelente ✓

✓ Tasa de Éxito: 92.3%
  Benchmark: >85% = Excelente ✓

✓ Tasa de Abandono: 7.7%
  Benchmark: <15% = Excelente ✓

✓ Tiempo Promedio de Completitud: 145.8 días
  Benchmark: <180 días = Óptimo ✓
```

#### 8.2 Evolución de Tasas por Año

```
EVOLUCIÓN DE INDICADORES DE CALIDAD

             Completitud    Éxito    Abandono
2026 (actual)  90.0%       95.0%      5.0%     ← Mejor año
2025           88.3%       92.0%      8.0%
2024           87.5%       90.5%      9.5%
2023           85.0%       88.0%     12.0%
2022           82.5%       85.0%     15.0%     ← Año base

TENDENCIA: ↗ Mejora continua en todos los indicadores
```

#### 8.3 Análisis de Tiempos de Completitud

```
DISTRIBUCIÓN DE TIEMPOS DE COMPLETITUD

<90 días       ███░░░░░░░░░░░░░░░░░  12 instancias (8.3%)   RÁPIDO
90-120 días    ██████████░░░░░░░░░░  35 instancias (24.1%)  ÓPTIMO
121-150 días   ████████████████████  58 instancias (40.0%)  NORMAL
151-180 días   ████████░░░░░░░░░░░░  28 instancias (19.3%)  LARGO
>180 días      ████░░░░░░░░░░░░░░░░  12 instancias (8.3%)   CRÍTICO

Instancia Más Rápida:   58 días  (2025-1)
Instancia Más Lenta:    245 días (2022-2)
```

#### 8.4 Fortalezas y Áreas de Mejora

```
✅ FORTALEZAS IDENTIFICADAS

1. Alta tasa de completitud (87.5%) supera el estándar
2. Mejora continua en tasa de éxito (+10 pp en 4 años)
3. Excelente participación de directores experimentados
4. Tiempo promedio dentro del rango óptimo
5. Bajo índice de abandono en comparación con otras modalidades

⚠️ ÁREAS DE MEJORA

1. Reducir instancias que superan 180 días (8.3%)
2. Aumentar participación en modalidades grupales
3. Estandarizar tiempos de revisión entre directores
4. Implementar seguimiento más estricto en fase inicial
```

---

### **SECCIÓN 9: ESTADÍSTICAS POR ESTADO**

#### 9.1 Distribución Histórica de Estados

```
ESTADOS DE MODALIDADES (Histórico Completo)

Estado                    Cantidad    Porcentaje
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Aprobado                    85         58.6%
Completado Exitoso          45         31.0%
En Revisión                 8           5.5%
Aprobado Consejo            4           2.8%
Aprobado Secretaría         2           1.4%
Abandonado                  1           0.7%
```

#### 9.2 Tiempo Promedio por Estado

```
TIEMPO PROMEDIO EN CADA ESTADO

Pendiente Aprobación    ████░░░░░░░░░░  15 días
En Revisión             ████████░░░░░░  32 días
Aprobado Secretaría     ██████░░░░░░░░  25 días
Aprobado                ████████████████████  145 días (desarrollo)
Aprobado Consejo        ████░░░░░░░░░░  18 días
```

---

### **SECCIÓN 10: PROYECCIONES FUTURAS**

#### 10.1 Proyecciones Estadísticas

```
┌──────────────────────────────────────────────────────┐
│ 🔮 PROYECCIONES PARA PRÓXIMOS PERIODOS              │
├──────────────────────────────────────────────────────┤
│                                                      │
│ Próximo Semestre (2026-2):                          │
│   Instancias Proyectadas: 22 ± 3                    │
│   Estudiantes Proyectados: 27 ± 4                   │
│   Nivel de Confianza: 78%                           │
│                                                      │
│ Próximo Año (2027):                                 │
│   Instancias Proyectadas: 42 ± 6                    │
│   Estudiantes Proyectados: 52 ± 8                   │
│   Nivel de Confianza: 65%                           │
│                                                      │
└──────────────────────────────────────────────────────┘
```

**Método de cálculo**: Media móvil ponderada con tendencia lineal.

#### 10.2 Proyección de Demanda

```
┌─────────────────────────────────────────┐
│ DEMANDA PROYECTADA: ALTA                │ ← Fondo verde/naranja/rojo
└─────────────────────────────────────────┘

ALTA:   Crecimiento sostenido esperado
MEDIA:  Estabilidad con ligeras variaciones
BAJA:   Posible declive o estancamiento
```

#### 10.3 Oportunidades Identificadas

```
🚀 OPORTUNIDADES

✓ Aumentar cupos en 2026-2 basado en tendencia de crecimiento
✓ Promover modalidades grupales (actualmente 27.6%, meta: 40%)
✓ Capitalizar alta tasa de éxito (92.3%) en campañas de promoción
✓ Expandir áreas temáticas basado en preferencias estudiantiles
✓ Involucrar a 3-4 directores adicionales para sostener crecimiento
```

#### 10.4 Riesgos Identificados

```
⚠️ RIESGOS Y MITIGACIONES

⚠️ Riesgo 1: Sobrecarga de directores experimentados
   Mitigación: Distribuir carga a directores junior con mentoría

⚠️ Riesgo 2: Posible saturación de recursos en periodos pico
   Mitigación: Planificar aumento de espacios y equipamiento

⚠️ Riesgo 3: Dependencia de pocos directores (top 5 = 50%)
   Mitigación: Capacitar nuevos directores en el área

⚠️ Riesgo 4: Aumento de abandonos si tiempos exceden 180 días
   Mitigación: Implementar hitos de seguimiento cada 30 días
```

#### 10.5 Acciones Recomendadas

```
📋 PLAN DE ACCIÓN SUGERIDO

CORTO PLAZO (Próximo semestre):
  1. Asignar 2-3 directores adicionales para 2026-2
  2. Implementar seguimiento quincenal de instancias >120 días
  3. Promover modalidades grupales mediante incentivos

MEDIANO PLAZO (Próximo año):
  4. Capacitar 4 nuevos directores en metodologías de supervisión
  5. Estandarizar tiempos de revisión (meta: <30 días por fase)
  6. Implementar sistema de alertas automáticas

LARGO PLAZO (2-3 años):
  7. Expandir oferta temática basado en preferencias
  8. Establecer convenios interinstitucionales para pasantías
  9. Digitalizar proceso de seguimiento y evaluación
```

---

### **SECCIÓN 11: HALLAZGOS CLAVE**

```
🔑 HALLAZGOS PRINCIPALES

1. CRECIMIENTO SOSTENIDO
   La modalidad ha crecido +63.6% en 4 años, pasando de 11 a 
   18 instancias promedio por semestre.

2. MEJORA DE CALIDAD
   La tasa de éxito mejoró de 85% (2022) a 95% (2026), 
   indicando mejor supervisión y preparación.

3. ESTACIONALIDAD IDENTIFICADA
   Los segundos semestres tienen 25% más instancias que los 
   primeros, sugiriendo planificación diferenciada.

4. CONCENTRACIÓN DE DIRECTORES
   El 50% de las instancias son supervisadas por los top 5 
   directores, lo que representa un riesgo operativo.

5. PREFERENCIA INDIVIDUAL
   72.4% de estudiantes eligen modalidad individual, indicando 
   necesidad de fomentar trabajo colaborativo.

6. EFICIENCIA MEJORADA
   El tiempo promedio de completitud se redujo de 170 a 135 
   días (-20.6%), demostrando mejora en procesos.
```

---

### **SECCIÓN 12: ANÁLISIS GRÁFICO VISUAL**

#### 12.1 Gráfico de Línea de Tendencia

```
EVOLUCIÓN TEMPORAL DE INSTANCIAS

30 ┤
   ┤                               ╱★ Pico: 25
25 ┤                             ╱
   ┤                           ╱
20 ┤                       ╱─╲       ╱─╲
   ┤                     ╱     ╲   ╱     ╲
15 ┤               ╱───╱         ╲╱         ╲ Actual: 18
   ┤             ╱                            
10 ┤         ╱─╲
   ┤       ╱     ╲
 5 ┤  ★──╱         
   ┤  Mínimo: 8
 0 ┼────────────────────────────────────────────────
   2022 2022 2023 2023 2024 2024 2025 2025 2026
     -1   -2   -1   -2   -1   -2   -1   -2   -1

Línea de Tendencia: ↗ +15.8% anual
```

#### 12.2 Gráfico de Barras Comparativas

```
COMPARATIVA MULTI-MÉTRICA POR AÑO

            Instancias  Estudiantes  Completadas
2026 (YTD)  ████░░░░░   ████░░░░░    ████░░░░░     18  22  15
2025        █████░░░░   █████░░░░    █████░░░░     35  43  31
2024        ██████░░░   ██████░░░    ██████░░░     43  48  38
2023        ████░░░░░   ████░░░░░    ███░░░░░░     27  30  23
2022        ███░░░░░░   ███░░░░░░    ██░░░░░░░     19  23  16
```

#### 12.3 Gráfico de Torta - Distribución de Resultados

```
DISTRIBUCIÓN DE RESULTADOS (Histórico)

        Completadas 
        Exitosas
          87.5%
      ╱─────────╲
    ╱   ████████  ╲
   │   ████████    │
   │   ████████    │
    ╲   ████████  ╱
      ╲─────────╱
    7.7%  │  4.8%
 Abandonadas Canceladas
```

---

### **SECCIÓN 13: ANÁLISIS COMPARATIVO CON OTRAS MODALIDADES**

*Solo si hay datos disponibles*

```
POSICIONAMIENTO RELATIVO EN EL PROGRAMA

Ranking de Popularidad: 2° de 7 modalidades

Comparativa con la media del programa:

                        Esta Modalidad   Media Programa
Instancias/Semestre:         18.0             12.5       ↗ +44%
Estudiantes/Semestre:        22.0             15.3       ↗ +43%
Tasa de Éxito:              92.3%            86.5%       ↗ +6.8pp
Tiempo Completitud:         145.8d           158.2d      ↗ -7.8%

VEREDICTO: Por encima del promedio en todos los indicadores
```

---

### **SECCIÓN 14: CONCLUSIONES Y RECOMENDACIONES ESTRATÉGICAS**

```
══════════════════════════════════════════════════════
CONCLUSIONES EJECUTIVAS
══════════════════════════════════════════════════════

1. DESEMPEÑO GENERAL
   La modalidad muestra un desempeño EXCELENTE con mejora 
   continua en todos los indicadores clave durante el periodo 
   analizado (2022-2026).

2. TENDENCIA POSITIVA
   Crecimiento sostenido de +15.8% anual con proyección de 
   mantenimiento para próximos periodos. Demanda estudiantil 
   en aumento.

3. CALIDAD SUPERIOR
   Tasa de éxito de 92.3% supera significativamente el 
   benchmark institucional (85%), demostrando efectividad 
   del modelo de supervisión.

4. EFICIENCIA OPERATIVA
   Reducción de 20.6% en tiempos de completitud refleja 
   mejoras en procesos y mayor experiencia de directores.

5. RETO DE CAPACIDAD
   El crecimiento proyectado requiere planificación de 
   recursos (directores, espacios, equipamiento) para 
   mantener calidad.

══════════════════════════════════════════════════════
RECOMENDACIONES ESTRATÉGICAS
══════════════════════════════════════════════════════

PARA JEFATURA DE PROGRAMA:

✓ Continuar promoción de esta modalidad (alta demanda y éxito)
✓ Planificar incremento de 20-25% en cupos para 2027
✓ Asignar presupuesto para equipamiento adicional
✓ Evaluar posibilidad de ofrecer en modalidad de verano

PARA GESTIÓN DE DIRECTORES:

✓ Incorporar 3-4 nuevos directores en 2026-2
✓ Implementar programa de mentoría director senior-junior
✓ Distribuir carga más equitativamente (reducir dependencia top 5)
✓ Reconocer desempeño destacado de top 3 directores

PARA MEJORA DE PROCESOS:

✓ Estandarizar tiempos de revisión en cada fase
✓ Implementar sistema de alertas para instancias >150 días
✓ Crear protocolo de seguimiento para grupos (menor éxito)
✓ Digitalizar formularios y trámites (reducir tiempos)

PARA ESTUDIANTES:

✓ Promover modalidades grupales mediante talleres y beneficios
✓ Publicar casos de éxito para motivar participación
✓ Ofrecer asesorías de inicio para reducir abandonos
✓ Crear guías de mejores prácticas basadas en histórico
```

---

### **PIE DE PÁGINA (Todas las Páginas)**

```
──────────────────────────────────────────────────────
Página 5 | Reporte Histórico: PROYECTO DE GRADO | Ingeniería de Sistemas | 18/02/2026
──────────────────────────────────────────────────────
```

---

## 💻 Ejemplos de Código

### Ejemplo 1: JavaScript/TypeScript (Frontend)

```typescript
interface ModalityHistoricalParams {
  modalityTypeId: number;
  periods?: number; // Default: 8
}

async function downloadHistoricalReport(params: ModalityHistoricalParams) {
  const token = localStorage.getItem('auth_token');
  const { modalityTypeId, periods = 8 } = params;
  
  try {
    console.log(`📊 Generando reporte histórico de modalidad ${modalityTypeId}...`);
    
    const url = `http://localhost:8080/reports/modalities/${modalityTypeId}/historical/pdf?periods=${periods}`;
    
    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.error || 'Error al generar reporte');
    }
    
    const blob = await response.blob();
    const downloadUrl = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = downloadUrl;
    
    // Extraer nombre del archivo del header
    const contentDisposition = response.headers.get('Content-Disposition');
    const filename = contentDisposition 
      ? contentDisposition.split('filename=')[1].replace(/"/g, '')
      : `Reporte_Historico_Modalidad_${modalityTypeId}.pdf`;
    
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    
    window.URL.revokeObjectURL(downloadUrl);
    document.body.removeChild(a);
    
    console.log('✅ Reporte histórico descargado exitosamente');
    
    // Mostrar información del reporte
    const totalRecords = response.headers.get('X-Total-Records');
    console.log(`📈 Periodos analizados: ${totalRecords}`);
    
  } catch (error) {
    console.error('❌ Error:', error);
    alert(`Error al generar reporte histórico: ${error.message}`);
    throw error;
  }
}

// Uso básico
downloadHistoricalReport({ modalityTypeId: 1 });

// Uso con más periodos
downloadHistoricalReport({ modalityTypeId: 1, periods: 12 });

// Uso con validación
async function generateReportWithValidation(modalityTypeId: number) {
  try {
    // Validar modalidad antes de generar
    const response = await fetch(`http://localhost:8080/api/modality-types/${modalityTypeId}`);
    if (!response.ok) {
      throw new Error('Modalidad no encontrada');
    }
    
    const modalityData = await response.json();
    console.log(`Generando reporte para: ${modalityData.name}`);
    
    await downloadHistoricalReport({ modalityTypeId, periods: 8 });
  } catch (error) {
    alert('No se pudo generar el reporte. Verifica el ID de la modalidad.');
  }
}
```

---

### Ejemplo 2: React Component Completo

```jsx
import React, { useState, useEffect } from 'react';
import axios from 'axios';

function ModalityHistoricalReportGenerator() {
  const [modalityTypes, setModalityTypes] = useState([]);
  const [selectedModality, setSelectedModality] = useState(null);
  const [periods, setPeriods] = useState(8);
  const [loading, setLoading] = useState(false);
  const [reportInfo, setReportInfo] = useState(null);
  
  // Cargar tipos de modalidad disponibles
  useEffect(() => {
    loadModalityTypes();
  }, []);
  
  const loadModalityTypes = async () => {
    try {
      const token = localStorage.getItem('auth_token');
      const response = await axios.get(
        'http://localhost:8080/api/modality-types',
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setModalityTypes(response.data);
    } catch (error) {
      console.error('Error cargando modalidades:', error);
    }
  };
  
  const downloadReport = async () => {
    if (!selectedModality) {
      alert('Por favor selecciona una modalidad');
      return;
    }
    
    setLoading(true);
    setReportInfo(null);
    
    try {
      const token = localStorage.getItem('auth_token');
      const url = `http://localhost:8080/reports/modalities/${selectedModality}/historical/pdf?periods=${periods}`;
      
      console.log(`📊 Generando reporte histórico...`);
      console.log(`   Modalidad: ${selectedModality}`);
      console.log(`   Periodos: ${periods}`);
      
      const response = await axios.get(url, {
        headers: {
          'Authorization': `Bearer ${token}`
        },
        responseType: 'blob',
        onDownloadProgress: (progressEvent) => {
          const percentCompleted = Math.round(
            (progressEvent.loaded * 100) / progressEvent.total
          );
          console.log(`Descargando: ${percentCompleted}%`);
        }
      });
      
      // Crear descarga
      const blob = new Blob([response.data], { type: 'application/pdf' });
      const downloadUrl = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = downloadUrl;
      
      // Obtener nombre de la modalidad para el archivo
      const modality = modalityTypes.find(m => m.id === parseInt(selectedModality));
      const modalityName = modality 
        ? modality.name.replace(/[^a-zA-Z0-9]/g, '_')
        : 'Modalidad';
      
      link.download = `Reporte_Historico_${modalityName}_${new Date().toISOString().split('T')[0]}.pdf`;
      document.body.appendChild(link);
      link.click();
      link.remove();
      
      // Extraer info del reporte
      const totalRecords = response.headers['x-total-records'];
      const generatedAt = response.headers['x-report-generated-at'];
      
      setReportInfo({
        modalityName: modality?.name || 'Desconocida',
        periodsAnalyzed: totalRecords || periods,
        generatedAt: generatedAt || new Date().toISOString(),
        fileSize: (blob.size / 1024).toFixed(2) + ' KB'
      });
      
      alert('✅ Reporte histórico descargado exitosamente');
      
    } catch (error) {
      console.error('❌ Error:', error);
      
      if (error.response?.status === 400) {
        alert('Modalidad no encontrada o ID inválido');
      } else if (error.response?.status === 403) {
        alert('No tienes permisos para generar este reporte');
      } else {
        alert('Error al generar el reporte histórico');
      }
    } finally {
      setLoading(false);
    }
  };
  
  const getModalityById = (id) => {
    return modalityTypes.find(m => m.id === parseInt(id));
  };
  
  return (
    <div className="historical-report-generator">
      <h2>📈 Reporte Histórico de Modalidad</h2>
      <p className="description">
        Análisis temporal completo con tendencias, estadísticas y proyecciones
      </p>
      
      <div className="form-group">
        <label>
          <strong>Tipo de Modalidad:</strong>
          <select 
            value={selectedModality || ''} 
            onChange={e => setSelectedModality(e.target.value)}
            disabled={loading}
          >
            <option value="">-- Selecciona una modalidad --</option>
            {modalityTypes.map(modality => (
              <option key={modality.id} value={modality.id}>
                {modality.name} (ID: {modality.id})
              </option>
            ))}
          </select>
        </label>
        
        <label>
          <strong>Periodos a Analizar:</strong>
          <input 
            type="number" 
            min="2" 
            max="20" 
            value={periods}
            onChange={e => setPeriods(parseInt(e.target.value) || 8)}
            disabled={loading}
          />
          <small>
            {periods} periodos = {Math.floor(periods / 2)} años 
            {periods % 2 === 1 ? ' y medio' : ''}
          </small>
        </label>
      </div>
      
      {selectedModality && (
        <div className="selected-modality-info">
          <h4>Modalidad Seleccionada:</h4>
          <p><strong>{getModalityById(selectedModality)?.name}</strong></p>
          <p>Se analizarán los últimos <strong>{periods} semestres</strong></p>
        </div>
      )}
      
      <button 
        onClick={downloadReport} 
        disabled={loading || !selectedModality}
        className="btn-primary"
      >
        {loading ? (
          <>
            <span className="spinner"></span>
            ⏳ Generando reporte histórico...
          </>
        ) : (
          '📥 Descargar Reporte Histórico PDF'
        )}
      </button>
      
      {reportInfo && (
        <div className="report-success-info">
          <h4>✅ Reporte Generado Exitosamente</h4>
          <ul>
            <li><strong>Modalidad:</strong> {reportInfo.modalityName}</li>
            <li><strong>Periodos analizados:</strong> {reportInfo.periodsAnalyzed}</li>
            <li><strong>Generado:</strong> {new Date(reportInfo.generatedAt).toLocaleString()}</li>
            <li><strong>Tamaño:</strong> {reportInfo.fileSize}</li>
          </ul>
        </div>
      )}
    </div>
  );
}

export default ModalityHistoricalReportGenerator;
```

---

### Ejemplo 3: PowerShell - Batch de Reportes

```powershell
# Script para generar reportes históricos de todas las modalidades
# Útil para archivos anuales o documentación institucional

$token = "tu_token_jwt_aqui"
$baseUrl = "http://localhost:8080/reports/modalities"
$outputDir = "Reportes_Historicos_2026"

# Crear directorio si no existe
if (!(Test-Path $outputDir)) {
    New-Item -ItemType Directory -Path $outputDir | Out-Null
    Write-Host "📁 Directorio creado: $outputDir" -ForegroundColor Green
}

# Headers
$headers = @{
    "Authorization" = "Bearer $token"
}

# IDs de modalidades a analizar
$modalidades = @(
    @{Id = 1; Nombre = "Proyecto_de_Grado"; Periodos = 12},
    @{Id = 5; Nombre = "Pasantia"; Periodos = 8},
    @{Id = 7; Nombre = "Practica_Profesional"; Periodos = 8},
    @{Id = 8; Nombre = "Seminario_de_Grado"; Periodos = 10},
    @{Id = 10; Nombre = "Emprendimiento"; Periodos = 6}
)

Write-Host "`n🚀 Iniciando generación de reportes históricos..." -ForegroundColor Cyan
Write-Host "Total de modalidades: $($modalidades.Count)`n" -ForegroundColor Cyan

$exitosos = 0
$fallidos = 0

foreach ($modalidad in $modalidades) {
    $id = $modalidad.Id
    $nombre = $modalidad.Nombre
    $periodos = $modalidad.Periodos
    
    Write-Host "📊 Procesando: $nombre (ID: $id, Periodos: $periodos)..." -ForegroundColor Yellow
    
    try {
        $url = "$baseUrl/$id/historical/pdf?periods=$periodos"
        $outputFile = Join-Path $outputDir "Reporte_Historico_${nombre}_$(Get-Date -Format 'yyyyMMdd').pdf"
        
        $response = Invoke-WebRequest `
            -Uri $url `
            -Method Get `
            -Headers $headers `
            -OutFile $outputFile `
            -PassThru
        
        $fileSize = [Math]::Round((Get-Item $outputFile).Length / 1KB, 2)
        $totalRecords = $response.Headers['X-Total-Records']
        
        Write-Host "   ✅ Descargado: $outputFile" -ForegroundColor Green
        Write-Host "   📄 Tamaño: $fileSize KB | Periodos: $totalRecords" -ForegroundColor Gray
        
        $exitosos++
        
        Start-Sleep -Milliseconds 500 # Evitar sobrecarga del servidor
        
    } catch {
        Write-Host "   ❌ Error: $_" -ForegroundColor Red
        $fallidos++
    }
}

Write-Host "`n════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "📊 RESUMEN DE GENERACIÓN" -ForegroundColor Cyan
Write-Host "════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "Exitosos: $exitosos" -ForegroundColor Green
Write-Host "Fallidos: $fallidos" -ForegroundColor Red
Write-Host "Directorio: $outputDir" -ForegroundColor Gray
Write-Host "`n✨ Proceso completado!" -ForegroundColor Green

# Abrir el directorio
explorer.exe $outputDir
```

---

### Ejemplo 4: Python - Cliente Avanzado

```python
import requests
from datetime import datetime
from typing import Optional, Dict, List
import os

class ModalityHistoricalReportClient:
    """
    Cliente para generar reportes históricos de modalidades
    """
    
    def __init__(self, base_url: str, token: str):
        self.base_url = base_url
        self.headers = {"Authorization": f"Bearer {token}"}
    
    def get_available_modalities(self) -> List[Dict]:
        """Obtiene lista de modalidades disponibles"""
        try:
            response = requests.get(
                f"{self.base_url}/api/modality-types",
                headers=self.headers
            )
            response.raise_for_status()
            return response.json()
        except Exception as e:
            print(f"❌ Error obteniendo modalidades: {e}")
            return []
    
    def download_historical_report(
        self, 
        modality_type_id: int, 
        periods: int = 8,
        output_dir: str = "reportes"
    ) -> Optional[str]:
        """
        Descarga reporte histórico de una modalidad
        
        Args:
            modality_type_id: ID del tipo de modalidad
            periods: Número de periodos a analizar (default: 8)
            output_dir: Directorio de salida
            
        Returns:
            Ruta del archivo descargado o None si falla
        """
        url = f"{self.base_url}/reports/modalities/{modality_type_id}/historical/pdf"
        params = {"periods": periods}
        
        try:
            print(f"📊 Generando reporte histórico...")
            print(f"   Modalidad ID: {modality_type_id}")
            print(f"   Periodos: {periods} ({periods // 2} años)")
            
            response = requests.get(
                url,
                headers=self.headers,
                params=params,
                stream=True
            )
            
            if response.status_code == 200:
                # Crear directorio si no existe
                os.makedirs(output_dir, exist_ok=True)
                
                # Generar nombre de archivo
                timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
                filename = f"Reporte_Historico_Modalidad_{modality_type_id}_{timestamp}.pdf"
                filepath = os.path.join(output_dir, filename)
                
                # Guardar archivo
                with open(filepath, 'wb') as f:
                    for chunk in response.iter_content(chunk_size=8192):
                        f.write(chunk)
                
                # Información del reporte
                file_size_kb = os.path.getsize(filepath) / 1024
                total_records = response.headers.get('X-Total-Records', 'N/A')
                generated_at = response.headers.get('X-Report-Generated-At', 'N/A')
                
                print(f"✅ Reporte descargado exitosamente")
                print(f"   Archivo: {filepath}")
                print(f"   Tamaño: {file_size_kb:.2f} KB")
                print(f"   Periodos: {total_records}")
                print(f"   Generado: {generated_at}")
                
                return filepath
            
            elif response.status_code == 400:
                error = response.json()
                print(f"❌ Error 400: {error.get('error')}")
                print("   Verifica que el ID de modalidad sea correcto")
                return None
            
            elif response.status_code == 403:
                print("❌ Error 403: Sin permisos para generar reportes")
                return None
            
            else:
                print(f"❌ Error {response.status_code}: {response.text}")
                return None
                
        except Exception as e:
            print(f"❌ Excepción: {str(e)}")
            return None
    
    def batch_download_all_modalities(
        self, 
        periods: int = 8,
        output_dir: str = "reportes_batch"
    ) -> Dict[str, int]:
        """
        Descarga reportes históricos de todas las modalidades
        
        Returns:
            Diccionario con contadores de éxito/fallo
        """
        modalities = self.get_available_modalities()
        
        if not modalities:
            print("❌ No se pudieron obtener modalidades")
            return {"success": 0, "failed": 0}
        
        print(f"\n🚀 Generando {len(modalities)} reportes históricos...")
        print(f"   Periodos por reporte: {periods}")
        print(f"   Directorio: {output_dir}\n")
        
        success_count = 0
        failed_count = 0
        
        for i, modality in enumerate(modalities, 1):
            print(f"[{i}/{len(modalities)}] {modality['name']}...", end=" ")
            
            filepath = self.download_historical_report(
                modality['id'],
                periods,
                output_dir
            )
            
            if filepath:
                success_count += 1
                print("✓")
            else:
                failed_count += 1
                print("✗")
        
        print("\n" + "="*50)
        print(f"📊 RESUMEN:")
        print(f"   Exitosos: {success_count}")
        print(f"   Fallidos: {failed_count}")
        print(f"   Directorio: {output_dir}")
        print("="*50)
        
        return {"success": success_count, "failed": failed_count}

# Uso
client = ModalityHistoricalReportClient(
    base_url="http://localhost:8080",
    token="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
)

# Reporte individual
client.download_historical_report(
    modality_type_id=1,
    periods=12
)

# Batch de todos
results = client.batch_download_all_modalities(periods=8)
print(f"Generados {results['success']} reportes")
```

---

### Ejemplo 5: cURL (Bash/Unix)

```bash
#!/bin/bash

TOKEN="tu_token_jwt_aqui"
BASE_URL="http://localhost:8080/reports/modalities"
OUTPUT_DIR="reportes_historicos"

# Crear directorio
mkdir -p "$OUTPUT_DIR"

# Función para descargar reporte
download_report() {
    local modality_id=$1
    local periods=$2
    local nombre=$3
    
    echo "📊 Generando reporte: $nombre (ID: $modality_id, Periodos: $periods)"
    
    curl -X GET \
        -H "Authorization: Bearer $TOKEN" \
        -o "${OUTPUT_DIR}/Reporte_Historico_${nombre}_$(date +%Y%m%d).pdf" \
        -w "\n✅ HTTP Status: %{http_code} | Tamaño: %{size_download} bytes\n" \
        "${BASE_URL}/${modality_id}/historical/pdf?periods=${periods}"
}

# Descargar reportes
echo "🚀 Iniciando generación de reportes históricos..."

download_report 1 12 "Proyecto_de_Grado"
download_report 5 8 "Pasantia"
download_report 7 8 "Practica_Profesional"

echo "✨ Proceso completado!"
ls -lh "$OUTPUT_DIR"
```

---

## 📊 Estructura de Datos del Reporte

### ModalityHistoricalReportDTO (Completo)

```typescript
interface ModalityHistoricalReportDTO {
  // Información básica
  generatedAt: string;
  generatedBy: string;
  academicProgramId: number;
  academicProgramName: string;
  academicProgramCode: string;
  
  // Información de la modalidad
  modalityInfo: ModalityInfoDTO;
  
  // Estado actual
  currentState: CurrentStateDTO;
  
  // Análisis histórico (array de periodos)
  historicalAnalysis: AcademicPeriodAnalysisDTO[];
  
  // Tendencias
  trendsEvolution: TrendsEvolutionDTO;
  
  // Comparativas
  comparativeAnalysis: ComparativeAnalysisDTO;
  
  // Estadísticas de directores
  directorStatistics: DirectorStatisticsDTO;
  
  // Estadísticas de estudiantes
  studentStatistics: StudentStatisticsDTO;
  
  // Análisis de desempeño
  performanceAnalysis: PerformanceAnalysisDTO;
  
  // Proyecciones futuras
  projections: ProjectionsDTO;
  
  // Metadata
  metadata: ReportMetadataDTO;
}
```

---

### ModalityInfoDTO

```typescript
interface ModalityInfoDTO {
  modalityId: number;                     // ID del tipo de modalidad
  modalityName: string;                   // Nombre
  modalityCode: string;                   // Código
  description: string;                    // Descripción
  requiresDirector: boolean;              // Requiere director
  modalityType: string;                   // INDIVIDUAL, GROUP
  isActive: boolean;                      // Está activa
  createdAt: string;                      // Fecha de creación
  yearsActive: number;                    // Años de operación
  totalHistoricalInstances: number;       // Total histórico
}
```

**Ejemplo**:
```json
{
  "modalityId": 1,
  "modalityName": "PROYECTO DE GRADO",
  "modalityCode": "MOD-PG-001",
  "description": "Desarrollo de proyecto de investigación aplicado...",
  "requiresDirector": true,
  "modalityType": "INDIVIDUAL",
  "isActive": true,
  "createdAt": "2010-08-15T00:00:00",
  "yearsActive": 15,
  "totalHistoricalInstances": 145
}
```

---

### CurrentStateDTO

```typescript
interface CurrentStateDTO {
  currentPeriodYear: number;              // Año actual
  currentPeriodSemester: number;          // Semestre actual
  activeInstances: number;                // Instancias activas
  totalStudentsEnrolled: number;          // Estudiantes inscritos
  assignedDirectors: number;              // Directores asignados
  completedInstances: number;             // Completadas
  inProgressInstances: number;            // En progreso
  inReviewInstances: number;              // En revisión
  averageCompletionDays: number;          // Días promedio
  currentPopularity: string;              // HIGH, MEDIUM, LOW
  positionInRanking: number;              // Posición en ranking
}
```

---

### AcademicPeriodAnalysisDTO

```typescript
interface AcademicPeriodAnalysisDTO {
  year: number;                           // Año del periodo
  semester: number;                       // Semestre (1 o 2)
  periodLabel: string;                    // Etiqueta (ej: "2024-2")
  totalInstances: number;                 // Total instancias
  studentsEnrolled: number;               // Estudiantes inscritos
  individualInstances: number;            // Instancias individuales
  groupInstances: number;                 // Instancias grupales
  completedSuccessfully: number;          // Completadas exitosas
  abandoned: number;                      // Abandonadas
  cancelled: number;                      // Canceladas
  completionRate: number;                 // Tasa completitud (%)
  averageCompletionDays: number;          // Días promedio
  directorsInvolved: number;              // Directores involucrados
  topDirectors: string[];                 // Top 3 directores
  averageGrade: number;                   // Calificación promedio
  distributionByStatus: {[key: string]: number}; // Por estado
  observations: string;                   // Observaciones
}
```

**Ejemplo**:
```json
{
  "year": 2025,
  "semester": 2,
  "periodLabel": "2025-2",
  "totalInstances": 20,
  "studentsEnrolled": 25,
  "individualInstances": 14,
  "groupInstances": 6,
  "completedSuccessfully": 18,
  "abandoned": 1,
  "cancelled": 1,
  "completionRate": 90.0,
  "averageCompletionDays": 142.5,
  "directorsInvolved": 15,
  "topDirectors": ["Dr. Carlos García", "Dra. María López", "Dr. Pedro Martínez"],
  "averageGrade": 4.2,
  "distributionByStatus": {
    "APROBADO": 12,
    "EN_REVISION": 5,
    "APROBADO_CONSEJO": 3
  },
  "observations": "Periodo con alto rendimiento"
}
```

---

### TrendsEvolutionDTO

```typescript
interface TrendsEvolutionDTO {
  overallTrend: string;                   // GROWING, STABLE, DECLINING
  growthRate: number;                     // Tasa crecimiento (%)
  peakYear: number;                       // Año pico
  peakSemester: number;                   // Semestre pico
  peakInstances: number;                  // Instancias en pico
  lowestYear: number;                     // Año mínimo
  lowestSemester: number;                 // Semestre mínimo
  lowestInstances: number;                // Instancias en mínimo
  evolutionPoints: TrendPointDTO[];       // Puntos de evolución
  popularityTrend: string;                // Tendencia popularidad
  completionTrend: string;                // Tendencia completitud
  identifiedPatterns: string[];           // Patrones detectados
}
```

**Ejemplo**:
```json
{
  "overallTrend": "GROWING",
  "growthRate": 15.8,
  "peakYear": 2024,
  "peakSemester": 2,
  "peakInstances": 25,
  "lowestYear": 2022,
  "lowestSemester": 1,
  "lowestInstances": 8,
  "evolutionPoints": [
    {
      "period": "2022-1→2022-2",
      "value": 37.5,
      "indicator": "UP",
      "changePercentage": 37.5
    }
    // ... más puntos
  ],
  "popularityTrend": "INCREASING",
  "completionTrend": "IMPROVING",
  "identifiedPatterns": [
    "Crecimiento en segundos semestres",
    "Pico cada 2 años",
    "Preferencia por modalidades individuales"
  ]
}
```

---

### PerformanceAnalysisDTO

```typescript
interface PerformanceAnalysisDTO {
  overallCompletionRate: number;          // Tasa completitud (%)
  averageCompletionTimeDays: number;      // Tiempo promedio (días)
  successRate: number;                    // Tasa éxito (%)
  abandonmentRate: number;                // Tasa abandono (%)
  fastestCompletionDays: number;          // Más rápida
  slowestCompletionDays: number;          // Más lenta
  completionRateByYear: {[year: string]: number}; // Por año
  successRateByYear: {[year: string]: number};    // Por año
  performanceVerdict: string;             // EXCELLENT, GOOD, REGULAR, NEEDS_IMPROVEMENT
  strengthPoints: string[];               // Fortalezas
  improvementAreas: string[];             // Áreas de mejora
}
```

**Ejemplo**:
```json
{
  "overallCompletionRate": 87.5,
  "averageCompletionTimeDays": 145.8,
  "successRate": 92.3,
  "abandonmentRate": 7.7,
  "fastestCompletionDays": 58,
  "slowestCompletionDays": 245,
  "completionRateByYear": {
    "2026": 90.0,
    "2025": 88.3,
    "2024": 87.5,
    "2023": 85.0,
    "2022": 82.5
  },
  "successRateByYear": {
    "2026": 95.0,
    "2025": 92.0,
    "2024": 90.5,
    "2023": 88.0,
    "2022": 85.0
  },
  "performanceVerdict": "EXCELLENT",
  "strengthPoints": [
    "Alta tasa de completitud supera estándar (87.5% vs 80%)",
    "Mejora continua en tasa de éxito (+10pp en 4 años)",
    "Tiempo promedio dentro del rango óptimo (<180 días)",
    "Bajo índice de abandono (7.7% vs 15% promedio)"
  ],
  "improvementAreas": [
    "Reducir instancias >180 días (actualmente 8.3%)",
    "Aumentar participación en modalidades grupales",
    "Estandarizar tiempos de revisión entre directores"
  ]
}
```

---

### ProjectionsDTO

```typescript
interface ProjectionsDTO {
  projectedNextSemester: number;          // Proyección próximo semestre
  projectedNextYear: number;              // Proyección próximo año
  demandProjection: string;               // HIGH, MEDIUM, LOW
  recommendedActions: string;             // Acciones recomendadas
  opportunities: string[];                // Oportunidades
  risks: string[];                        // Riesgos
  confidenceLevel: number;                // Confianza (0-100)
}
```

**Ejemplo**:
```json
{
  "projectedNextSemester": 22,
  "projectedNextYear": 42,
  "demandProjection": "HIGH",
  "recommendedActions": "Aumentar cupos en 20-25% y asignar 3 directores adicionales",
  "opportunities": [
    "Aumentar cupos basado en crecimiento sostenido",
    "Promover modalidades grupales (meta: 40%)",
    "Expandir áreas temáticas"
  ],
  "risks": [
    "Sobrecarga de directores experimentados",
    "Saturación de recursos en periodos pico",
    "Dependencia de pocos directores (top 5 = 50%)"
  ],
  "confidenceLevel": 78.0
}
```

---

## 🔍 Análisis Proporcionado

### 1. Análisis Temporal
- Evolución periodo a periodo
- Identificación de tendencias (crecimiento/declive)
- Detección de ciclos y estacionalidad
- Puntos de inflexión importantes

### 2. Análisis Comparativo
- Actual vs anterior
- Actual vs mismo periodo año anterior
- Mejor periodo vs peor periodo
- Promedios anuales evolutivos

### 3. Análisis de Directores
- Ranking histórico de participación
- Directores actuales más activos
- Análisis de experiencia y especialización
- Distribución de carga temporal

### 4. Análisis de Estudiantes
- Participación histórica total
- Preferencias (individual vs grupal)
- Distribución por periodo
- Tamaños de grupo históricos

### 5. Análisis de Desempeño
- Tasas de completitud, éxito y abandono
- Tiempos de completitud (distribución)
- Evolución de calidad por año
- Fortalezas y áreas de mejora

### 6. Análisis Predictivo
- Proyecciones para próximos periodos
- Nivel de demanda esperado
- Oportunidades y riesgos
- Acciones recomendadas

---

## 🎯 Usos Estratégicos

### Para Planificación Académica
- 📅 Dimensionar oferta de cupos por semestre
- 👥 Planificar necesidades de directores
- 📊 Identificar periodos de alta demanda
- 🎯 Establecer metas basadas en histórico

### Para Mejora Continua
- 📈 Identificar tendencias de mejora
- 📉 Detectar declives tempranos
- 🔍 Analizar causas de éxito/fracaso
- ⚙️ Ajustar procesos basados en datos

### Para Toma de Decisiones
- 💼 Fundamentar cambios curriculares
- 📋 Evaluar pertinencia de la modalidad
- 💰 Justificar inversiones en recursos
- 🎓 Proponer nuevas modalidades similares

### Para Reportes Institucionales
- 📊 Generar estadísticas oficiales
- 📑 Documentar evolución histórica
- 🏆 Evidenciar logros y mejoras
- 📈 Reportar a entidades externas

---

## 📈 Interpretación de Tendencias

### Tendencia: GROWING (↗)

**Indicadores**:
- Tasa de crecimiento > +5% anual
- Incremento sostenido en últimos 3+ periodos
- Proyección positiva

**Interpretación**:
- ✅ Modalidad exitosa y en demanda
- ✅ Estudiantes satisfechos
- ✅ Pertinencia académica confirmada

**Acciones**:
- Aumentar capacidad (cupos, directores)
- Mantener estándares de calidad
- Capitalizar el momentum

---

### Tendencia: STABLE (→)

**Indicadores**:
- Tasa de crecimiento entre -5% y +5%
- Variaciones menores entre periodos
- Comportamiento predecible

**Interpretación**:
- ➡️ Modalidad consolidada
- ➡️ Demanda estable
- ➡️ Situación controlada

**Acciones**:
- Mantener seguimiento periódico
- Evaluar oportunidades de innovación
- Optimizar procesos existentes

---

### Tendencia: DECLINING (↘)

**Indicadores**:
- Tasa de crecimiento < -5% anual
- Disminución en múltiples periodos
- Proyección negativa

**Interpretación**:
- ⚠️ Pérdida de interés estudiantil
- ⚠️ Posible obsolescencia
- ⚠️ Problemas operativos

**Acciones**:
- Investigar causas del declive
- Evaluar pertinencia y actualización
- Considerar rediseño o fusión
- Mejorar comunicación y promoción

---

## 🎨 Elementos Visuales Avanzados

### 1. Línea de Tiempo Histórica

```
LÍNEA DE TIEMPO - HITOS IMPORTANTES

2010 ●─────────────────────────────────────────────● 2026
     │                                             │
     ● 2010: Creación de la modalidad             ● 2026: Análisis actual
     │                                             │
     ● 2015: Primera revisión curricular          ● 2025: Pico histórico
     │                                             │
     ● 2020: Implementación digital               ● 2024: Mejora de procesos
     │                                             
     ● 2022: Normalización post-pandemia
```

---

### 2. Mapa de Calor por Año y Semestre

```
MAPA DE CALOR - INSTANCIAS POR PERIODO

        Semestre 1    Semestre 2
2026      18 🟢         --
2025      15 🟡         20 🟢
2024      18 🟢         25 🔴 PICO
2023      12 🟡         15 🟡
2022       8 ⚪ MIN     11 🟡

Escala:
⚪ 1-10   🟡 11-18   🟢 19-22   🔴 23+
```

---

### 3. Embudo de Conversión

```
EMBUDO DE PROGRESO (Promedio Histórico)

Inicio               │ 100% (18 instancias)
  ↓                  │
Aprobación           │  95% (17 instancias)
  ↓                  │
En Desarrollo        │  92% (16 instancias)
  ↓                  │
Revisión Final       │  90% (16 instancias)
  ↓                  │
Completadas Exitosas │  87% (15 instancias)

Tasa de Conversión Final: 87%
```

---

### 4. Gráfico de Dispersión - Días vs Éxito

```
RELACIÓN ENTRE TIEMPO Y TASA DE ÉXITO

100% │     ★  ★     ★           
 95% │   ★  ★  ★  ★  ★ ★        Tendencia: A mayor
 90% │  ★   ★     ★   ★         tiempo, menor éxito
 85% │ ★      ★      ★   ★      (correlación -0.65)
 80% │           ★         ★
 75% │                       ★
     └────────────────────────────
      50  100  150  200  250 días
      
Zona Óptima: 90-150 días (93% éxito promedio)
Zona Crítica: >180 días (78% éxito promedio)
```

---

## 🔄 Flujo Completo de Procesamiento

```
Usuario solicita: GET /reports/modalities/{id}/historical/pdf?periods=8
        ↓
Autenticación JWT → Valida token
        ↓
Verifica permiso PERM_VIEW_REPORT
        ↓
Extrae programa académico del usuario
        ↓
ReportService.generateModalityHistoricalReport(id, periods)
        ↓
┌──────────────────────────────────────────────────────┐
│ FASE 1: Validación y Obtención de Datos Base        │
├──────────────────────────────────────────────────────┤
│ • Validar que modalityTypeId existe                  │
│ • Validar que modalidad pertenece al programa        │
│ • Obtener información básica de la modalidad         │
│ • Determinar periodos a analizar (8 por defecto)     │
└──────────────────────────────────────────────────────┘
        ↓
┌──────────────────────────────────────────────────────┐
│ FASE 2: Recopilación de Datos Históricos            │
├──────────────────────────────────────────────────────┤
│ Para cada periodo:                                   │
│   • Consultar student_modalities del periodo         │
│   • Contar instancias totales                        │
│   • Contar estudiantes participantes                 │
│   • Clasificar individual vs grupal                  │
│   • Obtener resultados (completadas/abandonadas)     │
│   • Calcular tasa de completitud                     │
│   • Calcular tiempo promedio                         │
│   • Obtener directores involucrados                  │
│   • Construir AcademicPeriodAnalysisDTO              │
└──────────────────────────────────────────────────────┘
        ↓
┌──────────────────────────────────────────────────────┐
│ FASE 3: Análisis de Tendencias                      │
├──────────────────────────────────────────────────────┤
│ • Calcular tasa de crecimiento general               │
│ • Identificar periodo pico y mínimo                  │
│ • Calcular cambios periodo a periodo                 │
│ • Clasificar tendencia (GROWING/STABLE/DECLINING)    │
│ • Detectar patrones automáticamente                  │
│ • Construir TrendsEvolutionDTO                       │
└──────────────────────────────────────────────────────┘
        ↓
┌──────────────────────────────────────────────────────┐
│ FASE 4: Análisis Comparativo                        │
├──────────────────────────────────────────────────────┤
│ • Comparar actual vs anterior                        │
│ • Comparar actual vs mismo semestre año anterior     │
│ • Comparar mejor vs peor periodo                     │
│ • Calcular promedios anuales                         │
│ • Generar veredictos automáticos                     │
│ • Construir ComparativeAnalysisDTO                   │
└──────────────────────────────────────────────────────┘
        ↓
┌──────────────────────────────────────────────────────┐
│ FASE 5: Estadísticas de Directores                  │
├──────────────────────────────────────────────────────┤
│ • Contar directores únicos históricos                │
│ • Identificar directores actuales                    │
│ • Generar ranking histórico (top 10)                 │
│ • Generar ranking actual (top 5)                     │
│ • Calcular promedio instancias/director              │
│ • Construir DirectorStatisticsDTO                    │
└──────────────────────────────────────────────────────┘
        ↓
┌──────────────────────────────────────────────────────┐
│ FASE 6: Estadísticas de Estudiantes                 │
├──────────────────────────────────────────────────────┤
│ • Contar estudiantes históricos totales              │
│ • Calcular ratio individual vs grupal                │
│ • Analizar preferencias históricas                   │
│ • Calcular distribución por semestre                 │
│ • Construir StudentStatisticsDTO                     │
└──────────────────────────────────────────────────────┘
        ↓
┌──────────────────────────────────────────────────────┐
│ FASE 7: Análisis de Desempeño                       │
├──────────────────────────────────────────────────────┤
│ • Calcular tasas de completitud, éxito, abandono    │
│ • Analizar tiempos de completitud (min/max/avg)     │
│ • Calcular evolución de tasas por año                │
│ • Generar veredicto de desempeño                     │
│ • Identificar fortalezas automáticamente             │
│ • Identificar áreas de mejora automáticamente        │
│ • Construir PerformanceAnalysisDTO                   │
└──────────────────────────────────────────────────────┘
        ↓
┌──────────────────────────────────────────────────────┐
│ FASE 8: Proyecciones Futuras                        │
├──────────────────────────────────────────────────────┤
│ • Aplicar media móvil ponderada                      │
│ • Calcular tendencia lineal                          │
│ • Proyectar próximo semestre (±margen)               │
│ • Proyectar próximo año (±margen)                    │
│ • Clasificar demanda proyectada                      │
│ • Identificar oportunidades                          │
│ • Identificar riesgos                                │
│ • Generar recomendaciones estratégicas               │
│ • Calcular nivel de confianza                        │
│ • Construir ProjectionsDTO                           │
└──────────────────────────────────────────────────────┘
        ↓
Construir ModalityHistoricalReportDTO completo
        ↓
ModalityHistoricalPdfGenerator.generatePDF(report)
        ↓
┌──────────────────────────────────────────────────────┐
│ Generar PDF con Diseño Institucional:               │
│  • Portada profesional mejorada                      │
│  • Sección 1: Información de la modalidad            │
│  • Sección 2: Resumen ejecutivo histórico            │
│  • Sección 3: Análisis por periodo (tabla)           │
│  • Sección 4: Tendencias y evolución (gráficos)      │
│  • Sección 5: Análisis comparativo                   │
│  • Sección 6: Estadísticas de directores             │
│  • Sección 7: Estadísticas de estudiantes            │
│  • Sección 8: Análisis de desempeño                  │
│  • Sección 9: Estadísticas por estado                │
│  • Sección 10: Proyecciones futuras                  │
│  • Sección 11: Hallazgos clave                       │
│  • Sección 12: Análisis gráfico visual               │
│  • Sección 13: Comparativa con otras modalidades     │
│  • Sección 14: Conclusiones estratégicas             │
└──────────────────────────────────────────────────────┘
        ↓
Retorna ByteArrayOutputStream
        ↓
Convierte a ByteArrayResource
        ↓
Respuesta HTTP con PDF adjunto y headers informativos
```

---

## ❓ Preguntas Frecuentes (FAQ)

### ❓ ¿Cuántos periodos debo solicitar?

**Recomendaciones por escenario**:
- **Análisis reciente** (últimos 2 años): `periods=4`
- **Análisis estándar** (últimos 4 años): `periods=8` (default)
- **Análisis profundo** (últimos 6 años): `periods=12`
- **Análisis histórico completo**: `periods=20`

**Nota**: Más periodos = PDF más largo y mayor tiempo de generación.

---

### ❓ ¿Qué pasa si la modalidad tiene menos periodos de los solicitados?

El sistema genera el reporte con los periodos disponibles. Si solicitas 12 pero solo hay 6, mostrará 6.

**Ejemplo en PDF**:
```
Periodos solicitados: 12
Periodos disponibles: 6
Periodos analizados: 6
Nota: La modalidad tiene menos histórico del solicitado
```

---

### ❓ ¿Puedo analizar modalidades de otros programas?

**No**. El sistema filtra automáticamente por el programa del usuario autenticado. Si intentas analizar una modalidad de otro programa:
- Error 400: "Modalidad no pertenece a tu programa"
- O reporte vacío sin datos

---

### ❓ ¿Las proyecciones son exactas?

**No son garantías**, son **estimaciones estadísticas** basadas en tendencias históricas. El nivel de confianza indica la fiabilidad:
- **>80%**: Alta confianza (tendencias muy claras)
- **60-80%**: Confianza media (tendencias moderadas)
- **<60%**: Baja confianza (datos muy variables)

---

### ❓ ¿Qué significa "periodo pico"?

Es el semestre con **mayor cantidad de instancias** en todo el histórico. Indica:
- Momento de mayor demanda
- Posible referencia para capacidad máxima
- Periodo a estudiar para identificar causas del éxito

---

### ❓ ¿Cómo interpreto el "veredicto de desempeño"?

| Veredicto | Criterios | Interpretación |
|-----------|-----------|----------------|
| **EXCELLENT** | Completitud >85%, Éxito >90%, Abandono <10% | ✅ Modalidad ejemplar |
| **GOOD** | Completitud >75%, Éxito >80%, Abandono <15% | ✅ Buen desempeño |
| **REGULAR** | Completitud >65%, Éxito >70%, Abandono <20% | ⚠️ Mejorable |
| **NEEDS_IMPROVEMENT** | Por debajo de los anteriores | 🚨 Requiere intervención |

---

### ❓ ¿Puedo comparar dos modalidades entre sí?

Este endpoint analiza **una modalidad a la vez**. Para comparar múltiples modalidades, usa:
- Endpoint de comparativa: `/reports/modalities/comparison/pdf`
- O genera reportes individuales y compara manualmente

---

### ❓ ¿Con qué frecuencia debo generar este reporte?

**Recomendaciones**:
- **Anualmente**: Para documentación institucional
- **Fin de semestre**: Para evaluación de periodo
- **Antes de reuniones estratégicas**: Para fundamentar decisiones
- **Bajo demanda**: Cuando se evalúen cambios a la modalidad

---

## 🛠️ Troubleshooting

### Problema: Error 400 "Modalidad no encontrada"

**Causas**:
1. ID de modalidad incorrecto
2. Modalidad no existe en el sistema
3. Modalidad pertenece a otro programa

**Soluciones**:
1. Verificar ID correcto (consultar `/api/modality-types`)
2. Confirmar existencia de la modalidad
3. Asegurar que pertenece a tu programa

---

### Problema: PDF muy largo o tarda mucho

**Causa**: Muchos periodos solicitados (ej: 20) o modalidad muy popular.

**Soluciones**:
1. Reducir `periods` a 8 o menos
2. Esperar pacientemente (puede tomar 30-60 segundos)
3. Verificar conexión de red

---

### Problema: Proyecciones con baja confianza (<50%)

**Causa**: Datos históricos muy variables o insuficientes.

**Interpretación**:
- Tomar proyecciones con precaución
- Basar decisiones en tendencias claras, no en proyecciones
- Considerar factores externos no reflejados en datos

---

### Problema: Sección de comparativas vacía

**Causa**: Primer periodo de operación de la modalidad (sin histórico suficiente).

**Solución**:
- Esperar al menos 2 periodos para comparativas
- El reporte se genera igualmente con las secciones disponibles

---

### Problema: Error 403 Forbidden

**Causa**: Usuario sin permiso `PERM_VIEW_REPORT`.

**Solución**:
- Verificar rol y permisos del usuario
- Solicitar acceso al administrador
- Usar cuenta con permisos adecuados

---

## 📊 KPIs y Métricas Clave

### KPIs de Desempeño

| KPI | Meta | Alerta Si | Crítico Si | Ubicación PDF |
|-----|------|-----------|------------|---------------|
| Tasa de Completitud | >80% | <75% | <65% | Sección 8 |
| Tasa de Éxito | >85% | <80% | <70% | Sección 8 |
| Tasa de Abandono | <15% | >20% | >30% | Sección 8 |
| Tiempo Promedio | <180d | >200d | >250d | Sección 8 |
| Crecimiento Anual | +5 a +20% | <0% | <-15% | Sección 4 |
| Directores Activos | ≥10 | <8 | <5 | Sección 6 |

---

### KPIs de Capacidad

| KPI | Meta | Alerta Si | Crítico Si |
|-----|------|-----------|------------|
| Instancias/Semestre | 15-25 | <10 o >30 | <5 o >40 |
| Estudiantes/Semestre | 18-30 | <12 o >35 | <8 o >45 |
| Modal./Director | 3-4 | >5 | >7 |
| Directores Sobrecargados | 0-1 | 2-3 | >3 |

---

## 🎓 Valor Agregado del Reporte

### Lo Que Este Reporte Te Dice:

1. **¿La modalidad está creciendo o declinando?**
   - Sección 4: Tendencias y Evolución
   - Gráfico de línea temporal
   - Tasa de crecimiento calculada

2. **¿Cuál es la calidad del desempeño?**
   - Sección 8: Análisis de Desempeño
   - Tasas de éxito, completitud, abandono
   - Veredicto automático (EXCELLENT/GOOD/etc.)

3. **¿Qué directores son más experimentados?**
   - Sección 6: Estadísticas de Directores
   - Top 10 histórico
   - Top 5 actual

4. **¿Los estudiantes prefieren individual o grupal?**
   - Sección 7: Estadísticas de Estudiantes
   - Ratio histórico
   - Tendencia de preferencia

5. **¿Cuántas instancias esperar el próximo semestre?**
   - Sección 10: Proyecciones Futuras
   - Número proyectado ± margen
   - Nivel de confianza

6. **¿Qué periodos fueron mejores/peores y por qué?**
   - Sección 5: Análisis Comparativo
   - Identificación de causas
   - Lecciones aprendidas

7. **¿Qué acciones tomar para mejorar?**
   - Sección 10: Acciones Recomendadas
   - Sección 14: Conclusiones Estratégicas
   - Oportunidades y riesgos identificados

---

## 🆚 Comparación con Otros Reportes

| Aspecto | Reporte Global | Reporte Comparativo | **Reporte Histórico** |
|---------|----------------|---------------------|------------------------|
| **Enfoque** | Todas las modalidades (snapshot) | Comparación entre tipos | **Una modalidad (temporal)** |
| **Temporal** | Actual | Multi-periodo | **Histórico profundo** |
| **Granularidad** | Por modalidad | Por tipo | **Por periodo de una modalidad** |
| **Proyecciones** | No | No | **Sí (estadísticas)** |
| **Tendencias** | No | Sí | **Sí (detalladas)** |
| **Directores** | Lista actual | Distribución | **Ranking histórico** |
| **Estudiantes** | Actuales | Distribución | **Evolución temporal** |
| **Longitud PDF** | Media | Larga | **Muy larga** |
| **Uso** | Monitoreo general | Estrategia de tipos | **Estrategia de modalidad** |

---

## 💡 Casos de Análisis Estratégico

### Análisis 1: Evaluación para Renovación Curricular

**Objetivo**: Determinar si mantener, modificar o eliminar la modalidad.

**Request**:
```http
GET /reports/modalities/8/historical/pdf?periods=20
```

**Análisis en PDF**:
1. Revisar tendencia general (GROWING/DECLINING)
2. Evaluar desempeño (EXCELLENT/NEEDS_IMPROVEMENT)
3. Analizar demanda proyectada (HIGH/LOW)
4. Leer conclusiones estratégicas

**Decisión**:
- GROWING + EXCELLENT → **Mantener y fortalecer**
- STABLE + GOOD → **Mantener con mejoras**
- DECLINING + REGULAR → **Rediseñar o fusionar**
- DECLINING + NEEDS_IMPROVEMENT → **Considerar eliminación**

---

### Análisis 2: Justificación de Inversión

**Objetivo**: Fundamentar solicitud de presupuesto para recursos.

**Request**:
```http
GET /reports/modalities/1/historical/pdf?periods=12
```

**Uso del PDF**:
1. **Portada**: Presentación institucional
2. **Sección 2**: Indicadores históricos (evidencia de demanda)
3. **Sección 4**: Gráfico de crecimiento (tendencia visual)
4. **Sección 8**: Desempeño EXCELLENT (justifica inversión)
5. **Sección 10**: Proyecciones (necesidad futura)
6. **Sección 14**: Recomendaciones (plan de acción)

**Argumento**: "La modalidad creció 63.6% en 4 años con tasa de éxito de 92.3%. Se proyectan 42 instancias en 2027, requiriendo 3-4 directores adicionales."

---

### Análisis 3: Diagnóstico de Problemas

**Objetivo**: Identificar causas de bajo rendimiento.

**Request**:
```http
GET /reports/modalities/10/historical/pdf?periods=8
```

**Análisis en PDF**:
1. **Sección 4**: ¿Tendencia es DECLINING?
2. **Sección 8**: ¿Qué áreas de mejora identifica?
3. **Sección 3**: ¿En qué periodos empeoró?
4. **Sección 6**: ¿Falta de directores experimentados?
5. **Sección 10**: ¿Qué riesgos identifica?

**Plan de acción**: Implementar recomendaciones de la Sección 14.

---

### Análisis 4: Benchmarking con Otras Instituciones

**Objetivo**: Comparar desempeño con estándares nacionales.

**Request**:
```http
GET /reports/modalities/1/historical/pdf?periods=8
```

**Métricas a comparar**:
- Tasa de completitud: Tu programa vs Nacional
- Tiempo promedio: Tu programa vs Nacional
- Tasa de éxito: Tu programa vs Nacional
- Ratio individual/grupal: Tu programa vs Nacional

**Uso**: Identificar fortalezas y brechas competitivas.

---

## 🎯 Interpretación de Patrones

### Patrón: "Crecimiento en segundos semestres"

**Significado**: Más estudiantes inician en agosto que en enero.

**Posibles causas**:
- Estudiantes completan requisitos en primer semestre
- Planificación académica favorece segundo semestre
- Disponibilidad de directores en segundo semestre

**Acción**: Balancear oferta entre semestres.

---

### Patrón: "Pico cada 2 años"

**Significado**: Incrementos significativos cada 4 semestres.

**Posibles causas**:
- Ciclo de promociones estudiantiles
- Renovación curricular bienal
- Factores externos periódicos

**Acción**: Anticipar próximo pico y planificar recursos.

---

### Patrón: "Preferencia individual alta (>70%)"

**Significado**: Estudiantes eligen mayoritariamente trabajo solo.

**Posibles causas**:
- Facilidad de coordinación
- Preferencia personal
- Falta de incentivos para grupos

**Acción**: Implementar incentivos para modalidades grupales.

---

### Patrón: "Aumento de abandonos en periodos largos"

**Significado**: Instancias >180 días tienen mayor abandono.

**Posibles causas**:
- Falta de seguimiento
- Desmotivación estudiantil
- Problemas de supervisión

**Acción**: Implementar hitos de seguimiento cada 30-45 días.

---

## 📋 Secciones del PDF (Resumen)

| Sección | Título | Páginas | Condicional |
|---------|--------|---------|-------------|
| 0 | Portada Institucional | 1 | No |
| 1 | Información de la Modalidad | 1 | No |
| 2 | Resumen Ejecutivo Histórico | 1 | No |
| 3 | Análisis Histórico por Periodo | 2-3 | No |
| 4 | Análisis de Tendencias | 2 | No |
| 5 | Análisis Comparativo | 1-2 | Sí (≥2 periodos) |
| 6 | Estadísticas de Directores | 1-2 | Sí (si requiere director) |
| 7 | Estadísticas de Estudiantes | 1 | No |
| 8 | Análisis de Desempeño | 2 | No |
| 9 | Estadísticas por Estado | 1 | No |
| 10 | Proyecciones Futuras | 1-2 | Sí (≥3 periodos) |
| 11 | Hallazgos Clave | 1 | No |
| 12 | Análisis Gráfico Visual | 1-2 | No |
| 13 | Comparativa con Otras | 1 | Sí (si hay datos) |
| 14 | Conclusiones Estratégicas | 1-2 | No |

**Total estimado**: 15-25 páginas (según periodos y datos disponibles)

---

## 🔐 Seguridad

### Filtrado Automático
- ✅ Solo modalidades del programa del usuario
- ✅ No se expone información de otros programas
- ✅ Cumplimiento de políticas de privacidad

### Validaciones
- ✅ Token JWT válido y no expirado
- ✅ Permiso `PERM_VIEW_REPORT` requerido
- ✅ ID de modalidad debe existir
- ✅ Modalidad debe pertenecer al programa
- ✅ Parámetro `periods` validado (rango: 1-50)

---

## 📅 Changelog

| Versión | Fecha | Cambios |
|---------|-------|---------|
| 1.0 | 2026-02-17 | Implementación inicial |
| 1.5 | 2026-02-18 | Agregado análisis de tendencias |
| 2.0 | 2026-02-18 | Rediseño completo con colores institucionales |
| 2.5 | 2026-02-18 | Agregadas proyecciones futuras |
| 3.0 | 2026-02-18 | Análisis gráfico visual y patrones automáticos |
| 3.1 | 2026-02-18 | Documentación completa |

---

## ✅ Checklist de Uso

Antes de generar el reporte:

- [ ] Token JWT válido
- [ ] Permiso `PERM_VIEW_REPORT` activo
- [ ] ID de modalidad conocido y válido
- [ ] Modalidad pertenece a tu programa
- [ ] Decisión sobre cantidad de periodos (4-20 recomendado)
- [ ] Conexión estable (reporte puede tardar 30-60s)
- [ ] Espacio en disco para PDF (típicamente 200-500 KB)

---

## 📞 Información de Contacto

### Código Fuente
- **Controller**: `com.SIGMA.USCO.report.controller.GlobalModalityReportController`
- **Generator**: `com.SIGMA.USCO.report.service.ModalityHistoricalPdfGenerator`
- **Service**: `com.SIGMA.USCO.report.service.ReportService`
- **DTO**: `com.SIGMA.USCO.report.dto.ModalityHistoricalReportDTO`

### Documentación Relacionada
- [Reporte Global de Modalidades](./DOCUMENTACION_REPORTE_MODALIDADES_ACTIVAS.md)
- [Reporte Filtrado (RF-46)](./DOCUMENTACION_REPORTE_MODALIDADES_FILTRADO.md)
- [Reporte Comparativo (RF-48)](./DOCUMENTACION_REPORTE_COMPARATIVA_MODALIDADES.md)
- [Reporte de Directores (RF-49)](./DOCUMENTACION_REPORTE_DIRECTORES_MODALIDADES.md)

---

**Generado por**: SIGMA - Sistema de Gestión de Modalidades de Grado  
**Tipo de Reporte**: Análisis Histórico Evolutivo  
**Servicio**: ModalityHistoricalPdfGenerator  
**Última actualización**: 18 de Febrero de 2026  
**Versión**: 3.1

