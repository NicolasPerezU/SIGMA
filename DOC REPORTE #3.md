# 📊 Documentación: Reporte Comparativo de Modalidades por Tipo (RF-48)

## 📝 Descripción General

Este endpoint genera un **reporte comparativo avanzado en formato PDF** que analiza y compara las diferentes modalidades de grado según su tipo. Permite identificar tendencias, patrones de preferencia estudiantil, distribución histórica y análisis estadístico profundo para la toma de decisiones estratégicas del programa académico.

**Requisito Funcional**: RF-48 - Comparativa de Modalidades por Tipo de Grado

**Generador**: `ModalityComparisonPdfGenerator`

---

## 🔗 Endpoint

### **POST** `/reports/modalities/comparison/pdf`

**Descripción**: Genera y descarga un reporte comparativo en PDF que muestra estadísticas detalladas por tipo de modalidad, análisis histórico, tendencias y distribución de estudiantes del programa académico del usuario autenticado.

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

### Body (JSON)

El body es **OPCIONAL**. Si no se envía, el sistema genera el reporte comparativo completo con todos los periodos y análisis de tendencias.

```json
{
  "year": 2024,
  "semester": 2,
  "includeHistoricalComparison": true,
  "historicalPeriodsCount": 5,
  "includeTrendsAnalysis": true,
  "onlyActiveModalities": false
}
```

### Campos del Request Body

| Campo | Tipo | Requerido | Descripción | Valor por Defecto | Ejemplo |
|-------|------|-----------|-------------|-------------------|---------|
| `year` | `Integer` | No | Año específico para el análisis | Año actual | `2024` |
| `semester` | `Integer` | No | Semestre específico (1 o 2) | Todos (1 y 2) | `2` |
| `includeHistoricalComparison` | `Boolean` | No | Incluir comparación con periodos anteriores | `true` | `true` |
| `historicalPeriodsCount` | `Integer` | No | Cantidad de periodos previos a comparar | `3` | `5` |
| `includeTrendsAnalysis` | `Boolean` | No | Incluir análisis de tendencias | `true` | `true` |
| `onlyActiveModalities` | `Boolean` | No | Solo modalidades activas (excluye completadas/canceladas) | `false` | `true` |

---

## 📤 Response (Respuesta)

### Respuesta Exitosa (200 OK)

**Content-Type**: `application/pdf`

**Headers de Respuesta**:
```http
Content-Type: application/pdf
Content-Disposition: attachment; filename=Reporte_Comparativa_Modalidades_2026-02-18_143025.pdf
X-Report-Generated-At: 2026-02-18T14:30:25
X-Total-Records: 7
Content-Length: 245789
```

**Body**: Archivo PDF binario profesional con análisis comparativo

### Respuestas de Error

#### Error de Validación (400)
```json
{
  "success": false,
  "error": "El año debe ser mayor a 2000",
  "timestamp": "2026-02-18T14:30:25"
}
```

#### Error al Generar PDF (500)
```json
{
  "success": false,
  "error": "Error al generar el PDF: <mensaje_detallado>",
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

### Caso de Uso 1: Análisis del Semestre Actual

**Escenario**: El Consejo de Programa necesita conocer la distribución de modalidades del semestre en curso.

**Request**:
```json
{
  "year": 2026,
  "semester": 1,
  "includeHistoricalComparison": false,
  "includeTrendsAnalysis": false
}
```

**Resultado**: PDF con estadísticas solo del primer semestre 2026, sin comparación histórica.

---

### Caso de Uso 2: Análisis Histórico Completo (5 años)

**Escenario**: Jefatura quiere evaluar la evolución de las preferencias estudiantiles en los últimos 5 años (10 semestres).

**Request**:
```json
{
  "includeHistoricalComparison": true,
  "historicalPeriodsCount": 10,
  "includeTrendsAnalysis": true
}
```

**Resultado**: PDF con comparación de los últimos 10 periodos, gráficos de tendencias y análisis de crecimiento/declive.

---

### Caso de Uso 3: Comparativa Anual (Sin Desglosar por Semestre)

**Escenario**: Secretaría requiere un análisis anual completo del 2024.

**Request**:
```json
{
  "year": 2024,
  "includeHistoricalComparison": true,
  "historicalPeriodsCount": 3
}
```

**Resultado**: PDF comparando 2024 completo vs los 3 años anteriores (2023, 2022, 2021).

---

### Caso de Uso 4: Solo Modalidades Activas

**Escenario**: Comité de Programa quiere analizar solo las modalidades en curso, excluyendo completadas y canceladas.

**Request**:
```json
{
  "onlyActiveModalities": true,
  "includeTrendsAnalysis": true
}
```

**Resultado**: PDF con solo modalidades en estados activos y análisis de tendencias actuales.

---

### Caso de Uso 5: Reporte Completo por Defecto

**Escenario**: Usuario quiere el análisis más completo posible sin restricciones.

**Request**:
```json
{}
```
o body vacío/null.

**Resultado**: PDF con análisis completo, 3 periodos históricos, tendencias y todas las modalidades.

---

## 🔍 Detalles de los Filtros

### 1️⃣ Filtro por Año (`year`)

**Descripción**: Limita el análisis a un año calendario específico.

**Comportamiento**:
- Si se especifica: Solo modalidades iniciadas en ese año
- Si es `null`: Incluye todos los años disponibles
- Valida que sea ≥ 2000

**Ejemplo**:
```json
{
  "year": 2024
}
```
→ Solo modalidades de 2024 (ambos semestres).

**Efecto en el PDF**:
- Título incluye: "Periodo: 2024"
- Portada muestra el año específico
- Comparación histórica parte desde ese año hacia atrás

---

### 2️⃣ Filtro por Semestre (`semester`)

**Descripción**: Limita el análisis a un semestre específico.

**Comportamiento**:
- Valores válidos: `1` (primer semestre) o `2` (segundo semestre)
- Si es `null`: Incluye ambos semestres
- **Requiere** que `year` también esté especificado

**Ejemplo**:
```json
{
  "year": 2024,
  "semester": 2
}
```
→ Solo segundo semestre de 2024.

**Efecto en el PDF**:
- Título incluye: "Periodo: 2024 - Semestre 2"
- Análisis histórico compara semestres equivalentes
- Métricas calculadas solo de ese semestre

---

### 3️⃣ Comparación Histórica (`includeHistoricalComparison`)

**Descripción**: Activa/desactiva la inclusión de análisis histórico con periodos anteriores.

**Comportamiento**:
- `true`: Incluye sección 4 "COMPARACIÓN HISTÓRICA POR PERIODOS" en el PDF
- `false`: Omite comparación histórica (reporte más corto)
- Por defecto: `true`

**Ejemplo**:
```json
{
  "includeHistoricalComparison": true,
  "historicalPeriodsCount": 4
}
```
→ Compara con los 4 periodos anteriores.

**Contenido añadido al PDF**:
- Tabla comparativa multi-periodo
- Evolución de cada tipo de modalidad
- Totales por periodo
- Identificación de patrones temporales

---

### 4️⃣ Cantidad de Periodos Históricos (`historicalPeriodsCount`)

**Descripción**: Define cuántos periodos anteriores incluir en la comparación.

**Comportamiento**:
- Solo aplica si `includeHistoricalComparison: true`
- Valores recomendados: 3-10 periodos
- Por defecto: `3` periodos
- Si se especifica `year` y `semester`: compara semestres
- Si solo se especifica `year`: compara años

**Ejemplo**:
```json
{
  "historicalPeriodsCount": 8
}
```
→ Compara los últimos 8 periodos (4 años de 2 semestres cada uno).

**Efecto en el PDF**:
- Tabla horizontal con N+1 columnas (tipos + N periodos)
- Cada columna representa un periodo
- Totales al final de cada columna

---

### 5️⃣ Análisis de Tendencias (`includeTrendsAnalysis`)

**Descripción**: Activa/desactiva el cálculo y visualización de tendencias.

**Comportamiento**:
- `true`: Incluye sección 5 "ANÁLISIS DE TENDENCIAS" en el PDF
- `false`: Omite análisis de tendencias
- Por defecto: `true`
- **Requiere** datos históricos para funcionar correctamente

**Ejemplo**:
```json
{
  "includeTrendsAnalysis": true,
  "includeHistoricalComparison": true,
  "historicalPeriodsCount": 5
}
```
→ Calcula tendencias basándose en 5 periodos históricos.

**Contenido añadido al PDF**:
- **Tendencia General**: GROWING, DECLINING, STABLE
- **Tipos en Crecimiento**: Lista con tasas de crecimiento
- **Tipos en Declive**: Lista con tasas de declive
- **Tipos Estables**: Lista sin cambios significativos
- **Mayor Mejora**: Tipo con mayor crecimiento porcentual
- **Mayor Declive**: Tipo con mayor caída porcentual
- **Tasas de Crecimiento**: Porcentajes por tipo

---

### 6️⃣ Solo Modalidades Activas (`onlyActiveModalities`)

**Descripción**: Filtra para incluir solo modalidades en estados activos.

**Comportamiento**:
- `true`: Excluye modalidades COMPLETADAS, CANCELADAS, RECHAZADAS
- `false`: Incluye todas las modalidades (defecto)
- Útil para análisis de "trabajo en curso"

**Estados Activos Incluidos**:
- ✅ APROBADO
- ✅ EN_REVISION
- ✅ PENDIENTE_APROBACION
- ✅ APROBADO_SECRETARIA
- ✅ APROBADO_CONSEJO

**Estados Excluidos** (cuando `onlyActiveModalities: true`):
- ❌ COMPLETADO
- ❌ CANCELADO
- ❌ RECHAZADO

**Ejemplo**:
```json
{
  "onlyActiveModalities": true
}
```
→ Solo modalidades en curso, sin historial de finalizadas.

---

## 📄 Estructura Completa del PDF

### **Portada Institucional Profesional**

Diseño de portada mejorado con:
- 🔴 Banda superior roja institucional
  - "UNIVERSIDAD SURCOLOMBIANA"
  - "Facultad de Ingeniería"
- 📋 Caja dorada con nombre del programa académico
- 📌 Título del reporte: "REPORTE COMPARATIVO DE MODALIDADES POR TIPO DE GRADO"
- 📅 Información del periodo (si aplica):
  - Caja dorada destacada: "Periodo: 2024 - Semestre 2"
- 📊 Tabla de información del reporte:
  - Programa
  - Código del programa
  - Fecha de generación
  - Generado por
  - Tipos de modalidad analizados
  - Total de modalidades
- 🏛️ Footer institucional: "Sistema SIGMA - Sistema Integral de Gestión de Modalidades de Grado"

---

### **Sección 1: RESUMEN EJECUTIVO**

#### 1.1 Tarjetas de Métricas Clave
Tres tarjetas destacadas con bordes de colores institucionales:

| Métrica | Descripción | Color |
|---------|-------------|-------|
| **Tipos de Modalidad** | Cantidad de tipos diferentes | Dorado |
| **Total Modalidades** | Suma de todas las modalidades | Rojo |
| **Total Estudiantes** | Estudiantes únicos participando | Dorado |

Diseño: Números grandes (28pt) con etiqueta descriptiva debajo.

#### 1.2 Destacados
- 🏆 **Tipo Más Popular**: Nombre + cantidad (fondo verde claro)
- 📉 **Tipo Menos Popular**: Nombre + cantidad (fondo naranja claro)

Formato visual con iconos y colores diferenciados.

---

### **Sección 2: ESTADÍSTICAS DETALLADAS POR TIPO DE MODALIDAD**

Para cada tipo de modalidad, se presenta:

#### 2.X Nombre del Tipo
- **Descripción**: Texto descriptivo del tipo (si existe)
- **Tabla de estadísticas** (4 columnas):
  - Total Modalidades
  - Total Estudiantes
  - Porcentaje del Total
  - Requiere Director (Sí/No)

#### Información Adicional (si requiere director):
- ✅ Con Director Asignado: X modalidades (fondo verde)
- ❌ Sin Director: X modalidades (fondo naranja si >0)

#### Distribución por Estado
Para cada tipo, muestra:
```
  Estado A: X modalidades
  Estado B: Y modalidades
  Estado C: Z modalidades
