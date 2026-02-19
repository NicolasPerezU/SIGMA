# 📅 Documentación: Reporte de Calendario de Sustentaciones y Evaluaciones

## 📝 Descripción General

Este endpoint genera un **reporte completo en formato PDF** del calendario de sustentaciones (defensas de modalidades de grado), incluyendo sustentaciones próximas, pendientes de programación, en progreso y completadas recientemente. Proporciona análisis detallado de fechas, jurados, alertas, estadísticas temporales y análisis de preparación. Es una herramienta esencial para la gestión y planificación de evaluaciones finales de modalidades de grado.

**Generador**: `DefenseCalendarPdfGenerator`

**Tipo de Reporte**: Calendario y gestión de sustentaciones

---

## 🔗 Endpoint

### **GET** `/reports/defense-calendar/pdf`

**Descripción**: Genera y descarga un reporte en PDF con el calendario completo de sustentaciones del programa, incluyendo análisis de fechas próximas, alertas, estadísticas de jurados y análisis temporal.

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

### Query Parameters (Todos Opcionales)

| Parámetro | Tipo | Requerido | Descripción | Valor por Defecto | Ejemplo |
|-----------|------|-----------|-------------|-------------------|---------|
| `startDate` | `String` | No | Fecha inicio (ISO 8601) | Hace 30 días | `"2026-01-01T00:00:00"` |
| `endDate` | `String` | No | Fecha fin (ISO 8601) | Dentro de 60 días | `"2026-12-31T23:59:59"` |
| `includeCompleted` | `Boolean` | No | Incluir sustentaciones completadas | `false` | `true`, `false` |

### Formato de Fechas

**ISO 8601**: `YYYY-MM-DDTHH:mm:ss`

Ejemplos válidos:
- `2026-02-18T00:00:00`
- `2026-03-15T14:30:00`
- `2026-12-31T23:59:59`

### Ejemplos de URLs

```http
# Calendario completo (últimos 30 días + próximos 60 días)
GET /reports/defense-calendar/pdf

# Calendario del primer semestre 2026
GET /reports/defense-calendar/pdf?startDate=2026-01-01T00:00:00&endDate=2026-06-30T23:59:59

# Calendario del año 2025 con completadas
GET /reports/defense-calendar/pdf?startDate=2025-01-01T00:00:00&endDate=2025-12-31T23:59:59&includeCompleted=true

# Solo próximos 30 días
GET /reports/defense-calendar/pdf?startDate=2026-02-18T00:00:00&endDate=2026-03-20T23:59:59

# Incluir historial reciente
GET /reports/defense-calendar/pdf?includeCompleted=true
```

---

## 📤 Response (Respuesta)

### Respuesta Exitosa (200 OK)

**Content-Type**: `application/pdf`

**Headers de Respuesta**:
```http
Content-Type: application/pdf
Content-Disposition: attachment; filename=Calendario_Sustentaciones_2026-02-18_143025.pdf
X-Report-Generated-At: 2026-02-18T14:30:25
X-Report-Type: DEFENSE_CALENDAR
Content-Length: 298745
```

**Body**: Archivo PDF binario profesional con calendario de sustentaciones

### Respuestas de Error

#### Parámetros Inválidos (400)
```json
{
  "success": false,
  "error": "Parámetros inválidos: La fecha de inicio debe ser anterior a la fecha fin",
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

### Caso de Uso 1: Calendario Semanal

**Escenario**: Jefatura necesita ver sustentaciones de la próxima semana para planificación.

**Request**:
```http
GET /reports/defense-calendar/pdf?startDate=2026-02-18T00:00:00&endDate=2026-02-25T23:59:59
```

**Resultado**: PDF con sustentaciones de 7 días, alertas urgentes y checklist de preparación.

---

### Caso de Uso 2: Calendario Mensual

**Escenario**: Secretaría prepara calendario mensual para publicación.

**Request**:
```http
GET /reports/defense-calendar/pdf?startDate=2026-03-01T00:00:00&endDate=2026-03-31T23:59:59
```

**Resultado**: PDF con todas las sustentaciones de marzo, análisis de carga y disponibilidad.

---

### Caso de Uso 3: Calendario Semestral

**Escenario**: Consejo planifica recursos para todo el semestre.

**Request**:
```http
GET /reports/defense-calendar/pdf?startDate=2026-01-01T00:00:00&endDate=2026-06-30T23:59:59
```

**Resultado**: PDF con vista semestral, análisis mensual y proyecciones.

---

### Caso de Uso 4: Reporte con Historial

**Escenario**: Comité evalúa desempeño de sustentaciones recientes.

**Request**:
```http
GET /reports/defense-calendar/pdf?includeCompleted=true
```

**Resultado**: PDF con próximas + completadas recientes, análisis de resultados.

---

### Caso de Uso 5: Alertas Urgentes

**Escenario**: Jefatura revisa sustentaciones inmediatas (próximos 3 días).

**Request**:
```http
GET /reports/defense-calendar/pdf?startDate=2026-02-18T00:00:00&endDate=2026-02-21T23:59:59
```

**Resultado**: PDF con enfoque en urgencias, tareas pendientes y confirmaciones.

---

## 📄 Estructura Completa del PDF

### **PORTADA INSTITUCIONAL CON RANGO DE FECHAS**

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


    CALENDARIO DE SUSTENTACIONES
      Y EVALUACIONES FINALES              ← Título principal
                                             (rojo)

┌─────────────────────────────────────────────┐
│ PERIODO: 18 Feb 2026 - 18 Abr 2026         │ ← Caja dorada
│ 60 días de cobertura                        │   con rango
└─────────────────────────────────────────────┘


╔═══════════════════════════════════════════════════╗
║ Programa: Ingeniería de Sistemas                 ║
║ Código: IS-2020                                   ║
║ Fecha de Generación: 18/02/2026 - 14:30         ║ ← Tabla info
║ Generado por: Dr. Juan Pérez                     ║
║ Sustentaciones Programadas: 28                    ║
║ Próximas esta Semana: 5                           ║
║ Alertas Urgentes: 2                               ║
╚═══════════════════════════════════════════════════╝


┌──────────────────────────────────────────────────────┐
│  Sistema SIGMA - Calendario de Evaluaciones         │ ← Footer
│  Sistema Integral de Gestión de Modalidades         │   dorado
└──────────────────────────────────────────────────────┘
```

