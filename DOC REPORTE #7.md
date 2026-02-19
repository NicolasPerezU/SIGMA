# 📋 Documentación: Reporte de Listado de Estudiantes (Filtrado Avanzado)

## 📝 Descripción General

Este endpoint genera un **reporte avanzado en formato PDF** que lista estudiantes participando en modalidades de grado, con capacidad de **filtrado múltiple y análisis estadístico detallado**. Permite filtrar por estados, tipos de modalidad, semestres, y otros criterios, proporcionando una vista completa del progreso estudiantil con estadísticas, distribuciones y análisis de tendencias.

**Generador**: `StudentListingPdfGenerator`

**Tipo de Reporte**: Listado filtrado con análisis estadístico

---

## 🔗 Endpoint

### **POST** `/reports/students/listing/pdf`

**Descripción**: Genera y descarga un reporte en PDF con el listado de estudiantes en modalidades de grado del programa, aplicando filtros opcionales y mostrando análisis detallados por modalidad, estado y semestre.

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

El body es **OPCIONAL**. Si no se envía o está vacío, se listan todos los estudiantes del programa sin filtros.

```json
{
  "statuses": ["APROBADO", "EN_REVISION", "COMPLETADO"],
  "modalityTypes": ["PROYECTO DE GRADO", "PASANTIA"],
  "semesters": ["2025-1", "2025-2", "2026-1"],
  "year": 2025,
  "timelineStatus": "DELAYED",
  "modalityTypeFilter": "INDIVIDUAL",
  "hasDirector": true,
  "sortBy": "NAME",
  "sortDirection": "ASC",
  "includeInactive": false
}
```

### Campos del Request Body

| Campo | Tipo | Requerido | Descripción | Valor por Defecto | Ejemplo |
|-------|------|-----------|-------------|-------------------|---------|
| `statuses` | `List<String>` | No | Estados de modalidad a incluir | Todos | `["APROBADO", "EN_REVISION"]` |
| `modalityTypes` | `List<String>` | No | Tipos específicos de modalidad | Todos | `["PROYECTO DE GRADO"]` |
| `semesters` | `List<String>` | No | Semestres específicos (formato: YYYY-S) | Todos | `["2025-1", "2026-1"]` |
| `year` | `Integer` | No | Año específico | Todos | `2025` |
| `timelineStatus` | `String` | No | Estado de línea de tiempo | Todos | `"DELAYED"`, `"ON_TIME"`, `"AT_RISK"` |
| `modalityTypeFilter` | `String` | No | Tipo de modalidad (individual/grupal) | Todos | `"INDIVIDUAL"`, `"GROUP"` |
| `hasDirector` | `Boolean` | No | Filtrar si tiene director asignado | Todos | `true`, `false` |
| `sortBy` | `String` | No | Criterio de ordenamiento | `"NAME"` | `"NAME"`, `"DATE"`, `"STATUS"`, `"MODALITY"`, `"PROGRESS"` |
| `sortDirection` | `String` | No | Dirección de orden | `"ASC"` | `"ASC"`, `"DESC"` |
| `includeInactive` | `Boolean` | No | Incluir estudiantes inactivos | `false` | `true`, `false` |

### Estados Válidos

- `APROBADO`
- `EN_REVISION`
- `PENDIENTE_APROBACION`
- `APROBADO_SECRETARIA`
- `APROBADO_CONSEJO`
- `EN_PROGRESO`
- `COMPLETADO`
- `CANCELADO`
- `RECHAZADO`

### Timeline Status Válidos

- `ON_TIME`: A tiempo según cronograma
- `DELAYED`: Retrasado
- `AT_RISK`: En riesgo de retraso

---

## 📤 Response (Respuesta)

### Respuesta Exitosa (200 OK)

**Content-Type**: `application/pdf`

**Headers de Respuesta**:
```http
Content-Type: application/pdf
Content-Disposition: attachment; filename=Reporte_Listado_Estudiantes_2026-02-18_143025.pdf
X-Report-Generated-At: 2026-02-18T14:30:25
X-Total-Records: 45
Content-Length: 278945
```

**Body**: Archivo PDF binario profesional con listado de estudiantes

### Respuestas de Error

#### Error de Validación (400)
```json
{
  "success": false,
  "error": "Datos inválidos: El año debe ser mayor a 2000",
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

### Caso de Uso 1: Listado Completo Sin Filtros

**Escenario**: Jefatura necesita una lista completa de todos los estudiantes en modalidades.

**Request**:
```json
{}
```

**Resultado**: PDF con todos los estudiantes del programa, sin excepciones.

---

### Caso de Uso 2: Estudiantes en Revisión

**Escenario**: Comité necesita revisar estudiantes con modalidades pendientes de aprobación.

**Request**:
```json
{
  "statuses": ["EN_REVISION", "PENDIENTE_APROBACION"],
  "sortBy": "DATE",
  "sortDirection": "ASC"
}
```

**Resultado**: PDF con estudiantes ordenados por fecha de inicio, solo estados en revisión.

---

### Caso de Uso 3: Proyectos de Grado del 2025

**Escenario**: Secretaría necesita listar todos los proyectos de grado del año pasado.

**Request**:
```json
{
  "modalityTypes": ["PROYECTO DE GRADO"],
  "year": 2025,
  "sortBy": "NAME"
}
```

**Resultado**: PDF con proyectos de grado de 2025, alfabéticamente ordenados.

---

### Caso de Uso 4: Estudiantes Retrasados Sin Director

**Escenario**: Jefatura quiere identificar casos críticos que requieren intervención.

**Request**:
```json
{
  "timelineStatus": "DELAYED",
  "hasDirector": false,
  "sortBy": "PROGRESS"
}
```

**Resultado**: PDF con estudiantes retrasados sin director, ordenados por avance (menor a mayor).

---

### Caso de Uso 5: Modalidades Individuales Completadas

**Escenario**: Consejo evalúa éxito de modalidades individuales.

**Request**:
```json
{
  "statuses": ["COMPLETADO"],
  "modalityTypeFilter": "INDIVIDUAL",
  "year": 2025
}
```

**Resultado**: PDF con estudiantes que completaron modalidades individuales en 2025.

---

### Caso de Uso 6: Estudiantes del Semestre Actual

**Escenario**: Jefatura necesita monitorear el semestre en curso.

**Request**:
```json
{
  "semesters": ["2026-1"],
  "includeInactive": false
}
```

**Resultado**: PDF con estudiantes activos del primer semestre 2026.

---

## 📄 Estructura Completa del PDF

### **PORTADA INSTITUCIONAL**

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


      REPORTE DE LISTADO DE ESTUDIANTES
         EN MODALIDADES DE GRADO              ← Título principal
                                                 (rojo)

┌─────────────────────────────────────────────┐
│ FILTROS APLICADOS:                          │ ← Caja dorada
│ • Estados: Aprobado, En Revisión            │   (si hay filtros)
│ • Modalidades: Proyecto de Grado            │
│ • Año: 2025                                 │
└─────────────────────────────────────────────┘


╔═══════════════════════════════════════════════════╗
║ Programa: Ingeniería de Sistemas                 ║
║ Código: IS-2020                                   ║
║ Fecha de Generación: 18/02/2026 - 14:30         ║ ← Tabla info
║ Generado por: Dr. Juan Pérez                     ║
║ Total de Estudiantes: 45                          ║
║ Modalidades Diferentes: 8                         ║
╚═══════════════════════════════════════════════════╝


┌──────────────────────────────────────────────────────┐
│  Sistema SIGMA - Reporte de Estudiantes             │ ← Footer
│  Sistema Integral de Gestión de Modalidades         │   dorado
└──────────────────────────────────────────────────────┘
```

