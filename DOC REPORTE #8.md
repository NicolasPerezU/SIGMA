# 🎓 Documentación: Reporte de Modalidades Completadas (Exitosas y Fallidas)

## 📝 Descripción General

Este endpoint genera un **reporte completo en formato PDF** de modalidades de grado que han sido finalizadas, ya sea exitosamente o con resultado fallido. Incluye análisis detallado de resultados, calificaciones, distinciones académicas (Meritorio, Laureado), tiempos de completitud, desempeño de directores y tendencias temporales. Es una herramienta esencial para evaluar la efectividad del programa y reconocer logros académicos.

**Generador**: `CompletedModalitiesPdfGenerator`

**Tipo de Reporte**: Análisis de resultados finales con distinciones

---

## 🔗 Endpoint

### **POST** `/reports/modalities/completed/pdf`

**Descripción**: Genera y descarga un reporte en PDF con el análisis completo de modalidades completadas del programa, aplicando filtros opcionales y mostrando estadísticas de éxito, calificaciones, distinciones y análisis temporal.

### Autenticación
- **Requerida**: Sí
- **Tipo**: Bearer Token (JWT)
- **Permiso requerido**: `PERM_VIEW_REPORT`

---

## 📥 Request (Solicitud)

### Headers
```http
Authorization: Bearer <token_jwt>
Content-Type: application/json
```

### Request Body (Opcional)

El body es **OPCIONAL**. Si no se envía, se listan todas las modalidades completadas del programa.

```json
{
  "modalityTypes": ["PROYECTO DE GRADO", "PASANTIA"],
  "results": ["SUCCESS"],
  "year": 2025,
  "semester": 2,
  "startDate": "2025-01-01",
  "endDate": "2025-12-31",
  "onlyWithDistinction": true,
  "distinctionType": "MERITORIOUS",
  "directorId": 25,
  "minGrade": 4.0,
  "maxGrade": 5.0,
  "modalityTypeFilter": "INDIVIDUAL",
  "sortBy": "GRADE",
  "sortDirection": "DESC"
}
```

### Campos del Request Body

| Campo | Tipo | Requerido | Descripción | Valor por Defecto | Ejemplo |
|-------|------|-----------|-------------|-------------------|---------|
| `modalityTypes` | `List<String>` | No | Tipos específicos de modalidad | Todos | `["PROYECTO DE GRADO"]` |
| `results` | `List<String>` | No | Resultados a incluir | Todos | `["SUCCESS"]`, `["FAILED"]` |
| `year` | `Integer` | No | Año específico de finalización | Todos | `2025` |
| `semester` | `Integer` | No | Semestre específico (1 o 2) | Todos | `1`, `2` |
| `startDate` | `String` | No | Fecha inicio (YYYY-MM-DD) | Todas | `"2025-01-01"` |
| `endDate` | `String` | No | Fecha fin (YYYY-MM-DD) | Todas | `"2025-12-31"` |
| `onlyWithDistinction` | `Boolean` | No | Solo con distinción académica | `false` | `true` |
| `distinctionType` | `String` | No | Tipo de distinción específica | Todos | `"MERITORIOUS"`, `"LAUREATE"` |
| `directorId` | `Long` | No | Director específico | Todos | `25` |
| `minGrade` | `Double` | No | Calificación mínima | Sin mínimo | `4.0` |
| `maxGrade` | `Double` | No | Calificación máxima | Sin máximo | `5.0` |
| `modalityTypeFilter` | `String` | No | Individual/Grupal | Todos | `"INDIVIDUAL"`, `"GROUP"` |
| `sortBy` | `String` | No | Criterio de orden | `"DATE"` | `"DATE"`, `"GRADE"`, `"TYPE"`, `"DURATION"` |
| `sortDirection` | `String` | No | Dirección de orden | `"DESC"` | `"ASC"`, `"DESC"` |

### Resultados Válidos

- `SUCCESS`: Modalidades completadas exitosamente
- `FAILED`: Modalidades no completadas o reprobadas

### Tipos de Distinción

- `MERITORIOUS`: Trabajo de grado meritorio
- `LAUREATE`: Trabajo de grado laureado

---

## 📤 Response (Respuesta)

### Respuesta Exitosa (200 OK)

**Content-Type**: `application/pdf`

**Headers de Respuesta**:
```http
Content-Type: application/pdf
Content-Disposition: attachment; filename=Reporte_Modalidades_Completadas_2026-02-18_143025.pdf
X-Report-Generated-At: 2026-02-18T14:30:25
X-Total-Records: 68
Content-Length: 389456
```

**Body**: Archivo PDF binario profesional con análisis de modalidades completadas

### Respuestas de Error

#### Error de Validación (400)
```json
{
  "success": false,
  "error": "Datos inválidos: La fecha de inicio debe ser anterior a la fecha fin",
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

### Caso de Uso 1: Reporte Completo de Modalidades Exitosas

**Escenario**: Jefatura quiere evaluar todas las modalidades completadas exitosamente en 2025.

**Request**:
```json
{
  "results": ["SUCCESS"],
  "year": 2025,
  "sortBy": "GRADE",
  "sortDirection": "DESC"
}
```

**Resultado**: PDF con modalidades exitosas ordenadas por calificación (mayor a menor).

---

### Caso de Uso 2: Trabajos con Distinción Académica

**Escenario**: Consejo necesita identificar trabajos meritorios y laureados para ceremonias.

**Request**:
```json
{
  "onlyWithDistinction": true,
  "year": 2025,
  "sortBy": "GRADE"
}
```

**Resultado**: PDF con solo modalidades con distinción (Meritorio o Laureado).

---

### Caso de Uso 3: Proyectos de Grado Laureados

**Escenario**: Secretaría prepara listado para reconocimiento institucional.

**Request**:
```json
{
  "modalityTypes": ["PROYECTO DE GRADO"],
  "distinctionType": "LAUREATE",
  "year": 2025
}
```

**Resultado**: PDF con proyectos de grado laureados de 2025.

---

### Caso de Uso 4: Análisis de Modalidades Fallidas

**Escenario**: Comité académico evalúa causas de fracaso para mejoras.

**Request**:
```json
{
  "results": ["FAILED"],
  "year": 2025
}
```

**Resultado**: PDF con modalidades fallidas, incluyendo análisis de causas.

---

### Caso de Uso 5: Desempeño de Director Específico

**Escenario**: Evaluación de desempeño de un director en modalidades finalizadas.

**Request**:
```json
{
  "directorId": 25,
  "year": 2025
}
```

**Resultado**: PDF con modalidades completadas supervisadas por ese director.

---

### Caso de Uso 6: Calificaciones Excelentes (≥4.5)

**Escenario**: Identificar estudiantes destacados para becas de posgrado.

**Request**:
```json
{
  "results": ["SUCCESS"],
  "minGrade": 4.5,
  "year": 2025,
  "sortBy": "GRADE",
  "sortDirection": "DESC"
}
```

**Resultado**: PDF con modalidades de calificación ≥4.5, ordenadas descendentemente.

---

## 📄 Estructura Completa del PDF

### **PORTADA INSTITUCIONAL CON FILTROS**

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


   REPORTE DE MODALIDADES COMPLETADAS
      ANÁLISIS DE RESULTADOS Y LOGROS        ← Título principal
                                                (rojo)

┌─────────────────────────────────────────────┐
│ PERIODO: 2025 (Año completo)                │ ← Caja dorada
│ Incluye: Exitosas y Fallidas                │   (si hay filtros)
└─────────────────────────────────────────────┘


╔═══════════════════════════════════════════════════╗
║ Programa: Ingeniería de Sistemas                 ║
║ Código: IS-2020                                   ║
║ Fecha de Generación: 18/02/2026 - 14:30         ║ ← Tabla info
║ Generado por: Dr. Juan Pérez                     ║
║ Modalidades Completadas: 68                       ║
║ Periodo de Análisis: 2025                         ║
║ Tasa de Éxito: 92.6%                             ║
╚═══════════════════════════════════════════════════╝


┌──────────────────────────────────────────────────────┐
│  Sistema SIGMA - Reporte de Resultados Finales      │ ← Footer
│  Sistema Integral de Gestión de Modalidades         │   dorado
└──────────────────────────────────────────────────────┘
```