---

### **SECCIÓN 1: RESUMEN EJECUTIVO**

#### 1.1 Tarjetas de Métricas Clave (3×3)

**Fila 1 - Próximas Sustentaciones**:
```
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│      28      │  │      5       │  │      12      │
│  Programadas │  │ Esta Semana  │  │  Este Mes    │
│    Totales   │  │              │  │              │
└──────────────┘  └──────────────┘  └──────────────┘
```

**Fila 2 - Estado**:
```
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│      8       │  │      15      │  │      2       │
│ Pendientes   │  │ Completadas  │  │   Alertas    │
│  Programar   │  │  Este Mes    │  │  Urgentes    │
└──────────────┘  └──────────────┘  └──────────────┘
```

**Fila 3 - Jurados y Éxito**:
```
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│      18      │  │    95.2%     │  │ 19 Feb 2026  │
│   Jurados    │  │ Tasa Éxito   │  │   Próxima    │
│  Involucrados│  │              │  │ Sustentación │
└──────────────┘  └──────────────┘  └──────────────┘
```

#### 1.2 Indicadores de Calendario

```
┌─────────────────────────────────────────────────────┐
│ INDICADORES DEL CALENDARIO                          │
├─────────────────────────────────────────────────────┤
│                                                     │
│ Periodo Analizado:          18/02/2026 - 18/04/2026│
│ Total de Días:              60 días                 │
│                                                     │
│ Sustentaciones Programadas: 28                      │
│ ├─ Esta Semana (18-25 Feb): 5 🚨 Urgente           │
│ ├─ Próximas 2 Semanas:      8                       │
│ └─ Este Mes:                12                      │
│                                                     │
│ Pendientes de Programar:    8 ⚠️                   │
│ Sustentaciones Hoy:         0                       │
│ Vencidas sin Programar:     2 🚨 Crítico           │
│                                                     │
│ Completadas Recientemente:  15 (Este mes)           │
│ Tasa de Éxito Reciente:     95.2%                   │
│                                                     │
│ Jurados Totales:            18 profesores           │
│ Promedio Defensas/Jurado:   1.56                    │
│                                                     │
└─────────────────────────────────────────────────────┘
```

---

### **SECCIÓN 2: ALERTAS Y RECORDATORIOS**

```
═══════════════════════════════════════════════════════
🚨 ALERTAS URGENTES (Requieren Atención Inmediata)
═══════════════════════════════════════════════════════

1. SUSTENTACIÓN MAÑANA SIN JURADOS CONFIRMADOS
   ├─ Estudiante: Ana García López
   ├─ Modalidad: Proyecto de Grado
   ├─ Fecha: 19/02/2026 14:00
   ├─ Problema: Solo 1 de 3 jurados confirmado
   └─ Acción: Contactar jurados restantes HOY

2. VENCIMIENTO DE PLAZO CRÍTICO
   ├─ Estudiante: Carlos Ruiz Pérez
   ├─ Modalidad: Pasantía
   ├─ Plazo Original: 15/02/2026 (3 días vencido)
   ├─ Problema: Sin fecha de sustentación programada
   └─ Acción: Programar urgentemente esta semana

⚠️  ALERTAS DE ADVERTENCIA (Próxima Semana)
═══════════════════════════════════════════════════════

3. PREPARACIÓN INCOMPLETA
   ├─ Estudiante: Diana Morales Torres
   ├─ Modalidad: Proyecto de Grado
   ├─ Fecha: 25/02/2026 10:00
   ├─ Problema: Falta envío de documento final a jurados
   └─ Acción: Recordar envío 48 horas antes

4. CONFIRMACIÓN PENDIENTE DE SALA
   ├─ Estudiante: Eduardo López García
   ├─ Fecha: 26/02/2026 15:00
   ├─ Problema: Sin confirmación de Auditorio Principal
   └─ Acción: Confirmar disponibilidad de sala

ℹ️  RECORDATORIOS INFORMATIVOS
═══════════════════════════════════════════════════════

5. JURADO SOBRECARGADO
   ├─ Jurado: Dr. Carlos López
   ├─ Problema: 5 sustentaciones asignadas este mes
   ├─ Recomendación: Balancear carga con otros jurados
   └─ Disponibilidad: Limitada próximos 15 días

6. PICO DE SUSTENTACIONES
   ├─ Periodo: 10-15 de Marzo (6 sustentaciones)
   ├─ Problema: Concentración en 5 días
   ├─ Recomendación: Redistribuir algunas fechas
   └─ Riesgo: Conflictos de horario y salas
```

---

### **SECCIÓN 3: CALENDARIO VISUAL - PRÓXIMAS SUSTENTACIONES**

#### 3.1 Vista Semanal (Próximos 7 Días)

```
═══════════════════════════════════════════════════════
SEMANA: 18 - 25 de Febrero 2026
═══════════════════════════════════════════════════════

LUNES 18 FEBRERO
└─ Sin sustentaciones programadas

MARTES 19 FEBRERO 🚨 URGENTE
┌────────────────────────────────────────────────────┐
│ 14:00 - 16:00 │ Auditorio Principal                │
│ Ana García López                                    │
│ Proyecto de Grado: "Sistema de Gestión con IoT"    │
│ Director: Dr. Carlos López                          │
│ Jurados: Dr. López, Dra. Rodríguez, Dr. Martínez   │
│ Estado: ⚠️ Solo 1 jurado confirmado                │
└────────────────────────────────────────────────────┘

MIÉRCOLES 20 FEBRERO
┌────────────────────────────────────────────────────┐
│ 10:00 - 12:00 │ Sala de Conferencias               │
│ Carlos Ruiz Pérez, María Gómez                     │
│ Emprendimiento: "Startup EdTech"                   │
│ Director: Dra. Carmen Ortiz                         │
│ Jurados: Dra. Ortiz, Dr. Ramírez, Ing. Torres     │
│ Estado: ✓ Todos confirmados                        │
└────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────┐
│ 15:00 - 17:00 │ Aula Magna                         │
│ Diana Morales Torres                               │
│ Práctica Profesional                               │
│ Director: Dr. Pedro Martínez                        │
│ Jurados: Dr. Martínez, Dra. Fernández, Dr. Gómez  │
│ Estado: ✓ Todos confirmados                        │
└────────────────────────────────────────────────────┘

JUEVES 21 FEBRERO
└─ Sin sustentaciones programadas

VIERNES 22 FEBRERO
┌────────────────────────────────────────────────────┐
│ 09:00 - 11:00 │ Auditorio Principal                │
│ Eduardo López García                               │
│ Proyecto de Grado: "Blockchain Agrícola"          │
│ Director: Dr. José Ramírez                          │
│ Jurados: Dr. Ramírez, Dra. Morales, Dr. Castro    │
│ Estado: ✓ Todos confirmados                        │
└────────────────────────────────────────────────────┘

SÁBADO 23 FEBRERO
└─ Sin sustentaciones programadas

DOMINGO 24 FEBRERO
└─ Sin sustentaciones programadas

LUNES 25 FEBRERO
┌────────────────────────────────────────────────────┐
│ 10:00 - 12:00 │ Sala de Conferencias               │
│ Fernanda Ruiz Luna                                 │
│ Pasantía: "Desarrollo en TechCorp"                 │
│ Director: Ing. Pedro Torres                         │
│ Jurados: Ing. Torres, Dra. López, Dr. Díaz        │
│ Estado: ⚠️ Falta envío de documento final         │
└────────────────────────────────────────────────────┘

RESUMEN SEMANAL: 5 sustentaciones | 2 alertas
```