---

### **SECCIÓN 1: RESUMEN EJECUTIVO**

#### 1.1 Tarjetas de Métricas Clave (3×3)

**Fila 1**:
```
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│      45      │  │      35      │  │      10      │
│  Estudiantes │  │ Modalidades  │  │ Completadas  │
│    Totales   │  │   Activas    │  │              │
└──────────────┘  └──────────────┘  └──────────────┘
```

**Fila 2**:
```
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│      8       │  │      5       │  │    73.5%     │
│    Tipos     │  │   Estados    │  │   Progreso   │
│ Modalidades  │  │  Diferentes  │  │   Promedio   │
└──────────────┘  └──────────────┘  └──────────────┘
```

**Fila 3**:
```
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│ Proyecto de  │  │   Aprobado   │  │     35       │
│    Grado     │  │              │  │   A Tiempo   │
│ (Más común)  │  │ (Más común)  │  │   (77.8%)    │
└──────────────┘  └──────────────┘  └──────────────┘
```

#### 1.2 Estadísticas Rápidas

```
┌─────────────────────────────────────────────────────┐
│ ESTADÍSTICAS GENERALES                              │
├─────────────────────────────────────────────────────┤
│                                                     │
│ Total de Estudiantes:           45                  │
│ Modalidades Activas:            35 (77.8%)          │
│ Modalidades Completadas:        10 (22.2%)          │
│                                                     │
│ Modalidades Individuales:       30 (66.7%)          │
│ Modalidades Grupales:           15 (33.3%)          │
│ Ratio Individual:Grupal:        2.0:1               │
│                                                     │
│ Con Director Asignado:          40 (88.9%)          │
│ Sin Director:                    5 (11.1%)          │
│                                                     │
│ Progreso Promedio:              73.5%               │
│ Promedio Créditos Completados:  145.8              │
│ Promedio GPA Acumulado:         4.1                 │
│ Días Promedio en Modalidad:    125.3               │
│                                                     │
└─────────────────────────────────────────────────────┘
```

#### 1.3 Estado de Línea de Tiempo

```
ESTADO DE PROGRESO TEMPORAL

A Tiempo       ███████████████████░░  35 estudiantes (77.8%)
Retrasados     ████░░░░░░░░░░░░░░░░   7 estudiantes (15.6%)
En Riesgo      ██░░░░░░░░░░░░░░░░░░   3 estudiantes (6.7%)
```

---

### **SECCIÓN 2: LISTADO DETALLADO DE ESTUDIANTES**

*Tabla completa con información de cada estudiante*

#### Tabla de Estudiantes (Paginada)

| # | Estudiante | Código | Modalidad | Estado | Director | Inicio | Días | Progreso | Timeline |
|---|------------|--------|-----------|--------|----------|--------|------|----------|----------|
| 1 | Ana García López | 20191234567 | Proyecto de Grado | Aprobado | Dr. Carlos López | 15/08/2025 | 187 | 85% | ✓ A tiempo |
| 2 | Carlos Ruiz Pérez | 20192345678 | Pasantía | En Revisión | Dra. María Gómez | 01/09/2025 | 170 | 60% | ⚠ Retrasado |
| 3 | Diana Morales | 20193456789 | Proyecto de Grado | Aprobado | Dr. Pedro Martínez | 10/02/2026 | 8 | 15% | ✓ A tiempo |
| ... | ... | ... | ... | ... | ... | ... | ... | ... | ... |

**Características**:
- Encabezados rojos con texto blanco
- Filas alternadas (blanco/dorado claro)
- Alertas visuales para timeline (✓, ⚠, 🚨)
- Ordenamiento según filtro `sortBy`
- Agrupación por modalidad opcional

#### Detalle Expandido por Estudiante

Para cada estudiante, se puede mostrar:

```
═══════════════════════════════════════════════════════
ESTUDIANTE: ANA GARCÍA LÓPEZ
═══════════════════════════════════════════════════════

Información Personal:
├─ Código: 20191234567
├─ Email: ana.garcia@usco.edu.co
└─ Teléfono: +57 300 123 4567

Información Académica:
├─ Estado Académico: Activo
├─ Semestre Actual: 10
├─ Créditos Completados: 152/160
└─ Promedio Acumulado: 4.35

Modalidad de Grado:
├─ Tipo: PROYECTO DE GRADO
├─ Nombre: Proyecto de Grado
├─ Estado: Aprobado
├─ Fecha de Inicio: 15/08/2025
├─ Días en Modalidad: 187 días
├─ Progreso: 85%
└─ Timeline: ✓ A TIEMPO

Proyecto:
├─ Título: "Sistema de Gestión de Inventarios con IoT"
└─ Descripción: Desarrollo de plataforma web...

Director:
├─ Nombre: Dr. Carlos López García
└─ Email: carlos.lopez@usco.edu.co

Grupo (si aplica):
├─ Tamaño: 1 (Individual)
└─ Miembros: Ana García López (Líder)

Estadísticas:
├─ Días Esperados de Completitud: 180 días
├─ Días Transcurridos: 187 días
└─ Observaciones: Proyecto en fase final de desarrollo
```

---

### **SECCIÓN 3: DISTRIBUCIÓN Y ANÁLISIS**

#### 3.1 Distribución por Tipo de Modalidad

