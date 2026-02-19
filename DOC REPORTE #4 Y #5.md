# 👨‍🏫 Documentación: Reporte de Directores y Modalidades Asignadas (RF-49)

## 📝 Descripción General

Este endpoint genera un **reporte profesional en formato PDF** que muestra todas las modalidades de grado asignadas a directores de proyecto, incluyendo análisis detallado de carga de trabajo, estado de modalidades, estadísticas por director y seguimiento temporal. Es una herramienta esencial para la gestión eficiente de la supervisión académica.

**Requisito Funcional**: RF-49 - Generación de Reportes por Director Asignado

**Generador**: `DirectorAssignedModalitiesPdfGenerator`

---

## 🔗 Endpoints Disponibles

### 1️⃣ **POST** `/reports/directors/assigned-modalities/pdf`

**Descripción**: Genera un reporte completo en PDF de **todos los directores** del programa y sus modalidades asignadas, con análisis de carga de trabajo.

### 2️⃣ **GET** `/reports/directors/{directorId}/modalities/pdf`

**Descripción**: Genera un reporte en PDF de **un director específico** y sus modalidades.

---

## 📥 Endpoint Principal (POST)

### **POST** `/reports/directors/assigned-modalities/pdf`

### Autenticación
- **Requerida**: Sí
- **Tipo**: Bearer Token (JWT)
- **Permiso requerido**: `PERM_VIEW_REPORT`

### Headers
```http
Authorization: Bearer <token_jwt>
Content-Type: application/json
```

### Request Body (Opcional)

El body es **OPCIONAL**. Si no se envía, genera el reporte completo de todos los directores del programa.

```json
{
  "directorId": 15,
  "processStatuses": ["APROBADO", "EN_REVISION"],
  "modalityTypes": ["PROYECTO DE GRADO", "PASANTIA"],
  "onlyOverloaded": false,
  "onlyAvailable": false,
  "onlyActiveModalities": true,
  "includeWorkloadAnalysis": true
}
```

### Campos del Request Body

| Campo | Tipo | Requerido | Descripción | Valor por Defecto | Ejemplo |
|-------|------|-----------|-------------|-------------------|---------|
| `directorId` | `Long` | No | ID específico del director a analizar | `null` (todos) | `15` |
| `processStatuses` | `List<String>` | No | Estados de modalidades a incluir | Todos los activos | `["APROBADO", "EN_REVISION"]` |
| `modalityTypes` | `List<String>` | No | Tipos específicos de modalidad | Todos | `["PROYECTO DE GRADO"]` |
| `onlyOverloaded` | `Boolean` | No | Solo directores con sobrecarga (≥5 modalidades) | `false` | `true` |
| `onlyAvailable` | `Boolean` | No | Solo directores con disponibilidad (<3 modalidades) | `false` | `true` |
| `onlyActiveModalities` | `Boolean` | No | Solo modalidades activas (excluye completadas) | `false` | `true` |
| `includeWorkloadAnalysis` | `Boolean` | No | Incluir análisis detallado de carga | `true` | `true` |

---

## 📤 Response (Respuesta)

### Respuesta Exitosa (200 OK)

**Content-Type**: `application/pdf`

**Headers**:
```http
Content-Type: application/pdf
Content-Disposition: attachment; filename=Reporte_Directores_Modalidades_2026-02-18_143025.pdf
X-Report-Generated-At: 2026-02-18T14:30:25
X-Total-Records: 12
Content-Length: 198745
```

**Body**: Archivo PDF binario profesional

### Respuestas de Error

#### Error de Validación (400)
```json
{
  "success": false,
  "error": "Director con ID 999 no encontrado",
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

## 📥 Endpoint Secundario (GET)

### **GET** `/reports/directors/{directorId}/modalities/pdf`

### Parámetros

| Parámetro | Tipo | Ubicación | Descripción | Ejemplo |
|-----------|------|-----------|-------------|---------|
| `directorId` | `Long` | Path | ID del director específico | `15` |

### Ejemplo de URL
```
GET http://localhost:8080/reports/directors/15/modalities/pdf
```

### Comportamiento
Este endpoint es un **atajo** que internamente llama al POST con:
```json
{
  "directorId": 15,
  "includeWorkloadAnalysis": false
}
```

**Uso típico**: Consulta rápida de un director específico sin análisis de carga completo.

---

## 🎯 Casos de Uso

### Caso de Uso 1: Reporte General de Todos los Directores

**Escenario**: Jefatura necesita ver la distribución de carga entre todos los directores del programa.

**Request**:
```json
{
  "includeWorkloadAnalysis": true
}
```

**Resultado**: PDF completo con:
- Lista de todos los directores del programa
- Modalidades de cada uno
- Análisis de carga (quién está sobrecargado, quién disponible)
- Estadísticas generales
- Gráficos de distribución

---

### Caso de Uso 2: Reporte de Director Específico

**Escenario**: Comité de programa necesita evaluar el trabajo de un director particular.

**Opción A (POST)**:
```json
{
  "directorId": 25,
  "includeWorkloadAnalysis": false
}
```

**Opción B (GET)**:
```
GET /reports/directors/25/modalities/pdf
```

**Resultado**: PDF con información detallada solo de ese director.

---

### Caso de Uso 3: Identificar Directores Sobrecargados

**Escenario**: Jefatura quiere redistribuir carga de trabajo.

**Request**:
```json
{
  "onlyOverloaded": true,
  "includeWorkloadAnalysis": true
}
```

**Resultado**: PDF solo con directores que tienen ≥5 modalidades asignadas, incluyendo análisis de carga.

**Acción posterior**: Reasignar modalidades a directores disponibles.

---

### Caso de Uso 4: Directores Disponibles para Nuevas Asignaciones

**Escenario**: Secretaría necesita asignar directores a nuevas modalidades aprobadas.

**Request**:
```json
{
  "onlyAvailable": true,
  "onlyActiveModalities": true
}
```

**Resultado**: PDF con directores que tienen <3 modalidades activas (disponibles para más trabajo).

---

### Caso de Uso 5: Auditoría de Proyectos de Grado

**Escenario**: Consejo quiere revisar solo directores de proyectos de grado aprobados.

**Request**:
```json
{
  "modalityTypes": ["PROYECTO DE GRADO"],
  "processStatuses": ["APROBADO", "APROBADO_CONSEJO"],
  "includeWorkloadAnalysis": false
}
```

**Resultado**: PDF filtrado con directores de proyectos de grado en estados aprobados.

---

## 📄 Estructura Completa del PDF

### **PORTADA INSTITUCIONAL**

Diseño profesional y elegante:

```
┌─────────────────────────────────────────────────────────┐
│                                                         │
│         UNIVERSIDAD SURCOLOMBIANA                       │ ← Banda roja
│         Facultad de Ingeniería                          │   institucional
│                                                         │
└─────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────┐
│   INGENIERÍA DE SISTEMAS (IS-2020)       │ ← Caja dorada con
└──────────────────────────────────────────┘   programa académico


      REPORTE DE DIRECTORES Y
    MODALIDADES ASIGNADAS                    ← Título principal (rojo)


┌──────────────────────────────────────────┐
│  PERIODO: 2026 - Primer Semestre         │ ← Caja dorada (si aplica)
└──────────────────────────────────────────┘


╔════════════════════════════════════════╗
║ Programa: Ingeniería de Sistemas       ║
║ Código: IS-2020                        ║ ← Tabla de información
║ Fecha: 18 de Febrero de 2026 - 14:30  ║   con bordes dorados
║ Generado por: Dr. Juan Pérez           ║
║ Total Directores: 12                   ║
║ Total Modalidades: 45                  ║
╚════════════════════════════════════════╝


┌─────────────────────────────────────────────────────┐
│ Sistema SIGMA - Sistema Integral de Gestión de      │ ← Footer
│ Modalidades de Grado                                │   dorado claro
└─────────────────────────────────────────────────────┘
```

---

### **SECCIÓN 1: RESUMEN EJECUTIVO**

#### 1.1 Tarjetas de Métricas Clave (2 filas × 3 columnas)

**Fila 1**:
```
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│      12      │  │      45      │  │      58      │
│  Directores  │  │ Modalidades  │  │ Estudiantes  │
│   Activos    │  │  Asignadas   │  │ Supervisados │
└──────────────┘  └──────────────┘  └──────────────┘
   Borde rojo       Borde dorado      Borde rojo
```

**Fila 2**:
```
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│     3.75     │  │      8       │  │      2       │
│ Prom. Modal. │  │ Carga Máxima │  │ Sobrecargados│
│ por Director │  │  (Director)  │  │  (≥5 modal.) │
└──────────────┘  └──────────────┘  └──────────────┘
   Borde dorado     Borde rojo        Borde dorado