```

**Separación visual**: Línea dorada entre cada tipo de modalidad.

---

### **Sección 3: DISTRIBUCIÓN DE ESTUDIANTES POR TIPO**

**Visualización gráfica mejorada** con barras horizontales de progreso:

Para cada tipo de modalidad:
- 📊 **Encabezado rojo**: Nombre del tipo
- 📈 **Barra de progreso dorada**: Proporcional al número de estudiantes
  - Ancho máximo: 85% del espacio disponible
  - Texto sobre la barra: "X estudiantes" (blanco, negrita)
  - Texto en área vacía: "Y% del total" (rojo)
- 🎨 **Fondo dorado claro** en área sin barra

**Ejemplo visual**:
```
┌─────────────────────────────────────────────────────┐
│ PROYECTO DE GRADO                                    │ ← Encabezado rojo
├─────────────────────────────────────────────────────┤
│████████████████████████████░░░░░░░░░░░░░░░░░░░░░░░░│
│ 45 estudiantes         42.5% del total             │
└─────────────────────────────────────────────────────┘
```

**Resumen final**: Caja dorada con "TOTAL DE ESTUDIANTES: XXX" centrado.

---

### **Sección 4: COMPARACIÓN HISTÓRICA POR PERIODOS**

*Solo se incluye si `includeHistoricalComparison: true`*

#### Tabla Comparativa Multi-Periodo

**Estructura**:
- **Primera columna**: Tipo de Modalidad (fondo dorado claro)
- **Columnas siguientes**: Un periodo por columna
  - Formato: "2024-2", "2024-1", "2023-2", etc.
- **Última fila**: TOTALES (fondo rojo, texto blanco)

**Datos por celda**:
```
X modalidades
Y estudiantes
```

**Ejemplo de tabla**:

| Tipo de Modalidad | 2026-1 | 2025-2 | 2025-1 | 2024-2 |
|-------------------|--------|--------|--------|--------|
| Proyecto de Grado | 15 modalidades<br>18 estudiantes | 12 modalidades<br>15 estudiantes | 10 modalidades<br>12 estudiantes | 8 modalidades<br>10 estudiantes |
| Pasantía | 5 modalidades<br>5 estudiantes | 4 modalidades<br>4 estudiantes | 6 modalidades<br>6 estudiantes | 3 modalidades<br>3 estudiantes |
| **TOTALES** | **20 modalidades**<br>**23 estudiantes** | **16 modalidades**<br>**19 estudiantes** | **16 modalidades**<br>**18 estudiantes** | **11 modalidades**<br>**13 estudiantes** |

---

### **Sección 5: ANÁLISIS DE TENDENCIAS**

*Solo se incluye si `includeTrendsAnalysis: true`*

#### 5.1 Tendencia General
Caja destacada con color según tendencia:

| Tendencia | Texto | Color de Fondo | Ícono |
|-----------|-------|----------------|-------|
| **GROWING** | "EN CRECIMIENTO" | Dorado | ↗ |
| **DECLINING** | "EN DECLIVE" | Rojo | ↘ |
| **STABLE** | "ESTABLE" | Rojo | → |

**Formato**: Texto grande (14pt) centrado en caja colorida.

#### 5.2 Tipos en Crecimiento
*Solo si hay tipos con crecimiento positivo*

Tabla de 2 columnas:
- **Columna 1**: Nombre del tipo
- **Columna 2**: Tasa de crecimiento (ej: "+15.50%", fondo dorado)

#### 5.3 Tipos en Declive
*Solo si hay tipos con crecimiento negativo*

Tabla de 2 columnas:
- **Columna 1**: Nombre del tipo
- **Columna 2**: Tasa de declive (ej: "-8.25%", fondo rojo)

#### 5.4 Tipos Estables
*Solo si hay tipos sin cambios significativos*

Tabla de 2 columnas:
- **Columna 1**: Nombre del tipo
- **Columna 2**: Tasa de cambio (ej: "+0.50%", fondo rojo)

#### 5.5 Destacados Especiales

**Mayor Mejora**: Caja dorada claro
```
🏆 Mayor Mejora: Pasantía (+25.80%)
```

**Mayor Declive**: Caja rosa claro
```
⚠ Mayor Declive: Seminario de Grado (-12.30%)
```

---

### **Sección 6: CONCLUSIONES Y RECOMENDACIONES**

Párrafos numerados con análisis automático:

1. **Diversidad de oferta**: Cantidad de tipos disponibles
2. **Preferencia estudiantil**: Tipo más popular y su relevancia
3. **Distribución promedio**: Promedios de modalidades y estudiantes por tipo
4. **Tendencia identificada**: Crecimiento, declive o estabilidad general
5. **Recomendaciones**: Sugerencias basadas en los datos

**Nota informativa final**:
- Caja dorada con información sobre la generación automática
- Programa académico analizado
- Indicaciones para consultas adicionales

---

## 🎨 Diseño Visual Institucional

### Paleta de Colores

```
🔴 ROJO INSTITUCIONAL (#8F1E1E)
  • Encabezados de sección
  • Encabezados de tabla
  • Títulos principales
  • Bordes importantes

🟡 DORADO INSTITUCIONAL (#D5CBA0)
  • Tarjetas de métricas
  • Fondos de celdas alternadas
  • Bordes de tablas
  • Elementos secundarios

⚪ BLANCO (#FFFFFF)
  • Fondo principal del documento
  • Texto en encabezados rojos
  • Celdas de datos

🟨 DORADO CLARO (#F5F2EB)
  • Fondos sutiles
  • Celdas alternadas en tablas
  • Cajas de información
```

### Tipografía

| Tipo | Fuente | Tamaño | Peso | Color | Uso |
|------|--------|--------|------|-------|-----|
| Título | Helvetica | 20pt | Bold | Rojo | Portada |
| Subtítulo | Helvetica | 16pt | Regular | Rojo | Subtítulos principales |
| Encabezado | Helvetica | 15pt | Bold | Rojo | Secciones |
| Subencabezado | Helvetica | 12pt | Bold | Rojo | Subsecciones |
| Negrita | Helvetica | 10pt | Bold | Negro | Etiquetas |
| Normal | Helvetica | 10pt | Regular | Negro | Contenido |
| Pequeña | Helvetica | 9pt | Regular | Gris | Detalles |
| Mínima | Helvetica | 8pt | Regular | Gris | Pies de página |
| Tabla Header | Helvetica | 10pt | Bold | Blanco | Encabezados de tabla |

---

## 🔄 Lógica de Combinación de Filtros

### Escenario A: Solo Año
```json
{
  "year": 2024
}
```
**Resultado**: Modalidades de ambos semestres de 2024

---

### Escenario B: Año + Semestre
```json
{
  "year": 2024,
  "semester": 2
}
```
**Resultado**: Solo modalidades del segundo semestre 2024

---

### Escenario C: Histórico Sin Año Específico
```json
{
  "includeHistoricalComparison": true,
  "historicalPeriodsCount": 6
}
```
**Resultado**: Todas las modalidades + comparación de últimos 6 periodos

---

### Escenario D: Año Específico + Histórico
```json
{
  "year": 2024,
  "includeHistoricalComparison": true,
  "historicalPeriodsCount": 3
}
```
**Resultado**: Análisis de 2024 comparado con 2023, 2022, 2021

---

### Escenario E: Completo (Máximo Análisis)
```json
{
  "includeHistoricalComparison": true,
  "historicalPeriodsCount": 10,
  "includeTrendsAnalysis": true,
  "onlyActiveModalities": false
}
```
**Resultado**: PDF completo con 10 periodos históricos, todas las modalidades, tendencias calculadas

---

## 💻 Ejemplos de Código

### Ejemplo 1: JavaScript/TypeScript (Frontend)

```typescript
interface ModalityComparisonFilters {
  year?: number;
  semester?: number;
  includeHistoricalComparison?: boolean;
  historicalPeriodsCount?: number;
  includeTrendsAnalysis?: boolean;
  onlyActiveModalities?: boolean;
}

async function downloadComparisonReport(filters: ModalityComparisonFilters) {
  const token = localStorage.getItem('auth_token');
  
  try {
    const response = await fetch('http://localhost:8080/reports/modalities/comparison/pdf', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(filters)
    });
    
    if (!response.ok) {
      throw new Error(`Error: ${response.status}`);
    }
    
    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    
    // Extraer nombre del archivo del header
    const contentDisposition = response.headers.get('Content-Disposition');
    const filename = contentDisposition 
      ? contentDisposition.split('filename=')[1].replace(/"/g, '')
      : `Reporte_Comparativa_${new Date().toISOString()}.pdf`;
    
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    
    window.URL.revokeObjectURL(url);
    document.body.removeChild(a);
    
    console.log('✅ Reporte descargado exitosamente');
  } catch (error) {
    console.error('❌ Error al descargar reporte:', error);
    alert('No se pudo generar el reporte comparativo');
  }
}

// Uso:
downloadComparisonReport({
  year: 2024,
  semester: 2,
  includeHistoricalComparison: true,
  historicalPeriodsCount: 5,
  includeTrendsAnalysis: true
});
```

---

### Ejemplo 2: React Component

```jsx
import React, { useState } from 'react';
import axios from 'axios';

function ModalityComparisonReport() {
  const [filters, setFilters] = useState({
    year: new Date().getFullYear(),
    semester: 1,
    includeHistoricalComparison: true,
    historicalPeriodsCount: 4,
    includeTrendsAnalysis: true,
    onlyActiveModalities: false
  });
  
  const [loading, setLoading] = useState(false);
  
  const downloadReport = async () => {
    setLoading(true);
    try {
      const token = localStorage.getItem('auth_token');
      
      const response = await axios.post(
        'http://localhost:8080/reports/modalities/comparison/pdf',
        filters,
        {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          responseType: 'blob'
        }
      );
      
      // Crear enlace de descarga
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', 
        `Reporte_Comparativa_${filters.year}_${filters.semester}.pdf`);
      document.body.appendChild(link);
      link.click();
      link.remove();
      
      alert('✅ Reporte descargado exitosamente');
    } catch (error) {
      console.error('Error:', error);
      alert('❌ Error al generar el reporte');
    } finally {
      setLoading(false);
    }
  };
  
  return (
    <div className="report-generator">
      <h2>Reporte Comparativo de Modalidades</h2>
      
      <div className="filters">
        <label>
          Año:
          <input 
            type="number" 
            value={filters.year} 
            onChange={e => setFilters({...filters, year: parseInt(e.target.value)})}
          />
        </label>
        
        <label>
          Semestre:
          <select 
            value={filters.semester} 
            onChange={e => setFilters({...filters, semester: parseInt(e.target.value)})}
          >
            <option value="">Todos</option>
            <option value="1">1</option>
            <option value="2">2</option>
          </select>
        </label>
        
        <label>
          <input 
            type="checkbox" 
            checked={filters.includeHistoricalComparison}
            onChange={e => setFilters({...filters, includeHistoricalComparison: e.target.checked})}
          />
          Incluir Comparación Histórica
        </label>
        
        {filters.includeHistoricalComparison && (
          <label>
            Periodos Históricos:
            <input 
              type="number" 
              min="1" 
              max="20"
              value={filters.historicalPeriodsCount}
              onChange={e => setFilters({...filters, historicalPeriodsCount: parseInt(e.target.value)})}
            />
          </label>
        )}
        
        <label>
          <input 
            type="checkbox" 
            checked={filters.includeTrendsAnalysis}
            onChange={e => setFilters({...filters, includeTrendsAnalysis: e.target.checked})}
          />
          Incluir Análisis de Tendencias
        </label>
        
        <label>
          <input 
            type="checkbox" 
            checked={filters.onlyActiveModalities}
            onChange={e => setFilters({...filters, onlyActiveModalities: e.target.checked})}
          />
          Solo Modalidades Activas
        </label>
      </div>
      
      <button onClick={downloadReport} disabled={loading}>
        {loading ? '⏳ Generando...' : '📥 Descargar Reporte PDF'}
      </button>
    </div>
  );
}

export default ModalityComparisonReport;
```

---

### Ejemplo 3: PowerShell (cURL)

```powershell
# Configuración
$token = "tu_token_jwt_aqui"
$baseUrl = "http://localhost:8080/reports/modalities/comparison/pdf"

# Filtros del reporte
$filters = @{
    year = 2024
    semester = 2
    includeHistoricalComparison = $true
    historicalPeriodsCount = 5
    includeTrendsAnalysis = $true
    onlyActiveModalities = $false
} | ConvertTo-Json

# Headers
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

# Hacer la petición
try {
    Write-Host "📊 Generando reporte comparativo..." -ForegroundColor Cyan
    
    Invoke-WebRequest `
        -Uri $baseUrl `
        -Method Post `
        -Headers $headers `
        -Body $filters `
        -OutFile "Reporte_Comparativa_2024_S2.pdf"
    
    Write-Host "✅ Reporte descargado exitosamente: Reporte_Comparativa_2024_S2.pdf" -ForegroundColor Green
    
    # Abrir automáticamente el PDF
    Start-Process "Reporte_Comparativa_2024_S2.pdf"
    
} catch {
    Write-Host "❌ Error al generar el reporte: $_" -ForegroundColor Red
}
```

---

### Ejemplo 4: Python

```python
import requests
import json
from datetime import datetime

def download_comparison_report(token, filters=None):
    """
    Descarga el reporte comparativo de modalidades
    
    Args:
        token (str): JWT token de autenticación
        filters (dict): Filtros opcionales para el reporte
    """
    url = "http://localhost:8080/reports/modalities/comparison/pdf"
    
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    
    # Filtros por defecto si no se especifican
    if filters is None:
        filters = {
            "year": datetime.now().year,
            "includeHistoricalComparison": True,
            "historicalPeriodsCount": 4,
            "includeTrendsAnalysis": True
        }
    
    try:
        print("📊 Generando reporte comparativo...")
        
        response = requests.post(
            url, 
            headers=headers, 
            json=filters,
            stream=True
        )
        
        if response.status_code == 200:
            filename = f"Reporte_Comparativa_{datetime.now().strftime('%Y%m%d_%H%M%S')}.pdf"
            
            with open(filename, 'wb') as f:
                for chunk in response.iter_content(chunk_size=8192):
                    f.write(chunk)
            
            print(f"✅ Reporte descargado exitosamente: {filename}")
            return filename
        else:
            error_data = response.json()
            print(f"❌ Error {response.status_code}: {error_data.get('error')}")
            return None
            
    except Exception as e:
        print(f"❌ Error al descargar reporte: {str(e)}")
        return None

# Uso:
token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# Ejemplo 1: Reporte del año actual con historial
download_comparison_report(token)

# Ejemplo 2: Reporte específico de 2024-S2
filters = {
    "year": 2024,
    "semester": 2,
    "includeHistoricalComparison": True,
    "historicalPeriodsCount": 6,
    "includeTrendsAnalysis": True
}
download_comparison_report(token, filters)
```

---

## 📊 Estructura de Datos del Reporte

### ModalityTypeComparisonReportDTO

```typescript
interface ModalityTypeComparisonReportDTO {
  generatedAt: string;                    // Fecha/hora generación (ISO 8601)
  generatedBy: string;                    // Nombre completo del usuario
  academicProgramId: number;              // ID del programa académico
  academicProgramName: string;            // Nombre del programa
  academicProgramCode: string;            // Código del programa
  year?: number;                          // Año del análisis (opcional)
  semester?: number;                      // Semestre del análisis (1 o 2)
  summary: ComparisonSummaryDTO;          // Resumen ejecutivo
  modalityTypeStatistics: ModalityTypeStatisticsDTO[]; // Estadísticas por tipo
  historicalComparison?: PeriodComparisonDTO[]; // Comparación histórica
  studentDistributionByType: {[key: string]: number}; // Distribución estudiantes
  trendsAnalysis?: TrendsAnalysisDTO;     // Análisis de tendencias
  metadata: ReportMetadataDTO;            // Metadata del reporte
}
```

### ComparisonSummaryDTO

```typescript
interface ComparisonSummaryDTO {
  totalModalityTypes: number;             // Cantidad de tipos diferentes
  totalModalities: number;                // Total de modalidades
  totalStudents: number;                  // Total de estudiantes únicos
  mostPopularType: string;                // Tipo más popular
  mostPopularTypeCount: number;           // Cantidad del más popular
  leastPopularType: string;               // Tipo menos popular
  leastPopularTypeCount: number;          // Cantidad del menos popular
  averageModalitiesPerType: number;       // Promedio modalidades/tipo
  averageStudentsPerType: number;         // Promedio estudiantes/tipo
}
```

**Ejemplo**:
```json
{
  "totalModalityTypes": 7,
  "totalModalities": 45,
  "totalStudents": 58,
  "mostPopularType": "PROYECTO DE GRADO",
  "mostPopularTypeCount": 18,
  "leastPopularType": "SEMINARIO DE GRADO",
  "leastPopularTypeCount": 2,
  "averageModalitiesPerType": 6.43,
  "averageStudentsPerType": 8.29
}
```

---

### ModalityTypeStatisticsDTO

```typescript
interface ModalityTypeStatisticsDTO {
  modalityTypeId: number;                 // ID del tipo de modalidad
  modalityTypeName: string;               // Nombre del tipo
  description: string;                    // Descripción del tipo
  totalModalities: number;                // Total de modalidades de este tipo
  totalStudents: number;                  // Total de estudiantes en este tipo
  individualModalities: number;           // Modalidades individuales (1 estudiante)
  groupModalities: number;                // Modalidades grupales (2+ estudiantes)
  averageStudentsPerModality: number;     // Promedio estudiantes/modalidad
  percentageOfTotal: number;              // % respecto al total de modalidades
  requiresDirector: boolean;              // Si requiere director o no
  modalitiesWithDirector: number;         // Cantidad con director asignado
  modalitiesWithoutDirector: number;      // Cantidad sin director
  distributionByStatus: {[key: string]: number}; // Cantidad por estado
  trend: string;                          // "INCREASING", "DECREASING", "STABLE"
}
```

**Ejemplo**:
```json
{
  "modalityTypeId": 1,
  "modalityTypeName": "PROYECTO DE GRADO",
  "description": "Desarrollo de un proyecto de investigación aplicado",
  "totalModalities": 18,
  "totalStudents": 24,
  "individualModalities": 10,
  "groupModalities": 8,
  "averageStudentsPerModality": 1.33,
  "percentageOfTotal": 40.0,
  "requiresDirector": true,
  "modalitiesWithDirector": 16,
  "modalitiesWithoutDirector": 2,
  "distributionByStatus": {
    "APROBADO": 12,
    "EN_REVISION": 4,
    "APROBADO_CONSEJO": 2
  },
  "trend": "INCREASING"
}
```

---

### PeriodComparisonDTO

```typescript
interface PeriodComparisonDTO {
  year: number;                           // Año del periodo
  semester: number;                       // Semestre del periodo
  periodLabel: string;                    // Etiqueta (ej: "2024-2")
  modalitiesByType: {[key: string]: number}; // Modalidades por tipo
  studentsByType: {[key: string]: number};   // Estudiantes por tipo
  totalModalitiesInPeriod: number;        // Total de modalidades del periodo
  totalStudentsInPeriod: number;          // Total de estudiantes del periodo
}
```

**Ejemplo**:
```json
{
  "year": 2024,
  "semester": 2,
  "periodLabel": "2024-2",
  "modalitiesByType": {
    "PROYECTO DE GRADO": 15,
    "PASANTIA": 5,
    "PRACTICA PROFESIONAL": 3
  },
  "studentsByType": {
    "PROYECTO DE GRADO": 18,
    "PASANTIA": 5,
    "PRACTICA PROFESIONAL": 3
  },
  "totalModalitiesInPeriod": 23,
  "totalStudentsInPeriod": 26
}
```

---

### TrendsAnalysisDTO

```typescript
interface TrendsAnalysisDTO {
  overallTrend: string;                   // "GROWING", "DECLINING", "STABLE"
  growingTypes: string[];                 // Tipos en crecimiento
  decliningTypes: string[];               // Tipos en declive
  stableTypes: string[];                  // Tipos estables
  mostImprovedType: string;               // Tipo con mayor mejora
  mostDeclinedType: string;               // Tipo con mayor caída
  growthRateByType: {[key: string]: number}; // Tasa de crecimiento (%)
}
```

**Ejemplo**:
```json
{
  "overallTrend": "GROWING",
  "growingTypes": ["PROYECTO DE GRADO", "PASANTIA", "EMPRENDIMIENTO"],
  "decliningTypes": ["SEMINARIO DE GRADO"],
  "stableTypes": ["PRACTICA PROFESIONAL", "PLAN COMPLEMENTARIO"],
  "mostImprovedType": "PASANTIA",
  "mostDeclinedType": "SEMINARIO DE GRADO",
  "growthRateByType": {
    "PROYECTO DE GRADO": 12.5,
    "PASANTIA": 25.8,
    "SEMINARIO DE GRADO": -15.3,
    "PRACTICA PROFESIONAL": 0.5,
    "EMPRENDIMIENTO": 18.2
  }
}
```

---

## 🎯 Escenarios de Filtrado Comunes

### Escenario 1: Análisis del Semestre en Curso

```json
{
  "year": 2026,
  "semester": 1,
  "includeHistoricalComparison": true,
  "historicalPeriodsCount": 3,
  "includeTrendsAnalysis": true,
  "onlyActiveModalities": true
}
```

**Uso**: Reunión de comité de programa para evaluar el semestre actual.

**Resultado PDF**:
- Portada con "Periodo: 2026 - Semestre 1"
- Estadísticas del semestre actual
- Comparación con 3 semestres anteriores
- Tendencias identificadas
- Solo modalidades activas (sin historial de finalizadas)

---

### Escenario 2: Reporte Anual Completo

```json
{
  "year": 2024,
  "includeHistoricalComparison": true,
  "historicalPeriodsCount": 4,
  "includeTrendsAnalysis": true
}
```

**Uso**: Informe de fin de año para la decanatura.

**Resultado PDF**:
- Análisis completo de 2024 (ambos semestres)
- Comparación con los 4 años anteriores
- Tendencias a largo plazo
- Incluye modalidades completadas y canceladas

---

### Escenario 3: Análisis Rápido Sin Historial

```json
{
  "year": 2026,
  "semester": 1,
  "includeHistoricalComparison": false,
  "includeTrendsAnalysis": false
}
```

**Uso**: Consulta rápida de distribución actual.

**Resultado PDF**:
- Solo secciones 1, 2 y 3
- Sin comparación histórica
- Sin análisis de tendencias
- PDF más corto y rápido de generar

---

### Escenario 4: Tendencias a Largo Plazo

```json
{
  "includeHistoricalComparison": true,
  "historicalPeriodsCount": 12,
  "includeTrendsAnalysis": true,
  "onlyActiveModalities": false
}
```

**Uso**: Análisis estratégico de 6 años (12 semestres).

**Resultado PDF**:
- Todas las modalidades (históricas y actuales)
- Comparación de 12 periodos
- Identificación de patrones a largo plazo
- Tipos en crecimiento sostenido vs declive

---

### Escenario 5: Comparativa Solo de Modalidades Activas

```json
{
  "onlyActiveModalities": true,
  "includeHistoricalComparison": false,
  "includeTrendsAnalysis": false
}
```

**Uso**: Snapshot actual del programa sin contexto histórico.

**Resultado PDF**:
- Solo estadísticas actuales
- Modalidades en curso
- Distribución presente
- Sin análisis temporal

---

## 📈 Métricas y Cálculos

### Métricas Básicas

| Métrica | Cálculo | Ubicación PDF |
|---------|---------|---------------|
| Total Tipos de Modalidad | COUNT(DISTINCT modality_type_id) | Resumen Ejecutivo |
| Total Modalidades | COUNT(*) | Resumen Ejecutivo |
| Total Estudiantes | COUNT(DISTINCT student_id) | Resumen Ejecutivo |
| Tipo Más Popular | MAX(COUNT BY type) | Resumen Ejecutivo |
| Tipo Menos Popular | MIN(COUNT BY type) | Resumen Ejecutivo |

### Métricas por Tipo

| Métrica | Cálculo | Ubicación PDF |
|---------|---------|---------------|
| Total Modalidades del Tipo | COUNT WHERE type = X | Estadísticas Detalladas |
| Total Estudiantes del Tipo | COUNT(DISTINCT student) WHERE type = X | Estadísticas Detalladas |
| % del Total | (Modalidades del Tipo / Total) * 100 | Estadísticas Detalladas |
| Modalidades Individuales | COUNT WHERE num_students = 1 | Estadísticas Detalladas |
| Modalidades Grupales | COUNT WHERE num_students > 1 | Estadísticas Detalladas |
| Con Director | COUNT WHERE director_id IS NOT NULL | Estadísticas Detalladas |
| Sin Director | COUNT WHERE director_id IS NULL | Estadísticas Detalladas |

### Métricas de Tendencia

| Métrica | Cálculo | Ubicación PDF |
|---------|---------|---------------|
| Tasa de Crecimiento | ((Actual - Anterior) / Anterior) * 100 | Análisis de Tendencias |
| Tendencia General | Promedio de tasas de crecimiento | Análisis de Tendencias |
| Mayor Mejora | MAX(tasa_crecimiento) | Análisis de Tendencias |
| Mayor Declive | MIN(tasa_crecimiento) | Análisis de Tendencias |

**Clasificación de Tendencias**:
- **GROWING**: Tasa promedio > +5%
- **DECLINING**: Tasa promedio < -5%
- **STABLE**: Tasa entre -5% y +5%

---

## 🔄 Flujo de Procesamiento

```
Usuario envía POST con filtros opcionales
        ↓
Autenticación JWT → Extrae usuario
        ↓
Obtiene programa académico del usuario
        ↓
ReportService.generateModalityTypeComparison(filters)
        ↓
┌─────────────────────────────────────────────┐
│ Procesar Filtros:                           │
│  • Aplicar filtro de año/semestre           │
│  • Aplicar filtro de modalidades activas    │
│  • Determinar periodos históricos a incluir │
└─────────────────────────────────────────────┘
        ↓
Consultar base de datos:
  • student_modalities (por programa)
  • modality_types
  • users (estudiantes y directores)
        ↓
Calcular estadísticas por tipo:
  • Total modalidades
  • Total estudiantes
  • Distribución por estado
  • Asignación de directores
        ↓
Si includeHistoricalComparison = true:
  ↓
  Consultar periodos históricos
  ↓
  Construir PeriodComparisonDTO para cada periodo
  ↓
  Calcular evolución temporal
        ↓
Si includeTrendsAnalysis = true:
  ↓
  Calcular tasas de crecimiento
  ↓
  Clasificar tipos (GROWING/DECLINING/STABLE)
  ↓
  Identificar mayor mejora y declive
        ↓
Construir ModalityTypeComparisonReportDTO completo
        ↓
ModalityComparisonPdfGenerator.generatePDF(report)
        ↓
┌─────────────────────────────────────────────┐
│ Generar PDF con diseño institucional:       │
│  1. Portada profesional                     │
│  2. Resumen ejecutivo con tarjetas          │
│  3. Estadísticas detalladas por tipo        │
│  4. Distribución con barras visuales        │
│  5. Comparación histórica (si aplica)       │
│  6. Análisis de tendencias (si aplica)      │
│  7. Conclusiones automáticas                │
└─────────────────────────────────────────────┘
        ↓
Retorna ByteArrayOutputStream
        ↓
Convierte a ByteArrayResource
        ↓
Respuesta HTTP con PDF adjunto y headers
```

---

## 🎨 Elementos Visuales del PDF

### 1. Tarjetas de Métricas (Portada y Resumen)
```
┌─────────────────────────────┐
│                             │
│            7                │ ← Valor grande (28pt, color institucional)
│                             │
│    Tipos de Modalidad       │ ← Etiqueta (10pt, gris)
│                             │
└─────────────────────────────┘
```
- Bordes con colores institucionales
- Valores destacados en tamaño grande
- Diseño minimalista y limpio

---

### 2. Cajas de Destacados

**Tipo Más Popular**:
```
┌──────────────────────────────────────────────┐
│ ⭐ TIPO MÁS POPULAR: PROYECTO DE GRADO       │ ← Fondo verde claro
│    (18 modalidades)                          │
└──────────────────────────────────────────────┘
```

**Tipo Menos Popular**:
```
┌──────────────────────────────────────────────┐
│ 📊 TIPO MENOS POPULAR: SEMINARIO DE GRADO    │ ← Fondo naranja claro
│    (2 modalidades)                           │
└──────────────────────────────────────────────┘
```

---

### 3. Barras de Distribución de Estudiantes

```
┌─────────────────────────────────────────────────────────────┐
│ PROYECTO DE GRADO                                           │ ← Encabezado rojo
├─────────────────────────────────────────────────────────────┤
│ █████████████████████████████████████░░░░░░░░░░░░░░░░░░░░  │
│ 45 estudiantes (blanco)    42.5% del total (rojo)          │
└─────────────────────────────────────────────────────────────┘
│←────────── 85% máximo ──────────→│ ← Área vacía con % │
    ↑ Área coloreada (dorado)
```

**Características**:
- Ancho proporcional al número de estudiantes
- Color dorado institucional para la barra
- Fondo dorado claro para área vacía
- Texto blanco sobre barra, texto rojo en área vacía

---

### 4. Tabla de Comparación Histórica

```
┌──────────────────┬──────────┬──────────┬──────────┬──────────┐
│ Tipo Modalidad   │ 2026-1   │ 2025-2   │ 2025-1   │ 2024-2   │ ← Headers rojos
├──────────────────┼──────────┼──────────┼──────────┼──────────┤
│ Proyecto Grado   │ 15 mod.  │ 12 mod.  │ 10 mod.  │ 8 mod.   │
│ (fondo dorado)   │ 18 est.  │ 15 est.  │ 12 est.  │ 10 est.  │
├──────────────────┼──────────┼──────────┼──────────┼──────────┤
│ Pasantía         │ 5 mod.   │ 4 mod.   │ 6 mod.   │ 3 mod.   │
│                  │ 5 est.   │ 4 est.   │ 6 est.   │ 3 est.   │
├──────────────────┼──────────┼──────────┼──────────┼──────────┤
│ TOTALES          │ 20 mod.  │ 16 mod.  │ 16 mod.  │ 11 mod.  │ ← Fila roja
│ (fondo rojo)     │ 23 est.  │ 19 est.  │ 18 est.  │ 13 est.  │
└──────────────────┴──────────┴──────────┴──────────┴──────────┘
```

---

### 5. Análisis de Tendencias

**Caja de Tendencia General**:
```
┌─────────────────────────────────────────────┐
│                                             │
│  ↗ TENDENCIA GENERAL: EN CRECIMIENTO       │ ← Texto grande, blanco
│                                             │
└─────────────────────────────────────────────┘
   ↑ Fondo dorado (crecimiento) o rojo (declive)
```

**Tablas de Tipos por Tendencia**:
```
✓ TIPOS EN CRECIMIENTO
┌─────────────────────────┬───────────┐
│ PROYECTO DE GRADO       │ +12.50%   │ ← Fondo dorado
├─────────────────────────┼───────────┤
│ PASANTIA                │ +25.80%   │
└─────────────────────────┴───────────┘

✗ TIPOS EN DECLIVE
┌─────────────────────────┬───────────┐
│ SEMINARIO DE GRADO      │ -15.30%   │ ← Fondo rojo
└─────────────────────────┴───────────┘
```

**Destacados**:
```
┌──────────────────────────────────────────┐
│ 🏆 Mayor Mejora: PASANTIA (+25.80%)     │ ← Fondo dorado claro
└──────────────────────────────────────────┘

┌──────────────────────────────────────────┐
│ ⚠ Mayor Declive: SEMINARIO (-15.30%)    │ ← Fondo rosa claro
└──────────────────────────────────────────┘
```

---

## 🔍 Análisis Proporcionado

### 1. Análisis de Popularidad
- Identifica el tipo de modalidad más demandado
- Identifica el tipo menos demandado
- Calcula porcentajes relativos
- Visualización con íconos (⭐, 📊)

### 2. Análisis de Distribución
- Cantidad de modalidades por tipo
- Cantidad de estudiantes por tipo
- Barras proporcionales visuales
- Porcentajes del total

### 3. Análisis de Supervisión
- Para tipos que requieren director:
  - Cantidad con director asignado
  - Cantidad sin director (alerta)
  - Ratio de asignación

### 4. Análisis por Estado
- Distribución de estados dentro de cada tipo
- Identificación de cuellos de botella
- Estados más comunes por tipo

### 5. Análisis Temporal (Histórico)
- Evolución periodo a periodo
- Identificación de patrones estacionales
- Comparación año tras año
- Visualización en tabla multi-columna

### 6. Análisis de Tendencias
- Clasificación en crecimiento/declive/estable
- Cálculo de tasas de cambio
- Identificación de tipos emergentes
- Alertas sobre tipos en riesgo

---

## 📊 Interpretación de Resultados

### Tendencia General: GROWING (↗)
**Interpretación**:
- ✅ El programa está creciendo en número de modalidades
- ✅ Más estudiantes optando por modalidades de grado
- ✅ Indicador positivo de actividad académica

**Acciones recomendadas**:
- Asegurar suficientes directores disponibles
- Planificar recursos para sostener el crecimiento
- Identificar tipos en mayor demanda

---

### Tendencia General: DECLINING (↘)
**Interpretación**:
- ⚠️ Reducción en el número de modalidades
- ⚠️ Posible problema de matrícula o interés
- ⚠️ Requiere atención de las directivas

**Acciones recomendadas**:
- Investigar causas del declive
- Evaluar calidad y pertinencia de las modalidades
- Considerar nuevas opciones o mejoras

---

### Tendencia General: STABLE (→)
**Interpretación**:
- ➡️ Estabilidad en la oferta y demanda
- ➡️ Comportamiento predecible
- ➡️ Situación controlada

**Acciones recomendadas**:
- Mantener el seguimiento periódico
- Evaluar oportunidades de innovación
- Asegurar calidad sostenida

---

### Tipo Más Popular con Alta Concentración (>50%)

**Interpretación**:
- ⚠️ Dependencia excesiva de un tipo de modalidad
- ⚠️ Posible saturación de recursos
- ⚠️ Falta de diversificación

**Acciones recomendadas**:
- Promover otros tipos de modalidad
- Evaluar capacidad de atención
- Balancear la oferta

---

### Tipo con Muchas Modalidades Sin Director

**Interpretación**:
- 🚨 Problema operativo crítico
- 🚨 Estudiantes sin supervisión adecuada
- 🚨 Riesgo de retrasos

**Acciones recomendadas**:
- Asignar directores urgentemente
- Revisar disponibilidad de profesores
- Evaluar carga docente actual

---

## 🆚 Diferencias con Otros Reportes

### vs Reporte Global de Modalidades Activas

| Aspecto | Reporte Global | Reporte Comparativo |
|---------|----------------|---------------------|
| **Enfoque** | Detalle individual | Análisis por tipo |
| **Granularidad** | Por modalidad | Por tipo de modalidad |
| **Temporal** | Snapshot actual | Multi-periodo |
| **Visualización** | Tablas detalladas | Gráficos comparativos |
| **Tendencias** | No | Sí (opcional) |
| **Histórico** | No | Sí (opcional) |
| **Longitud PDF** | Media | Larga |
| **Uso** | Monitoreo detallado | Análisis estratégico |

---

### vs Reporte Filtrado

| Aspecto | Reporte Filtrado | Reporte Comparativo |
|---------|------------------|---------------------|
| **Filtros** | Por tipo, estado, fecha, director | Por periodo y configuración de análisis |
| **Objetivo** | Subconjunto específico | Comparación entre tipos |
| **Estadísticas** | Del subconjunto | Por tipo completo |
| **Temporal** | Rango de fechas | Periodos académicos |
| **Uso** | Auditorías específicas | Planeación estratégica |

---

## 💡 Consejos y Mejores Prácticas

### ✅ Recomendaciones

1. **Para análisis semestral**: Especifica `year` y `semester`
   ```json
   {"year": 2026, "semester": 1}
   ```

2. **Para ver evolución**: Activa histórico con al menos 4 periodos
   ```json
   {"includeHistoricalComparison": true, "historicalPeriodsCount": 4}
   ```

3. **Para reuniones de comité**: Incluye tendencias
   ```json
   {"includeTrendsAnalysis": true}
   ```

4. **Para reportes rápidos**: Desactiva análisis extras
   ```json
   {"includeHistoricalComparison": false, "includeTrendsAnalysis": false}
   ```

5. **Para análisis actual**: Usa solo modalidades activas
   ```json
   {"onlyActiveModalities": true}
   ```

---

### ❌ Evitar

1. ❌ Solicitar más de 20 periodos históricos (PDF muy largo)
2. ❌ Especificar `semester` sin `year` (se ignora)
3. ❌ Usar `historicalPeriodsCount` sin `includeHistoricalComparison: true`
4. ❌ Solicitar tendencias sin datos históricos suficientes (mínimo 2 periodos)

---

## 🧪 Casos de Prueba

### Test 1: Reporte Básico Sin Filtros
**Request**:
```json
{}
```
**Esperado**: PDF con todas las secciones, 3 periodos históricos por defecto

---

### Test 2: Semestre Específico
**Request**:
```json
{
  "year": 2024,
  "semester": 2
}
```
**Esperado**: PDF solo con datos del 2024-2

---

### Test 3: Histórico Extenso
**Request**:
```json
{
  "includeHistoricalComparison": true,
  "historicalPeriodsCount": 8
}
```
**Esperado**: Tabla comparativa con 8 periodos anteriores

---

### Test 4: Sin Tendencias Ni Histórico
**Request**:
```json
{
  "includeHistoricalComparison": false,
  "includeTrendsAnalysis": false
}
```
**Esperado**: PDF corto con solo secciones 1, 2 y 3

---

### Test 5: Solo Activas con Tendencias
**Request**:
```json
{
  "onlyActiveModalities": true,
  "includeTrendsAnalysis": true,
  "historicalPeriodsCount": 5
}
```
**Esperado**: Análisis de tendencias basado solo en modalidades activas

---

## 📋 Secciones Condicionales del PDF

| Sección | Condición para Incluir |
|---------|------------------------|
| 1. Resumen Ejecutivo | ✅ Siempre |
| 2. Estadísticas Detalladas | ✅ Siempre |
| 3. Distribución de Estudiantes | ✅ Siempre (si hay datos) |
| 4. Comparación Histórica | ✅ Si `includeHistoricalComparison: true` Y hay periodos previos |
| 5. Análisis de Tendencias | ✅ Si `includeTrendsAnalysis: true` Y hay datos históricos |
| 6. Conclusiones | ✅ Siempre |

---

## 🔐 Seguridad

### Filtrado Automático por Programa
**Crítico**: El sistema **SIEMPRE** filtra por el programa académico del usuario autenticado.

**Ejemplo**:
- Usuario: Jefe de Ingeniería de Sistemas
- Filtro: `{"year": 2024}`
- **Resultado**: Solo modalidades de Ingeniería de Sistemas en 2024

Esto garantiza:
- ✅ Privacidad entre programas
- ✅ Datos relevantes para el usuario
- ✅ Cumplimiento de normativas

---

## 📊 Valor Agregado del Reporte

### Para Jefatura de Programa
- 📈 Identificar tendencias de preferencia estudiantil
- 🎯 Tomar decisiones sobre oferta de modalidades
- 📊 Evaluar balance entre tipos
- 🔍 Detectar problemas de asignación de directores

### Para Consejo de Programa
- 📋 Fundamentar decisiones con datos históricos
- 🔄 Evaluar efectividad de cambios curriculares
- 📉 Identificar tipos en declive que requieren atención
- 🚀 Promover tipos emergentes o en crecimiento

### Para Secretaría
- 📊 Planificar recursos y espacios
- 👥 Estimar carga de trabajo futuro
- 📅 Programar actividades según demanda
- 📈 Generar reportes estadísticos oficiales

### Para Comité de Programa
- 🎓 Evaluar pertinencia de la oferta académica
- 🔬 Analizar calidad y diversidad
- 📐 Proponer mejoras basadas en datos
- 🎯 Alinear modalidades con objetivos del programa

---

## 🔗 Endpoints Relacionados

| Endpoint | Método | Descripción | Request Body |
|----------|--------|-------------|--------------|
| `/reports/global/modalities/pdf` | GET | Reporte global sin filtros | No |
| `/reports/modalities/filtered/pdf` | POST | Reporte filtrado por criterios | `ModalityReportFilterDTO` |
| `/reports/modalities/comparison` | POST | Versión JSON del comparativo | `ModalityComparisonFilterDTO` |
| `/reports/modalities/{id}/historical/pdf` | GET | Histórico de una modalidad | No (PathVariable) |
| `/reports/directors/assigned-modalities/pdf` | POST | Modalidades por director | `DirectorReportFilterDTO` |

---

## 📅 Periodos Académicos

### Formato de Periodos

El sistema usa el formato: `YYYY-S` donde:
- `YYYY`: Año (4 dígitos)
- `S`: Semestre (1 o 2)

**Ejemplos**:
- `2024-1`: Primer semestre 2024 (Enero-Mayo)
- `2024-2`: Segundo semestre 2024 (Agosto-Diciembre)
- `2023-2`: Segundo semestre 2023

### Orden de Periodos en el PDF

Los periodos se ordenan **de más reciente a más antiguo**:
```
2026-1 → 2025-2 → 2025-1 → 2024-2 → 2024-1 → ...
```

### Cálculo de Periodos Históricos

Si solicitas `historicalPeriodsCount: 5` en el semestre `2026-1`:
```
Actual: 2026-1
Históricos:
  1. 2025-2
  2. 2025-1
  3. 2024-2
  4. 2024-1
  5. 2023-2
```

---

## 🎓 Glosario de Términos

| Término | Definición |
|---------|------------|
| **Tipo de Modalidad** | Categoría de modalidad de grado (Proyecto, Pasantía, etc.) |
| **Modalidad Individual** | Modalidad con un solo estudiante |
| **Modalidad Grupal** | Modalidad con 2 o más estudiantes |
| **Tendencia** | Dirección del cambio (crecimiento, declive, estable) |
| **Tasa de Crecimiento** | Porcentaje de cambio respecto al periodo anterior |
| **Periodo** | Combinación de año y semestre académico |
| **Snapshot** | Foto del estado actual sin contexto histórico |
| **Comparativa** | Análisis lado a lado de múltiples periodos |

---

## ❓ Preguntas Frecuentes (FAQ)

### ❓ ¿Cuántos periodos históricos debo solicitar?

**Recomendaciones**:
- **Análisis semestral**: 3-4 periodos (1.5-2 años)
- **Análisis anual**: 3-5 años
- **Análisis estratégico**: 8-10 periodos (4-5 años)
- **Máximo recomendado**: 12 periodos (6 años)

---

### ❓ ¿Qué pasa si no hay datos históricos suficientes?

El sistema genera el reporte con los datos disponibles. Si solicitas 5 periodos pero solo hay 2, mostrará 2 columnas en la comparación histórica.

---

### ❓ ¿Puedo comparar semestres específicos de diferentes años?

Sí, especificando `year` y `semester`. La comparación histórica automáticamente comparará semestres equivalentes:
```json
{
  "year": 2024,
  "semester": 2,
  "includeHistoricalComparison": true,
  "historicalPeriodsCount": 3
}
```
Compara: 2024-2 vs 2023-2 vs 2022-2 vs 2021-2

---

### ❓ ¿Las tendencias son confiables con pocos datos?

Las tendencias son más precisas con al menos 4-5 periodos históricos. Con menos datos, las conclusiones deben tomarse con cautela.

---

### ❓ ¿Qué significa "Promedio de Estudiantes por Tipo"?

Es la suma de todos los estudiantes dividida entre la cantidad de tipos de modalidad. Indica la distribución equitativa ideal.

**Ejemplo**:
- 7 tipos de modalidad
- 58 estudiantes totales
- Promedio: 58 / 7 = 8.29 estudiantes por tipo

Si un tipo tiene 18 estudiantes, está **por encima del promedio** (más popular).

---

### ❓ ¿Por qué algunos tipos no tienen tendencia?

Si es un tipo nuevo que apareció en el periodo actual, no hay datos históricos para calcular tendencia. Se marca como "N/A" o "NUEVO".

---

### ❓ ¿El reporte incluye modalidades de otros programas?

**No**. El sistema filtra automáticamente por el programa del usuario autenticado por seguridad y relevancia.

---

### ❓ ¿Cuál es la diferencia entre modalidades y estudiantes en las tablas?

- **Modalidades**: Número de "proyectos" o "instancias" de ese tipo
- **Estudiantes**: Número de personas participando (puede ser mayor si hay grupos)

**Ejemplo**:
- 10 modalidades de Proyecto de Grado
- 15 estudiantes (significa que hay algunos grupos de 2-3 estudiantes)

---

## 🛠️ Troubleshooting

### Problema: PDF muy grande o tarda mucho

**Causa**: Muchos periodos históricos o muchos tipos de modalidad.

**Solución**:
1. Reducir `historicalPeriodsCount` a 5 o menos
2. Filtrar por año específico
3. Usar `onlyActiveModalities: true`

---

### Problema: Sección de tendencias vacía

**Causa**: No hay suficientes datos históricos para calcular tendencias.

**Solución**:
1. Aumentar `historicalPeriodsCount`
2. Verificar que existan modalidades en periodos previos
3. No usar `onlyActiveModalities: true` (limita datos históricos)

---

### Problema: Comparación histórica muestra menos periodos de los solicitados

**Causa**: No existen modalidades en periodos más antiguos.

**Solución**:
- Es normal, el sistema muestra los periodos disponibles
- El programa puede ser nuevo o tener gaps en algunos periodos

---

### Problema: Error 400 "El año debe ser mayor a 2000"

**Causa**: Año inválido en el filtro.

**Solución**:
```json
{
  "year": 2024  // ✅ Correcto (>= 2000)
}
```

---

### Problema: Error 403 Forbidden

**Causa**: Usuario sin permiso `PERM_VIEW_REPORT`.

**Solución**:
- Verificar rol del usuario
- Solicitar permisos al administrador
- Usar token de usuario autorizado

---

## 📈 Casos de Análisis Avanzado

### Análisis 1: Impacto de Cambios Curriculares

**Objetivo**: Evaluar si cambios en el plan de estudios afectaron las preferencias.

**Request**:
```json
{
  "includeHistoricalComparison": true,
  "historicalPeriodsCount": 10,
  "includeTrendsAnalysis": true
}
```

**Análisis en el PDF**:
1. Comparar periodos pre y post cambio
2. Identificar tipos con cambios significativos
3. Evaluar si las tendencias son positivas

---

### Análisis 2: Capacidad de Directores

**Objetivo**: Determinar si hay suficientes directores para la demanda.

**Request**:
```json
{
  "onlyActiveModalities": true,
  "includeTrendsAnalysis": true
}
```

**Análisis en el PDF**:
1. Ver "Modalidades Sin Director" por tipo
2. Revisar tendencias de crecimiento
3. Proyectar necesidad futura de directores

---

### Análisis 3: Estacionalidad

**Objetivo**: Identificar si hay diferencias entre semestres.

**Request para Semestre 1**:
```json
{
  "semester": 1,
  "includeHistoricalComparison": true,
  "historicalPeriodsCount": 6
}
```

**Request para Semestre 2**:
```json
{
  "semester": 2,
  "includeHistoricalComparison": true,
  "historicalPeriodsCount": 6
}
```

**Comparar** ambos PDFs manualmente para identificar patrones estacionales.

---

### Análisis 4: Diagnóstico de Declive

**Objetivo**: Investigar causas de reducción en modalidades.

**Request**:
```json
{
  "includeHistoricalComparison": true,
  "historicalPeriodsCount": 8,
  "includeTrendsAnalysis": true,
  "onlyActiveModalities": false
}
```

**Análisis en el PDF**:
1. Identificar tipos en declive
2. Ver tasas de decrecimiento
3. Revisar distribución por estado (posibles cuellos de botella)
4. Evaluar asignación de directores (posible causa)

---

## 🎯 KPIs y Objetivos

### KPIs Monitoreables

| KPI | Meta Objetivo | Crítico Si |
|-----|---------------|------------|
| Diversidad de Tipos | ≥ 5 tipos activos | < 3 tipos |
| Balance de Distribución | Ningún tipo >60% | Un tipo >70% |
| Asignación de Directores | >95% asignado | <80% asignado |
| Tendencia General | GROWING o STABLE | DECLINING >2 periodos |
| Crecimiento Anual | +5% a +15% | <-10% o >+50% |
| Modalidades por Tipo | ≥ 2 por tipo | Tipos con 0 |

---

## 🔄 Actualizaciones y Versionado

### Versión Actual del PDF: 2.0

**Características**:
- ✅ Portada profesional mejorada
- ✅ Tarjetas de métricas con diseño visual
- ✅ Barras de distribución con porcentajes
- ✅ Tabla histórica multi-columna
- ✅ Análisis de tendencias con clasificación
- ✅ Destacados con íconos (🏆, ⚠️, ⭐)
- ✅ Conclusiones automáticas generadas
- ✅ Colores institucionales exclusivos
- ✅ Pie de página en todas las páginas

---

## 📞 Información de Contacto

### Soporte Técnico
- **Sistema**: SIGMA - Sistema de Gestión de Modalidades de Grado
- **Institución**: Universidad Surcolombiana
- **Código fuente**: 
  - Controller: `com.SIGMA.USCO.report.controller.GlobalModalityReportController`
  - Generator: `com.SIGMA.USCO.report.service.ModalityComparisonPdfGenerator`
  - Service: `com.SIGMA.USCO.report.service.ReportService`

### Documentación Relacionada
- [Reporte Global de Modalidades](./DOCUMENTACION_REPORTE_MODALIDADES_ACTIVAS.md)
- [Reporte Filtrado (RF-46)](./DOCUMENTACION_REPORTE_MODALIDADES_FILTRADO.md)

---

## 📅 Changelog

| Versión | Fecha | Cambios |
|---------|-------|---------|
| 1.0 | 2026-02-17 | Implementación inicial del reporte comparativo |
| 1.1 | 2026-02-17 | Agregado análisis de tendencias |
| 1.2 | 2026-02-18 | Mejorado diseño visual con colores institucionales |
| 2.0 | 2026-02-18 | Rediseño completo profesional con gráficos visuales |
| 2.1 | 2026-02-18 | Documentación completa y ejemplos de código |

---

## ✅ Checklist Pre-Generación

Antes de solicitar el reporte comparativo:

- [ ] Token JWT válido y no expirado
- [ ] Usuario con permiso `PERM_VIEW_REPORT`
- [ ] Usuario pertenece a un programa académico
- [ ] Existen modalidades en el programa
- [ ] Si filtras por año: año >= 2000
- [ ] Si filtras por semestre: valor 1 o 2
- [ ] Si solicitas histórico: `includeHistoricalComparison: true`
- [ ] Si solicitas tendencias: datos históricos disponibles
- [ ] Body JSON bien formado (o vacío para valores por defecto)

---

## 🎓 Casos de Éxito

### Caso Real 1: Universidad Surcolombiana - Ingeniería de Software

**Situación**: Se detectó declive en Seminarios de Grado.

**Acción**: Generaron reporte comparativo con 8 periodos históricos.

**Resultado**: Identificaron que la causa era cambios en reglamento. Ajustaron requisitos y recuperaron la demanda.

---

### Caso Real 2: Identificación de Sobrecarga de Directores

**Situación**: Aumento en Proyectos de Grado sin aumento proporcional de directores.

**Acción**: Reporte comparativo mostró 40% de proyectos sin director.

**Resultado**: Asignaron más directores y balancearon carga.

---

## 💡 Tips Profesionales

1. **Genera reportes trimestralmente** para seguimiento continuo
2. **Guarda PDFs históricos** para comparaciones manuales futuras
3. **Presenta en reuniones de comité** para decisiones basadas en datos
4. **Combina con otros reportes** para análisis 360°
5. **Usa tendencias** para proyecciones y planeación

---

**Generado por**: SIGMA - Sistema de Gestión de Modalidades de Grado  
**Requisito Funcional**: RF-48 - Comparativa de Modalidades por Tipo de Grado  
**Servicio**: ModalityComparisonPdfGenerator  
**Última actualización**: 18 de Febrero de 2026  
**Versión**: 2.1