```
ESTUDIANTES POR TIPO DE MODALIDAD

Proyecto de Grado        ████████████████████  25 est. (55.6%)
Pasantía                 ████████░░░░░░░░░░░░  10 est. (22.2%)
Práctica Profesional     ████░░░░░░░░░░░░░░░░   5 est. (11.1%)
Emprendimiento           ██░░░░░░░░░░░░░░░░░░   3 est. (6.7%)
Portafolio Profesional   ██░░░░░░░░░░░░░░░░░░   2 est. (4.4%)
```

#### 3.2 Distribución por Estado

```
ESTUDIANTES POR ESTADO DE MODALIDAD

Aprobado                 ████████████████████  30 est. (66.7%)
En Revisión              ████░░░░░░░░░░░░░░░░   6 est. (13.3%)
Completado               ███░░░░░░░░░░░░░░░░░   5 est. (11.1%)
Aprobado Consejo         ██░░░░░░░░░░░░░░░░░░   3 est. (6.7%)
Pendiente Aprobación     █░░░░░░░░░░░░░░░░░░░   1 est. (2.2%)
```

#### 3.3 Distribución por Semestre

```
ESTUDIANTES POR SEMESTRE DE INICIO

2026-1 ████████████████░░░░  18 estudiantes
2025-2 ████████████░░░░░░░░  15 estudiantes
2025-1 ████████░░░░░░░░░░░░  10 estudiantes
2024-2 ██░░░░░░░░░░░░░░░░░░   2 estudiantes
```

#### 3.4 Distribución por Género

```
DISTRIBUCIÓN POR GÉNERO

Masculino    ███████████████░░░░░  27 estudiantes (60.0%)
Femenino     ████████████░░░░░░░░  17 estudiantes (37.8%)
Otro         █░░░░░░░░░░░░░░░░░░░   1 estudiante  (2.2%)
```

---

### **SECCIÓN 4: ESTADÍSTICAS POR MODALIDAD**

*Para cada tipo de modalidad*

```
═══════════════════════════════════════════════════════
PROYECTO DE GRADO
═══════════════════════════════════════════════════════

Estudiantes Totales: 25
├─ Activos: 20 (80.0%)
└─ Completados: 5 (20.0%)

Rendimiento:
├─ Tasa de Completitud: 90.0%
├─ Promedio Días para Completar: 165.5 días
├─ Mínimo: 120 días
└─ Máximo: 210 días

Top Directores:
1. Dr. Carlos López (8 estudiantes)
2. Dra. María Rodríguez (5 estudiantes)
3. Dr. Pedro Martínez (4 estudiantes)

Promedio GPA de Estudiantes: 4.25
```

---

### **SECCIÓN 5: ESTADÍSTICAS POR ESTADO**

*Para cada estado de modalidad*

```
═══════════════════════════════════════════════════════
APROBADO
═══════════════════════════════════════════════════════

Estudiantes: 30 (66.7%)

Promedio de Días en Estado: 125.8 días

Modalidades Principales:
1. Proyecto de Grado (18 estudiantes)
2. Pasantía (7 estudiantes)
3. Práctica Profesional (5 estudiantes)

Tendencia: ↗ CRECIENTE (+12% vs semestre anterior)

Análisis: Estado con mayor concentración de estudiantes.
Indica buen ritmo de aprobación de propuestas.
```

---

### **SECCIÓN 6: ESTADÍSTICAS POR SEMESTRE**

*Para cada semestre analizado*

```
═══════════════════════════════════════════════════════
SEMESTRE 2026-1
═══════════════════════════════════════════════════════

Estudiantes: 18 (40.0%)

Modalidades Iniciadas: 18
Modalidades Completadas: 2
Tasa de Completitud: 11.1%

Promedio GPA: 4.18

Tipos de Modalidad Más Populares:
1. Proyecto de Grado (10 estudiantes, 55.6%)
2. Pasantía (4 estudiantes, 22.2%)
3. Práctica Profesional (4 estudiantes, 22.2%)

Análisis: Semestre en curso con alta inscripción.
```

---

### **SECCIÓN 7: ANÁLISIS DE DESEMPEÑO**

#### 7.1 Indicadores de Avance

```
┌─────────────────────────────────────────────────────┐
│ INDICADORES DE PROGRESO                             │
├─────────────────────────────────────────────────────┤
│                                                     │
│ Progreso Promedio General:     73.5%               │
│ Estudiantes con >80% progreso: 18 (40.0%)          │
│ Estudiantes con 50-80%:        20 (44.4%)          │
│ Estudiantes con <50%:           7 (15.6%)          │
│                                                     │
│ GPA Promedio:                  4.1/5.0             │
│ Créditos Promedio Completados: 145.8/160           │
│                                                     │
└─────────────────────────────────────────────────────┘
```

#### 7.2 Timeline Performance

```
DESEMPEÑO TEMPORAL

✓ A TIEMPO (77.8%)
  35 estudiantes cumpliendo cronograma esperado
  Promedio de progreso: 82.3%

⚠ RETRASADOS (15.6%)
  7 estudiantes con retraso moderado (10-30 días)
  Promedio de progreso: 55.2%
  Requiere seguimiento

🚨 EN RIESGO (6.7%)
  3 estudiantes con retraso significativo (>30 días)
  Promedio de progreso: 38.5%
  Requiere intervención urgente
```

#### 7.3 Análisis de Directores

```
ASIGNACIÓN DE DIRECTORES

Con Director Asignado: 40 estudiantes (88.9%)
├─ Promedio días desde asignación: 95.3 días
└─ Progreso promedio: 76.8%

Sin Director: 5 estudiantes (11.1%)
├─ Promedio días en espera: 45.2 días
└─ Progreso promedio: 42.1%

HALLAZGO: Estudiantes con director tienen 34.7 puntos
porcentuales más de progreso en promedio.
```

---

### **SECCIÓN 8: ANÁLISIS COMPARATIVO**

#### 8.1 Individual vs Grupal

```
COMPARATIVA: MODALIDADES INDIVIDUALES VS GRUPALES

                        Individual      Grupal
Estudiantes:               30            15
Porcentaje:              66.7%         33.3%

Promedio Progreso:       78.5%         63.2%
Promedio Días:           118.2d        145.8d
Tasa Completitud:        92.0%         85.0%

Promedio GPA:             4.22          4.05
Con Director:            95.0%         80.0%

ANÁLISIS: Las modalidades individuales muestran mejor
desempeño en todos los indicadores clave.
```

#### 8.2 Por Rango de Semestre