---

### **SECCIÓN 1: RESUMEN EJECUTIVO**

#### 1.1 Tarjetas de Métricas Clave (3×3)

**Fila 1 - Resultados**:
```
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│      68      │  │      63      │  │      5       │
│  Completadas │  │   Exitosas   │  │   Fallidas   │
│    Totales   │  │   (92.6%)    │  │   (7.4%)     │
└──────────────┘  └──────────────┘  └──────────────┘
```

**Fila 2 - Calificaciones**:
```
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│     4.45     │  │     5.0      │  │     3.8      │
│   Promedio   │  │    Máxima    │  │   Mínima     │
│ Calificación │  │              │  │              │
└──────────────┘  └──────────────┘  └──────────────┘
```

**Fila 3 - Distinciones y Tiempo**:
```
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│      12      │  │      3       │  │    165.2     │
│  Meritorios  │  │  Laureados   │  │ Días Prom.   │
│   (17.6%)    │  │   (4.4%)     │  │ Completitud  │
└──────────────┘  └──────────────┘  └──────────────┘
```

#### 1.2 Indicadores de Desempeño

```
┌─────────────────────────────────────────────────────┐
│ INDICADORES GENERALES                               │
├─────────────────────────────────────────────────────┤
│                                                     │
│ Modalidades Completadas:        68                  │
│ Tasa de Éxito:                  92.6%  ✓ Excelente │
│ Tasa de Fallo:                  7.4%   ✓ Bajo      │
│                                                     │
│ Total de Estudiantes:           75                  │
│ Directores Involucrados:        18                  │
│                                                     │
│ Con Distinción Académica:       15 (22.1%)          │
│ ├─ Meritorios:                  12 (17.6%)          │
│ └─ Laureados:                    3 (4.4%)           │
│                                                     │
│ Promedio Calificación:          4.45/5.0            │
│ Promedio Días Completitud:      165.2 días          │
│ Completitud Más Rápida:         95 días             │
│ Completitud Más Lenta:          245 días            │
│                                                     │
└─────────────────────────────────────────────────────┘
```

#### 1.3 Distribución de Resultados (Gráfico de Torta)

```
         DISTRIBUCIÓN DE RESULTADOS

              Exitosas
               92.6%
          ╱──────────╲
        ╱   ████████   ╲
       │    ████████    │
       │    ████████    │
        ╲   ████████   ╱
          ╲──────────╱
           │ 7.4% Fallidas

VEREDICTO: Tasa de éxito EXCELENTE (>90%)
```

---

### **SECCIÓN 2: ANÁLISIS POR RESULTADO**

#### 2.1 Modalidades Exitosas (SUCCESS)

```
═══════════════════════════════════════════════════════
MODALIDADES EXITOSAS
═══════════════════════════════════════════════════════

Cantidad: 63 (92.6%)

Estadísticas:
├─ Calificación Promedio: 4.52/5.0
├─ Días Promedio Completitud: 158.3 días
├─ Con Distinción: 15 (23.8% de exitosas)
│  ├─ Meritorios: 12 (19.0%)
│  └─ Laureados: 3 (4.8%)
└─ Sin Distinción: 48 (76.2%)

Distribución de Calificaciones:
5.0      ████░░░░░░░░░░░░░░░░   8 modalidades (12.7%)
4.5-4.9  ████████████░░░░░░░░  25 modalidades (39.7%)
4.0-4.4  ████████░░░░░░░░░░░░  20 modalidades (31.7%)
3.5-3.9  ████░░░░░░░░░░░░░░░░  10 modalidades (15.9%)

FACTORES DE ÉXITO IDENTIFICADOS:

✓ Supervisión consistente de director experimentado
✓ Cumplimiento de hitos de avance cada 30 días
✓ Buena preparación para sustentación (ensayos previos)
✓ Estudiantes con promedio académico ≥4.0
✓ Inicio temprano del proyecto (semestres 9-10)
```

#### 2.2 Modalidades Fallidas (FAILED)

```
═══════════════════════════════════════════════════════
MODALIDADES FALLIDAS
═══════════════════════════════════════════════════════

Cantidad: 5 (7.4%)

Estadísticas:
├─ Calificación Promedio: 2.85/5.0
├─ Días Promedio Completitud: 215.8 días
└─ Ninguna con distinción

Distribución de Causas:
├─ Deficiencias metodológicas: 2 casos (40%)
├─ Falta de seguimiento: 2 casos (40%)
└─ Presentación inadecuada: 1 caso (20%)

RAZONES DE FALLO IDENTIFICADAS:

⚠ Supervisión intermitente o cambio de director
⚠ Falta de avances significativos por >60 días
⚠ Inicio tardío (semestre 12+)
⚠ Problemas en definición del alcance del proyecto
⚠ Preparación deficiente para sustentación
```

#### 2.3 Comparativa Éxito vs Fallo

```
COMPARATIVA DETALLADA

                        Exitosas      Fallidas      Diferencia
Calificación Prom:        4.52         2.85         +1.67
Días Completitud:        158.3d       215.8d        -57.5d
Con Director Exp.:       85.7%        40.0%         +45.7pp
Estudiantes GPA>4.0:     76.2%        20.0%         +56.2pp

VEREDICTO: Supervisión y preparación académica son factores 
clave para el éxito.

RECOMENDACIONES:

1. Asignar directores con >5 proyectos de experiencia
2. Implementar seguimiento quincenal obligatorio
3. Realizar ensayos de sustentación 2 semanas antes
4. Requisito de promedio mínimo 3.8 para iniciar modalidad
```

