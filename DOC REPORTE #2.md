# 📊 Documentación: Reporte de Modalidades Filtrado (RF-46)

## 📝 Descripción General

Este endpoint genera un **reporte personalizado en formato PDF** de modalidades de grado aplicando múltiples criterios de filtrado. Permite a los usuarios del sistema (Secretaría, Consejo, Comité de Programa) obtener reportes específicos según sus necesidades de análisis.

**Requisito Funcional**: RF-46 - Filtrado por Tipo de Modalidad

---

## 🔗 Endpoint

### **POST** `/reports/modalities/filtered/pdf`

**Descripción**: Genera y descarga un reporte en PDF de modalidades filtradas según criterios específicos del programa académico del usuario autenticado.

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

El body es **OPCIONAL**. Si no se envía ningún filtro, el sistema generará el reporte completo de todas las modalidades activas del programa.

```json
{
  "degreeModalityIds": [1, 3, 5],
  "degreeModalityNames": ["PROYECTO DE GRADO", "PASANTIA"],
  "processStatuses": ["APROBADO", "EN_REVISION", "APROBADO_SECRETARIA"],
  "startDate": "2024-01-01T00:00:00",
  "endDate": "2024-12-31T23:59:59",
  "includeWithoutDirector": true,
  "onlyWithDirector": false
}
```

### Campos del Request Body

| Campo | Tipo | Requerido | Descripción | Ejemplo |
|-------|------|-----------|-------------|---------|
| `degreeModalityIds` | `List<Long>` | No | IDs específicos de tipos de modalidad a incluir | `[1, 3, 5]` |
| `degreeModalityNames` | `List<String>` | No | Nombres de tipos de modalidad a incluir | `["PROYECTO DE GRADO", "PASANTIA"]` |
| `processStatuses` | `List<String>` | No | Estados de proceso a filtrar | `["APROBADO", "EN_REVISION"]` |
| `startDate` | `String` | No | Fecha de inicio del rango (ISO 8601) | `"2024-01-01T00:00:00"` |
| `endDate` | `String` | No | Fecha de fin del rango (ISO 8601) | `"2024-12-31T23:59:59"` |
| `includeWithoutDirector` | `Boolean` | No | Incluir modalidades sin director | `true` |
| `onlyWithDirector` | `Boolean` | No | Solo modalidades con director asignado | `false` |

---

## 📤 Response (Respuesta)

### Respuesta Exitosa (200 OK)

**Content-Type**: `application/pdf`

**Headers de Respuesta**:
```http
Content-Type: application/pdf
Content-Disposition: attachment; filename=Reporte_Modalidades_Filtrado_2026-02-18_143025.pdf
X-Report-Generated-At: 2026-02-18T14:30:25
X-Total-Records: 15
Content-Length: 89456
```

**Body**: Archivo PDF binario descargable

### Respuestas de Error

#### Error al Generar PDF (500)
```json
{
  "success": false,
  "error": "Error al generar el PDF: <mensaje_detallado>",
  "timestamp": "2026-02-18T14:30:25"
}
```