```
COMPARATIVA POR SEMESTRE ACTUAL DEL ESTUDIANTE

Semestre 9-10:  22 est. │ Progreso: 85.2% │ GPA: 4.28
Semestre 11-12: 18 est. │ Progreso: 68.5% │ GPA: 4.10
Semestre 13+:    5 est. │ Progreso: 45.3% │ GPA: 3.85

TENDENCIA: Mayor avance en estudiantes de semestres 9-10
(etapa óptima según plan curricular).
```

---

### **SECCIÓN 9: CASOS ESPECIALES Y ALERTAS**

#### 9.1 Estudiantes Destacados

```
🏆 TOP 5 - MAYOR PROGRESO

1. Ana García López         95% │ Proyecto de Grado │ GPA: 4.85
2. Carlos Ruiz Martínez     92% │ Pasantía         │ GPA: 4.65
3. Diana Morales Pérez      90% │ Proyecto de Grado │ GPA: 4.55
4. Eduardo Torres García    88% │ Emprendimiento    │ GPA: 4.45
5. Fernanda Rojas Luna      87% │ Práctica Prof.    │ GPA: 4.40

Promedio GPA Top 5: 4.58
Promedio Progreso: 90.4%
```

#### 9.2 Casos que Requieren Atención

```
⚠️ ESTUDIANTES EN RIESGO (Requieren Intervención)

1. Pedro Gómez Silva
   ├─ Modalidad: Proyecto de Grado
   ├─ Estado: Aprobado
   ├─ Progreso: 25%
   ├─ Días en modalidad: 210 días (esperado: 180)
   ├─ Director: Dr. José Ramírez
   └─ Alerta: Sin avances registrados en 45 días

2. Laura Fernández Cruz
   ├─ Modalidad: Pasantía
   ├─ Estado: En Revisión
   ├─ Progreso: 30%
   ├─ Días en modalidad: 195 días (esperado: 150)
   ├─ Director: No asignado
   └─ Alerta: Sin director por 60 días

3. Miguel Torres Ruiz
   ├─ Modalidad: Proyecto de Grado
   ├─ Estado: Aprobado
   ├─ Progreso: 35%
   ├─ Días en modalidad: 180 días (esperado: 180)
   ├─ Director: Dra. Carmen Ortiz
   └─ Alerta: Alcanzó tiempo esperado sin completar

RECOMENDACIONES:
• Contactar estudiantes para evaluación de estado
• Asignar director a Laura Fernández urgentemente
• Programar reuniones de seguimiento con directores
```

#### 9.3 Sin Director Asignado

```
🚨 ESTUDIANTES SIN DIRECTOR (5)

1. Laura Fernández Cruz     │ Pasantía          │ 60 días esperando
2. Roberto Pérez Gómez      │ Proyecto de Grado │ 45 días esperando
3. Sandra Morales López     │ Práctica Prof.    │ 30 días esperando
4. Andrés García Torres     │ Proyecto de Grado │ 25 días esperando
5. Carolina Ruiz Díaz       │ Emprendimiento    │ 15 días esperando

ACCIÓN REQUERIDA: Asignar directores prioritariamente.
Casos críticos: Laura F. y Roberto P. (>30 días).
```

---

### **SECCIÓN 10: TENDENCIAS Y PROYECCIONES**

#### 10.1 Tendencia de Inscripción

```
TENDENCIA DE INSCRIPCIÓN POR SEMESTRE

2024-2   ██░░░░░░░░░░░░░░░░░░   2 estudiantes
2025-1   ████████░░░░░░░░░░░░  10 estudiantes  ↗ +400%
2025-2   ███████████░░░░░░░░░  15 estudiantes  ↗ +50%
2026-1   ████████████████░░░░  18 estudiantes  ↗ +20%

PROYECCIÓN 2026-2: 21 ± 3 estudiantes
Nivel de confianza: 72%
```

#### 10.2 Tendencia de Completitud

```
TASA DE COMPLETITUD POR SEMESTRE

2024-2:  75.0%  (3 completadas / 4 iniciadas)
2025-1:  80.0%  (8 completadas / 10 iniciadas)
2025-2:  85.0%  (13 completadas / 15 iniciadas)
2026-1:  11.1%  (2 completadas / 18 iniciadas) [En curso]

PROYECCIÓN FINAL 2026-1: ~88% 
(basado en progreso actual y tiempo restante)
```

---

### **SECCIÓN 11: RECOMENDACIONES Y ACCIONES**

```
═══════════════════════════════════════════════════════
RECOMENDACIONES ESTRATÉGICAS
═══════════════════════════════════════════════════════

ACCIONES INMEDIATAS (Próxima semana):

1. ASIGNACIÓN DE DIRECTORES
   • Asignar director a 5 estudiantes en espera
   • Priorizar casos con >30 días: Laura F. y Roberto P.

2. SEGUIMIENTO DE CASOS EN RIESGO
   • Reunión con 3 estudiantes en riesgo
   • Evaluar causas de retraso
   • Establecer plan de recuperación

3. REVISIÓN DE MODALIDADES ESTANCADAS
   • Contactar estudiantes con 0% de avance en 30+ días
   • Evaluar viabilidad de continuar o cambiar modalidad

ACCIONES DE MEDIANO PLAZO (Próximo mes):

4. OPTIMIZACIÓN DE PROCESOS
   • Reducir tiempo de asignación de director (<15 días)
   • Implementar alertas automáticas por inactividad

5. PROMOCIÓN DE MODALIDADES GRUPALES
   • Incentivar trabajo colaborativo (meta: 40% grupales)
   • Facilitar conformación de grupos al inicio

6. SEGUIMIENTO PREVENTIVO
   • Reuniones quincenales con estudiantes <50% progreso
   • Sistema de hitos de avance cada 30 días

ACCIONES A LARGO PLAZO (Semestre):

7. MEJORA DE TASAS DE COMPLETITUD
   • Meta: Aumentar de 85% a 90%
   • Capacitación a directores en seguimiento efectivo

8. BALANCEO DE CARGA DE DIRECTORES
   • Redistribuir estudiantes (meta: <5 por director)
   • Incorporar nuevos directores capacitados
```

---

### **SECCIÓN 12: CONCLUSIONES**