---

### **SECCIÓN 3: LISTADO DETALLADO DE MODALIDADES COMPLETADAS**

*Tabla completa ordenada según filtro `sortBy`*

#### Tabla de Modalidades (Paginada)

| # | Modalidad | Estudiante(s) | Director | Resultado | Calificación | Distinción | Días | Fecha Fin |
|---|-----------|---------------|----------|-----------|--------------|------------|------|-----------|
| 1 | Proyecto de Grado | Ana García (L) | Dr. Carlos López | ✓ Exitoso | 5.0 | 🏆 Laureado | 145 | 15/12/2025 |
| 2 | Proyecto de Grado | Juan Pérez (L), María Ruiz | Dra. Carmen Ortiz | ✓ Exitoso | 4.9 | ⭐ Meritorio | 158 | 18/12/2025 |
| 3 | Pasantía | Carlos Díaz | Ing. Pedro Torres | ✓ Exitoso | 4.8 | ⭐ Meritorio | 142 | 20/11/2025 |
| ... | ... | ... | ... | ... | ... | ... | ... | ... |
| 64 | Proyecto de Grado | Luis Gómez | Dr. José Ramírez | ✗ Fallido | 2.5 | - | 230 | 15/07/2025 |

**Leyenda**:
- ✓ Exitoso / ✗ Fallido
- 🏆 Laureado / ⭐ Meritorio / - Sin distinción
- (L) = Líder del grupo

#### Detalle Expandido por Modalidad

Para cada modalidad destacada:

```
═══════════════════════════════════════════════════════
#1 - PROYECTO DE GRADO (Laureado)
═══════════════════════════════════════════════════════

Resultado: ✓ EXITOSO - LAUREADO 🏆
Calificación Final: 5.0/5.0

Estudiante(s):
└─ Ana García López (Líder)
   ├─ Código: 20191234567
   ├─ Email: ana.garcia@usco.edu.co
   ├─ GPA Acumulado: 4.85
   └─ Créditos Completados: 160/160

Director:
└─ Dr. Carlos López García
   ├─ Email: carlos.lopez@usco.edu.co
   └─ Proyectos Completados: 15

Proyecto:
├─ Título: "Sistema de Gestión de Inventarios con IoT"
└─ Descripción: Plataforma web integrada con sensores...

Fechas Clave:
├─ Selección: 15/06/2025
├─ Sustentación: 10/12/2025
└─ Finalización: 15/12/2025
    Total: 145 días

Sustentación:
├─ Fecha: 10/12/2025 14:00
├─ Lugar: Auditorio Principal
└─ Jurados:
   1. Dr. Carlos López (Director)
   2. Dra. María Rodríguez (Evaluador)
   3. Dr. Pedro Martínez (Evaluador)

Distinción Académica:
🏆 TRABAJO DE GRADO LAUREADO
   Otorgado por unanimidad del jurado evaluador
   Reconocimiento a la excelencia académica

Observaciones:
Proyecto innovador que integra IoT con machine learning.
Propuesta de publicación en revista indexada.
```

---

### **SECCIÓN 4: ANÁLISIS POR TIPO DE MODALIDAD**

*Para cada tipo de modalidad*

```
═══════════════════════════════════════════════════════
PROYECTO DE GRADO
═══════════════════════════════════════════════════════

Completadas: 45 (66.2% del total)

Resultados:
├─ Exitosas: 42 (93.3%)
└─ Fallidas: 3 (6.7%)

Calificaciones:
├─ Promedio: 4.52/5.0
├─ Máxima: 5.0
└─ Mínima: 2.5

Tiempo de Completitud:
├─ Promedio: 162.5 días
├─ Más rápido: 120 días
└─ Más lento: 210 días

Distinciones:
├─ Laureados: 3 (6.7%)
├─ Meritorios: 10 (22.2%)
└─ Sin distinción: 32 (71.1%)

Top Directores:
1. Dr. Carlos López (8 proyectos, 100% éxito, 2 laureados)
2. Dra. María Rodríguez (6 proyectos, 100% éxito, 1 meritorio)
3. Dr. Pedro Martínez (5 proyectos, 100% éxito, 3 meritorios)

DESEMPEÑO: EXCELENTE ✓
Tasa de éxito superior al 90%, con alto porcentaje de
distinciones académicas (28.9%).
```

```
═══════════════════════════════════════════════════════
PASANTÍA
═══════════════════════════════════════════════════════

Completadas: 15 (22.1% del total)

Resultados:
├─ Exitosas: 14 (93.3%)
└─ Fallidas: 1 (6.7%)

Calificaciones:
├─ Promedio: 4.35/5.0
├─ Máxima: 4.9
└─ Mínima: 3.0

Tiempo de Completitud:
├─ Promedio: 152.8 días
├─ Más rápido: 95 días
└─ Más lento: 180 días

Distinciones:
├─ Laureados: 0 (0%)
├─ Meritorios: 2 (13.3%)
└─ Sin distinción: 13 (86.7%)

Top Supervisores:
1. Ing. Pedro Torres (4 pasantías, 100% éxito)
2. Dra. Laura Fernández (3 pasantías, 100% éxito)
3. Dr. Miguel Gómez (3 pasantías, 66.7% éxito)

DESEMPEÑO: BUENO ✓
Tasa de éxito alta, tiempos de completitud menores al
promedio general. Menor índice de distinciones debido a
naturaleza práctica de la modalidad.
```

---

### **SECCIÓN 5: ANÁLISIS TEMPORAL**

#### 5.1 Evolución Semestral

```
MODALIDADES COMPLETADAS POR SEMESTRE

2025-1  ████████████████░░░░  32 completadas │ 30 exitosas │ 93.8% éxito
2025-2  ██████████████░░░░░░  28 completadas │ 26 exitosas │ 92.9% éxito
                               │               │
2024-2  ████████░░░░░░░░░░░░  18 completadas │ 16 exitosas │ 88.9% éxito
2024-1  ██████░░░░░░░░░░░░░░  12 completadas │ 11 exitosas │ 91.7% éxito

TENDENCIA: ↗ MEJORA CONTINUA
Incremento de 167% en completitud (2024-1 → 2025-1)
Tasa de éxito se mantiene alta (>90% constante)
```

#### 5.2 Evolución de Calificaciones

```
CALIFICACIÓN PROMEDIO POR PERIODO

5.0 ┤
4.5 ┤      ★           ★─────★  4.52 (2025-1)
4.0 ┤    ╱   ╲       ╱         4.50 (2025-2)
3.5 ┤  ╱       ╲   ╱           4.42 (2024-2)
3.0 ┤╱           ╲╱            4.35 (2024-1)
    └────────────────────────
     2024-1 2024-2 2025-1 2025-2

TENDENCIA: ↗ MEJORANDO
Mejora de +0.17 puntos en promedio (4.35 → 4.52)
```