---

### **SECCIÓN 4: LISTADO DETALLADO DE SUSTENTACIONES PRÓXIMAS**

*Tabla completa ordenada cronológicamente*

#### Tabla de Sustentaciones

| # | Fecha | Hora | Estudiante(s) | Modalidad | Director | Jurados | Sala | Estado | Días |
|---|-------|------|---------------|-----------|----------|---------|------|--------|------|
| 1 | 19/02 | 14:00 | Ana García (L) | Proyecto | Dr. López | 1/3 ⚠️ | Auditorio | Incompleto | 1 🚨 |
| 2 | 20/02 | 10:00 | Carlos Ruiz (L), María Gómez | Emprendimiento | Dra. Ortiz | 3/3 ✓ | Sala Conf. | Listo | 2 |
| 3 | 20/02 | 15:00 | Diana Morales | Práctica P. | Dr. Martínez | 3/3 ✓ | Aula Magna | Listo | 2 |
| 4 | 22/02 | 09:00 | Eduardo López | Proyecto | Dr. Ramírez | 3/3 ✓ | Auditorio | Listo | 4 |
| 5 | 25/02 | 10:00 | Fernanda Ruiz | Pasantía | Ing. Torres | 3/3 ✓ | Sala Conf. | Doc. Pend. | 7 |
| ... | ... | ... | ... | ... | ... | ... | ... | ... | ... |

**Leyenda**:
- (L) = Líder del grupo
- ✓ = Todos confirmados
- ⚠️ = Confirmación parcial
- 🚨 = Urgente (≤3 días)

#### Detalle Expandido por Sustentación

```
═══════════════════════════════════════════════════════
#1 - ANA GARCÍA LÓPEZ (19 Febrero 2026)
═══════════════════════════════════════════════════════

🚨 URGENCIA: MAÑANA - Requiere atención inmediata

Fecha y Hora: Martes 19 de Febrero de 2026 - 14:00
Duración Estimada: 120 minutos (2 horas)
Ubicación: Auditorio Principal
Días Hasta Sustentación: 1 día 🚨

Estudiante:
└─ Ana García López (Líder)
   ├─ Código: 20191234567
   ├─ Email: ana.garcia@usco.edu.co
   ├─ Teléfono: +57 300 123 4567
   └─ Promedio: 4.85

Modalidad:
├─ Tipo: Proyecto de Grado
├─ Estado: Aprobado (Listo para sustentación)
└─ Título: "Sistema de Gestión de Inventarios con IoT"

Director:
└─ Dr. Carlos López García
   ├─ Email: carlos.lopez@usco.edu.co
   └─ Teléfono: +57 311 234 5678

Jurados Asignados:
1. Dr. Carlos López García (Director/Presidente) ✓ Confirmado
2. Dra. María Rodríguez Pérez (Jurado Principal) ⚠️ Sin confirmar
3. Dr. Pedro Martínez Torres (Jurado Auxiliar) ⚠️ Sin confirmar

Estado de Preparación:
├─ Readiness: 75%
├─ Documento Final: ✓ Entregado
├─ Jurados Asignados: ✓ Completo
├─ Jurados Confirmados: ⚠️ Parcial (1/3)
├─ Sala Reservada: ✓ Confirmada
├─ Recordatorio Enviado: ✓ Enviado (hace 3 días)
└─ Presentación Cargada: ✓ Sistema

Tareas Pendientes:
⚠️ 1. Confirmar asistencia Dra. Rodríguez (URGENTE)
⚠️ 2. Confirmar asistencia Dr. Martínez (URGENTE)
ℹ️  3. Verificar equipos audiovisuales

Próximas Acciones:
• HOY: Contactar jurados sin confirmar
• HOY 16:00: Verificar equipos en Auditorio
• MAÑANA 13:00: Reunión pre-sustentación

Observaciones:
Estudiante con excelente promedio (4.85). Proyecto bien
preparado. Problema: Solo director ha confirmado asistencia.
Se requiere confirmación urgente de jurados restantes.
```

---

### **SECCIÓN 5: SUSTENTACIONES PENDIENTES DE PROGRAMAR**

```
═══════════════════════════════════════════════════════
MODALIDADES LISTAS PARA PROGRAMAR SUSTENTACIÓN
═══════════════════════════════════════════════════════

Total: 8 modalidades esperando fecha de sustentación

1. Gabriel Pérez Castro 🚨 VENCIDO
   ├─ Modalidad: Proyecto de Grado
   ├─ Director: Dr. José Ramírez
   ├─ Estado: Aprobado Consejo
   ├─ Fecha Aprobación: 10/02/2026 (8 días)
   ├─ Plazo Máximo: 15/02/2026 (VENCIDO hace 3 días)
   └─ Acción: Programar URGENTEMENTE esta semana

2. Helena García Díaz
   ├─ Modalidad: Proyecto de Grado
   ├─ Director: Dra. Carmen Ortiz
   ├─ Estado: Aprobado Secretaría
   ├─ Fecha Aprobación: 12/02/2026 (6 días)
   ├─ Plazo Máximo: 27/02/2026 (9 días restantes)
   └─ Acción: Programar en próximos 5 días

3. Iván Torres Morales
   ├─ Modalidad: Emprendimiento
   ├─ Director: Dra. María Rodríguez
   ├─ Estado: Aprobado
   ├─ Fecha Aprobación: 15/02/2026 (3 días)
   ├─ Plazo Máximo: 01/03/2026 (11 días restantes)
   └─ Acción: Coordinar con estudiante esta semana

4-8. [Continúa listado...]

RECOMENDACIONES:
• Programar casos vencidos en próximos 3 días
• Priorizar según antigüedad de aprobación
• Coordinar disponibilidad de jurados y salas
```