#### Error Inesperado (500)
```json
{
  "success": false,
  "error": "Error inesperado: <mensaje_detallado>",
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

### Caso de Uso 1: Filtrar Solo Proyectos de Grado Aprobados

**Escenario**: El Consejo de Programa necesita revisar únicamente los proyectos de grado que ya están aprobados.

**Request**:
```json
{
  "degreeModalityNames": ["PROYECTO DE GRADO"],
  "processStatuses": ["APROBADO"]
}
```

**Resultado**: PDF con solo proyectos de grado en estado "APROBADO" del programa.

---

### Caso de Uso 2: Identificar Modalidades Sin Director en un Periodo

**Escenario**: Jefatura quiere identificar modalidades que iniciaron este año y no tienen director asignado para tomar acciones correctivas.

**Request**:
```json
{
  "startDate": "2026-01-01T00:00:00",
  "endDate": "2026-12-31T23:59:59",
  "includeWithoutDirector": true,
  "onlyWithDirector": false
}
```

**Resultado**: PDF con modalidades del 2026 incluyendo aquellas sin director.

---

### Caso de Uso 3: Análisis de Pasantías y Prácticas Profesionales

**Escenario**: Secretaría necesita generar un reporte específico de modalidades relacionadas con el sector productivo.

**Request**:
```json
{
  "degreeModalityNames": ["PASANTIA", "PRACTICA PROFESIONAL"],
  "processStatuses": ["APROBADO", "EN_REVISION", "APROBADO_SECRETARIA", "APROBADO_CONSEJO"]
}
```

**Resultado**: PDF con pasantías y prácticas profesionales en diversos estados.

---

### Caso de Uso 4: Reporte Completo (Sin Filtros)

**Escenario**: Usuario quiere ver todas las modalidades del programa sin restricciones.

**Request**:
```json
{}
```
o simplemente enviar un body vacío.

**Resultado**: PDF idéntico al reporte global de modalidades activas.

---

## 🔍 Detalles de los Filtros

### 1️⃣ Filtro por IDs de Modalidad (`degreeModalityIds`)

**Descripción**: Filtra por los IDs específicos de tipos de modalidad.

**Comportamiento**:
- Si se proporciona, **solo** se incluyen las modalidades cuyos tipos coincidan con estos IDs
- Es un filtro **inclusivo** (OR): incluye modalidades que coincidan con al menos uno de los IDs
- Si está vacío o es `null`, no aplica este filtro

**Ejemplo**:
```json
{
  "degreeModalityIds": [1, 5, 8]
}
```
→ Solo incluye modalidades de tipo 1, 5 u 8.

---

### 2️⃣ Filtro por Nombres de Modalidad (`degreeModalityNames`)

**Descripción**: Filtra por los nombres de tipos de modalidad.

**Comportamiento**:
- Similar a `degreeModalityIds` pero usando nombres
- **No distingue mayúsculas/minúsculas**
- Filtro **inclusivo** (OR)
- Si se proporciona junto con `degreeModalityIds`, el sistema usa ambos criterios (OR combinado)

**Nombres válidos** (ejemplos del sistema):
- `"PROYECTO DE GRADO"`
- `"PASANTIA"`
- `"PRACTICA PROFESIONAL"`
- `"PRODUCCION ACADEMICA DE ALTO NIVEL"`
- `"SEMINARIO DE GRADO"`
- `"PLAN COMPLEMENTARIO POSGRADO"`
- `"EMPRENDIMIENTO Y FORTALECIMIENTO DE EMPRESA"`

**Ejemplo**:
```json
{
  "degreeModalityNames": ["PROYECTO DE GRADO", "PASANTIA"]
}
```
→ Solo incluye Proyectos de Grado y Pasantías.

---

### 3️⃣ Filtro por Estados de Proceso (`processStatuses`)

**Descripción**: Filtra modalidades según su estado actual en el sistema.

**Comportamiento**:
- Filtro **inclusivo** (OR): incluye modalidades en cualquiera de los estados especificados
- Si está vacío o es `null`, incluye todos los estados activos

**Estados disponibles**:

| Estado | Descripción |
|--------|-------------|
| `APROBADO` | Modalidad aprobada completamente |
| `EN_REVISION` | En proceso de revisión |
| `APROBADO_SECRETARIA` | Aprobada por Secretaría, pendiente Consejo |
| `APROBADO_CONSEJO` | Aprobada por Consejo |
| `PENDIENTE_APROBACION` | Pendiente de aprobación inicial |
| `RECHAZADO` | Modalidad rechazada |
| `CANCELADO` | Modalidad cancelada |
| `COMPLETADO` | Modalidad finalizada |

**Ejemplo**:
```json
{
  "processStatuses": ["APROBADO", "APROBADO_SECRETARIA", "APROBADO_CONSEJO"]
}
```
→ Solo modalidades en estados aprobados.

---

### 4️⃣ Filtro por Rango de Fechas (`startDate`, `endDate`)

**Descripción**: Filtra modalidades según su fecha de inicio.

**Comportamiento**:
- Compara con la fecha de inicio (`start_date`) de la modalidad
- Si solo se proporciona `startDate`: incluye desde esa fecha en adelante
- Si solo se proporciona `endDate`: incluye hasta esa fecha
- Si se proporcionan ambas: incluye el rango completo [startDate, endDate]

**Formato**: ISO 8601 → `"YYYY-MM-DDTHH:mm:ss"`

**Ejemplo**:
```json
{
  "startDate": "2024-08-01T00:00:00",
  "endDate": "2024-12-31T23:59:59"
}
```
→ Modalidades que iniciaron en el segundo semestre de 2024.

---

### 5️⃣ Filtro de Directores (`includeWithoutDirector`, `onlyWithDirector`)

**Descripción**: Controla la inclusión de modalidades según asignación de director.

**Comportamiento**:

| `includeWithoutDirector` | `onlyWithDirector` | Resultado |
|--------------------------|--------------------|-----------| 
| `true` | `false` o `null` | Incluye **todas** las modalidades (con y sin director) |
| `false` o `null` | `true` | Solo modalidades **con director asignado** |
| `false` o `null` | `false` o `null` | Incluye todas (comportamiento por defecto) |
| `true` | `true` | ⚠️ Conflicto lógico - se prioriza `onlyWithDirector` |

**Ejemplo 1** - Solo con director:
```json
{
  "onlyWithDirector": true
}
```

**Ejemplo 2** - Incluir sin director:
```json
{
  "includeWithoutDirector": true
}
```

**Nota importante**: Las modalidades que **no requieren director** (Seminario, Plan Complementario, Producción Académica) se incluyen siempre independientemente de este filtro.

---

## 🔄 Lógica de Combinación de Filtros

Los filtros se aplican con lógica **AND** (todos deben cumplirse):

```
Resultado = 
  (degreeModalityIds OR degreeModalityNames) 
  AND processStatuses 
  AND rango de fechas 
  AND condición de director
  AND del programa académico del usuario