#### 5.3 Mejor y Peor Periodo

```
🏆 MEJOR PERIODO: 2025-1
   • 32 modalidades completadas
   • 30 exitosas (93.8% éxito)
   • Calificación promedio: 4.52
   • 8 distinciones (25% del total)

⚠️ PEOR PERIODO: 2024-1
   • 12 modalidades completadas
   • 11 exitosas (91.7% éxito)
   • Calificación promedio: 4.35
   • 2 distinciones (16.7% del total)

ANÁLISIS: Incremento sostenido en cantidad y calidad.
```

---

### **SECCIÓN 6: ANÁLISIS DE DISTINCIONES ACADÉMICAS**

#### 6.1 Resumen de Distinciones

```
┌─────────────────────────────────────────────────────┐
│ DISTINCIONES ACADÉMICAS                             │
├─────────────────────────────────────────────────────┤
│                                                     │
│ Total con Distinción:        15 (22.1%)            │
│                                                     │
│ Trabajos Laureados:           3 (4.4%)  🏆         │
│ Trabajos Meritorios:         12 (17.6%) ⭐         │
│ Sin Distinción:              53 (77.9%)            │
│                                                     │
└─────────────────────────────────────────────────────┘

DISTRIBUCIÓN DE DISTINCIONES

Laureados    ███░░░░░░░░░░░░░░░░   3 (4.4%)
Meritorios   ████████░░░░░░░░░░░  12 (17.6%)
Sin Distinc. ████████████████████  53 (77.9%)
```

#### 6.2 Trabajos Laureados (🏆 Máxima Distinción)

```
═══════════════════════════════════════════════════════
TRABAJOS DE GRADO LAUREADOS
═══════════════════════════════════════════════════════

1. "Sistema de Gestión de Inventarios con IoT"
   ├─ Estudiante: Ana García López
   ├─ Director: Dr. Carlos López
   ├─ Modalidad: Proyecto de Grado
   ├─ Calificación: 5.0
   └─ Fecha: 15/12/2025

2. "Plataforma de Telemedicina con IA"
   ├─ Estudiante: Carlos Ruiz Pérez
   ├─ Director: Dra. María Rodríguez
   ├─ Modalidad: Proyecto de Grado
   ├─ Calificación: 5.0
   └─ Fecha: 18/12/2025

3. "Blockchain para Trazabilidad Agrícola"
   ├─ Estudiante: Diana Morales Torres
   ├─ Director: Dr. Pedro Martínez
   ├─ Modalidad: Proyecto de Grado
   ├─ Calificación: 5.0
   └─ Fecha: 20/12/2025

CARACTERÍSTICAS COMUNES:
✓ Calificación perfecta (5.0)
✓ Innovación tecnológica significativa
✓ Aplicación práctica con impacto social
✓ Potencial de publicación científica
✓ Directores con alta experiencia (>10 proyectos)
```

#### 6.3 Trabajos Meritorios (⭐ Alta Distinción)

Lista de 12 trabajos meritorios con calificaciones entre 4.7-4.9 y reconocimiento especial por calidad académica.

#### 6.4 Modalidades con Más Distinciones

```
TIPOS CON MÁS DISTINCIONES

1. Proyecto de Grado:           13 distinciones (28.9%)
2. Emprendimiento:               2 distinciones (40.0%)
3. Pasantía:                     2 distinciones (13.3%)
```

#### 6.5 Directores con Más Distinciones

```
TOP 5 DIRECTORES POR DISTINCIONES OBTENIDAS

1. Dr. Carlos López             5 distinciones │ 2 laureados, 3 meritorios
2. Dra. María Rodríguez         4 distinciones │ 1 laureado, 3 meritorios
3. Dr. Pedro Martínez           3 distinciones │ 1 laureado, 2 meritorios
4. Dra. Carmen Ortiz            2 distinciones │ 0 laureados, 2 meritorios
5. Dr. José Ramírez             1 distinción   │ 0 laureados, 1 meritorio
```

---

### **SECCIÓN 7: DESEMPEÑO DE DIRECTORES**

#### 7.1 Resumen General

```
PARTICIPACIÓN DE DIRECTORES

Total de Directores: 18
Promedio Modalidades/Director: 3.78
Tasa Promedio de Éxito: 92.6%
```

#### 7.2 Ranking de Directores por Desempeño

```
TOP 10 DIRECTORES - MODALIDADES COMPLETADAS

 Pos │ Director                │ Total │ Exit. │ Fall. │ %Éxito│ Prom │ Dist.│
─────┼─────────────────────────┼───────┼───────┼───────┼───────┼──────┼──────┤
  1  │ Dr. Carlos López        │   8   │   8   │   0   │ 100%  │ 4.68 │  5   │
  2  │ Dra. María Rodríguez    │   7   │   7   │   0   │ 100%  │ 4.62 │  4   │
  3  │ Dr. Pedro Martínez      │   6   │   6   │   0   │ 100%  │ 4.55 │  3   │
  4  │ Dra. Carmen Ortiz       │   5   │   5   │   0   │ 100%  │ 4.48 │  2   │
  5  │ Dr. José Ramírez        │   5   │   4   │   1   │  80%  │ 4.20 │  1   │
  6  │ Ing. Pedro Torres       │   4   │   4   │   0   │ 100%  │ 4.40 │  0   │
  7  │ Dra. Laura Fernández    │   4   │   4   │   0   │ 100%  │ 4.35 │  0   │
  8  │ Dr. Miguel Gómez        │   4   │   3   │   1   │  75%  │ 4.12 │  0   │
  9  │ Ing. Ana García M.      │   3   │   3   │   0   │ 100%  │ 4.30 │  0   │
 10  │ Dr. David Torres        │   3   │   3   │   0   │ 100%  │ 4.28 │  0   │
```

#### 7.3 Mejor Director del Periodo

```
┌─────────────────────────────────────────────────────┐
│ 🏆 DIRECTOR DESTACADO: DR. CARLOS LÓPEZ GARCÍA      │
├─────────────────────────────────────────────────────┤
│                                                     │
│ Modalidades Supervisadas: 8                         │
│ Tasa de Éxito: 100%                                 │
│ Calificación Promedio: 4.68/5.0                     │
│                                                     │
│ Distinciones Obtenidas: 5                           │
│ ├─ Laureados: 2                                     │
│ └─ Meritorios: 3                                    │
│                                                     │
│ Reconocimiento: Director con mejor desempeño        │
│ en modalidades completadas 2025.                    │
│                                                     │
└─────────────────────────────────────────────────────┘
```

---

### **SECCIÓN 8: ANÁLISIS ESTADÍSTICO AVANZADO**

#### 8.1 Distribución de Calificaciones