---

### **SECCIÓN 6: SUSTENTACIONES EN PROGRESO (PRE-DEFENSA)**

```
═══════════════════════════════════════════════════════
MODALIDADES EN PROCESO HACIA SUSTENTACIÓN
═══════════════════════════════════════════════════════

Total: 15 modalidades en etapas previas a sustentación

Estado: EN REVISIÓN FINAL (5)
──────────────────────────────────────────────────────

1. Julia Mora Castro
   ├─ Modalidad: Proyecto de Grado
   ├─ Director: Dr. Carlos López
   ├─ Estado Actual: En Revisión (12 días)
   ├─ Progreso: 90%
   ├─ Pasos Completados:
   │  ✓ Documento final entregado
   │  ✓ Revisión de director
   │  ⏳ Correcciones menores pendientes
   ├─ Próximo Paso: Aprobación de director
   ├─ Fecha Esperada Sustentación: 05/03/2026
   └─ Observaciones: Cerca de finalizar revisiones

Estado: APROBADO - PENDIENTE ASIGNACIÓN JURADOS (4)
──────────────────────────────────────────────────────

2. Kevin Ruiz López
   ├─ Modalidad: Pasantía
   ├─ Director: Ing. Pedro Torres
   ├─ Estado Actual: Aprobado (5 días)
   ├─ Progreso: 80%
   ├─ Pasos Completados:
   │  ✓ Aprobado por director
   │  ✓ Aprobado por secretaría
   │  ⏳ Pendiente asignación de jurados
   ├─ Próximo Paso: Asignar 2 jurados evaluadores
   ├─ Fecha Esperada Sustentación: 28/02/2026
   └─ Acción: Secretaría debe asignar jurados

Estado: DOCUMENTO EN PREPARACIÓN (6)
──────────────────────────────────────────────────────

3-15. [Continúa listado por estados...]
```

---

### **SECCIÓN 7: SUSTENTACIONES COMPLETADAS RECIENTEMENTE**

*Solo si `includeCompleted=true`*

```
═══════════════════════════════════════════════════════
SUSTENTACIONES REALIZADAS (Últimos 30 días)
═══════════════════════════════════════════════════════

Total: 15 sustentaciones completadas este mes

✓ APROBADAS (14)
──────────────────────────────────────────────────────

1. Laura Herrera Cruz - 15/02/2026 (hace 3 días)
   ├─ Modalidad: Proyecto de Grado
   ├─ Director: Dr. Carlos López
   ├─ Resultado: APROBADO ✓
   ├─ Calificación: 5.0 🏆 LAUREADO
   ├─ Jurados:
   │  1. Dr. Carlos López (5.0)
   │  2. Dra. María Rodríguez (5.0)
   │  3. Dr. Pedro Martínez (5.0)
   ├─ Distinción: Trabajo de Grado Laureado
   └─ Ubicación: Auditorio Principal

2. Miguel Torres Vega - 14/02/2026 (hace 4 días)
   ├─ Modalidad: Pasantía
   ├─ Director: Ing. Pedro Torres
   ├─ Resultado: APROBADO ✓
   ├─ Calificación: 4.8 ⭐ MERITORIO
   ├─ Jurados:
   │  1. Ing. Pedro Torres (4.8)
   │  2. Dra. Laura Fernández (4.8)
   │  3. Dr. Miguel Gómez (4.8)
   ├─ Distinción: Trabajo Meritorio
   └─ Ubicación: Sala de Conferencias

3-14. [Continúa listado...]

✗ REQUIEREN CORRECCIONES (1)
──────────────────────────────────────────────────────

15. Natalia Pérez García - 10/02/2026 (hace 8 días)
    ├─ Modalidad: Proyecto de Grado
    ├─ Director: Dr. José Ramírez
    ├─ Resultado: CORRECCIONES REQUERIDAS ⚠️
    ├─ Calificación Provisional: 3.8
    ├─ Jurados:
    │  1. Dr. José Ramírez (4.0)
    │  2. Dra. Carmen Ortiz (3.5)
    │  3. Dr. David Torres (4.0)
    ├─ Correcciones: Ajustes metodológicos, referencias
    ├─ Plazo: 15 días (hasta 25/02/2026)
    └─ Nueva Sustentación: Por programar tras correcciones

RESUMEN:
├─ Tasa de Aprobación: 93.3% (14/15)
├─ Calificación Promedio: 4.55/5.0
├─ Con Distinción: 8 (53.3%)
│  ├─ Laureados: 2 (13.3%)
│  └─ Meritorios: 6 (40.0%)
└─ Requieren Correcciones: 1 (6.7%)
```

---

### **SECCIÓN 8: ESTADÍSTICAS DE SUSTENTACIONES**

#### 8.1 Estadísticas Generales

```
┌─────────────────────────────────────────────────────┐
│ ESTADÍSTICAS GENERALES DE SUSTENTACIONES            │
├─────────────────────────────────────────────────────┤
│                                                     │
│ Total Programadas (Periodo): 28                     │
│ Total Completadas (Mes Actual): 15                  │
│ Total Pendientes Programar: 8                       │
│ Total Canceladas: 1                                 │
│                                                     │
│ Resultados (Este Mes):                              │
│ ├─ Aprobadas: 14 (93.3%)                           │
│ ├─ Requieren Correcciones: 1 (6.7%)                │
│ └─ Reprobadas: 0 (0%)                              │
│                                                     │
│ Tiempos Promedio:                                   │
│ ├─ Días Hasta Sustentación: 185.5 días             │
│ └─ Duración Promedio Sustentación: 95 minutos      │
│                                                     │
│ Calificaciones:                                     │
│ ├─ Promedio: 4.55/5.0                              │
│ ├─ Máxima: 5.0                                     │
│ └─ Mínima: 3.8                                     │
│                                                     │
│ Distinciones:                                       │
│ ├─ Laureados: 2 (13.3%)                            │
│ ├─ Meritorios: 6 (40.0%)                           │
│ └─ Tasa Distinción: 53.3%                          │
│                                                     │
└─────────────────────────────────────────────────────┘
```