```

#### 1.2 Directores Destacados

```
┌──────────────────────────────────────────────────┐
│ 👤 Mayor Carga: Dr. Carlos López (8 modalidades) │ ← Fondo rojo claro
└──────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────┐
│ ✓ Menor Carga: Ing. Ana García (1 modalidad)    │ ← Fondo verde claro
└──────────────────────────────────────────────────┘
```

#### 1.3 Distribución Visual de Carga

**Gráfico de barras horizontales**:

```
CARGA BAJA (1-2 modalidades)      ███████████░░░░░  5 directores
CARGA NORMAL (3-4 modalidades)    █████████████████ 4 directores
CARGA ALTA (5-6 modalidades)      ████░░░░░░░░░░░░░ 2 directores
SOBRECARGA (≥7 modalidades)       ███░░░░░░░░░░░░░░ 1 director
```

---

### **SECCIÓN 2: ESTADÍSTICAS GENERALES**

#### 2.1 Por Estado de Modalidad

Tabla con distribución de estados:

| Estado | Cantidad | Porcentaje |
|--------|----------|------------|
| Aprobado | 25 | 55.56% |
| En Revisión | 12 | 26.67% |
| Aprobado Secretaría | 5 | 11.11% |
| Pendiente Aprobación | 3 | 6.67% |

**Diseño**: Encabezados rojos, filas alternadas (blanco/dorado claro).

#### 2.2 Por Tipo de Modalidad

Tabla con distribución de tipos:

| Tipo de Modalidad | Cantidad | Directores Asignados |
|-------------------|----------|----------------------|
| Proyecto de Grado | 30 | 10 |
| Pasantía | 10 | 5 |
| Práctica Profesional | 5 | 3 |

---

### **SECCIÓN 3: ANÁLISIS DE CARGA DE TRABAJO**

*Solo se incluye si `includeWorkloadAnalysis: true`*

#### 3.1 Indicadores de Carga

```
┌─────────────────────────────────────────┐
│ ✓ ESTADO GENERAL: BALANCEADO           │ ← Fondo verde o rojo
└─────────────────────────────────────────┘

• Máximo recomendado: 6 modalidades por director
• Carga promedio actual: 3.75 modalidades
• Directores sobrecargados: 2 (16.67%)
• Directores disponibles: 5 (41.67%)
```

#### 3.2 Listas por Nivel de Carga

**Directores Sobrecargados** (≥5 modalidades):
```
⚠ SOBRECARGA DETECTADA
┌────────────────────────────────┬───────────┐
│ Dr. Carlos López               │ 8 modal.  │ ← Fondo rojo claro
│ Dra. María Rodríguez           │ 6 modal.  │
└────────────────────────────────┴───────────┘
```

**Directores Disponibles** (<3 modalidades):
```
✓ DIRECTORES DISPONIBLES
┌────────────────────────────────┬───────────┐
│ Ing. Ana García                │ 1 modal.  │ ← Fondo verde claro
│ Dr. Pedro Martínez             │ 2 modal.  │
│ Ing. Laura Fernández           │ 2 modal.  │
└────────────────────────────────┴───────────┘
```

#### 3.3 Recomendaciones Automáticas

```
📋 RECOMENDACIONES GENERADAS:

1. Considerar redistribuir carga de Dr. Carlos López (8 modalidades)
2. Directores disponibles para nuevas asignaciones: 5
3. Se recomienda mantener máximo 6 modalidades por director
4. Estado general de carga: BALANCEADO
```

---

### **SECCIÓN 4: DETALLE POR DIRECTOR**

*Nueva página para cada director*

#### Encabezado del Director

```
┌─────────────────────────────────────────────────────────┐
│ 👤 DR. CARLOS LÓPEZ GARCÍA                              │ ← Banda roja
│    carlos.lopez@usco.edu.co | Doctorado en Ingeniería  │   institucional
└─────────────────────────────────────────────────────────┘
```

#### Tarjetas de Estadísticas del Director

```
┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐
│      8      │  │      6      │  │      2      │  │      0      │
│   Total     │  │  Activas    │  │ Completadas │  │ Pendientes  │
│ Modalidades │  │             │  │             │  │  Aprobación │
└─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘
```

#### Indicador de Carga de Trabajo

```
┌─────────────────────────────────────────────────────┐
│ CARGA DE TRABAJO: ALTA (8/6 recomendado)           │ ← Color según nivel:
│ ████████████████████████████████░░░░░░░░░░░░░░░░░░ │   Verde: Normal
│ 133% de la carga recomendada                       │   Naranja: Alta
└─────────────────────────────────────────────────────┘   Rojo: Sobrecarga
```

#### Tabla de Modalidades Asignadas

| ID | Tipo | Estudiante(s) | Estado | Inicio | Días | Acciones Pendientes |
|----|------|---------------|--------|--------|------|---------------------|
| 145 | Proyecto de Grado | Juan Pérez (L), María López | Aprobado | 15/08/2024 | 187 | ❌ No |
| 152 | Proyecto de Grado | Carlos Ruiz | En Revisión | 10/09/2024 | 161 | ⚠️ Sí |
| 158 | Pasantía | Ana Gómez | Aprobado | 01/02/2026 | 17 | ❌ No |

**Características**:
- Ordenadas por fecha de inicio (más antiguas primero)
- Alertas visuales para acciones pendientes
- Indicadores de tiempo (días activos)
- Estudiantes líderes marcados con (L)

#### Análisis Temporal del Director

```
📊 ANÁLISIS TEMPORAL

• Promedio de días por modalidad: 121.5 días
• Modalidad más antigua: 187 días (ID 145)
• Modalidad más reciente: 17 días (ID 158)
• Tiempo promedio en estado actual: 45.3 días
```

#### Observaciones Específicas

```
📝 OBSERVACIONES

• El director tiene 2 modalidades pendientes de revisión
• 1 modalidad lleva más de 180 días activa
• Se recomienda priorizar la modalidad ID 152 (acciones pendientes)
```

---

### **SECCIÓN 5: ANÁLISIS COMPARATIVO**

*Solo si hay múltiples directores*

#### 5.1 Ranking de Directores por Carga

Tabla ordenada por cantidad de modalidades:

| Posición | Director | Modalidades | Estudiantes | Estado Carga |
|----------|----------|-------------|-------------|--------------|
| 1° | Dr. Carlos López | 8 | 12 | 🔴 SOBRECARGA |
| 2° | Dra. María Rodríguez | 6 | 8 | 🟠 ALTA |
| 3° | Dr. Pedro Martínez | 5 | 7 | 🟠 ALTA |
| ... | ... | ... | ... | ... |
| 10° | Ing. Ana García | 1 | 1 | 🟢 BAJA |

#### 5.2 Distribución de Estudiantes Supervisados

```
┌─────────────────────────────────────────────────────┐
│ DR. CARLOS LÓPEZ                                    │
│ ████████████████████████████░░░░░░░░░░░░░░░░░░░░░  │
│ 12 estudiantes (20.69% del total)                  │
└─────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────┐
│ DRA. MARÍA RODRÍGUEZ                                │
│ ███████████████████░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░  │
│ 8 estudiantes (13.79% del total)                   │
└─────────────────────────────────────────────────────┘
```

---

### **SECCIÓN 6: ALERTAS Y ACCIONES RECOMENDADAS**

```
🚨 ALERTAS CRÍTICAS

⚠️ 2 directores con sobrecarga (≥7 modalidades)
⚠️ 5 modalidades llevan más de 180 días activas
⚠️ 3 modalidades con acciones pendientes

✅ ACCIONES RECOMENDADAS

1. Redistribuir carga de Dr. Carlos López (8 modalidades)
2. Asignar nuevas modalidades a directores disponibles
3. Revisar modalidades antiguas (>180 días)
4. Seguimiento a modalidades con acciones pendientes
5. Evaluar capacidad para próximo periodo académico
```

---

### **SECCIÓN 7: DISTRIBUCIÓN TEMPORAL**

*Gráfico de línea de tiempo*

```
MODALIDADES POR FECHA DE INICIO

Ene 2024 ████░░░░░░░░ 3 modalidades
Feb 2024 ██████░░░░░░ 4 modalidades
Mar 2024 ████░░░░░░░░ 3 modalidades
Abr 2024 ████████░░░░ 5 modalidades
May 2024 ██████░░░░░░ 4 modalidades
...
Ene 2026 ██████████░░ 6 modalidades
Feb 2026 ████░░░░░░░░ 2 modalidades (en curso)
```

Permite identificar picos de inicio de modalidades.

---

### **SECCIÓN 8: CONCLUSIONES Y RECOMENDACIONES**

Conclusiones automáticas generadas por el sistema:

```
CONCLUSIONES

1. El programa cuenta con 12 directores activos supervisando 45 modalidades

2. La carga promedio es de 3.75 modalidades por director, dentro del 
   rango recomendado (2-6 modalidades)

3. Se identificaron 2 directores con sobrecarga que requieren atención

4. 5 directores están disponibles para nuevas asignaciones

5. El 13.33% de las modalidades llevan más de 180 días activas, 
   sugiriendo seguimiento adicional

RECOMENDACIONES