```

**Ejemplo completo**:
```json
{
  "degreeModalityNames": ["PROYECTO DE GRADO"],
  "processStatuses": ["APROBADO"],
  "startDate": "2024-01-01T00:00:00",
  "onlyWithDirector": true
}
```

**Resultado**: 
- ✅ Tipo: Proyecto de Grado
- ✅ Estado: Aprobado
- ✅ Inicio: Desde enero 2024
- ✅ Director: Asignado
- ✅ Programa: Del usuario autenticado

---

## 💻 Ejemplos de Uso

### Ejemplo 1: JavaScript (Frontend)

```javascript
const token = localStorage.getItem('auth_token');

const filters = {
  degreeModalityNames: ["PROYECTO DE GRADO", "PASANTIA"],
  processStatuses: ["APROBADO"],
  startDate: "2024-01-01T00:00:00",
  onlyWithDirector: true
};

fetch('http://localhost:8080/reports/modalities/filtered/pdf', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify(filters)
})
.then(response => {
  if (!response.ok) throw new Error('Error al generar reporte');
  return response.blob();
})
.then(blob => {
  // Descargar automáticamente
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `Reporte_Filtrado_${new Date().toISOString()}.pdf`;
  document.body.appendChild(a);
  a.click();
  window.URL.revokeObjectURL(url);
  document.body.removeChild(a);
})
.catch(error => {
  console.error('Error:', error);
  alert('No se pudo generar el reporte');
});
```

---

### Ejemplo 2: Postman

**1. Configuración**:
- **Método**: POST
- **URL**: `http://localhost:8080/reports/modalities/filtered/pdf`

**2. Headers**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json
```

**3. Body** (raw JSON):
```json
{
  "degreeModalityNames": ["PROYECTO DE GRADO"],
  "processStatuses": ["APROBADO", "EN_REVISION"],
  "includeWithoutDirector": true
}
```

**4. Send** → **Save Response** → **Save to a file**

**5. Resultado**: Archivo `Reporte_Modalidades_Filtrado_2026-02-18_143025.pdf` descargado

---

### Ejemplo 3: cURL (PowerShell)

```powershell
$token = "tu_token_jwt_aqui"
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