#### 8.2 Distribución por Tipo de Modalidad

```
SUSTENTACIONES POR TIPO DE MODALIDAD

Proyecto de Grado    ████████████████████  20 (71.4%)
Pasantía             ████░░░░░░░░░░░░░░░░   4 (14.3%)
Práctica Profesional ██░░░░░░░░░░░░░░░░░░   2 (7.1%)
Emprendimiento       ██░░░░░░░░░░░░░░░░░░   2 (7.1%)

Tasa de Éxito por Tipo:
├─ Proyecto de Grado: 95.0%
├─ Pasantía: 100%
├─ Práctica Profesional: 100%
└─ Emprendimiento: 85.7%
```

#### 8.3 Distribución Temporal

```
SUSTENTACIONES POR MES (Últimos 6 meses)

Feb 2026 ████████████████████  15 completadas │ 12 programadas
Ene 2026 ████████████░░░░░░░░  12 completadas │ 10 programadas
Dic 2025 ██████░░░░░░░░░░░░░░   8 completadas │  8 programadas
Nov 2025 ████████░░░░░░░░░░░░  10 completadas │  9 programadas
Oct 2025 ████░░░░░░░░░░░░░░░░   5 completadas │  6 programadas
Sep 2025 ██████░░░░░░░░░░░░░░   7 completadas │  7 programadas

TENDENCIA: ↗ CRECIENTE (+112% vs Sep 2025)
```

---

### **SECCIÓN 9: ANÁLISIS DE JURADOS**

#### 9.1 Resumen de Participación

```
PARTICIPACIÓN DE JURADOS

Total de Jurados: 18 profesores
Jurados Activos Este Mes: 12
Promedio Defensas/Jurado: 1.56
```

#### 9.2 Top 10 Jurados Más Activos

```
RANKING DE JURADOS - SUSTENTACIONES PRÓXIMAS

 Pos │ Jurado                  │ Próximas │ Este Mes │ Tipo Principal │ Disponib.│
─────┼─────────────────────────┼──────────┼──────────┼────────────────┼──────────┤
  1  │ Dr. Carlos López        │    5     │    8     │ Director/Pres. │ Media    │
  2  │ Dra. María Rodríguez    │    4     │    6     │ Jurado Princ.  │ Alta     │
  3  │ Dr. Pedro Martínez      │    4     │    5     │ Jurado Aux.    │ Alta     │
  4  │ Dra. Carmen Ortiz       │    3     │    5     │ Director/Pres. │ Media    │
  5  │ Dr. José Ramírez        │    3     │    4     │ Director/Pres. │ Baja     │
  6  │ Ing. Pedro Torres       │    2     │    4     │ Director/Pres. │ Alta     │
  7  │ Dra. Laura Fernández    │    2     │    3     │ Jurado Princ.  │ Alta     │
  8  │ Dr. Miguel Gómez        │    2     │    3     │ Jurado Aux.    │ Media    │
  9  │ Dr. David Torres        │    1     │    2     │ Jurado Princ.  │ Alta     │
 10  │ Dra. Ana García M.      │    1     │    2     │ Jurado Aux.    │ Alta     │
```

#### 9.3 Jurado Más Activo del Periodo

```
┌─────────────────────────────────────────────────────┐
│ 🏆 JURADO MÁS ACTIVO: DR. CARLOS LÓPEZ GARCÍA       │
├─────────────────────────────────────────────────────┤
│                                                     │
│ Sustentaciones Este Mes: 8                          │
│ Sustentaciones Próximas: 5                          │
│                                                     │
│ Rol Principal: Director/Presidente                  │
│ Calificación Promedio Otorgada: 4.65               │
│                                                     │
│ Disponibilidad: MEDIA (Carga alta)                  │
│ Recomendación: Balancear carga en próximos meses   │
│                                                     │
└─────────────────────────────────────────────────────┘
```

#### 9.4 Distribución por Tipo de Jurado

```
JURADOS POR TIPO DE PARTICIPACIÓN

Director/Presidente  ████████████████████  20 asignaciones (47.6%)
Jurado Principal     ████████████░░░░░░░░  14 asignaciones (33.3%)
Jurado Auxiliar      ████████░░░░░░░░░░░░   8 asignaciones (19.0%)
```

#### 9.5 Alertas de Jurados

```
⚠️  ALERTAS DE JURADOS

1. JURADO SOBRECARGADO
   ├─ Dr. Carlos López: 5 sustentaciones en 15 días
   └─ Recomendación: Redistribuir nuevas asignaciones

2. JURADO CON BAJA DISPONIBILIDAD
   ├─ Dr. José Ramírez: 3 sustentaciones + compromiso externo
   └─ Recomendación: No asignar nuevas hasta 15 de Marzo

3. NECESIDAD DE MÁS JURADOS
   ├─ 8 modalidades pendientes de programar
   ├─ Requieren 16 jurados evaluadores adicionales
   └─ Recomendación: Involucrar jurados con alta disponibilidad
```

---

### **SECCIÓN 10: ANÁLISIS MENSUAL DETALLADO**

```
═══════════════════════════════════════════════════════
FEBRERO 2026 - ANÁLISIS COMPLETO
═══════════════════════════════════════════════════════

Sustentaciones Programadas: 12
Sustentaciones Completadas: 15
Sustentaciones Pendientes: 3

Resultados:
├─ Aprobadas: 14 (93.3%)
├─ Correcciones: 1 (6.7%)
└─ Reprobadas: 0 (0%)

Calificaciones:
├─ Promedio: 4.55/5.0
├─ Máxima: 5.0
└─ Mínima: 3.8

Total de Estudiantes: 18
Directores Involucrados: 8
Jurados Involucrados: 12

Días Pico (Más Sustentaciones):
1. 20 de Febrero (3 sustentaciones)
2. 15 de Febrero (2 sustentaciones)
3. 22 de Febrero (2 sustentaciones)

COMPARATIVA CON ENERO:
├─ Incremento en completadas: +25% (12 → 15)
├─ Calificación promedio: +0.08 (4.47 → 4.55)
└─ Tasa de éxito: Estable (93.3%)
```