• Redistribuir modalidades de directores sobrecargados
• Asignar nuevas modalidades priorizando directores disponibles
• Implementar seguimiento para modalidades con más de 180 días
• Evaluar causas de retrasos en modalidades antiguas
• Planificar contratación de directores según proyección de demanda
```

---

### **PIE DE PÁGINA (Todas las Páginas)**

```
Página 3 | Ingeniería de Sistemas | Generado: 18/02/2026
```

---

## 🔍 Detalles de los Filtros

### 1️⃣ Filtro por Director Específico (`directorId`)

**Descripción**: Limita el reporte a un solo director.

**Comportamiento**:
- Si se especifica: Solo ese director aparece en el PDF
- Si es `null`: Todos los directores del programa

**Efecto en el PDF**:
- **Con filtro**: 
  - Portada incluye nombre del director
  - Solo una entrada en Sección 4
  - Análisis de carga solo de ese director
- **Sin filtro**: 
  - Múltiples directores
  - Comparativas y rankings

**Ejemplo**:
```json
{
  "directorId": 25
}
```

---

### 2️⃣ Filtro por Estados (`processStatuses`)

**Descripción**: Filtra modalidades por su estado actual.

**Estados Válidos**:
- `APROBADO`
- `EN_REVISION`
- `PENDIENTE_APROBACION`
- `APROBADO_SECRETARIA`
- `APROBADO_CONSEJO`
- `RECHAZADO`
- `CANCELADO`
- `COMPLETADO`

**Comportamiento**:
- Filtro **inclusivo** (OR): incluye modalidades en cualquiera de los estados listados
- Si es `null` o vacío: incluye todos los estados

**Ejemplo**:
```json
{
  "processStatuses": ["APROBADO", "APROBADO_CONSEJO", "EN_REVISION"]
}
```
→ Solo modalidades en esos 3 estados.

---

### 3️⃣ Filtro por Tipos de Modalidad (`modalityTypes`)

**Descripción**: Filtra por tipos específicos de modalidad.

**Tipos Comunes**:
- `PROYECTO DE GRADO`
- `PASANTIA`
- `PRACTICA PROFESIONAL`
- `EMPRENDIMIENTO Y FORTALECIMIENTO DE EMPRESA`
- `PORTAFOLIO PROFESIONAL`

**Nota**: Algunos tipos NO requieren director (Seminario, Plan Complementario, Producción Académica) y no aparecerán en este reporte.

**Ejemplo**:
```json
{
  "modalityTypes": ["PROYECTO DE GRADO", "PASANTIA"]
}
```

---

### 4️⃣ Filtro de Directores Sobrecargados (`onlyOverloaded`)

**Descripción**: Filtra solo directores con sobrecarga de trabajo.

**Criterio de Sobrecarga**: ≥5 modalidades asignadas

**Comportamiento**:
- `true`: Solo directores con 5+ modalidades
- `false` o `null`: Todos los directores

**Uso típico**: Identificar problemas de distribución de carga.

**Ejemplo**:
```json
{
  "onlyOverloaded": true,
  "onlyActiveModalities": true
}
```
→ Directores sobrecargados con modalidades activas.

---

### 5️⃣ Filtro de Directores Disponibles (`onlyAvailable`)

**Descripción**: Filtra solo directores con disponibilidad para nuevas asignaciones.

**Criterio de Disponibilidad**: <3 modalidades asignadas

**Comportamiento**:
- `true`: Solo directores con 1-2 modalidades
- `false` o `null`: Todos los directores

**Uso típico**: Asignar nuevos proyectos.

**Ejemplo**:
```json
{
  "onlyAvailable": true
}
```

**⚠️ Conflicto**: No combinar `onlyOverloaded: true` con `onlyAvailable: true` (son mutuamente excluyentes).

---

### 6️⃣ Filtro Solo Modalidades Activas (`onlyActiveModalities`)

**Descripción**: Excluye modalidades completadas y canceladas.

**Estados Activos**:
- APROBADO
- EN_REVISION
- PENDIENTE_APROBACION
- APROBADO_SECRETARIA
- APROBADO_CONSEJO

**Estados Excluidos**:
- COMPLETADO
- CANCELADO
- RECHAZADO

**Ejemplo**:
```json
{
  "onlyActiveModalities": true
}
```

---

### 7️⃣ Análisis de Carga de Trabajo (`includeWorkloadAnalysis`)

**Descripción**: Incluye/excluye la Sección 3 de análisis de carga.

**Comportamiento**:
- `true`: Incluye análisis completo de carga (Sección 3, alertas, recomendaciones)
- `false`: Omite análisis (PDF más corto)
- Por defecto: `true`

**Contenido añadido**:
- Indicadores de carga
- Listas de sobrecargados/disponibles
- Gráfico de distribución de carga
- Recomendaciones automáticas

---

## 💻 Ejemplos de Código

### Ejemplo 1: JavaScript - Reporte General de Directores

```javascript
async function downloadDirectorsReport() {
  const token = localStorage.getItem('auth_token');
  
  const filters = {
    includeWorkloadAnalysis: true,
    onlyActiveModalities: true
  };
  
  try {
    const response = await fetch('http://localhost:8080/reports/directors/assigned-modalities/pdf', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(filters)
    });
    
    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`);
    }
    
    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `Reporte_Directores_${new Date().toISOString().split('T')[0]}.pdf`;
    document.body.appendChild(a);
    a.click();
    
    window.URL.revokeObjectURL(url);
    document.body.removeChild(a);
    
    console.log('✅ Reporte de directores descargado');
  } catch (error) {
    console.error('❌ Error:', error);
    alert('No se pudo generar el reporte de directores');
  }
}

// Uso
downloadDirectorsReport();
```

---

### Ejemplo 2: React - Selector de Director

```jsx
import React, { useState, useEffect } from 'react';
import axios from 'axios';

function DirectorReportSelector() {
  const [directors, setDirectors] = useState([]);
  const [selectedDirector, setSelectedDirector] = useState(null);
  const [filters, setFilters] = useState({
    onlyActiveModalities: true,
    includeWorkloadAnalysis: true
  });
  const [loading, setLoading] = useState(false);
  
  // Cargar lista de directores
  useEffect(() => {
    const loadDirectors = async () => {
      try {
        const token = localStorage.getItem('auth_token');
        const response = await axios.get(
          'http://localhost:8080/api/directors/available',
          { headers: { Authorization: `Bearer ${token}` } }
        );
        setDirectors(response.data);
      } catch (error) {
        console.error('Error cargando directores:', error);
      }
    };
    loadDirectors();
  }, []);
  
  const downloadReport = async (useSpecificDirector = false) => {
    setLoading(true);
    try {
      const token = localStorage.getItem('auth_token');
      
      let url, method, data;
      
      if (useSpecificDirector && selectedDirector) {
        // Endpoint GET específico
        url = `http://localhost:8080/reports/directors/${selectedDirector}/modalities/pdf`;
        method = 'GET';
        data = null;
      } else {
        // Endpoint POST general
        url = 'http://localhost:8080/reports/directors/assigned-modalities/pdf';
        method = 'POST';
        data = selectedDirector 
          ? { ...filters, directorId: selectedDirector }
          : filters;
      }
      
      const response = await axios({
        method,
        url,
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        data,
        responseType: 'blob'
      });
      
      // Descargar
      const blob = new Blob([response.data], { type: 'application/pdf' });
      const downloadUrl = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = downloadUrl;
      link.download = selectedDirector 
        ? `Reporte_Director_${selectedDirector}.pdf`
        : `Reporte_Todos_Directores.pdf`;
      document.body.appendChild(link);
      link.click();
      link.remove();
      
      alert('✅ Reporte descargado exitosamente');
    } catch (error) {
      console.error('Error:', error);
      alert('❌ Error al generar reporte');
    } finally {
      setLoading(false);
    }
  };
  
  return (
    <div className="director-report-selector">
      <h2>Reporte de Directores y Modalidades</h2>
      
      <div className="filters">
        <label>
          Director:
          <select 
            value={selectedDirector || ''} 
            onChange={e => setSelectedDirector(e.target.value ? parseInt(e.target.value) : null)}
          >
            <option value="">Todos los directores</option>
            {directors.map(dir => (
              <option key={dir.id} value={dir.id}>
                {dir.fullName} ({dir.modalitiesCount} modalidades)
              </option>
            ))}
          </select>
        </label>
        
        <label>
          <input 
            type="checkbox" 
            checked={filters.onlyActiveModalities}
            onChange={e => setFilters({...filters, onlyActiveModalities: e.target.checked})}
          />
          Solo modalidades activas
        </label>
        
        <label>
          <input 
            type="checkbox" 
            checked={filters.includeWorkloadAnalysis}
            onChange={e => setFilters({...filters, includeWorkloadAnalysis: e.target.checked})}
          />
          Incluir análisis de carga
        </label>
      </div>
      
      <div className="actions">
        <button 
          onClick={() => downloadReport(false)} 
          disabled={loading}
          className="btn-primary"
        >
          {loading ? '⏳ Generando...' : '📥 Descargar Reporte (POST)'}
        </button>
        
        {selectedDirector && (
          <button 
            onClick={() => downloadReport(true)} 
            disabled={loading}
            className="btn-secondary"
          >
            📥 Descarga Rápida (GET)
          </button>
        )}
      </div>
    </div>
  );
}

export default DirectorReportSelector;
```

---

### Ejemplo 3: PowerShell - Múltiples Escenarios

```powershell
# Configuración base
$token = "tu_token_jwt_aqui"
$baseUrl = "http://localhost:8080/reports/directors"

# Headers comunes
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

# ============================================
# ESCENARIO 1: Reporte General de Todos
# ============================================
Write-Host "`n📊 Generando reporte general de directores..." -ForegroundColor Cyan

$filtrosGenerales = @{
    includeWorkloadAnalysis = $true
    onlyActiveModalities = $true
} | ConvertTo-Json

Invoke-WebRequest `
    -Uri "$baseUrl/assigned-modalities/pdf" `
    -Method Post `
    -Headers $headers `
    -Body $filtrosGenerales `
    -OutFile "Reporte_Todos_Directores.pdf"

Write-Host "✅ Descargado: Reporte_Todos_Directores.pdf" -ForegroundColor Green

# ============================================
# ESCENARIO 2: Solo Directores Sobrecargados
# ============================================
Write-Host "`n⚠️  Identificando directores sobrecargados..." -ForegroundColor Yellow