```
═══════════════════════════════════════════════════════
CONCLUSIONES GENERALES
═══════════════════════════════════════════════════════

1. POBLACIÓN ANALIZADA
   El reporte analiza 45 estudiantes distribuidos en 8 tipos
   de modalidades diferentes, con alta concentración en
   Proyectos de Grado (55.6%).

2. DESEMPEÑO GENERAL
   El 77.8% de estudiantes está a tiempo según cronograma,
   con un progreso promedio de 73.5%, indicando buen
   desempeño general del programa.

3. ASIGNACIÓN DE DIRECTORES
   El 88.9% tiene director asignado. Los 5 estudiantes sin
   director muestran progreso significativamente menor
   (42.1% vs 76.8%), evidenciando la importancia de
   supervisión temprana.

4. MODALIDADES INDIVIDUALES VS GRUPALES
   Las modalidades individuales superan a las grupales en
   progreso (+15.3pp), completitud (+7pp) y GPA (+0.17),
   sugiriendo mayor efectividad o preferencia estudiantil.

5. CASOS CRÍTICOS
   Se identificaron 3 estudiantes en riesgo (6.7%) que
   requieren intervención inmediata, y 5 sin director
   que necesitan asignación prioritaria.

6. TENDENCIA POSITIVA
   La inscripción creció 400% entre 2024-2 y 2025-1,
   manteniendo crecimiento sostenido (+20% en 2026-1),
   con proyección de 21 estudiantes para 2026-2.

7. CALIDAD ACADÉMICA
   GPA promedio de 4.1/5.0 indica que estudiantes de alto
   rendimiento académico participan en modalidades de grado,
   correlacionando positivamente con tasa de completitud.
```

---

### **PIE DE PÁGINA (Todas las Páginas)**

```
──────────────────────────────────────────────────────
Página 8 | Reporte de Estudiantes | Ingeniería de Sistemas | 18/02/2026
──────────────────────────────────────────────────────
```

---

## 💻 Ejemplos de Código

### Ejemplo 1: JavaScript/TypeScript (Frontend)

```typescript
interface StudentListingFilters {
  statuses?: string[];
  modalityTypes?: string[];
  semesters?: string[];
  year?: number;
  timelineStatus?: 'ON_TIME' | 'DELAYED' | 'AT_RISK';
  modalityTypeFilter?: 'INDIVIDUAL' | 'GROUP';
  hasDirector?: boolean;
  sortBy?: 'NAME' | 'DATE' | 'STATUS' | 'MODALITY' | 'PROGRESS';
  sortDirection?: 'ASC' | 'DESC';
  includeInactive?: boolean;
}

async function downloadStudentListingReport(filters?: StudentListingFilters) {
  const token = localStorage.getItem('auth_token');
  
  try {
    console.log('📋 Generando reporte de estudiantes...');
    
    const response = await fetch('http://localhost:8080/reports/students/listing/pdf', {
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
    
    // Extraer nombre del archivo
    const contentDisposition = response.headers.get('Content-Disposition');
    const filename = contentDisposition 
      ? contentDisposition.split('filename=')[1].replace(/"/g, '')
      : `Reporte_Estudiantes_${new Date().toISOString().split('T')[0]}.pdf`;
    
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    
    window.URL.revokeObjectURL(url);
    document.body.removeChild(a);
    
    // Mostrar información
    const totalRecords = response.headers.get('X-Total-Records');
    console.log(`✅ Reporte descargado: ${totalRecords} estudiantes`);
    
  } catch (error) {
    console.error('❌ Error:', error);
    alert(`Error al generar reporte: ${error.message}`);
  }
}

// Uso: Reporte completo
downloadStudentListingReport();

// Uso: Estudiantes retrasados
downloadStudentListingReport({
  timelineStatus: 'DELAYED',
  sortBy: 'PROGRESS',
  sortDirection: 'ASC'
});

// Uso: Proyectos de grado 2025
downloadStudentListingReport({
  modalityTypes: ['PROYECTO DE GRADO'],
  year: 2025,
  sortBy: 'NAME'
});
```

---

### Ejemplo 2: React Component Avanzado