---

### **SECCIÓN 11: CHECKLIST DE PREPARACIÓN**

```
═══════════════════════════════════════════════════════
CHECKLIST GENERAL DE PREPARACIÓN
═══════════════════════════════════════════════════════

PARA SUSTENTACIONES DE ESTA SEMANA (5)

□ DOCUMENTACIÓN
  ├─ ✓ Documentos finales entregados (5/5)
  ├─ ⚠️ Documentos distribuidos a jurados (3/5)
  └─ ✓ Actas preparadas (5/5)

□ JURADOS
  ├─ ✓ Jurados asignados (5/5 sustentaciones)
  ├─ ⚠️ Jurados confirmados (4/5 sustentaciones)
  └─ ✓ Correos de invitación enviados (5/5)

□ LOGÍSTICA
  ├─ ✓ Salas reservadas (5/5)
  ├─ ⚠️ Salas confirmadas (4/5)
  ├─ ✓ Equipos audiovisuales verificados (4/5)
  └─ ⚠️ Soporte técnico asignado (2/5)

□ ESTUDIANTES
  ├─ ✓ Recordatorios enviados (5/5)
  ├─ ✓ Presentaciones cargadas (5/5)
  └─ ⚠️ Ensayos previos realizados (3/5)

□ ADMINISTRACIÓN
  ├─ ✓ Calendario publicado (Sí)
  ├─ ✓ Invitaciones enviadas (Sí)
  └─ ⚠️ Refrigerios coordinados (No)

TAREAS CRÍTICAS PENDIENTES:
⚠️ 1. Confirmar jurados faltantes (2 jurados)
⚠️ 2. Confirmar Auditorio para 26/Feb
⚠️ 3. Asignar soporte técnico (3 sustentaciones)
⚠️ 4. Coordinar refrigerios para toda la semana
```

---

### **SECCIÓN 12: RECOMENDACIONES OPERATIVAS**

```
═══════════════════════════════════════════════════════
RECOMENDACIONES BASADAS EN ANÁLISIS
═══════════════════════════════════════════════════════

CORTO PLAZO (Esta Semana):

1. URGENTE: Confirmar jurados faltantes
   • Ana García (19/Feb): 2 jurados sin confirmar
   • Contactar hoy antes de las 17:00

2. CRÍTICO: Programar modalidades vencidas
   • 2 modalidades con plazo excedido
   • Programar antes del viernes 22/Feb

3. IMPORTANTE: Balancear carga de Dr. López
   • 5 sustentaciones en 15 días
   • Redistribuir 2 asignaciones a otros jurados

MEDIANO PLAZO (Próximas 2 Semanas):

4. Redistribuir sustentaciones del pico 10-15 Marzo
   • 6 sustentaciones en 5 días
   • Reubicar 2-3 a semana anterior o posterior

5. Programar 8 modalidades pendientes
   • Coordinar con estudiantes y jurados
   • Meta: Todas programadas antes del 01/Marzo

6. Preparar calendario de Abril
   • 12 modalidades estimadas
   • Iniciar coordinación de fechas

LARGO PLAZO (Este Semestre):

7. Incrementar pool de jurados evaluadores
   • Actual: 18 jurados
   • Meta: 25 jurados para balancear carga

8. Implementar sistema de confirmación automática
   • Reducir seguimiento manual
   • Recordatorios automatizados 7 y 3 días antes

9. Digitalizar proceso de evaluación
   • Actas digitales
   • Calificaciones en línea
```

---

### **SECCIÓN 13: CONCLUSIONES**

```
═══════════════════════════════════════════════════════
CONCLUSIONES DEL CALENDARIO
═══════════════════════════════════════════════════════

1. VOLUMEN SALUDABLE
   28 sustentaciones programadas para los próximos 60 días
   representa una carga manejable con distribución adecuada.

2. ALTA TASA DE ÉXITO
   Tasa de aprobación de 93.3% refleja buena preparación de
   estudiantes y efectividad de directores.

3. ALERTAS GESTIONABLES
   2 alertas urgentes y 4 de advertencia son manejables con
   acciones inmediatas. Ninguna crítica irresoluble.

4. CARGA DE JURADOS CONCENTRADA
   Top 5 jurados participan en 65% de sustentaciones.
   Recomendable balancear carga con jurados menos activos.

5. PENDIENTES BAJO CONTROL
   8 modalidades pendientes de programar están dentro de
   plazos, excepto 2 casos vencidos que requieren atención.

6. TENDENCIA POSITIVA
   Incremento de 112% en sustentaciones vs semestre anterior
   indica crecimiento saludable del programa.

7. EXCELENTE CALIDAD
   Calificación promedio de 4.55 y 53.3% con distinción
   evidencian alta calidad de trabajos de grado.

═══════════════════════════════════════════════════════
VEREDICTO GENERAL: EXCELENTE GESTIÓN ⭐⭐⭐⭐⭐
═══════════════════════════════════════════════════════

El calendario de sustentaciones muestra organización eficiente,
alta tasa de éxito y calidad académica sobresaliente. Alertas
identificadas son manejables con acciones correctivas inmediatas.
Se recomienda continuar con modelo actual, implementando mejoras
sugeridas para balancear carga de jurados.
```

---

### **PIE DE PÁGINA (Todas las Páginas)**

```
──────────────────────────────────────────────────────
Página 10 | Calendario de Sustentaciones | Ingeniería de Sistemas | 18/02/2026
──────────────────────────────────────────────────────
```

---

## 💻 Ejemplos de Código

### Ejemplo 1: JavaScript/TypeScript