$body = @{
    degreeModalityNames = @("PROYECTO DE GRADO", "PASANTIA")
    processStatuses = @("APROBADO")
    startDate = "2024-01-01T00:00:00"
    onlyWithDirector = $true
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/reports/modalities/filtered/pdf" `
    -Method Post `
    -Headers $headers `
    -Body $body `
    -OutFile "Reporte_Filtrado.pdf"

Write-Host "Reporte descargado exitosamente: Reporte_Filtrado.pdf"
```

---

### Ejemplo 4: Java (Cliente REST)

```java
@Autowired
private RestTemplate restTemplate;

public byte[] downloadFilteredReport(String token) {
    String url = "http://localhost:8080/reports/modalities/filtered/pdf";
    
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    headers.setContentType(MediaType.APPLICATION_JSON);
    
    ModalityReportFilterDTO filters = ModalityReportFilterDTO.builder()
        .degreeModalityNames(List.of("PROYECTO DE GRADO", "PASANTIA"))
        .processStatuses(List.of("APROBADO", "EN_REVISION"))
        .onlyWithDirector(true)
        .build();
    
    HttpEntity<ModalityReportFilterDTO> request = new HttpEntity<>(filters, headers);
    
    ResponseEntity<byte[]> response = restTemplate.exchange(
        url, 
        HttpMethod.POST, 
        request, 
        byte[].class
    );
    
    return response.getBody();
}
```

---

## 📄 Estructura del PDF Generado

El PDF generado con este endpoint tiene **la misma estructura visual** que el reporte global de modalidades activas, pero con el contenido filtrado según los criterios especificados.

### Secciones del PDF:

1. **Encabezado Institucional**
   - Logo SIGMA
   - Universidad Surcolombiana
   - Diseño con colores institucionales

2. **Información del Reporte**
   - Título: "REPORTE GENERAL DE MODALIDADES ACTIVAS" (o "FILTRADO" si hay filtros)
   - Programa académico
   - Fecha de generación
   - Usuario que generó
   - Total de registros (después del filtrado)

3. **Sección 1: Resumen Ejecutivo**
   - Métricas calculadas sobre las modalidades filtradas
   - Distribución por tipo (solo tipos filtrados)
   - Distribución por estado (solo estados filtrados)

4. **Sección 2: Indicadores de Gestión**
   - Alertas sobre modalidades sin director (si aplica)
   - Eficiencia operativa del subconjunto filtrado
   - Top 5 modalidades más antiguas del filtro

5. **Sección 3: Análisis Visual**
   - Comparativa Individual vs Grupal (datos filtrados)

6. **Sección 4: Análisis de Directores**
   - Solo directores de las modalidades filtradas
   - Carga de trabajo relativa al subconjunto

7. **Sección 5: Detalle de Modalidades**
   - Tabla con todas las modalidades que cumplen los criterios
   - Formato de 7 columnas idéntico al reporte global

8. **Sección 6: Observaciones**
   - **Nota especial**: Indica qué filtros fueron aplicados
   - Footer institucional

---

## 🎨 Diseño Visual

El PDF mantiene el diseño institucional profesional:

- **Colores Primarios**: Blanco y Rojo `#8F1E1E`
- **Color Secundario**: Dorado `#D5CBA0`
- **Fuentes**: Helvetica con jerarquía clara
- **Formato**: A4, orientación vertical
- **Márgenes**: 50 puntos

---

## 🔐 Seguridad y Restricciones

### Filtrado Automático por Programa
**Importante**: Independientemente de los filtros proporcionados, el sistema **siempre** filtra por el programa académico del usuario autenticado.

**Ejemplo**:
- Usuario pertenece a "Ingeniería de Sistemas"
- Solicita filtro por "PROYECTO DE GRADO"
- **Resultado**: Solo proyectos de grado de Ingeniería de Sistemas

Esto garantiza:
- ✅ Privacidad entre programas
- ✅ Información relevante
- ✅ Cumplimiento de políticas de acceso

### Validación de Permisos
Solo usuarios con el permiso `PERM_VIEW_REPORT` pueden acceder.

---

## 📊 Escenarios Comunes de Filtrado

### Escenario A: Auditoría de Modalidades en Revisión

```json
{
  "processStatuses": ["EN_REVISION", "PENDIENTE_APROBACION"]
}
```
**Uso**: Identificar modalidades que requieren atención urgente.

---

### Escenario B: Análisis de Modalidades Individuales

```json
{
  "degreeModalityNames": ["PROYECTO DE GRADO", "PRACTICA PROFESIONAL"]
}
```
Luego filtrar manualmente en el PDF las modalidades individuales.

---

### Escenario C: Detección de Problemas (Sin Director)

```json
{
  "processStatuses": ["APROBADO"],
  "includeWithoutDirector": true
}
```
**Uso**: Modalidades aprobadas que aún no tienen director asignado.

---

### Escenario D: Reporte de Último Semestre

```json
{
  "startDate": "2024-08-01T00:00:00",
  "endDate": "2024-12-31T23:59:59"
}
```
**Uso**: Análisis de modalidades del último periodo académico.

---

### Escenario E: Solo Modalidades con Supervisión

```json
{
  "onlyWithDirector": true,
  "processStatuses": ["APROBADO", "EN_REVISION"]
}
```
**Uso**: Verificar modalidades activas con supervisión docente.

---

## 🔄 Flujo de Procesamiento

```
Usuario envía POST con filtros
        ↓
Autenticación JWT → Extrae usuario
        ↓
Obtiene programa académico del usuario
        ↓
ReportService.generateFilteredReport(filters)
        ↓
Aplica filtros en query SQL:
  • Filtra por programa académico (automático)
  • Aplica degreeModalityIds / degreeModalityNames
  • Aplica processStatuses
  • Aplica rango de fechas
  • Aplica filtros de director
        ↓
Calcula estadísticas sobre el subconjunto
        ↓
Construye GlobalModalityReportDTO (filtrado)
        ↓
PdfReport.generatePDF(report)
        ↓
Genera PDF con diseño institucional
        ↓
Retorna ByteArrayOutputStream
        ↓
Convierte a ByteArrayResource
        ↓
Respuesta HTTP con PDF adjunto
```

---

## 📋 Diferencias con el Reporte Global

| Aspecto | Reporte Global | Reporte Filtrado |
|---------|----------------|------------------|
| **Endpoint** | GET `/global/modalities/pdf` | POST `/modalities/filtered/pdf` |
| **Request Body** | No requiere | Recibe `ModalityReportFilterDTO` |
| **Contenido** | Todas las modalidades activas | Solo modalidades que cumplen filtros |
| **Estadísticas** | Del total del programa | Del subconjunto filtrado |
| **Nota en PDF** | "Todas las modalidades activas" | "Filtros aplicados: [lista]" |
| **Nombre archivo** | `Reporte_Global_Modalidades_*.pdf` | `Reporte_Modalidades_Filtrado_*.pdf` |

---

## ⚙️ Configuraciones Avanzadas

### Combinación Compleja de Filtros

**Escenario**: Proyectos y Pasantías aprobadas del último año con director

```json
{
  "degreeModalityNames": ["PROYECTO DE GRADO", "PASANTIA"],
  "processStatuses": ["APROBADO", "APROBADO_CONSEJO"],
  "startDate": "2025-01-01T00:00:00",
  "endDate": "2025-12-31T23:59:59",
  "onlyWithDirector": true
}
```

**Aplicación de filtros**:
1. ✅ Tipo = Proyecto de Grado OR Pasantía
2. ✅ Estado = Aprobado OR Aprobado por Consejo
3. ✅ Fecha inicio entre 2025-01-01 y 2025-12-31
4. ✅ Tiene director asignado
5. ✅ Del programa del usuario autenticado

---

### Filtrado Mínimo (Más Permisivo)

```json
{
  "degreeModalityNames": ["PROYECTO DE GRADO"]
}
```
→ Todos los proyectos de grado del programa, sin importar estado, fecha o director.

---

### Filtrado Máximo (Más Restrictivo)

```json
{
  "degreeModalityIds": [1],
  "degreeModalityNames": ["PROYECTO DE GRADO"],
  "processStatuses": ["APROBADO"],
  "startDate": "2026-01-01T00:00:00",
  "endDate": "2026-02-18T23:59:59",
  "onlyWithDirector": true
}
```
→ Solo proyectos de grado aprobados de 2026 con director asignado.

---

## 🛠️ Manejo de Errores

### Error: Filtros Inválidos

**Causa**: Formato de fecha incorrecto o valores no válidos

**Response**:
```json
{
  "success": false,
  "error": "Error inesperado: Invalid date format",
  "timestamp": "2026-02-18T14:30:25"
}
```

**Solución**: Verificar formato ISO 8601 en fechas.

---

### Error: Sin Resultados

**Comportamiento**: Si los filtros no coinciden con ninguna modalidad:
- El PDF se genera normalmente
- Las tablas estarán vacías
- El resumen ejecutivo mostrará ceros
- No se lanza error

**Ejemplo de PDF sin resultados**:
- Total de Modalidades Activas: **0**
- Total de Estudiantes: **0**
- Mensaje: "No se encontraron modalidades que cumplan con los criterios especificados"

---

### Error: Token Expirado

**Response**: HTTP 401 Unauthorized
```json
{
  "error": "Unauthorized",
  "message": "Token expired"
}
```

---

## 📊 Contenido de las Secciones del PDF Filtrado

### 1. Resumen Ejecutivo (Filtrado)
Todas las métricas se calculan **solo** sobre las modalidades filtradas:
- Total de modalidades (después del filtro)
- Total de estudiantes (en modalidades filtradas)
- Directores únicos (en modalidades filtradas)
- Distribución por tipo (solo tipos presentes después del filtro)
- Distribución por estado (solo estados presentes después del filtro)

### 2. Indicadores de Gestión (Filtrado)
- Modalidad más antigua **del subconjunto filtrado**
- Promedio de días **de las modalidades filtradas**
- Alertas **solo de las modalidades en el filtro**

### 3. Detalle de Modalidades (Filtrado)
Tabla con **solo las modalidades que cumplen todos los criterios**.

---

## 🎯 Comparativa: Global vs Filtrado

### Reporte Global
✅ Úsalo cuando:
- Necesitas visión completa del programa
- Análisis general sin restricciones
- Reportes periódicos estándar
- No tienes criterios específicos

### Reporte Filtrado
✅ Úsalo cuando:
- Necesitas análisis específico
- Auditoría de un tipo de modalidad
- Revisión de estados específicos
- Análisis temporal (por fechas)
- Detección de problemas (sin director)

---

## 🔍 Validaciones del Sistema

El sistema valida automáticamente:

1. ✅ **Usuario autenticado**: Token JWT válido
2. ✅ **Permisos**: Usuario tiene `PERM_VIEW_REPORT`
3. ✅ **Programa académico**: Usuario pertenece a un programa
4. ✅ **Formato de fechas**: Si se proporcionan, deben ser válidas
5. ✅ **Tipos de modalidad**: Existen en el sistema
6. ✅ **Estados**: Son estados válidos del enum

---

## 📝 Notas Importantes

### ⚠️ Nota 1: Filtro por Programa (Automático)
**El sistema SIEMPRE filtra por el programa del usuario**, incluso si no se especifica. No es posible obtener modalidades de otros programas.

### ⚠️ Nota 2: Filtros Vacíos
Si se envía un body vacío `{}`, el sistema genera el reporte completo (equivalente al global).

### ⚠️ Nota 3: Modalidades Sin Director
Las modalidades que **por diseño no requieren director** (Seminario, Plan Complementario, Producción Académica) se incluyen **siempre** en `includeWithoutDirector: true`.

### ⚠️ Nota 4: Rendimiento
Filtros muy amplios (sin restricciones) pueden generar PDFs grandes. Se recomienda aplicar al menos un filtro para optimizar.

### ⚠️ Nota 5: Fechas en UTC
Las fechas se procesan en la zona horaria del servidor. Considerar esto en rangos de búsqueda.

---

## 🧪 Casos de Prueba

### Test Case 1: Filtro por Tipo
```json
{
  "degreeModalityNames": ["PROYECTO DE GRADO"]
}
```
**Resultado Esperado**: PDF con solo proyectos de grado

---

### Test Case 2: Filtro por Estado
```json
{
  "processStatuses": ["APROBADO"]
}
```
**Resultado Esperado**: PDF con modalidades en estado "APROBADO"

---

### Test Case 3: Filtro Combinado
```json
{
  "degreeModalityNames": ["PASANTIA"],
  "processStatuses": ["APROBADO", "EN_REVISION"],
  "onlyWithDirector": true
}
```
**Resultado Esperado**: Pasantías aprobadas o en revisión con director

---

### Test Case 4: Sin Filtros
```json
{}
```
**Resultado Esperado**: PDF completo del programa (como reporte global)

---

### Test Case 5: Rango de Fechas
```json
{
  "startDate": "2024-08-01T00:00:00",
  "endDate": "2024-12-31T23:59:59"
}
```
**Resultado Esperado**: Modalidades del segundo semestre 2024

---

## 📈 Estadísticas y Métricas Calculadas

Las siguientes métricas se calculan **dinámicamente** sobre el subconjunto filtrado:

| Métrica | Fórmula | Ubicación en PDF |
|---------|---------|------------------|
| Total Modalidades Filtradas | COUNT(*) | Sección 1 - Resumen |
| Total Estudiantes Filtrados | COUNT(DISTINCT student_id) | Sección 1 - Resumen |
| Directores Únicos | COUNT(DISTINCT director_id) | Sección 1 - Resumen |
| Modalidades Individuales | COUNT WHERE num_students = 1 | Sección 1 - Resumen |
| Modalidades Grupales | COUNT WHERE num_students > 1 | Sección 1 - Resumen |
| Modalidades Sin Director | COUNT WHERE director_id IS NULL | Sección 2 - Alertas |
| Promedio Días Activo | AVG(CURRENT_DATE - start_date) | Sección 2 - Eficiencia |
| Ratio Estudiantes/Director | Total Estudiantes / Total Directores | Sección 2 - Eficiencia |

---

## 🔗 Endpoints Relacionados

| Endpoint | Método | Descripción | Body |
|----------|--------|-------------|------|
| `/reports/global/modalities/pdf` | GET | Reporte global sin filtros | No |
| `/reports/modalities/filtered` | POST | Versión JSON del reporte filtrado | Sí |
| `/reports/modalities/comparison/pdf` | POST | Comparativa entre tipos | Sí |
| `/reports/modalities/{id}/historical/pdf` | GET | Histórico de una modalidad | No |
| `/reports/directors/assigned-modalities/pdf` | POST | Modalidades por director | Opcional |

---

## 💡 Consejos de Uso

### ✅ Buenas Prácticas

1. **Especifica al menos un filtro** para obtener resultados relevantes
2. **Usa nombres exactos** de modalidades (consulta antes con `/reports/modalities/types`)
3. **Combina filtros lógicamente** para análisis específicos
4. **Usa rangos de fechas** para análisis temporales
5. **Guarda los PDFs** con nombres descriptivos para referencia futura

### ❌ Evitar

1. No enviar filtros imposibles (ej: fecha fin antes de fecha inicio)
2. No combinar `onlyWithDirector: true` con `includeWithoutDirector: false` (redundante)
3. No usar nombres de modalidad incorrectos (no coincidirán)
4. No solicitar reportes muy grandes sin filtros (puede ser lento)

---

## 🆚 Comparación de Endpoints de Reportes

| Característica | `/global/modalities/pdf` | `/modalities/filtered/pdf` |
|----------------|--------------------------|----------------------------|
| Método HTTP | GET | POST |
| Requiere Body | ❌ No | ✅ Sí (opcional) |
| Filtros | ❌ No | ✅ Sí (7 tipos) |
| Contenido | Todo el programa | Subconjunto filtrado |
| Complejidad | Baja | Media |
| Flexibilidad | Baja | Alta |
| Velocidad | Rápida | Variable (según filtros) |
| Caso de uso | Reportes estándar | Análisis específicos |

---

## 📞 Preguntas Frecuentes (FAQ)

### ❓ ¿Puedo filtrar por múltiples tipos de modalidad?
**Sí**. Usa el array `degreeModalityNames` con varios elementos:
```json
{
  "degreeModalityNames": ["PROYECTO DE GRADO", "PASANTIA", "SEMINARIO DE GRADO"]
}
```

---

### ❓ ¿Qué pasa si mis filtros no coinciden con ninguna modalidad?
El PDF se genera normalmente pero con tablas vacías y métricas en cero. No se lanza error.

---

### ❓ ¿Puedo ver modalidades de otros programas?
**No**. El sistema siempre filtra por tu programa académico automáticamente por seguridad.

---

### ❓ ¿Los filtros son obligatorios?
**No**. Puedes enviar un body vacío `{}` o `null` y obtendrás el reporte completo (como el global).

---

### ❓ ¿Cómo sé qué nombres de modalidad usar?
Consulta primero el endpoint `GET /reports/modalities/types` para obtener la lista completa de modalidades disponibles en tu programa.

---

### ❓ ¿Puedo combinar `degreeModalityIds` y `degreeModalityNames`?
**Sí**. El sistema usa ambos con lógica OR, incluyendo modalidades que coincidan con cualquiera de los dos criterios.

---

### ❓ ¿El filtro por director afecta a modalidades que no requieren director?
**No**. Las modalidades sin requisito de director (Seminario, Plan Complementario, Producción Académica) se incluyen independientemente de estos filtros.

---

### ❓ ¿Cómo filtro solo modalidades sin director asignado?
Lamentablemente, no hay un filtro directo para esto. Usa:
```json
{
  "includeWithoutDirector": true
}
```
Y luego revisa manualmente en el PDF la columna "Director" con valor "Sin asignar".

---

## 🚀 Ejemplos Prácticos Avanzados

### Ejemplo 1: Reporte Trimestral de Proyectos

```json
{
  "degreeModalityNames": ["PROYECTO DE GRADO"],
  "startDate": "2026-01-01T00:00:00",
  "endDate": "2026-03-31T23:59:59",
  "processStatuses": ["APROBADO", "EN_REVISION"]
}
```

---

### Ejemplo 2: Auditoría de Modalidades Sin Supervisión

```json
{
  "processStatuses": ["APROBADO", "EN_REVISION"],
  "includeWithoutDirector": true
}
```

Luego buscar en el PDF las que tienen "Sin asignar" en Director.

---

### Ejemplo 3: Análisis de Modalidades Prácticas

```json
{
  "degreeModalityNames": [
    "PASANTIA", 
    "PRACTICA PROFESIONAL", 
    "EMPRENDIMIENTO Y FORTALECIMIENTO DE EMPRESA"
  ],
  "startDate": "2024-01-01T00:00:00"
}
```

---

### Ejemplo 4: Modalidades Aprobadas con Director (Calidad Asegurada)

```json
{
  "processStatuses": ["APROBADO"],
  "onlyWithDirector": true,
  "startDate": "2025-08-01T00:00:00"
}
```

---

## 📊 Interpretación de Resultados

### Si el PDF tiene pocas páginas:
- Filtros muy restrictivos
- Pocos datos en el periodo seleccionado
- ✅ Normal si es lo que esperabas

### Si el PDF es muy grande:
- Filtros muy amplios
- Muchas modalidades activas
- 💡 Considera agregar más filtros para acotar

### Si todas las métricas son cero:
- Ninguna modalidad cumple los filtros
- Verificar criterios de búsqueda
- Revisar nombres de modalidad y estados

---

## 🎓 Actores y Permisos

### Roles con Acceso
- ✅ **Jefatura de Programa** (`PROGRAM_HEAD`) con `PERM_VIEW_REPORT`
- ✅ **Consejo de Programa** con `PERM_VIEW_REPORT`
- ✅ **Comité de Programa** con `PERM_VIEW_REPORT`
- ✅ **Secretaría** con `PERM_VIEW_REPORT`

### Roles sin Acceso
- ❌ Estudiantes
- ❌ Directores (a menos que tengan el permiso específico)
- ❌ Evaluadores
- ❌ Usuarios sin autenticar

---

## 📚 Documentación Relacionada

- [Reporte Global de Modalidades Activas](./DOCUMENTACION_REPORTE_MODALIDADES_ACTIVAS.md)
- Reporte Comparativo de Modalidades (RF-48)
- Reporte por Director Asignado (RF-49)
- Reporte Histórico de Modalidad

---

## 📅 Changelog

| Versión | Fecha | Cambios |
|---------|-------|---------|
| 1.0 | 2026-02-18 | Documentación inicial del endpoint filtrado |
| 1.1 | 2026-02-18 | Agregados ejemplos prácticos y casos de uso |
| 1.2 | 2026-02-18 | Ampliada sección de combinación de filtros |

---

## ✅ Checklist de Validación

Antes de generar el reporte filtrado, verificar:

- [ ] Usuario autenticado con token JWT válido
- [ ] Usuario tiene permiso `PERM_VIEW_REPORT`
- [ ] Body JSON bien formado (si se envía)
- [ ] Nombres de modalidad escritos correctamente
- [ ] Estados válidos según el enum del sistema
- [ ] Fechas en formato ISO 8601
- [ ] Filtros lógicamente consistentes
- [ ] No hay conflicto entre `includeWithoutDirector` y `onlyWithDirector`

---

## 🔧 Troubleshooting

### Problema: "Error al generar el PDF"

**Causas posibles**:
1. Formato de fecha incorrecto
2. Nombres de modalidad con caracteres especiales
3. Estados no válidos

**Solución**:
1. Validar formato ISO 8601 en fechas
2. Usar nombres exactos (consultar `/reports/modalities/types`)
3. Verificar enum de estados válidos

---

### Problema: PDF vacío aunque existen modalidades

**Causas posibles**:
1. Filtros muy restrictivos
2. Combinación de filtros sin intersección
3. Fechas fuera de rango

**Solución**:
1. Relajar algunos filtros
2. Probar con menos criterios
3. Ampliar rango de fechas
4. Verificar que existan modalidades en tu programa

---

### Problema: Token expirado

**Solución**:
1. Renovar token con endpoint de login
2. Actualizar token en el header de autorización
3. Reintentar la petición

---

## 📞 Soporte

Para asistencia técnica o consultas:
- **Sistema**: SIGMA - Sistema de Gestión de Modalidades de Grado
- **Institución**: Universidad Surcolombiana
- **Código fuente**: `com.SIGMA.USCO.report.controller.GlobalModalityReportController`
- **Método**: `exportFilteredModalityReportToPDF()`

---

**Generado por**: SIGMA - Sistema de Gestión de Modalidades de Grado  
**Requisito Funcional**: RF-46 - Filtrado por Tipo de Modalidad  
**Última actualización**: 18 de Febrero de 2026