```jsx
import React, { useState } from 'react';
import axios from 'axios';

function StudentListingReportGenerator() {
  const [filters, setFilters] = useState({
    statuses: [],
    modalityTypes: [],
    semesters: [],
    year: null,
    timelineStatus: '',
    modalityTypeFilter: '',
    hasDirector: null,
    sortBy: 'NAME',
    sortDirection: 'ASC',
    includeInactive: false
  });
  
  const [loading, setLoading] = useState(false);
  
  // Opciones disponibles
  const statusOptions = [
    'APROBADO', 'EN_REVISION', 'PENDIENTE_APROBACION',
    'APROBADO_SECRETARIA', 'APROBADO_CONSEJO',
    'EN_PROGRESO', 'COMPLETADO', 'CANCELADO'
  ];
  
  const modalityTypeOptions = [
    'PROYECTO DE GRADO', 'PASANTIA', 'PRACTICA PROFESIONAL',
    'EMPRENDIMIENTO Y FORTALECIMIENTO DE EMPRESA',
    'PORTAFOLIO PROFESIONAL', 'SEMINARIO DE GRADO'
  ];
  
  const semesterOptions = [
    '2024-1', '2024-2', '2025-1', '2025-2', '2026-1', '2026-2'
  ];
  
  const handleCheckboxChange = (field, value) => {
    setFilters(prev => ({
      ...prev,
      [field]: prev[field].includes(value)
        ? prev[field].filter(v => v !== value)
        : [...prev[field], value]
    }));
  };
  
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
      
      console.log('📋 Generando reporte con filtros:', cleanFilters);
      
      const response = await axios.post(
        'http://localhost:8080/reports/students/listing/pdf',
        cleanFilters,
        {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          responseType: 'blob'
        }
      );
      
      // Descargar PDF
      const blob = new Blob([response.data], { type: 'application/pdf' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `Reporte_Estudiantes_${new Date().toISOString().split('T')[0]}.pdf`;
      document.body.appendChild(link);
      link.click();
      link.remove();
      
      const totalRecords = response.headers['x-total-records'];
      alert(`✅ Reporte generado: ${totalRecords} estudiantes`);
      
    } catch (error) {
      console.error('❌ Error:', error);
      alert('Error al generar el reporte');
    } finally {
      setLoading(false);
    }
  };
  
  const clearFilters = () => {
    setFilters({
      statuses: [],
      modalityTypes: [],
      semesters: [],
      year: null,
      timelineStatus: '',
      modalityTypeFilter: '',
      hasDirector: null,
      sortBy: 'NAME',
      sortDirection: 'ASC',
      includeInactive: false
    });
  };
  
  return (
    <div className="student-listing-report">
      <h2>📋 Reporte de Listado de Estudiantes</h2>
      
      <div className="filters-container">
        <div className="filter-group">
          <h4>Estados</h4>
          {statusOptions.map(status => (
            <label key={status}>
              <input
                type="checkbox"
                checked={filters.statuses.includes(status)}
                onChange={() => handleCheckboxChange('statuses', status)}
              />
              {status}
            </label>
          ))}
        </div>
        
        <div className="filter-group">
          <h4>Tipos de Modalidad</h4>
          {modalityTypeOptions.map(type => (
            <label key={type}>
              <input
                type="checkbox"
                checked={filters.modalityTypes.includes(type)}
                onChange={() => handleCheckboxChange('modalityTypes', type)}
              />
              {type}
            </label>
          ))}
        </div>
        
        <div className="filter-group">
          <h4>Semestres</h4>
          {semesterOptions.map(semester => (
            <label key={semester}>
              <input
                type="checkbox"
                checked={filters.semesters.includes(semester)}
                onChange={() => handleCheckboxChange('semesters', semester)}
              />
              {semester}
            </label>
          ))}
        </div>
        
        <div className="filter-group">
          <h4>Año Específico</h4>
          <input
            type="number"
            value={filters.year || ''}
            onChange={e => setFilters({...filters, year: parseInt(e.target.value) || null})}
            placeholder="Ej: 2025"
          />
        </div>
        
        <div className="filter-group">
          <h4>Estado de Timeline</h4>
          <select
            value={filters.timelineStatus}
            onChange={e => setFilters({...filters, timelineStatus: e.target.value})}
          >
            <option value="">Todos</option>
            <option value="ON_TIME">A Tiempo</option>
            <option value="DELAYED">Retrasados</option>
            <option value="AT_RISK">En Riesgo</option>
          </select>
        </div>
        
        <div className="filter-group">
          <h4>Tipo Individual/Grupal</h4>
          <select
            value={filters.modalityTypeFilter}
            onChange={e => setFilters({...filters, modalityTypeFilter: e.target.value})}
          >
            <option value="">Todos</option>
            <option value="INDIVIDUAL">Individual</option>
            <option value="GROUP">Grupal</option>
          </select>
        </div>
        
        <div className="filter-group">
          <h4>Director Asignado</h4>
          <select
            value={filters.hasDirector === null ? '' : filters.hasDirector}
            onChange={e => setFilters({
              ...filters, 
              hasDirector: e.target.value === '' ? null : e.target.value === 'true'
            })}
          >
            <option value="">Todos</option>
            <option value="true">Con Director</option>
            <option value="false">Sin Director</option>
          </select>
        </div>
        
        <div className="filter-group">
          <h4>Ordenar Por</h4>
          <select
            value={filters.sortBy}
            onChange={e => setFilters({...filters, sortBy: e.target.value})}
          >
            <option value="NAME">Nombre</option>
            <option value="DATE">Fecha</option>
            <option value="STATUS">Estado</option>
            <option value="MODALITY">Modalidad</option>
            <option value="PROGRESS">Progreso</option>
          </select>
          
          <select
            value={filters.sortDirection}
            onChange={e => setFilters({...filters, sortDirection: e.target.value})}
          >
            <option value="ASC">Ascendente</option>
            <option value="DESC">Descendente</option>
          </select>
        </div>
        
        <div className="filter-group">
          <label>
            <input
              type="checkbox"
              checked={filters.includeInactive}
              onChange={e => setFilters({...filters, includeInactive: e.target.checked})}
            />
            Incluir Inactivos
          </label>
        </div>
      </div>
      
      <div className="actions">
        <button onClick={clearFilters} className="btn-secondary">
          🔄 Limpiar Filtros
        </button>
        
        <button onClick={downloadReport} disabled={loading} className="btn-primary">
          {loading ? '⏳ Generando...' : '📥 Descargar Reporte PDF'}
        </button>
      </div>
      
      {Object.values(filters).some(v => 
        Array.isArray(v) ? v.length > 0 : v !== null && v !== '' && v !== false
      ) && (
        <div className="active-filters-summary">
          <h4>Filtros Activos:</h4>
          <ul>
            {filters.statuses.length > 0 && <li>Estados: {filters.statuses.join(', ')}</li>}
            {filters.modalityTypes.length > 0 && <li>Modalidades: {filters.modalityTypes.join(', ')}</li>}
            {filters.semesters.length > 0 && <li>Semestres: {filters.semesters.join(', ')}</li>}
            {filters.year && <li>Año: {filters.year}</li>}
            {filters.timelineStatus && <li>Timeline: {filters.timelineStatus}</li>}
            {filters.modalityTypeFilter && <li>Tipo: {filters.modalityTypeFilter}</li>}
            {filters.hasDirector !== null && <li>Director: {filters.hasDirector ? 'Con director' : 'Sin director'}</li>}
          </ul>
        </div>
      )}
    </div>
  );
}

export default StudentListingReportGenerator;
```

---

### Ejemplo 3: PowerShell - Múltiples Escenarios

```powershell
# Script para generar reportes de estudiantes con diferentes filtros

$token = "tu_token_jwt_aqui"
$baseUrl = "http://localhost:8080/reports/students/listing/pdf"
$outputDir = "Reportes_Estudiantes"

# Crear directorio
if (!(Test-Path $outputDir)) {
    New-Item -ItemType Directory -Path $outputDir | Out-Null
}

# Headers
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

# ============================================
# ESCENARIO 1: Reporte Completo
# ============================================
Write-Host "`n📋 Generando reporte completo..." -ForegroundColor Cyan

$filtros = @{} | ConvertTo-Json

Invoke-WebRequest `
    -Uri $baseUrl `
    -Method Post `
    -Headers $headers `
    -Body $filtros `
    -OutFile "$outputDir/Reporte_Completo.pdf"

Write-Host "✅ Generado: Reporte_Completo.pdf" -ForegroundColor Green

# ============================================
# ESCENARIO 2: Estudiantes Retrasados
# ============================================
Write-Host "`n⚠️  Generando reporte de retrasados..." -ForegroundColor Yellow

$filtrosRetrasados = @{
    timelineStatus = "DELAYED"
    sortBy = "PROGRESS"
    sortDirection = "ASC"
} | ConvertTo-Json

Invoke-WebRequest `
    -Uri $baseUrl `
    -Method Post `
    -Headers $headers `
    -Body $filtrosRetrasados `
    -OutFile "$outputDir/Reporte_Retrasados.pdf"

Write-Host "✅ Generado: Reporte_Retrasados.pdf" -ForegroundColor Green

# ============================================
# ESCENARIO 3: Sin Director Asignado
# ============================================
Write-Host "`n🚨 Generando reporte de sin director..." -ForegroundColor Red