```typescript
async function downloadDefenseCalendarReport(
  startDate?: string,
  endDate?: string,
  includeCompleted: boolean = false
) {
  const token = localStorage.getItem('auth_token');
  
  try {
    console.log('📅 Generando calendario de sustentaciones...');
    
    // Construir URL con parámetros
    const params = new URLSearchParams();
    if (startDate) params.append('startDate', startDate);
    if (endDate) params.append('endDate', endDate);
    if (includeCompleted) params.append('includeCompleted', 'true');
    
    const url = `http://localhost:8080/reports/defense-calendar/pdf?${params.toString()}`;
    
    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    
    if (!response.ok) {
      throw new Error('Error al generar reporte');
    }
    
    const blob = await response.blob();
    const downloadUrl = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = downloadUrl;
    
    const contentDisposition = response.headers.get('Content-Disposition');
    const filename = contentDisposition 
      ? contentDisposition.split('filename=')[1].replace(/"/g, '')
      : `Calendario_Sustentaciones_${new Date().toISOString().split('T')[0]}.pdf`;
    
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    
    window.URL.revokeObjectURL(downloadUrl);
    document.body.removeChild(a);
    
    console.log('✅ Calendario descargado exitosamente');
    
  } catch (error) {
    console.error('❌ Error:', error);
    alert(`Error al generar calendario: ${error.message}`);
  }
}

// Uso: Calendario completo (default)
downloadDefenseCalendarReport();

// Uso: Próxima semana
const today = new Date();
const nextWeek = new Date(today);
nextWeek.setDate(today.getDate() + 7);
downloadDefenseCalendarReport(
  today.toISOString(),
  nextWeek.toISOString()
);

// Uso: Mes actual con completadas
const firstDay = new Date(today.getFullYear(), today.getMonth(), 1);
const lastDay = new Date(today.getFullYear(), today.getMonth() + 1, 0);
downloadDefenseCalendarReport(
  firstDay.toISOString(),
  lastDay.toISOString(),
  true
);

// Uso: Semestre completo
const semesterStart = new Date('2026-01-01T00:00:00');
const semesterEnd = new Date('2026-06-30T23:59:59');
downloadDefenseCalendarReport(
  semesterStart.toISOString(),
  semesterEnd.toISOString()
);
```

---

### Ejemplo 2: React Component

```jsx
import React, { useState } from 'react';
import axios from 'axios';

function DefenseCalendarReportGenerator() {
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [includeCompleted, setIncludeCompleted] = useState(false);
  const [loading, setLoading] = useState(false);
  
  const presets = {
    thisWeek: () => {
      const today = new Date();
      const nextWeek = new Date(today);
      nextWeek.setDate(today.getDate() + 7);
      setStartDate(today.toISOString().slice(0, 16));
      setEndDate(nextWeek.toISOString().slice(0, 16));
    },
    thisMonth: () => {
      const today = new Date();
      const firstDay = new Date(today.getFullYear(), today.getMonth(), 1);
      const lastDay = new Date(today.getFullYear(), today.getMonth() + 1, 0);
      setStartDate(firstDay.toISOString().slice(0, 16));
      setEndDate(lastDay.toISOString().slice(0, 16));
    },
    thisSemester: () => {
      setStartDate('2026-01-01T00:00');
      setEndDate('2026-06-30T23:59');
    }
  };
  
  const downloadReport = async () => {
    setLoading(true);
    
    try {
      const token = localStorage.getItem('auth_token');
      
      const params = new URLSearchParams();
      if (startDate) params.append('startDate', startDate + ':00');
      if (endDate) params.append('endDate', endDate + ':59');
      if (includeCompleted) params.append('includeCompleted', 'true');
      
      const url = `http://localhost:8080/reports/defense-calendar/pdf?${params.toString()}`;
      
      const response = await axios.get(url, {
        headers: {
          'Authorization': `Bearer ${token}`
        },
        responseType: 'blob'
      });
      
      const blob = new Blob([response.data], { type: 'application/pdf' });
      const downloadUrl = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = downloadUrl;
      link.download = `Calendario_Sustentaciones_${new Date().toISOString().split('T')[0]}.pdf`;
      document.body.appendChild(link);
      link.click();
      link.remove();
      
      alert('✅ Calendario de sustentaciones descargado');
      
    } catch (error) {
      console.error('❌ Error:', error);
      alert('Error al generar el calendario');
    } finally {
      setLoading(false);
    }
  };
  
  return (
    <div className="defense-calendar-report">
      <h2>📅 Calendario de Sustentaciones</h2>
      
      <div className="presets">
        <button onClick={presets.thisWeek}>Esta Semana</button>
        <button onClick={presets.thisMonth}>Este Mes</button>
        <button onClick={presets.thisSemester}>Este Semestre</button>
      </div>
      
      <div className="filters">
        <div className="filter-group">
          <label>Fecha Inicio</label>
          <input 
            type="datetime-local" 
            value={startDate}
            onChange={e => setStartDate(e.target.value)}
          />
        </div>
        
        <div className="filter-group">
          <label>Fecha Fin</label>
          <input 
            type="datetime-local" 
            value={endDate}
            onChange={e => setEndDate(e.target.value)}
          />
        </div>
        
        <div className="filter-group">
          <label>
            <input 
              type="checkbox" 
              checked={includeCompleted}
              onChange={e => setIncludeCompleted(e.target.checked)}
            />
            Incluir Completadas
          </label>
        </div>
      </div>
      
      <button onClick={downloadReport} disabled={loading}>
        {loading ? '⏳ Generando...' : '📥 Descargar Calendario PDF'}
      </button>
      
      {(startDate || endDate) && (
        <div className="date-info">
          <p>
            📅 Periodo: {startDate ? new Date(startDate).toLocaleDateString() : 'Inicio'} 
            {' → '}
            {endDate ? new Date(endDate).toLocaleDateString() : 'Fin'}
          </p>
        </div>
      )}
    </div>
  );
}

export default DefenseCalendarReportGenerator;
```

---

### Ejemplo 3: Python

```python
import requests
from datetime import datetime, timedelta
import os