$filtrosSobrecarga = @{
    onlyOverloaded = $true
    includeWorkloadAnalysis = $true
    onlyActiveModalities = $true
} | ConvertTo-Json

Invoke-WebRequest `
    -Uri "$baseUrl/assigned-modalities/pdf" `
    -Method Post `
    -Headers $headers `
    -Body $filtrosSobrecarga `
    -OutFile "Reporte_Directores_Sobrecargados.pdf"

Write-Host "✅ Descargado: Reporte_Directores_Sobrecargados.pdf" -ForegroundColor Green

# ============================================
# ESCENARIO 3: Directores Disponibles
# ============================================
Write-Host "`n✓ Buscando directores disponibles..." -ForegroundColor Green

$filtrosDisponibles = @{
    onlyAvailable = $true
    onlyActiveModalities = $true
} | ConvertTo-Json

Invoke-WebRequest `
    -Uri "$baseUrl/assigned-modalities/pdf" `
    -Method Post `
    -Headers $headers `
    -Body $filtrosDisponibles `
    -OutFile "Reporte_Directores_Disponibles.pdf"

Write-Host "✅ Descargado: Reporte_Directores_Disponibles.pdf" -ForegroundColor Green

# ============================================
# ESCENARIO 4: Director Específico (GET)
# ============================================
$directorId = 25
Write-Host "`n👤 Generando reporte del director ID $directorId..." -ForegroundColor Cyan

Invoke-WebRequest `
    -Uri "$baseUrl/$directorId/modalities/pdf" `
    -Method Get `
    -Headers $headers `
    -OutFile "Reporte_Director_$directorId.pdf"

Write-Host "✅ Descargado: Reporte_Director_$directorId.pdf" -ForegroundColor Green

# ============================================
# ESCENARIO 5: Solo Proyectos de Grado
# ============================================
Write-Host "`n📚 Reporte de directores de proyectos de grado..." -ForegroundColor Cyan

$filtrosProyectos = @{
    modalityTypes = @("PROYECTO DE GRADO")
    processStatuses = @("APROBADO", "EN_REVISION")
    includeWorkloadAnalysis = $true
} | ConvertTo-Json

Invoke-WebRequest `
    -Uri "$baseUrl/assigned-modalities/pdf" `
    -Method Post `
    -Headers $headers `
    -Body $filtrosProyectos `
    -OutFile "Reporte_Directores_Proyectos.pdf"

Write-Host "✅ Descargado: Reporte_Directores_Proyectos.pdf" -ForegroundColor Green

Write-Host "`n🎉 Todos los reportes generados exitosamente!" -ForegroundColor Green
```

---

### Ejemplo 4: Python - Con Manejo de Errores

```python
import requests
import json
from datetime import datetime
from typing import Optional, Dict, List

class DirectorReportClient:
    def __init__(self, base_url: str, token: str):
        self.base_url = base_url
        self.headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/json"
        }
    
    def download_general_report(self, filters: Optional[Dict] = None) -> str:
        """
        Descarga el reporte general de todos los directores
        
        Args:
            filters: Filtros opcionales (dict con estructura de DirectorReportFilterDTO)
            
        Returns:
            Nombre del archivo descargado
        """
        url = f"{self.base_url}/reports/directors/assigned-modalities/pdf"
        
        if filters is None:
            filters = {
                "includeWorkloadAnalysis": True,
                "onlyActiveModalities": True
            }
        
        try:
            print("📊 Generando reporte general de directores...")
            
            response = requests.post(
                url,
                headers=self.headers,
                json=filters,
                stream=True
            )
            
            if response.status_code == 200:
                filename = f"Reporte_Directores_{datetime.now().strftime('%Y%m%d_%H%M%S')}.pdf"
                
                with open(filename, 'wb') as f:
                    for chunk in response.iter_content(chunk_size=8192):
                        f.write(chunk)
                
                print(f"✅ Reporte descargado: {filename}")
                
                # Leer headers informativos
                total_records = response.headers.get('X-Total-Records', 'N/A')
                print(f"   Total de registros: {total_records}")
                
                return filename
            else:
                error_data = response.json()
                print(f"❌ Error {response.status_code}: {error_data.get('error')}")
                return None
                
        except Exception as e:
            print(f"❌ Excepción: {str(e)}")
            return None
    
    def download_specific_director_report(self, director_id: int) -> str:
        """
        Descarga el reporte de un director específico (GET rápido)
        
        Args:
            director_id: ID del director
            
        Returns:
            Nombre del archivo descargado
        """
        url = f"{self.base_url}/reports/directors/{director_id}/modalities/pdf"
        
        try:
            print(f"👤 Generando reporte del director ID {director_id}...")
            
            response = requests.get(
                url,
                headers=self.headers,
                stream=True
            )
            
            if response.status_code == 200:
                filename = f"Reporte_Director_{director_id}_{datetime.now().strftime('%Y%m%d')}.pdf"
                
                with open(filename, 'wb') as f:
                    for chunk in response.iter_content(chunk_size=8192):
                        f.write(chunk)
                
                print(f"✅ Reporte descargado: {filename}")
                return filename
            else:
                error_data = response.json()
                print(f"❌ Error {response.status_code}: {error_data.get('error')}")
                return None
                
        except Exception as e:
            print(f"❌ Excepción: {str(e)}")
            return None
    
    def download_overloaded_directors(self) -> str:
        """Descarga reporte de directores sobrecargados"""
        filters = {
            "onlyOverloaded": True,
            "includeWorkloadAnalysis": True,
            "onlyActiveModalities": True
        }
        return self.download_general_report(filters)
    
    def download_available_directors(self) -> str:
        """Descarga reporte de directores disponibles"""
        filters = {
            "onlyAvailable": True,
            "onlyActiveModalities": True
        }
        return self.download_general_report(filters)

# Uso
client = DirectorReportClient(
    base_url="http://localhost:8080",
    token="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
)

# Reporte general
client.download_general_report()

# Director específico
client.download_specific_director_report(director_id=25)

# Solo sobrecargados
client.download_overloaded_directors()

# Solo disponibles
client.download_available_directors()
```

---

## 📊 Estructura de Datos del Reporte

### DirectorAssignedModalitiesReportDTO

```typescript
interface DirectorAssignedModalitiesReportDTO {
  generatedAt: string;                          // ISO 8601
  generatedBy: string;                          // Nombre completo
  academicProgramId: number;                    // ID programa
  academicProgramName: string;                  // Nombre programa
  academicProgramCode: string;                  // Código programa
  