```
DISTRIBUCIÓN DE CALIFICACIONES FINALES

5.0      ████░░░░░░░░░░░░░░░░   8 modalidades (11.8%)  EXCELENTE
4.5-4.9  ████████████░░░░░░░░  28 modalidades (41.2%)  MUY BUENO
4.0-4.4  ████████░░░░░░░░░░░░  22 modalidades (32.4%)  BUENO
3.5-3.9  ███░░░░░░░░░░░░░░░░░   7 modalidades (10.3%)  ACEPTABLE
3.0-3.4  █░░░░░░░░░░░░░░░░░░░   2 modalidades (2.9%)   REGULAR
<3.0     █░░░░░░░░░░░░░░░░░░░   1 modalidad  (1.5%)    BAJO

Media: 4.45    Mediana: 4.50    Moda: 4.5-4.9
```

#### 8.2 Distribución de Tiempos de Completitud

```
TIEMPO DE COMPLETITUD (DÍAS)

<120     ███░░░░░░░░░░░░░░░░░   5 modalidades (7.4%)   MUY RÁPIDO
120-150  ████████░░░░░░░░░░░░  18 modalidades (26.5%)  RÁPIDO
151-180  ████████████████░░░░  32 modalidades (47.1%)  NORMAL
181-210  ████░░░░░░░░░░░░░░░░  10 modalidades (14.7%)  LENTO
>210     ██░░░░░░░░░░░░░░░░░░   3 modalidades (4.4%)   MUY LENTO

Media: 165.2d   Mediana: 160d   Desv. Est.: 35.8d
```

#### 8.3 Correlación Tiempo vs Calificación

```
RELACIÓN: TIEMPO DE COMPLETITUD vs CALIFICACIÓN

5.0 │  ★                       Correlación: -0.42
4.5 │   ★  ★  ★     ★          (Negativa moderada)
4.0 │      ★  ★  ★  ★  ★       
3.5 │         ★     ★     ★    INTERPRETACIÓN:
3.0 │               ★       ★  A menor tiempo, tendencia
    └────────────────────────  a mayor calificación
     100  150  200  250 días

Zona Óptima: 120-180 días (Promedio: 4.52)
Zona Crítica: >210 días (Promedio: 3.85)
```

#### 8.4 Individual vs Grupal

```
COMPARATIVA: MODALIDADES INDIVIDUALES VS GRUPALES

                        Individual     Grupal      Diferencia
Cantidad:                  52            16         -
Porcentaje:              76.5%         23.5%        -

Tasa Éxito:              94.2%         87.5%       +6.7pp
Calificación Prom:        4.48          4.35       +0.13
Días Prom:               162.5d        175.8d      -13.3d
Con Distinción:          17.3%         18.8%       -1.5pp

ANÁLISIS: Modalidades individuales muestran ligeramente
mejor desempeño en éxito y calificación, pero diferencia
no es estadísticamente significativa.
```

---

### **SECCIÓN 9: CASOS DESTACADOS**

#### 9.1 Calificación Perfecta (5.0)

```
🌟 MODALIDADES CON CALIFICACIÓN PERFECTA (8)

1. Ana García López       │ Proyecto de Grado │ 🏆 Laureado
2. Carlos Ruiz Pérez      │ Proyecto de Grado │ 🏆 Laureado
3. Diana Morales Torres   │ Proyecto de Grado │ 🏆 Laureado
4. Eduardo López García   │ Proyecto de Grado │ ⭐ Meritorio
5. Fernanda Ruiz Luna     │ Emprendimiento    │ ⭐ Meritorio
6. Gabriel Pérez Castro   │ Proyecto de Grado │ ⭐ Meritorio
7. Helena García Díaz     │ Proyecto de Grado │ Sin distinción
8. Iván Torres Morales    │ Proyecto de Grado │ Sin distinción

CARACTERÍSTICAS COMUNES:
✓ GPA promedio: 4.65
✓ Tiempo promedio: 148 días (bajo el promedio)
✓ 75% con distinción académica
✓ 87.5% supervisados por top 5 directores
```

#### 9.2 Completitud Más Rápida

```
⚡ COMPLETITUD MÁS RÁPIDA: 95 DÍAS

Modalidad: Pasantía
Estudiante: Andrés Gómez Pérez
Director: Ing. Pedro Torres
Calificación: 4.8
Distinción: ⭐ Meritorio
Empresa: TechCorp S.A.S.

Factores de Rapidez:
✓ Modalidad de pasantía (menor duración estándar)
✓ Estudiante con experiencia laboral previa
✓ Supervisión semanal del director y tutor empresarial
✓ Definición clara de objetivos desde el inicio
```

#### 9.3 Mayor Mejora (Estudiante en Riesgo → Éxito)

```
📈 CASO DE SUPERACIÓN

Modalidad: Proyecto de Grado
Estudiante: Laura Fernández Cruz
Director: Dra. Carmen Ortiz
Resultado: ✓ EXITOSO
Calificación: 4.0
Tiempo: 210 días

Historia:
• Inicio con retraso de 45 días por falta de director
• A los 90 días: 20% de progreso (esperado: 50%)
• Intervención de jefatura y reasignación de director
• Sesiones de recuperación intensivas
• Completitud exitosa con calificación aceptable

Lecciones Aprendidas:
✓ Importancia de asignación temprana de director
✓ Efectividad de intervención oportuna
✓ Valor de sesiones de recuperación estructuradas
```

---

### **SECCIÓN 10: ANÁLISIS DE SUSTENTACIONES**

#### 10.1 Estadísticas de Sustentación

```
SUSTENTACIONES REALIZADAS

Total: 68
Aprobadas Primera Vez: 63 (92.6%)
Aprobadas Segunda Vez: 5 (7.4%)
Reprobadas: 0 (0%)

Duración Promedio: 75 minutos
Rango: 45-120 minutos
```

#### 10.2 Jurados Más Frecuentes

```
TOP 5 JURADOS EVALUADORES

1. Dr. Carlos López         32 evaluaciones │ 100% aprobación
2. Dra. María Rodríguez     28 evaluaciones │ 100% aprobación
3. Dr. Pedro Martínez       24 evaluaciones │ 95.8% aprobación
4. Dra. Carmen Ortiz        20 evaluaciones │ 100% aprobación
5. Dr. José Ramírez         18 evaluaciones │ 94.4% aprobación
```

#### 10.3 Ubicaciones de Sustentación

```
DISTRIBUCIÓN POR UBICACIÓN

Auditorio Principal      ████████████████░░░░  38 (55.9%)
Sala de Conferencias     ████████░░░░░░░░░░░░  20 (29.4%)
Aula Magna               ████░░░░░░░░░░░░░░░░  10 (14.7%)
```

---

### **SECCIÓN 11: RECOMENDACIONES Y MEJORES PRÁCTICAS**