class DefenseCalendarReportClient:
    def __init__(self, base_url: str, token: str):
        self.base_url = base_url
        self.headers = {"Authorization": f"Bearer {token}"}
    
    def download_calendar(
        self,
        start_date: datetime = None,
        end_date: datetime = None,
        include_completed: bool = False,
        output_dir: str = "reportes"
    ) -> str:
        """Descarga calendario de sustentaciones"""
        
        url = f"{self.base_url}/reports/defense-calendar/pdf"
        
        # Construir parámetros
        params = {}
        if start_date:
            params['startDate'] = start_date.isoformat()
        if end_date:
            params['endDate'] = end_date.isoformat()
        if include_completed:
            params['includeCompleted'] = 'true'
        
        try:
            print("📅 Generando calendario de sustentaciones...")
            
            response = requests.get(
                url,
                headers=self.headers,
                params=params,
                stream=True
            )
            
            if response.status_code == 200:
                os.makedirs(output_dir, exist_ok=True)
                
                timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
                filename = f"Calendario_Sustentaciones_{timestamp}.pdf"
                filepath = os.path.join(output_dir, filename)
                
                with open(filepath, 'wb') as f:
                    for chunk in response.iter_content(chunk_size=8192):
                        f.write(chunk)
                
                file_size_kb = os.path.getsize(filepath) / 1024
                
                print(f"✅ Calendario descargado: {filepath}")
                print(f"   Tamaño: {file_size_kb:.2f} KB")
                
                return filepath
            else:
                print(f"❌ Error {response.status_code}")
                return None
                
        except Exception as e:
            print(f"❌ Excepción: {str(e)}")
            return None
    
    # Métodos de conveniencia
    
    def download_this_week(self) -> str:
        """Calendario de esta semana"""
        today = datetime.now()
        next_week = today + timedelta(days=7)
        return self.download_calendar(today, next_week)
    
    def download_this_month(self) -> str:
        """Calendario de este mes"""
        today = datetime.now()
        first_day = datetime(today.year, today.month, 1)
        
        # Último día del mes
        if today.month == 12:
            last_day = datetime(today.year + 1, 1, 1) - timedelta(days=1)
        else:
            last_day = datetime(today.year, today.month + 1, 1) - timedelta(days=1)
        
        return self.download_calendar(first_day, last_day)
    
    def download_semester(self, year: int, semester: int) -> str:
        """Calendario de semestre específico"""
        if semester == 1:
            start = datetime(year, 1, 1)
            end = datetime(year, 6, 30, 23, 59, 59)
        else:
            start = datetime(year, 7, 1)
            end = datetime(year, 12, 31, 23, 59, 59)
        
        return self.download_calendar(start, end)
    
    def download_with_history(self) -> str:
        """Calendario con historial reciente"""
        return self.download_calendar(include_completed=True)

# Uso
client = DefenseCalendarReportClient(
    base_url="http://localhost:8080",
    token="tu_token_jwt"
)

# Calendario completo
client.download_calendar()

# Esta semana
client.download_this_week()

# Este mes
client.download_this_month()

# Semestre específico
client.download_semester(2026, 1)

# Con historial
client.download_with_history()
```

---

## 📊 Estructura de Datos

### DefenseCalendarReportDTO (Principal)

```typescript
interface DefenseCalendarReportDTO {
  generatedAt: string;
  generatedBy: string;
  academicProgramId: number;
  academicProgramName: string;
  academicProgramCode: string;
  
  appliedFilters: AppliedFiltersDTO;
  executiveSummary: ExecutiveSummaryDTO;
  upcomingDefenses: UpcomingDefenseDTO[];
  inProgressDefenses: InProgressDefenseDTO[];
  recentCompletedDefenses: CompletedDefenseDTO[];
  statistics: DefenseStatisticsDTO;
  monthlyAnalysis: MonthlyDefenseAnalysisDTO[];
  examinerAnalysis: ExaminerAnalysisDTO;
  alerts: DefenseAlertDTO[];
  metadata: ReportMetadataDTO;
}
```

---

### UpcomingDefenseDTO

```typescript
interface UpcomingDefenseDTO {
  modalityId: number;
  modalityType: string;
  modalityTypeName: string;
  defenseDate: string;
  defenseTime: string;
  defenseLocation: string;
  daysUntilDefense: number;
  urgency: 'URGENT' | 'SOON' | 'NORMAL';
  
  students: StudentBasicInfoDTO[];
  leaderName: string;
  studentCount: number;
  
  directorName: string;
  directorEmail: string;
  directorPhone: string;
  
  examiners: ExaminerInfoDTO[];
  allExaminersConfirmed: boolean;
  confirmedExaminers: number;
  totalExaminers: number;
  
  preparationStatus: 'READY' | 'PENDING_CONFIRMATION' | 'INCOMPLETE';
  pendingTasks: string[];
  readinessPercentage: number;
  
  projectTitle: string;
  estimatedDuration: number; // minutos
  modalityStatus: string;
  scheduledDate: string;
  reminderSent: boolean;
}
```

---

## 🎯 Valor Agregado del Reporte

### Para Jefatura de Programa
- 📅 **Vista completa** del calendario de sustentaciones
- 🚨 **Alertas urgentes** de problemas inminentes
- 📊 **Planificación** de recursos (salas, jurados)
- ⏱️ **Control de plazos** y vencimientos

### Para Secretaría
- 📋 **Checklist de preparación** detallado
- 📞 **Información de contacto** completa
- 🏛️ **Reservas de salas** y logística
- 📄 **Documentación** pendiente

### Para Comité de Programa
- 📊 **Estadísticas** de desempeño
- 📈 **Tendencias** temporales
- 🎓 **Tasas de éxito** y calidad
- 👥 **Desempeño de jurados**

---

## ✅ Checklist de Uso

- [ ] Token JWT válido
- [ ] Permiso `PERM_VIEW_REPORT`
- [ ] Definir rango de fechas (opcional)
- [ ] Decidir si incluir completadas
- [ ] Formato ISO 8601 correcto

---

## 📞 Información de Contacto

### Código Fuente
- **Controller**: `com.SIGMA.USCO.report.controller.GlobalModalityReportController`
- **Generator**: `com.SIGMA.USCO.report.service.DefenseCalendarPdfGenerator`
- **Service**: `com.SIGMA.USCO.report.service.DefenseCalendarReportService`
- **DTO**: `com.SIGMA.USCO.report.dto.DefenseCalendarReportDTO`

### Documentación Relacionada
- [Reporte de Completadas](./DOCUMENTACION_REPORTE_MODALIDADES_COMPLETADAS.md)
- [Reporte de Estudiantes](./DOCUMENTACION_REPORTE_LISTADO_ESTUDIANTES.md)
- [Reporte de Directores](./DOCUMENTACION_REPORTE_DIRECTORES_MODALIDADES.md)

---

**Generado por**: SIGMA - Sistema de Gestión de Modalidades de Grado  
**Tipo de Reporte**: Calendario y Gestión de Sustentaciones  
**Servicio**: DefenseCalendarPdfGenerator  
**Última actualización**: 18 de Febrero de 2026  
**Versión**: 1.0