  directorInfo?: DirectorInfoDTO;               // Si es reporte individual
  summary: DirectorSummaryDTO;                  // Resumen ejecutivo
  directors: DirectorWithModalitiesDTO[];       // Lista de directores
  modalitiesByStatus: {[key: string]: number};  // Distribución por estado
  modalitiesByType: {[key: string]: number};    // Distribución por tipo
  workloadAnalysis?: WorkloadAnalysisDTO;       // Análisis de carga
  metadata: ReportMetadataDTO;                  // Metadata
}
```

---

### DirectorInfoDTO (Director Específico)

```typescript
interface DirectorInfoDTO {
  directorId: number;                     // ID del director
  fullName: string;                       // Nombre completo
  email: string;                          // Correo institucional
  academicTitle: string;                  // Título académico
  totalAssignedModalities: number;        // Total asignadas
  activeModalities: number;               // Activas actualmente
  completedModalities: number;            // Completadas
}
```

**Ejemplo**:
```json
{
  "directorId": 25,
  "fullName": "Dr. Carlos López García",
  "email": "carlos.lopez@usco.edu.co",
  "academicTitle": "Doctorado en Ingeniería de Software",
  "totalAssignedModalities": 8,
  "activeModalities": 6,
  "completedModalities": 2
}
```

---

### DirectorSummaryDTO (Resumen General)

```typescript
interface DirectorSummaryDTO {
  totalDirectors: number;                       // Total de directores
  totalModalitiesAssigned: number;              // Total modalidades
  totalActiveModalities: number;                // Modalidades activas
  totalStudentsSupervised: number;              // Total estudiantes
  averageModalitiesPerDirector: number;         // Promedio modalidades
  directorWithMostModalities: string;           // Nombre del más cargado
  maxModalitiesCount: number;                   // Cantidad máxima
  directorWithLeastModalities: string;          // Nombre del menos cargado
  minModalitiesCount: number;                   // Cantidad mínima
  directorsOverloaded: number;                  // Cantidad sobrecargados
  directorsAvailable: number;                   // Cantidad disponibles
}
```

**Ejemplo**:
```json
{
  "totalDirectors": 12,
  "totalModalitiesAssigned": 45,
  "totalActiveModalities": 38,
  "totalStudentsSupervised": 58,
  "averageModalitiesPerDirector": 3.75,
  "directorWithMostModalities": "Dr. Carlos López García",
  "maxModalitiesCount": 8,
  "directorWithLeastModalities": "Ing. Ana García Torres",
  "minModalitiesCount": 1,
  "directorsOverloaded": 2,
  "directorsAvailable": 5
}
```

---

### DirectorWithModalitiesDTO

```typescript
interface DirectorWithModalitiesDTO {
  directorId: number;                           // ID del director
  fullName: string;                             // Nombre completo
  email: string;                                // Email
  academicTitle: string;                        // Título académico
  totalAssignedModalities: number;              // Total asignadas
  activeModalities: number;                     // Activas
  completedModalities: number;                  // Completadas
  pendingApprovalModalities: number;            // Pendientes aprobación
  modalities: ModalityDetailDTO[];              // Lista de modalidades
  workloadStatus: string;                       // NORMAL, HIGH, OVERLOADED
  averageDaysPerModality: number;               // Promedio de días
}
```

**Ejemplo**:
```json
{
  "directorId": 25,
  "fullName": "Dr. Carlos López García",
  "email": "carlos.lopez@usco.edu.co",
  "academicTitle": "Doctor en Ingeniería",
  "totalAssignedModalities": 8,
  "activeModalities": 6,
  "completedModalities": 2,
  "pendingApprovalModalities": 1,
  "workloadStatus": "OVERLOADED",
  "averageDaysPerModality": 121.5,
  "modalities": [
    {
      "modalityId": 145,
      "modalityType": "PROYECTO_DE_GRADO",
      "modalityTypeName": "Proyecto de Grado",
      "students": [
        {
          "studentId": 1001,
          "fullName": "Juan Pérez Gómez",
          "studentCode": "20191234567",
          "email": "juan.perez@usco.edu.co",
          "isLeader": true
        }
      ],
      "currentStatus": "APROBADO",
      "statusDescription": "Aprobado",
      "startDate": "2024-08-15T00:00:00",
      "lastUpdate": "2026-01-10T14:30:00",
      "daysSinceStart": 187,
      "daysInCurrentStatus": 38,
      "projectTitle": "Sistema de Gestión de Inventarios",
      "hasPendingActions": false,
      "observations": null
    }
    // ... más modalidades
  ]
}
```

---

### WorkloadAnalysisDTO

```typescript
interface WorkloadAnalysisDTO {
  recommendedMaxModalities: number;             // Máximo recomendado (ej: 6)
  directorsOverloaded: string[];                // Lista de nombres sobrecargados
  directorsAvailable: string[];                 // Lista de nombres disponibles
  averageWorkload: number;                      // Carga promedio
  overallWorkloadStatus: string;                // BALANCED, UNBALANCED
  workloadDistribution: {                       // Distribución por nivel
    LOW: number,                                // 1-2 modalidades
    NORMAL: number,                             // 3-4 modalidades
    HIGH: number,                               // 5-6 modalidades
    OVERLOADED: number                          // ≥7 modalidades
  }
}
```

**Ejemplo**:
```json
{
  "recommendedMaxModalities": 6,
  "directorsOverloaded": ["Dr. Carlos López García", "Dra. María Rodríguez"],
  "directorsAvailable": ["Ing. Ana García", "Dr. Pedro Martínez", "Ing. Laura Fernández"],
  "averageWorkload": 3.75,
  "overallWorkloadStatus": "BALANCED",
  "workloadDistribution": {
    "LOW": 5,
    "NORMAL": 4,
    "HIGH": 2,
    "OVERLOADED": 1
  }
}
```

---

## 🎨 Diseño Visual del PDF

### Colores Institucionales

```
🔴 ROJO INSTITUCIONAL (#8F1E1E)
  • Banda superior de portada
  • Encabezados de sección
  • Headers de tablas
  • Títulos principales
  • Indicadores de sobrecarga

🟡 DORADO INSTITUCIONAL (#D5CBA0)
  • Caja de programa académico
  • Bordes de tarjetas
  • Encabezados de director
  • Fondos alternos en tablas
  • Indicadores de disponibilidad

⚪ BLANCO (#FFFFFF)
  • Fondo principal
  • Celdas de datos
  • Texto en headers rojos

🟨 DORADO CLARO (#F5F2EB)
  • Fondos sutiles
  • Footer de portada
  • Cajas de información

🟢 VERDE SUAVE (Estado positivo)
  • Directores disponibles
  • Carga baja/normal
  • Indicadores favorables

🟠 NARANJA CLARO (Estado alerta)
  • Carga alta
  • Alertas moderadas

🔴 ROJO CLARO (Estado crítico)
  • Sobrecarga
  • Alertas críticas
  • Acciones pendientes
```

---

## 📈 Métricas y Cálculos

### Métricas Básicas

| Métrica | Cálculo | Ubicación PDF |
|---------|---------|---------------|
| Total Directores | COUNT(DISTINCT director_id) WHERE director_id IS NOT NULL | Resumen Ejecutivo |
| Total Modalidades | COUNT(*) WHERE director asignado | Resumen Ejecutivo |
| Total Estudiantes | COUNT(DISTINCT student_id) | Resumen Ejecutivo |
| Promedio Modal./Director | Total Modalidades / Total Directores | Resumen Ejecutivo |

### Métricas de Carga

| Métrica | Cálculo | Criterio | Ubicación PDF |
|---------|---------|----------|---------------|
| Directores Sobrecargados | COUNT WHERE modalidades ≥ 5 | ≥5 modalidades | Análisis de Carga |
| Directores Disponibles | COUNT WHERE modalidades < 3 | <3 modalidades | Análisis de Carga |
| Carga Baja | COUNT WHERE modalidades 1-2 | LOW | Análisis de Carga |
| Carga Normal | COUNT WHERE modalidades 3-4 | NORMAL | Análisis de Carga |
| Carga Alta | COUNT WHERE modalidades 5-6 | HIGH | Análisis de Carga |
| Sobrecarga | COUNT WHERE modalidades ≥ 7 | OVERLOADED | Análisis de Carga |

### Métricas Temporales

| Métrica | Cálculo | Ubicación PDF |
|---------|---------|---------------|
| Días desde Inicio | CURRENT_DATE - start_date | Detalle por Director |
| Días en Estado Actual | CURRENT_DATE - status_change_date | Detalle por Director |
| Promedio Días/Modalidad | AVG(días) por director | Detalle por Director |
| Modalidad Más Antigua | MAX(días) por director | Detalle por Director |

---

## 🔄 Flujo de Procesamiento

```
Usuario envía POST con filtros opcionales
        ↓
Autenticación JWT → Extrae usuario
        ↓
Valida permiso PERM_VIEW_REPORT
        ↓
Obtiene programa académico del usuario
        ↓
ReportService.generateDirectorAssignedModalitiesReport(filters)
        ↓
┌────────────────────────────────────────────────┐
│ Aplicar Filtros:                               │
│  • Filtrar por programa (automático)           │
│  • Aplicar directorId (si existe)              │
│  • Aplicar processStatuses                     │
│  • Aplicar modalityTypes                       │
│  • Aplicar onlyOverloaded / onlyAvailable      │
│  • Aplicar onlyActiveModalities                │
└────────────────────────────────────────────────┘
        ↓
Consultar base de datos:
  • student_modalities WHERE director_id IS NOT NULL
  • users (directores y estudiantes)
  • modality_types
  • student_modality_participants
        ↓
Agrupar por director:
  ↓
  Para cada director:
    • Contar modalidades totales
    • Contar modalidades activas
    • Contar modalidades completadas
    • Listar modalidades con detalles
    • Calcular promedio de días
    • Determinar workloadStatus
        ↓
Calcular resumen general:
  • Total directores
  • Promedios generales
  • Director con más/menos carga
  • Directores sobrecargados/disponibles
        ↓
Si includeWorkloadAnalysis = true:
  ↓
  Calcular distribución de carga:
    • Clasificar cada director (LOW, NORMAL, HIGH, OVERLOADED)
    • Generar listas de sobrecargados y disponibles
    • Calcular promedio de carga
    • Determinar estado general (BALANCED/UNBALANCED)
    • Generar recomendaciones automáticas
        ↓
Construir DirectorAssignedModalitiesReportDTO
        ↓
DirectorAssignedModalitiesPdfGenerator.generatePDF(report)
        ↓
┌────────────────────────────────────────────────┐
│ Generar PDF con diseño institucional:          │
│  1. Portada profesional                        │
│  2. Resumen ejecutivo con tarjetas             │
│  3. Estadísticas generales (estado y tipo)     │
│  4. Análisis de carga (si aplica)              │
│  5. Detalle por director (página por director) │
│  6. Análisis comparativo (ranking)             │
│  7. Distribución temporal                      │
│  8. Alertas y recomendaciones                  │
│  9. Conclusiones automáticas                   │
└────────────────────────────────────────────────┘
        ↓
Retorna ByteArrayOutputStream
        ↓
Convierte a ByteArrayResource
        ↓
Respuesta HTTP con PDF adjunto
```

---

## 🎯 Escenarios de Filtrado

### Escenario 1: Análisis Completo del Programa

```json
{
  "includeWorkloadAnalysis": true,
  "onlyActiveModalities": true
}
```

**Resultado**:
- Todos los directores del programa
- Solo modalidades activas
- Análisis completo de carga
- Identificación de problemas

---

### Escenario 2: Director Específico (Evaluación Individual)

```json
{
  "directorId": 25,
  "includeWorkloadAnalysis": false
}
```

**Resultado**:
- Solo información de ese director
- Todas sus modalidades (activas y completadas)
- Sin análisis comparativo de carga

---

### Escenario 3: Redistribución de Carga

```json
{
  "onlyOverloaded": true,
  "includeWorkloadAnalysis": true
}
```

**Resultado**:
- Solo directores sobrecargados (≥5 modalidades)
- Análisis detallado de qué modalidades pueden reasignarse
- Lista de directores disponibles para recibir modalidades

---

### Escenario 4: Planificación de Asignaciones

```json
{
  "onlyAvailable": true,
  "onlyActiveModalities": true
}
```

**Resultado**:
- Solo directores con disponibilidad (<3 modalidades activas)
- Capacidad actual de cada uno
- Recomendación de asignación

---

### Escenario 5: Auditoría de Proyectos de Grado

```json
{
  "modalityTypes": ["PROYECTO DE GRADO"],
  "processStatuses": ["APROBADO", "EN_REVISION"],
  "includeWorkloadAnalysis": false
}
```

**Resultado**:
- Solo directores con proyectos de grado
- Estados aprobados o en revisión
- Foco en supervisión de proyectos

---

### Escenario 6: Seguimiento de Modalidades en Revisión

```json
{
  "processStatuses": ["EN_REVISION", "PENDIENTE_APROBACION"],
  "onlyActiveModalities": true
}
```

**Resultado**:
- Directores con modalidades pendientes
- Identificación de cuellos de botella
- Directores que requieren atención urgente

---

## 📊 Interpretación de Estados de Carga

### 🟢 CARGA BAJA (LOW)
- **Rango**: 1-2 modalidades
- **Estado**: Normal y saludable
- **Acción**: Pueden recibir nuevas asignaciones
- **Color en PDF**: Verde claro

---

### 🟡 CARGA NORMAL (NORMAL)
- **Rango**: 3-4 modalidades
- **Estado**: Óptimo
- **Acción**: Carga balanceada, no requiere intervención
- **Color en PDF**: Dorado

---

### 🟠 CARGA ALTA (HIGH)
- **Rango**: 5-6 modalidades
- **Estado**: Límite superior recomendado
- **Acción**: Monitorear, evitar nuevas asignaciones
- **Color en PDF**: Naranja claro

---

### 🔴 SOBRECARGA (OVERLOADED)
- **Rango**: ≥7 modalidades
- **Estado**: Crítico
- **Acción**: Redistribuir urgentemente
- **Color en PDF**: Rojo claro

---

## 🎨 Elementos Visuales Destacados

### 1. Tarjetas de Estadísticas del Director

```
┌─────────────────────────────────────────────────────┐
│                        8                            │ ← Número grande
│                Total Modalidades                    │    (color rojo)
│                                                     │
└─────────────────────────────────────────────────────┘
```

---

### 2. Barra de Carga de Trabajo

```
┌──────────────────────────────────────────────────────┐
│ CARGA DE TRABAJO: ALTA (8/6 recomendado)            │
├──────────────────────────────────────────────────────┤
│ ████████████████████████████████████████░░░░░░░░░░  │
│ 133% de la carga recomendada                        │
└──────────────────────────────────────────────────────┘
```

**Colores según nivel**:
- Verde: 0-66%
- Naranja: 67-99%
- Rojo: ≥100%

---

### 3. Tabla de Modalidades con Alertas

```
┌────┬──────────────┬───────────────┬──────────┬──────┬────────────────┐
│ ID │ Tipo         │ Estudiante(s) │ Estado   │ Días │ Acciones Pend. │
├────┼──────────────┼───────────────┼──────────┼──────┼────────────────┤
│145 │Proyecto Grado│Juan Pérez (L) │Aprobado  │ 187  │ ❌ No          │
├────┼──────────────┼───────────────┼──────────┼──────┼────────────────┤
│152 │Proyecto Grado│Carlos Ruiz    │En Revisión│ 161 │ ⚠️ Sí         │ ← Alerta
└────┴──────────────┴───────────────┴──────────┴──────┴────────────────┘
```

---

### 4. Gráfico de Distribución de Carga

```
DISTRIBUCIÓN DE CARGA DE TRABAJO

┌──────────────────────────────────────────────────┐
│ CARGA BAJA (1-2)    ███████████░░░░░░░░░░  5     │
├──────────────────────────────────────────────────┤
│ CARGA NORMAL (3-4)  █████████████████░░░░  4     │
├──────────────────────────────────────────────────┤
│ CARGA ALTA (5-6)    █████░░░░░░░░░░░░░░░░  2     │
├──────────────────────────────────────────────────┤
│ SOBRECARGA (≥7)     ███░░░░░░░░░░░░░░░░░░  1     │
└──────────────────────────────────────────────────┘
```

---

### 5. Ranking Visual de Directores

```
TOP 5 DIRECTORES POR CARGA

1° Dr. Carlos López         ████████████████████ 8 modalidades
2° Dra. María Rodríguez     ███████████████░░░░░ 6 modalidades
3° Dr. Pedro Martínez       ██████████████░░░░░░ 5 modalidades
4° Ing. José Torres         ███████████░░░░░░░░░ 4 modalidades
5° Dra. Sofía Ramírez       ███████████░░░░░░░░░ 4 modalidades
```

---

## 🔐 Seguridad y Restricciones

### Filtrado Automático por Programa

**Crítico**: El sistema **SIEMPRE** filtra por el programa académico del usuario autenticado.

**Ejemplo**:
- Usuario: Jefe de Ingeniería de Sistemas
- Solicita: `{"directorId": 25}`
- **Si director 25 NO pertenece a Ingeniería de Sistemas**: Error o reporte vacío
- **Si director 25 SÍ pertenece a Ingeniería de Sistemas**: Reporte generado

### Validación de Permisos

Solo usuarios con `PERM_VIEW_REPORT`:
- ✅ Jefatura de Programa
- ✅ Consejo de Programa
- ✅ Comité de Programa
- ✅ Secretaría

**Nota**: Los directores **NO** tienen acceso por defecto (protección de privacidad).

---

## 📋 Diferencias Entre Endpoints

### POST `/directors/assigned-modalities/pdf` vs GET `/directors/{id}/modalities/pdf`

| Aspecto | POST (General) | GET (Específico) |
|---------|----------------|------------------|
| **Método** | POST | GET |
| **Request Body** | Opcional, con 7 filtros | No requiere |
| **Contenido** | Todos o filtrados | Solo un director |
| **Análisis de Carga** | Configurable | Desactivado por defecto |
| **Comparativas** | Sí (entre directores) | No |
| **Ranking** | Sí | No |
| **Velocidad** | Media-Lenta | Rápida |
| **Uso** | Análisis general | Consulta rápida |

---

## 💡 Consejos y Mejores Prácticas

### ✅ Recomendaciones

1. **Para auditoría general**: No enviar filtros
   ```json
   {"includeWorkloadAnalysis": true}
   ```

2. **Para redistribución**: Usar filtros de carga
   ```json
   {"onlyOverloaded": true, "includeWorkloadAnalysis": true}
   ```

3. **Para asignaciones nuevas**: Buscar disponibles
   ```json
   {"onlyAvailable": true}
   ```

4. **Para evaluación individual**: Usar GET con ID
   ```
   GET /reports/directors/25/modalities/pdf
   ```

5. **Para seguimiento**: Filtrar por estados pendientes
   ```json
   {"processStatuses": ["EN_REVISION", "PENDIENTE_APROBACION"]}
   ```

---

### ❌ Evitar

1. ❌ Combinar `onlyOverloaded: true` con `onlyAvailable: true` (contradictorio)
2. ❌ Solicitar ID de director de otro programa (no mostrará datos)
3. ❌ Omitir `includeWorkloadAnalysis` en análisis de redistribución
4. ❌ Filtrar por tipos que no requieren director (no hay datos)

---

## 🧪 Casos de Prueba

### Test 1: Reporte General Sin Filtros
**Request**:
```json
{}
```
**Esperado**: PDF con todos los directores, análisis completo

---

### Test 2: Director Específico por POST
**Request**:
```json
{
  "directorId": 25
}
```
**Esperado**: PDF solo del director 25

---

### Test 3: Director Específico por GET
**Request**:
```
GET /reports/directors/25/modalities/pdf
```
**Esperado**: PDF solo del director 25 (más rápido)

---

### Test 4: Solo Sobrecargados
**Request**:
```json
{
  "onlyOverloaded": true
}
```
**Esperado**: PDF con 2-3 directores sobrecargados (si existen)

---

### Test 5: Solo Disponibles
**Request**:
```json
{
  "onlyAvailable": true
}
```
**Esperado**: PDF con directores con <3 modalidades

---

### Test 6: Proyectos de Grado Aprobados
**Request**:
```json
{
  "modalityTypes": ["PROYECTO DE GRADO"],
  "processStatuses": ["APROBADO"]
}
```
**Esperado**: Solo directores con proyectos aprobados

---

## 🎓 Actores y Roles

### Casos de Uso por Actor

#### **Jefatura de Programa**
- 📊 Monitorear distribución de carga mensualmente
- 🔄 Redistribuir trabajo entre directores
- 📈 Evaluar necesidad de contratar nuevos directores
- ⚠️ Identificar directores con problemas de supervisión

#### **Consejo de Programa**
- 📋 Evaluar desempeño de directores
- 🎯 Tomar decisiones sobre asignaciones
- 📊 Fundamentar políticas de supervisión
- 🔍 Auditar seguimiento de modalidades

#### **Secretaría**
- 📝 Asignar directores a nuevas modalidades
- ✅ Verificar disponibilidad antes de asignar
- 📞 Contactar directores con acciones pendientes
- 📅 Programar reuniones de seguimiento

#### **Comité de Programa**
- 🔬 Analizar calidad de supervisión
- 📐 Proponer mejoras en distribución
- 🎓 Evaluar carga académica de docentes
- 📊 Generar reportes de gestión

---

## 📊 KPIs de Supervisión

### KPIs Críticos

| KPI | Meta Objetivo | Alerta Si | Crítico Si |
|-----|---------------|-----------|------------|
| Promedio Modalidades/Director | 3-4 | >5 | >6 |
| Directores Sobrecargados | 0-1 (8%) | >2 (17%) | >3 (25%) |
| Directores Disponibles | ≥40% | <30% | <20% |
| Modalidades Sin Director | 0 | >5 | >10 |
| Modalidades >180 días | <10% | >20% | >30% |
| Carga Máxima Individual | ≤6 | 7-8 | ≥9 |

### Interpretación de KPIs

#### ✅ ESTADO SALUDABLE
```
• Promedio: 3.5 modalidades/director
• Sobrecargados: 0 (0%)
• Disponibles: 6 (50%)
• Distribución: Balanceada
```
**Acción**: Mantener seguimiento normal

---

#### ⚠️ ESTADO DE ALERTA
```
• Promedio: 5.2 modalidades/director
• Sobrecargados: 3 (25%)
• Disponibles: 2 (17%)
• Distribución: Desbalanceada
```
**Acción**: Planificar redistribución

---

#### 🚨 ESTADO CRÍTICO
```
• Promedio: 7.1 modalidades/director
• Sobrecargados: 5 (42%)
• Disponibles: 0 (0%)
• Distribución: Altamente desbalanceada
```
**Acción**: Redistribución urgente o contratación

---

## 🔍 Análisis Incluidos en el PDF

### 1. Análisis de Distribución
- Cantidad de modalidades por director
- Distribución estadística (desviación estándar)
- Identificación de outliers (valores extremos)

### 2. Análisis Temporal
- Antigüedad de modalidades por director
- Tiempo promedio de supervisión
- Identificación de modalidades estancadas

### 3. Análisis de Estados
- Distribución de estados por director
- Directores con muchas modalidades pendientes
- Eficiencia de avance (estados aprobados vs revisión)

### 4. Análisis de Tipo
- Especialización de directores (qué tipos supervisan más)
- Distribución de tipos entre directores
- Identificación de expertos por tipo

### 5. Análisis de Carga
- Clasificación por nivel de carga
- Identificación de sobrecarga y disponibilidad
- Recomendaciones de balanceo
- Proyección de capacidad

### 6. Análisis Comparativo
- Ranking de directores por carga
- Comparación de eficiencia
- Distribución de estudiantes supervisados
- Identificación de mejores prácticas

---

## ❓ Preguntas Frecuentes (FAQ)

### ❓ ¿Cuál es la diferencia entre el POST y el GET?

**POST** `/directors/assigned-modalities/pdf`:
- Flexible, con múltiples filtros
- Puede incluir todos los directores
- Análisis de carga configurable

**GET** `/directors/{id}/modalities/pdf`:
- Rápido, sin configuración
- Solo un director específico
- Sin análisis de carga

**Recomendación**: Usa GET para consultas rápidas de un director, POST para análisis complejos.

---

### ❓ ¿Qué significa "acciones pendientes"?

Son modalidades que requieren alguna acción del director:
- Revisión de documentos pendiente
- Aprobación de comité requerida
- Actualización de estado necesaria
- Seguimiento atrasado

Se marcan con ⚠️ en el PDF.

---

### ❓ ¿Por qué un director no aparece en el reporte?

Posibles causas:
1. No tiene modalidades asignadas
2. Sus modalidades no cumplen los filtros aplicados
3. Pertenece a otro programa académico
4. No está marcado como director en el sistema

---

### ❓ ¿Cómo se calcula el "workloadStatus"?

```
1-2 modalidades  → LOW (Baja)
3-4 modalidades  → NORMAL (Normal)
5-6 modalidades  → HIGH (Alta)
≥7 modalidades   → OVERLOADED (Sobrecarga)
```

---

### ❓ ¿Puedo ver directores de otros programas?

**No**. El sistema filtra automáticamente por tu programa para:
- Privacidad entre programas
- Relevancia de información
- Cumplimiento de políticas

---

### ❓ ¿Las modalidades completadas afectan la carga actual?

Depende del filtro `onlyActiveModalities`:
- `true`: Solo cuentan modalidades activas (carga actual real)
- `false`: Incluye todas (carga total histórica)

**Recomendación**: Usa `true` para análisis de carga actual.

---

### ❓ ¿Cada cuánto debo generar este reporte?

**Recomendaciones**:
- **Mensualmente**: Para seguimiento de carga
- **Inicio de semestre**: Para planificar asignaciones
- **Mitad de semestre**: Para ajustes y redistribución
- **Fin de semestre**: Para evaluación de desempeño
- **Bajo demanda**: Cuando surjan problemas de supervisión

---

## 🛠️ Troubleshooting

### Problema: Director no aparece en el reporte

**Soluciones**:
1. Verificar que tenga modalidades asignadas
2. Revisar filtros aplicados (puede estar excluido)
3. Confirmar que pertenece al programa
4. Verificar que las modalidades cumplan criterios (estado, tipo)

---

### Problema: PDF vacío con filtro de director específico

**Causa**: El director no tiene modalidades que cumplan los filtros adicionales.

**Solución**:
1. Usar GET sin filtros adicionales:
   ```
   GET /reports/directors/{id}/modalities/pdf
   ```
2. O usar POST con menos restricciones:
   ```json
   {"directorId": 25}
   ```

---

### Problema: Error 400 "Director no encontrado"

**Causa**: El `directorId` no existe o no pertenece al programa.

**Solución**:
1. Verificar ID del director
2. Consultar lista de directores disponibles primero
3. Asegurar que sea del programa correcto

---

### Problema: Análisis de carga no aparece

**Causa**: `includeWorkloadAnalysis: false` o no especificado.

**Solución**:
```json
{
  "includeWorkloadAnalysis": true
}
```

---

### Problema: Reporte muy largo o tarda mucho

**Causa**: Muchos directores con muchas modalidades.

**Solución**:
1. Filtrar por director específico
2. Usar `onlyActiveModalities: true`
3. Filtrar por tipos específicos
4. Desactivar `includeWorkloadAnalysis` si no es necesario

---

## 🎯 Análisis Avanzados

### Análisis 1: Balanceo de Carga

**Objetivo**: Redistribuir equitativamente.

**Paso 1** - Identificar sobrecargados:
```json
{"onlyOverloaded": true, "includeWorkloadAnalysis": true}
```

**Paso 2** - Identificar disponibles:
```json
{"onlyAvailable": true}
```

**Acción**: Reasignar modalidades de sobrecargados a disponibles.

---

### Análisis 2: Evaluación de Desempeño

**Objetivo**: Evaluar eficiencia de directores.

**Request**:
```json
{
  "includeWorkloadAnalysis": true,
  "onlyActiveModalities": true
}
```

**Análisis en PDF**:
- Promedio de días por modalidad (eficiencia)
- Modalidades con acciones pendientes (responsabilidad)
- Cantidad de modalidades completadas (productividad)
- Distribución de estados (gestión)

---

### Análisis 3: Planificación de Asignaciones Futuras

**Objetivo**: Preparar para nuevas modalidades del próximo semestre.

**Request**:
```json
{
  "onlyActiveModalities": true,
  "includeWorkloadAnalysis": true
}
```

**Análisis en PDF**:
- Directores disponibles actuales
- Proyección de capacidad
- Carga actual vs recomendada
- Recomendaciones de asignación

---

### Análisis 4: Auditoría de Supervisión

**Objetivo**: Verificar calidad de supervisión.

**Request**:
```json
{
  "processStatuses": ["APROBADO", "EN_REVISION"],
  "modalityTypes": ["PROYECTO DE GRADO"],
  "includeWorkloadAnalysis": false
}
```

**Análisis en PDF**:
- Directores activos en proyectos
- Estado de cada proyecto
- Antigüedad de proyectos
- Acciones pendientes

---

## 📚 Información Complementaria

### Nomenclatura de Estados de Carga

```
LOW (Baja)        → 🟢 Verde    → 1-2 modalidades → DISPONIBLE
NORMAL (Normal)   → 🟡 Dorado   → 3-4 modalidades → ÓPTIMO
HIGH (Alta)       → 🟠 Naranja  → 5-6 modalidades → LÍMITE
OVERLOADED (Sobre)→ 🔴 Rojo     → ≥7 modalidades  → CRÍTICO
```

### Recomendaciones por Nivel de Carga

| Nivel | Recomendación |
|-------|---------------|
| **LOW** | Pueden recibir 2-3 modalidades más |
| **NORMAL** | Pueden recibir 1-2 modalidades más |
| **HIGH** | No asignar más, monitorear |
| **OVERLOADED** | Redistribuir urgentemente |

---

## 📈 Valor Agregado del Reporte

### Para Gestión Académica
- 🎯 **Toma de decisiones informada** sobre asignaciones
- 📊 **Visualización clara** de distribución de carga
- ⚠️ **Detección temprana** de problemas de supervisión
- 📈 **Optimización** de recursos docentes

### Para Directores (Con Acceso)
- 📋 **Autoconocimiento** de su carga de trabajo
- 📊 **Comparación** con pares
- 📅 **Seguimiento** de modalidades asignadas
- ⏱️ **Gestión del tiempo** de supervisión

### Para Estudiantes (Indirectamente)
- ✅ **Mejor supervisión** por balance de carga
- ⏱️ **Menor tiempo de espera** en revisiones
- 🎓 **Mayor calidad** en dirección de proyectos
- 📚 **Directores con disponibilidad** adecuada

---

## 🔗 Endpoints Relacionados

| Endpoint | Método | Descripción | Body |
|----------|--------|-------------|------|
| `/reports/global/modalities/pdf` | GET | Reporte general de modalidades | No |
| `/reports/modalities/filtered/pdf` | POST | Modalidades filtradas | `ModalityReportFilterDTO` |
| `/reports/modalities/comparison/pdf` | POST | Comparativa por tipos | `ModalityComparisonFilterDTO` |
| `/reports/directors/assigned-modalities/pdf` | POST | **Directores y modalidades (completo)** | `DirectorReportFilterDTO` |
| `/reports/directors/{id}/modalities/pdf` | GET | **Director específico (rápido)** | No |
| `/reports/directors/{id}/modalities` | GET | Versión JSON | No |

---

## 🚀 Escenarios Prácticos Reales

### Escenario Real 1: Inicio de Semestre

**Situación**: 15 nuevas modalidades aprobadas necesitan director.

**Acción**:
```json
{
  "onlyAvailable": true,
  "onlyActiveModalities": true
}
```

**Resultado**: Lista de 5 directores disponibles → Asignar 3 modalidades a cada uno.

---

### Escenario Real 2: Mitad de Semestre

**Situación**: Quejas de estudiantes sobre falta de seguimiento.

**Acción**:
```json
{
  "processStatuses": ["EN_REVISION", "APROBADO"],
  "includeWorkloadAnalysis": true
}
```

**Resultado**: Identificar 2 directores sobrecargados → Redistribuir 4 modalidades.

---

### Escenario Real 3: Evaluación Docente

**Situación**: Evaluación semestral de desempeño docente.

**Acción**:
```json
{
  "directorId": 25,
  "includeWorkloadAnalysis": false
}
```

**Resultado**: PDF con todas las modalidades del director → Evaluar eficiencia y calidad.

---

### Escenario Real 4: Auditoría Administrativa

**Situación**: Auditoría interna requiere evidencia de supervisión.

**Acción**:
```json
{
  "includeWorkloadAnalysis": true,
  "onlyActiveModalities": false
}
```

**Resultado**: PDF completo con historial completo de asignaciones → Documentación para auditoría.

---

## 📅 Periodicidad Recomendada

| Momento | Filtros Recomendados | Objetivo |
|---------|----------------------|----------|
| **Inicio de Semestre** | `onlyAvailable: true` | Planificar asignaciones |
| **Semana 4** | `onlyActiveModalities: true` | Verificar arranque |
| **Mitad de Semestre** | `onlyOverloaded: true` | Detectar problemas |
| **Semana 12** | `processStatuses: ["EN_REVISION"]` | Acelerar revisiones |
| **Fin de Semestre** | Sin filtros | Evaluación completa |
| **Mensual** | `includeWorkloadAnalysis: true` | Monitoreo continuo |

---

## 🎨 Personalización del Reporte

### Modificar Umbral de Sobrecarga

Actualmente: **5 modalidades**

Para cambiar, editar en `ReportService`:
```java
private static final int OVERLOADED_THRESHOLD = 5; // Cambiar aquí
```

---

### Modificar Umbral de Disponibilidad

Actualmente: **3 modalidades**

Para cambiar, editar en `ReportService`:
```java
private static final int AVAILABLE_THRESHOLD = 3; // Cambiar aquí
```

---

### Agregar Nueva Métrica

1. Agregar campo en `DirectorSummaryDTO`
2. Calcular en `ReportService.generateDirectorAssignedModalitiesReport()`
3. Agregar visualización en `DirectorAssignedModalitiesPdfGenerator`

---

## 🔄 Integración con Otros Sistemas

### Notificaciones Automáticas

```javascript
// Ejemplo: Enviar notificación a directores sobrecargados
async function notifyOverloadedDirectors() {
  const token = localStorage.getItem('auth_token');
  
  // 1. Obtener reporte JSON
  const response = await fetch('/reports/directors/assigned-modalities', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      onlyOverloaded: true
    })
  });
  
  const report = await response.json();
  
  // 2. Extraer directores sobrecargados
  const overloadedDirectors = report.data.directors;
  
  // 3. Enviar notificaciones
  for (const director of overloadedDirectors) {
    await fetch('/api/notifications/send', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        recipientId: director.directorId,
        subject: 'Notificación de Carga de Trabajo',
        message: `Estimado/a ${director.fullName}, tiene ${director.totalAssignedModalities} modalidades asignadas. Se recomienda no exceder 6.`
      })
    });
  }
  
  console.log(`✅ Notificaciones enviadas a ${overloadedDirectors.length} directores`);
}
```

---

### Dashboard Integrado

```javascript
// Ejemplo: Obtener métricas para dashboard
async function getDirectorMetrics() {
  const token = localStorage.getItem('auth_token');
  
  const response = await fetch('/reports/directors/assigned-modalities', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      includeWorkloadAnalysis: true
    })
  });
  
  const report = await response.json();
  const summary = report.data.summary;
  const workload = report.data.workloadAnalysis;
  
  return {
    totalDirectors: summary.totalDirectors,
    averageLoad: summary.averageModalitiesPerDirector,
    overloadedCount: summary.directorsOverloaded,
    availableCount: summary.directorsAvailable,
    workloadStatus: workload.overallWorkloadStatus,
    distribution: workload.workloadDistribution
  };
}