```
═══════════════════════════════════════════════════════
RECOMENDACIONES BASADAS EN DATOS
═══════════════════════════════════════════════════════

PARA MEJORAR TASA DE ÉXITO (Actual: 92.6% → Meta: 95%):

1. ASIGNACIÓN DE DIRECTORES
   ✓ Priorizar directores con tasa 100% éxito (10 disponibles)
   ✓ Asignar en <15 días desde aprobación de propuesta
   ✓ Evitar sobrecarga (máximo 5 simultáneas por director)

2. SEGUIMIENTO Y CONTROL
   ✓ Reuniones quincenales obligatorias
   ✓ Hitos de avance cada 30 días (20%, 40%, 60%, 80%)
   ✓ Alertas automáticas si progreso <esperado por 15 días

3. PREPARACIÓN DE SUSTENTACIÓN
   ✓ Ensayo previo 2 semanas antes (obligatorio)
   ✓ Revisión de presentación por director
   ✓ Simulacro con jurados externos

PARA INCREMENTAR DISTINCIONES (Actual: 22.1% → Meta: 25%):

4. CRITERIOS DE EXCELENCIA
   ✓ Promocionar casos de éxito de años anteriores
   ✓ Incentivar proyectos con potencial de publicación
   ✓ Ofrecer asesoría metodológica especializada

5. SOPORTE ADICIONAL
   ✓ Talleres de escritura científica
   ✓ Acceso a bases de datos especializadas
   ✓ Revisión por pares antes de sustentación

PARA REDUCIR TIEMPO PROMEDIO (Actual: 165d → Meta: 150d):

6. OPTIMIZACIÓN DE PROCESOS
   ✓ Digitalizar formularios y trámites
   ✓ Reducir tiempo de revisión de comités (<15 días)
   ✓ Programar sustentaciones cada 2 semanas

7. INICIO TEMPRANO
   ✓ Promover inicio en semestres 9-10
   ✓ Pre-asignación de directores en semestre anterior
   ✓ Asesoría de definición de tema al finalizar semestre 8
```

---

### **SECCIÓN 12: CONCLUSIONES**

```
═══════════════════════════════════════════════════════
CONCLUSIONES GENERALES
═══════════════════════════════════════════════════════

1. EXCELENTE DESEMPEÑO GENERAL
   El programa muestra una tasa de éxito de 92.6%, superando
   ampliamente el estándar institucional (80%) y nacional
   (75%), evidenciando efectividad en supervisión y apoyo.

2. CALIDAD ACADÉMICA SOBRESALIENTE
   Calificación promedio de 4.45/5.0 con 22.1% de distinciones
   académicas (15 trabajos), incluyendo 3 laureados y 12
   meritorios, demuestra excelencia en formación.

3. DIRECTORES EXPERIMENTADOS CLAVE DEL ÉXITO
   Los top 5 directores (con 100% éxito) supervisan 45.6% de
   modalidades completadas, correlacionando experiencia con
   resultados excepcionales. Se recomienda priorizar su
   asignación y capacitar nuevos directores con su mentoría.

4. TIEMPOS ADECUADOS DE COMPLETITUD
   Promedio de 165.2 días dentro del rango óptimo (150-180),
   con oportunidad de reducción mediante optimización de
   procesos administrativos y seguimiento más estricto.

5. MODALIDADES FALLIDAS ANALIZABLES Y PREVENIBLES
   Solo 5 casos (7.4%) con causas identificadas (supervisión
   inadecuada, falta de seguimiento, preparación deficiente),
   todas prevenibles con implementación de recomendaciones.

6. TENDENCIA POSITIVA SOSTENIDA
   Incremento de 167% en completitud entre 2024-1 y 2025-1,
   manteniendo tasa de éxito >90%, indicando crecimiento con
   calidad. Proyección para 2026: 70+ modalidades completadas.

7. DISTINCIONES CONCENTRADAS EN PROYECTOS DE GRADO
   El 86.7% de distinciones corresponden a proyectos de grado,
   sugiriendo mayor rigor y potencial de esta modalidad para
   reconocimientos académicos.

8. SUSTENTACIONES EFECTIVAS
   92.6% aprobadas en primera instancia evidencia buena
   preparación de estudiantes y acompañamiento de directores.
   Casos de segunda oportunidad (7.4%) mejorables con ensayos
   previos obligatorios.

═══════════════════════════════════════════════════════
VEREDICTO FINAL: EXCELENTE ⭐⭐⭐⭐⭐
═══════════════════════════════════════════════════════

El programa de modalidades de grado muestra desempeño
excepcional en todos los indicadores clave. Se recomienda
mantener y fortalecer las prácticas actuales, implementando
mejoras sugeridas para alcanzar tasa de éxito del 95% y
25% de distinciones en próximos periodos.
```

---

### **PIE DE PÁGINA (Todas las Páginas)**

```
──────────────────────────────────────────────────────
Página 12 | Reporte de Completadas | Ingeniería de Sistemas | 18/02/2026
──────────────────────────────────────────────────────
```

---

## 💻 Ejemplos de Código

### Ejemplo 1: JavaScript/TypeScript

```typescript
interface CompletedModalitiesFilters {
  modalityTypes?: string[];
  results?: ('SUCCESS' | 'FAILED')[];
  year?: number;
  semester?: 1 | 2;
  startDate?: string; // YYYY-MM-DD
  endDate?: string;
  onlyWithDistinction?: boolean;
  distinctionType?: 'MERITORIOUS' | 'LAUREATE';
  directorId?: number;
  minGrade?: number;
  maxGrade?: number;
  modalityTypeFilter?: 'INDIVIDUAL' | 'GROUP';
  sortBy?: 'DATE' | 'GRADE' | 'TYPE' | 'DURATION';
  sortDirection?: 'ASC' | 'DESC';
}

async function downloadCompletedModalitiesReport(filters?: CompletedModalitiesFilters) {
  const token = localStorage.getItem('auth_token');
  
  try {
    console.log('🎓 Generando reporte de modalidades completadas...');
    
    const response = await fetch('http://localhost:8080/reports/modalities/completed/pdf', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(filters || {})
    });
    
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.error || 'Error al generar reporte');
    }
    
    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    
    const contentDisposition = response.headers.get('Content-Disposition');
    const filename = contentDisposition 
      ? contentDisposition.split('filename=')[1].replace(/"/g, '')
      : `Reporte_Completadas_${new Date().toISOString().split('T')[0]}.pdf`;
    
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    
    window.URL.revokeObjectURL(url);
    document.body.removeChild(a);
    
    const totalRecords = response.headers.get('X-Total-Records');
    console.log(`✅ Reporte descargado: ${totalRecords} modalidades`);
    
  } catch (error) {
    console.error('❌ Error:', error);
    alert(`Error al generar reporte: ${error.message}`);
  }
}

// Uso: Reporte completo
downloadCompletedModalitiesReport();

// Uso: Solo exitosas 2025
downloadCompletedModalitiesReport({
  results: ['SUCCESS'],
  year: 2025,
  sortBy: 'GRADE',
  sortDirection: 'DESC'
});

// Uso: Solo con distinción
downloadCompletedModalitiesReport({
  onlyWithDistinction: true,
  sortBy: 'GRADE'
});

// Uso: Laureados
downloadCompletedModalitiesReport({
  distinctionType: 'LAUREATE',
  year: 2025
});
```