$filtrosSinDirector = @{
    hasDirector = $false
    sortBy = "DATE"
    sortDirection = "ASC"
} | ConvertTo-Json

Invoke-WebRequest `
    -Uri $baseUrl `
    -Method Post `
    -Headers $headers `
    -Body $filtrosSinDirector `
    -OutFile "$outputDir/Reporte_Sin_Director.pdf"

Write-Host "✅ Generado: Reporte_Sin_Director.pdf" -ForegroundColor Green

# ============================================
# ESCENARIO 4: Proyectos de Grado 2025
# ============================================
Write-Host "`n📚 Generando reporte de proyectos 2025..." -ForegroundColor Cyan

$filtrosProyectos = @{
    modalityTypes = @("PROYECTO DE GRADO")
    year = 2025
    sortBy = "NAME"
    sortDirection = "ASC"
} | ConvertTo-Json

Invoke-WebRequest `
    -Uri $baseUrl `
    -Method Post `
    -Headers $headers `
    -Body $filtrosProyectos `
    -OutFile "$outputDir/Reporte_Proyectos_2025.pdf"

Write-Host "✅ Generado: Reporte_Proyectos_2025.pdf" -ForegroundColor Green

# ============================================
# ESCENARIO 5: Completados Exitosos
# ============================================
Write-Host "`n🎓 Generando reporte de completados..." -ForegroundColor Green

$filtrosCompletados = @{
    statuses = @("COMPLETADO")
    year = 2025
    sortBy = "PROGRESS"
    sortDirection = "DESC"
} | ConvertTo-Json

Invoke-WebRequest `
    -Uri $baseUrl `
    -Method Post `
    -Headers $headers `
    -Body $filtrosCompletados `
    -OutFile "$outputDir/Reporte_Completados_2025.pdf"

Write-Host "✅ Generado: Reporte_Completados_2025.pdf" -ForegroundColor Green

Write-Host "`n🎉 Todos los reportes generados!" -ForegroundColor Green
explorer.exe $outputDir
```

---

### Ejemplo 4: Python - Cliente Completo

```python
import requests
from datetime import datetime
from typing import Optional, List, Dict
import os

class StudentListingReportClient:
    """
    Cliente para generar reportes de listado de estudiantes
    """
    
    def __init__(self, base_url: str, token: str):
        self.base_url = base_url
        self.headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/json"
        }
    
    def download_report(
        self,
        statuses: Optional[List[str]] = None,
        modality_types: Optional[List[str]] = None,
        semesters: Optional[List[str]] = None,
        year: Optional[int] = None,
        timeline_status: Optional[str] = None,
        modality_type_filter: Optional[str] = None,
        has_director: Optional[bool] = None,
        sort_by: str = "NAME",
        sort_direction: str = "ASC",
        include_inactive: bool = False,
        output_dir: str = "reportes"
    ) -> Optional[str]:
        """
        Descarga reporte de listado de estudiantes
        
        Returns:
            Ruta del archivo descargado o None si falla
        """
        url = f"{self.base_url}/reports/students/listing/pdf"
        
        # Construir filtros
        filters = {}
        if statuses:
            filters['statuses'] = statuses
        if modality_types:
            filters['modalityTypes'] = modality_types
        if semesters:
            filters['semesters'] = semesters
        if year:
            filters['year'] = year
        if timeline_status:
            filters['timelineStatus'] = timeline_status
        if modality_type_filter:
            filters['modalityTypeFilter'] = modality_type_filter
        if has_director is not None:
            filters['hasDirector'] = has_director
        if sort_by:
            filters['sortBy'] = sort_by
        if sort_direction:
            filters['sortDirection'] = sort_direction
        if include_inactive:
            filters['includeInactive'] = include_inactive
        
        try:
            print("📋 Generando reporte de estudiantes...")
            if filters:
                print(f"   Filtros aplicados: {len(filters)}")
            
            response = requests.post(
                url,
                headers=self.headers,
                json=filters,
                stream=True
            )
            
            if response.status_code == 200:
                # Crear directorio
                os.makedirs(output_dir, exist_ok=True)
                
                # Generar nombre de archivo
                timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
                filename = f"Reporte_Estudiantes_{timestamp}.pdf"
                filepath = os.path.join(output_dir, filename)
                
                # Guardar
                with open(filepath, 'wb') as f:
                    for chunk in response.iter_content(chunk_size=8192):
                        f.write(chunk)
                
                # Info
                file_size_kb = os.path.getsize(filepath) / 1024
                total_records = response.headers.get('X-Total-Records', 'N/A')
                
                print(f"✅ Reporte descargado exitosamente")
                print(f"   Archivo: {filepath}")
                print(f"   Tamaño: {file_size_kb:.2f} KB")
                print(f"   Estudiantes: {total_records}")
                
                return filepath
            
            else:
                error = response.json()
                print(f"❌ Error {response.status_code}: {error.get('error')}")
                return None
                
        except Exception as e:
            print(f"❌ Excepción: {str(e)}")
            return None
    
    # Métodos de conveniencia
    
    def download_delayed_students(self) -> Optional[str]:
        """Estudiantes retrasados"""
        return self.download_report(
            timeline_status="DELAYED",
            sort_by="PROGRESS",
            sort_direction="ASC"
        )
    
    def download_without_director(self) -> Optional[str]:
        """Estudiantes sin director"""
        return self.download_report(
            has_director=False,
            sort_by="DATE",
            sort_direction="ASC"
        )
    
    def download_at_risk(self) -> Optional[str]:
        """Estudiantes en riesgo"""
        return self.download_report(
            timeline_status="AT_RISK"
        )
    
    def download_by_modality(self, modality_type: str, year: Optional[int] = None) -> Optional[str]:
        """Por tipo de modalidad y año"""
        return self.download_report(
            modality_types=[modality_type],
            year=year,
            sort_by="NAME"
        )
    
    def download_completed(self, year: Optional[int] = None) -> Optional[str]:
        """Completados"""
        return self.download_report(
            statuses=["COMPLETADO"],
            year=year,
            sort_by="PROGRESS",
            sort_direction="DESC"
        )

# Uso
client = StudentListingReportClient(
    base_url="http://localhost:8080",
    token="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
)

# Reporte completo
client.download_report()

# Estudiantes retrasados
client.download_delayed_students()

# Sin director
client.download_without_director()

# Proyectos de grado 2025
client.download_by_modality("PROYECTO DE GRADO", 2025)