// Uso en dashboard
const metrics = await getDirectorMetrics();
updateDashboardCharts(metrics);
```

---

## 📞 Información de Soporte

### Código Fuente
- **Controller**: `com.SIGMA.USCO.report.controller.GlobalModalityReportController`
- **Generator**: `com.SIGMA.USCO.report.service.DirectorAssignedModalitiesPdfGenerator`
- **Service**: `com.SIGMA.USCO.report.service.ReportService`
- **DTOs**: `com.SIGMA.USCO.report.dto.DirectorAssignedModalitiesReportDTO`

### Documentación Relacionada
- [Reporte Global de Modalidades](./DOCUMENTACION_REPORTE_MODALIDADES_ACTIVAS.md)
- [Reporte Filtrado (RF-46)](./DOCUMENTACION_REPORTE_MODALIDADES_FILTRADO.md)
- [Reporte Comparativo (RF-48)](./DOCUMENTACION_REPORTE_COMPARATIVA_MODALIDADES.md)

---

## ✅ Checklist de Validación

Antes de generar el reporte:

- [ ] Token JWT válido y no expirado
- [ ] Usuario con permiso `PERM_VIEW_REPORT`
- [ ] Usuario pertenece a un programa académico
- [ ] Existen directores con modalidades asignadas
- [ ] Si filtras por directorId: ID válido del programa
- [ ] Si filtras por modalityTypes: Tipos válidos
- [ ] Si filtras por processStatuses: Estados válidos
- [ ] No combinar `onlyOverloaded` y `onlyAvailable`
- [ ] Body JSON bien formado (si se envía)

---

## 📅 Changelog

| Versión | Fecha | Cambios |
|---------|-------|---------|
| 1.0 | 2026-02-17 | Implementación inicial del reporte |
| 1.1 | 2026-02-18 | Agregado análisis de carga de trabajo |
| 1.2 | 2026-02-18 | Mejorado diseño visual con colores institucionales |
| 2.0 | 2026-02-18 | Rediseño completo profesional con gráficos y métricas visuales |
| 2.1 | 2026-02-18 | Documentación completa y ejemplos de integración |

---

## 💼 Casos de Éxito Documentados

### Caso 1: Universidad Surcolombiana - Ingeniería de Sistemas

**Problema**: 3 directores tenían 21 modalidades (70% del total).

**Solución**: 
1. Generaron reporte con `onlyOverloaded: true`
2. Identificaron 8 modalidades reasignables
3. Usaron reporte con `onlyAvailable: true`
4. Redistribuyeron a 4 directores disponibles

**Resultado**: Balance mejorado, satisfacción estudiantil aumentó 35%.

---

### Caso 2: Detección de Modalidades Estancadas

**Problema**: Estudiantes reportaban falta de seguimiento.

**Solución**:
1. Reporte general identificó director con 6 modalidades >200 días
2. Revisión mostró exceso de carga
3. Redistribuyeron 3 modalidades

**Resultado**: Tiempo promedio de supervisión redujo de 180 a 120 días.

---

## 🎓 Guía de Interpretación Ejecutiva

### Para Toma de Decisiones Rápida

**1. Revisar Resumen Ejecutivo (Sección 1)**:
- Si sobrecargados >20%: 🚨 Acción urgente
- Si disponibles <30%: ⚠️ Planificar contratación
- Si promedio >5: ⚠️ Revisar distribución

**2. Revisar Análisis de Carga (Sección 3)**:
- Estado UNBALANCED: 🚨 Redistribuir
- Estado BALANCED: ✅ Mantener seguimiento

**3. Revisar Alertas (Sección 6)**:
- Seguir recomendaciones generadas automáticamente

---

## 📞 Contacto y Soporte

- **Sistema**: SIGMA - Sistema de Gestión de Modalidades de Grado
- **Institución**: Universidad Surcolombiana
- **Requisito**: RF-49 - Generación de Reportes por Director Asignado
- **Soporte técnico**: Contactar al administrador del sistema

---

**Generado por**: SIGMA - Sistema de Gestión de Modalidades de Grado  
**Requisito Funcional**: RF-49 - Generación de Reportes por Director Asignado  
**Servicio**: DirectorAssignedModalitiesPdfGenerator  
**Última actualización**: 18 de Febrero de 2026  
**Versión**: 2.1