---

### Ejemplo 2: React Component

```jsx
import React, { useState } from 'react';
import axios from 'axios';

function CompletedModalitiesReportGenerator() {
  const [filters, setFilters] = useState({
    modalityTypes: [],
    results: [],
    year: null,
    semester: null,
    onlyWithDistinction: false,
    distinctionType: '',
    minGrade: null,
    maxGrade: null,
    sortBy: 'DATE',
    sortDirection: 'DESC'
  });
  
  const [loading, setLoading] = useState(false);
  
  const downloadReport = async () => {
    setLoading(true);
    
    try {
      const token = localStorage.getItem('auth_token');
      
      // Limpiar filtros vacíos
      const cleanFilters = Object.entries(filters).reduce((acc, [key, value]) => {
        if (value && (Array.isArray(value) ? value.length > 0 : true)) {
          acc[key] = value;
        }
        return acc;
      }, {});
      
      const response = await axios.post(
        'http://localhost:8080/reports/modalities/completed/pdf',
        cleanFilters,
        {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          responseType: 'blob'
        }
      );
      
      const blob = new Blob([response.data], { type: 'application/pdf' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `Reporte_Completadas_${new Date().toISOString().split('T')[0]}.pdf`;
      document.body.appendChild(link);
      link.click();
      link.remove();
      
      const totalRecords = response.headers['x-total-records'];
      alert(`✅ Reporte generado: ${totalRecords} modalidades`);
      
    } catch (error) {
      console.error('❌ Error:', error);
      alert('Error al generar el reporte');
    } finally {
      setLoading(false);
    }
  };
  
  return (
    <div className="completed-modalities-report">
      <h2>🎓 Reporte de Modalidades Completadas</h2>
      
      <div className="filters">
        <div className="filter-group">
          <label>Resultado</label>
          <select multiple value={filters.results} onChange={e => setFilters({
            ...filters,
            results: Array.from(e.target.selectedOptions, opt => opt.value)
          })}>
            <option value="SUCCESS">Exitosas</option>
            <option value="FAILED">Fallidas</option>
          </select>
        </div>
        
        <div className="filter-group">
          <label>Año</label>
          <input 
            type="number" 
            value={filters.year || ''} 
            onChange={e => setFilters({...filters, year: parseInt(e.target.value) || null})}
            placeholder="Ej: 2025"
          />
        </div>
        
        <div className="filter-group">
          <label>Semestre</label>
          <select value={filters.semester || ''} onChange={e => setFilters({
            ...filters, 
            semester: e.target.value ? parseInt(e.target.value) : null
          })}>
            <option value="">Todos</option>
            <option value="1">1</option>
            <option value="2">2</option>
          </select>
        </div>
        
        <div className="filter-group">
          <label>
            <input 
              type="checkbox" 
              checked={filters.onlyWithDistinction}
              onChange={e => setFilters({...filters, onlyWithDistinction: e.target.checked})}
            />
            Solo con Distinción
          </label>
        </div>
        
        {filters.onlyWithDistinction && (
          <div className="filter-group">
            <label>Tipo de Distinción</label>
            <select value={filters.distinctionType} onChange={e => setFilters({
              ...filters,
              distinctionType: e.target.value
            })}>
              <option value="">Todas</option>
              <option value="MERITORIOUS">Meritorio</option>
              <option value="LAUREATE">Laureado</option>
            </select>
          </div>
        )}
        
        <div className="filter-group">
          <label>Calificación Mínima</label>
          <input 
            type="number" 
            step="0.1" 
            min="0" 
            max="5"
            value={filters.minGrade || ''} 
            onChange={e => setFilters({...filters, minGrade: parseFloat(e.target.value) || null})}
            placeholder="Ej: 4.0"
          />
        </div>
        
        <div className="filter-group">
          <label>Ordenar Por</label>
          <select value={filters.sortBy} onChange={e => setFilters({...filters, sortBy: e.target.value})}>
            <option value="DATE">Fecha</option>
            <option value="GRADE">Calificación</option>
            <option value="TYPE">Tipo</option>
            <option value="DURATION">Duración</option>
          </select>
        </div>
      </div>
      
      <button onClick={downloadReport} disabled={loading}>
        {loading ? '⏳ Generando...' : '📥 Descargar Reporte PDF'}
      </button>
    </div>
  );
}

export default CompletedModalitiesReportGenerator;
```

---

### Ejemplo 3: Python

```python
import requests
from datetime import datetime
from typing import Optional, List
import os

class CompletedModalitiesReportClient:
    def __init__(self, base_url: str, token: str):
        self.base_url = base_url
        self.headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/json"
        }
    
    def download_report(
        self,
        modality_types: Optional[List[str]] = None,
        results: Optional[List[str]] = None,
        year: Optional[int] = None,
        semester: Optional[int] = None,
        only_with_distinction: bool = False,
        distinction_type: Optional[str] = None,
        min_grade: Optional[float] = None,
        max_grade: Optional[float] = None,
        sort_by: str = "DATE",
        sort_direction: str = "DESC",
        output_dir: str = "reportes"
    ) -> Optional[str]:
        """Descarga reporte de modalidades completadas"""
        
        url = f"{self.base_url}/reports/modalities/completed/pdf"
        
        # Construir filtros
        filters = {}
        if modality_types:
            filters['modalityTypes'] = modality_types
        if results:
            filters['results'] = results
        if year:
            filters['year'] = year
        if semester:
            filters['semester'] = semester
        if only_with_distinction:
            filters['onlyWithDistinction'] = only_with_distinction
        if distinction_type:
            filters['distinctionType'] = distinction_type
        if min_grade:
            filters['minGrade'] = min_grade
        if max_grade:
            filters['maxGrade'] = max_grade
        filters['sortBy'] = sort_by
        filters['sortDirection'] = sort_direction
        
        try:
            print("🎓 Generando reporte de modalidades completadas...")
            
            response = requests.post(
                url,
                headers=self.headers,
                json=filters,
                stream=True
            )
            
            if response.status_code == 200:
                os.makedirs(output_dir, exist_ok=True)
                
                timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
                filename = f"Reporte_Completadas_{timestamp}.pdf"
                filepath = os.path.join(output_dir, filename)
                
                with open(filepath, 'wb') as f:
                    for chunk in response.iter_content(chunk_size=8192):
                        f.write(chunk)
                
                file_size_kb = os.path.getsize(filepath) / 1024
                total_records = response.headers.get('X-Total-Records', 'N/A')
                
                print(f"✅ Reporte descargado: {filepath}")
                print(f"   Tamaño: {file_size_kb:.2f} KB")
                print(f"   Modalidades: {total_records}")
                
                return filepath
            else:
                print(f"❌ Error {response.status_code}")
                return None
                
        except Exception as e:
            print(f"❌ Excepción: {str(e)}")
            return None
    
    # Métodos de conveniencia
    
    def download_successful(self, year: Optional[int] = None) -> Optional[str]:
        """Solo exitosas"""
        return self.download_report(
            results=['SUCCESS'],
            year=year,
            sort_by='GRADE',
            sort_direction='DESC'
        )
    
    def download_with_distinction(self, year: Optional[int] = None) -> Optional[str]:
        """Solo con distinción"""
        return self.download_report(
            only_with_distinction=True,
            year=year,
            sort_by='GRADE'
        )
    
    def download_laureates(self, year: Optional[int] = None) -> Optional[str]:
        """Solo laureados"""
        return self.download_report(
            distinction_type='LAUREATE',
            year=year
        )
    
    def download_failed(self, year: Optional[int] = None) -> Optional[str]:
        """Solo fallidas"""
        return self.download_report(
            results=['FAILED'],
            year=year
        )

# Uso
client = CompletedModalitiesReportClient(
    base_url="http://localhost:8080",
    token="tu_token_jwt"
)

# Reporte completo
client.download_report()

# Solo exitosas 2025
client.download_successful(2025)

# Con distinción
client.download_with_distinction(2025)

# Laureados
client.download_laureates(2025)
```