# Completados 2025
client.download_completed(2025)
```

---

## 📊 Estructura de Datos del Reporte

### StudentListingReportDTO (Principales)

```typescript
interface StudentListingReportDTO {
  // Información básica
  generatedAt: string;
  generatedBy: string;
  academicProgramId: number;
  academicProgramName: string;
  academicProgramCode: string;
  
  // Filtros aplicados
  appliedFilters: AppliedFiltersDTO;
  
  // Resumen ejecutivo
  executiveSummary: ExecutiveSummaryDTO;
  
  // Listado de estudiantes
  students: StudentDetailDTO[];
  
  // Estadísticas generales
  generalStatistics: GeneralStatisticsDTO;
  
  // Distribuciones
  distributionAnalysis: DistributionAnalysisDTO;
  
  // Estadísticas por dimensión
  modalityStatistics: ModalityStatisticsDTO[];
  statusStatistics: StatusStatisticsDTO[];
  semesterStatistics: SemesterStatisticsDTO[];
  
  // Metadata
  metadata: ReportMetadataDTO;
}
```

---

### StudentDetailDTO

```typescript
interface StudentDetailDTO {
  // Personal
  studentId: number;
  studentCode: string;
  fullName: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  
  // Académico
  academicStatus: string;
  cumulativeAverage: number;
  completedCredits: number;
  totalCredits: number;
  currentSemester: number;
  
  // Modalidad
  modalityId: number;
  modalityType: string;
  modalityName: string;
  modalityStatus: string;
  modalityStatusDescription: string;
  selectionDate: string;
  lastUpdateDate: string;
  daysInModality: number;
  
  // Director
  directorName: string;
  directorEmail: string;
  
  // Proyecto
  projectTitle: string;
  projectDescription: string;
  
  // Grupo
  groupSize: number;
  groupMembers: string[];
  
  // Estadísticas
  progressPercentage: number;
  timelineStatus: string; // ON_TIME, DELAYED, AT_RISK
  expectedCompletionDays: number;
  observations: string;
}
```

**Ejemplo**:
```json
{
  "studentId": 1001,
  "studentCode": "20191234567",
  "fullName": "Ana García López",
  "firstName": "Ana",
  "lastName": "García López",
  "email": "ana.garcia@usco.edu.co",
  "phone": "+57 300 123 4567",
  "academicStatus": "ACTIVO",
  "cumulativeAverage": 4.35,
  "completedCredits": 152,
  "totalCredits": 160,
  "currentSemester": 10,
  "modalityId": 145,
  "modalityType": "PROYECTO_DE_GRADO",
  "modalityName": "Proyecto de Grado",
  "modalityStatus": "APROBADO",
  "modalityStatusDescription": "Aprobado",
  "selectionDate": "2025-08-15T00:00:00",
  "lastUpdateDate": "2026-02-10T14:30:00",
  "daysInModality": 187,
  "directorName": "Dr. Carlos López García",
  "directorEmail": "carlos.lopez@usco.edu.co",
  "projectTitle": "Sistema de Gestión de Inventarios con IoT",
  "projectDescription": "Desarrollo de plataforma web...",
  "groupSize": 1,
  "groupMembers": ["Ana García López"],
  "progressPercentage": 85.0,
  "timelineStatus": "ON_TIME",
  "expectedCompletionDays": 180,
  "observations": "Proyecto en fase final"
}
```

---

### ExecutiveSummaryDTO

```typescript
interface ExecutiveSummaryDTO {
  totalStudents: number;
  totalModalities: number;
  activeModalities: number;
  completedModalities: number;
  differentModalityTypes: number;
  differentStatuses: number;
  averageProgress: number;
  mostCommonModalityType: string;
  mostCommonStatus: string;
  quickStats: {[key: string]: number};
}
```

**Ejemplo**:
```json
{
  "totalStudents": 45,
  "totalModalities": 45,
  "activeModalities": 35,
  "completedModalities": 10,
  "differentModalityTypes": 8,
  "differentStatuses": 5,
  "averageProgress": 73.5,
  "mostCommonModalityType": "PROYECTO DE GRADO",
  "mostCommonStatus": "APROBADO",
  "quickStats": {
    "on_time": 35,
    "delayed": 7,
    "at_risk": 3,
    "with_director": 40,
    "without_director": 5
  }
}
```

---

## 🎯 Valor Agregado del Reporte

### Para Jefatura de Programa
- 📊 **Vista completa** de estudiantes en modalidades
- 🔍 **Identificación rápida** de casos críticos
- 📈 **Seguimiento de progreso** individual y colectivo
- ⚠️ **Alertas tempranas** de problemas

### Para Secretaría
- 📋 **Listados oficiales** para trámites
- 📞 **Información de contacto** actualizada
- 📅 **Fechas clave** para seguimiento
- 📊 **Estadísticas** para reportes

### Para Consejo de Programa
- 📊 **Análisis estadístico** para decisiones
- 📈 **Tendencias** de participación
- 🎯 **Evaluación de efectividad** por modalidad
- 🔄 **Comparativas** temporales

---

## ✅ Checklist de Uso

- [ ] Token JWT válido
- [ ] Permiso `PERM_VIEW_REPORT`
- [ ] Decidir qué filtros aplicar
- [ ] Determinar criterio de ordenamiento
- [ ] Request Body bien formado (JSON válido)
- [ ] Conexión estable

---

## 📞 Información de Contacto

### Código Fuente
- **Controller**: `com.SIGMA.USCO.report.controller.GlobalModalityReportController`
- **Generator**: `com.SIGMA.USCO.report.service.StudentListingPdfGenerator`
- **Service**: `com.SIGMA.USCO.report.service.ReportService`
- **DTOs**: `com.SIGMA.USCO.report.dto.StudentListingReportDTO`, `StudentListingFilterDTO`

### Documentación Relacionada
- [Reporte Global de Modalidades](./DOCUMENTACION_REPORTE_MODALIDADES_ACTIVAS.md)
- [Reporte de Directores](./DOCUMENTACION_REPORTE_DIRECTORES_MODALIDADES.md)
- [Reporte Histórico](./DOCUMENTACION_REPORTE_HISTORICO_MODALIDAD.md)

---

**Generado por**: SIGMA - Sistema de Gestión de Modalidades de Grado  
**Tipo de Reporte**: Listado de Estudiantes con Filtrado Avanzado  
**Servicio**: StudentListingPdfGenerator  
**Última actualización**: 18 de Febrero de 2026  
**Versión**: 1.0