---

## 📊 Estructura de Datos

### CompletedModalitiesReportDTO (Principal)

```typescript
interface CompletedModalitiesReportDTO {
  generatedAt: string;
  generatedBy: string;
  academicProgramId: number;
  academicProgramName: string;
  academicProgramCode: string;
  
  appliedFilters: AppliedFiltersDTO;
  executiveSummary: ExecutiveSummaryDTO;
  completedModalities: CompletedModalityDetailDTO[];
  generalStatistics: GeneralStatisticsDTO;
  resultAnalysis: ResultAnalysisDTO;
  modalityTypeAnalysis: ModalityTypeAnalysisDTO[];
  temporalAnalysis: TemporalAnalysisDTO;
  directorPerformance: DirectorPerformanceDTO;
  distinctionAnalysis: DistinctionAnalysisDTO;
  metadata: ReportMetadataDTO;
}
```

---

### CompletedModalityDetailDTO

```typescript
interface CompletedModalityDetailDTO {
  modalityId: number;
  modalityType: string;
  modalityTypeName: string;
  result: 'SUCCESS' | 'FAILED';
  completionDate: string;
  completionDays: number;
  
  finalGrade: number;
  gradeDescription: string;
  academicDistinction: 'LAUREATE' | 'MERITORIOUS' | null;
  
  students: StudentInfoDTO[];
  studentCount: number;
  isGroup: boolean;
  
  directorName: string;
  directorEmail: string;
  
  selectionDate: string;
  defenseDate: string;
  defenseLocation: string;
  examiners: string[];
  
  year: number;
  semester: number;
  periodLabel: string;
  
  observations: string;
}
```

**Ejemplo**:
```json
{
  "modalityId": 145,
  "modalityType": "PROYECTO_DE_GRADO",
  "modalityTypeName": "Proyecto de Grado",
  "result": "SUCCESS",
  "completionDate": "2025-12-15T00:00:00",
  "completionDays": 145,
  "finalGrade": 5.0,
  "gradeDescription": "Excelente",
  "academicDistinction": "LAUREATE",
  "students": [
    {
      "studentId": 1001,
      "studentCode": "20191234567",
      "fullName": "Ana García López",
      "email": "ana.garcia@usco.edu.co",
      "cumulativeGPA": 4.85,
      "completedCredits": 160,
      "isLeader": true
    }
  ],
  "studentCount": 1,
  "isGroup": false,
  "directorName": "Dr. Carlos López García",
  "directorEmail": "carlos.lopez@usco.edu.co",
  "selectionDate": "2025-06-15T00:00:00",
  "defenseDate": "2025-12-10T14:00:00",
  "defenseLocation": "Auditorio Principal",
  "examiners": [
    "Dr. Carlos López (Director)",
    "Dra. María Rodríguez",
    "Dr. Pedro Martínez"
  ],
  "year": 2025,
  "semester": 2,
  "periodLabel": "2025-2",
  "observations": "Proyecto innovador con potencial de publicación"
}
```

---

## 🎯 Valor Agregado del Reporte

### Para Evaluación Institucional
- 📊 **Tasa de éxito** medible y comparable
- 🎓 **Reconocimiento de excelencia** (distinciones)
- 📈 **Tendencias** de mejora o declive
- 🔍 **Identificación de mejores prácticas**

### Para Ceremonias de Grado
- 🏆 **Listado de laureados** para menciones especiales
- ⭐ **Trabajos meritorios** para reconocimiento público
- 📜 **Información completa** para diplomas y actas

### Para Mejora Continua
- 📊 **Análisis de causas de fallo** (prevenibles)
- ✅ **Factores de éxito** replicables
- 👥 **Desempeño de directores** para capacitación
- ⏱️ **Tiempos óptimos** como referencia

---

## ✅ Checklist de Uso

- [ ] Token JWT válido
- [ ] Permiso `PERM_VIEW_REPORT`
- [ ] Decidir filtros a aplicar
- [ ] Verificar rango de fechas (si aplica)
- [ ] Request Body bien formado (JSON válido)

---

## 📞 Información de Contacto

### Código Fuente
- **Controller**: `com.SIGMA.USCO.report.controller.GlobalModalityReportController`
- **Generator**: `com.SIGMA.USCO.report.service.CompletedModalitiesPdfGenerator`
- **Service**: `com.SIGMA.USCO.report.service.ReportService`
- **DTOs**: `com.SIGMA.USCO.report.dto.CompletedModalitiesReportDTO`, `CompletedModalitiesFilterDTO`

### Documentación Relacionada
- [Reporte Global](./DOCUMENTACION_REPORTE_MODALIDADES_ACTIVAS.md)
- [Reporte Histórico](./DOCUMENTACION_REPORTE_HISTORICO_MODALIDAD.md)
- [Reporte de Estudiantes](./DOCUMENTACION_REPORTE_LISTADO_ESTUDIANTES.md)

---

**Generado por**: SIGMA - Sistema de Gestión de Modalidades de Grado  
**Tipo de Reporte**: Análisis de Modalidades Completadas con Distinciones  
**Servicio**: CompletedModalitiesPdfGenerator  
**Última actualización**: 18 de Febrero de 2026  
**Versión**: 1.0

