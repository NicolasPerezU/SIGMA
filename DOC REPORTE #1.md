# 📊 Documentación: Reporte de Modalidades Activas (PdfReport) (El primer endpoint)

## 📝 Descripción General

La clase `PdfReport` es un generador de reportes en formato PDF que presenta un análisis completo de las **modalidades de grado activas** en el sistema SIGMA. Este reporte está diseñado con un estilo profesional e institucional utilizando los colores oficiales de la Universidad Surcolombiana.

---

## 🎨 Diseño Visual

### Colores Institucionales
- **Color Primario (Rojo Institucional)**: `#8F1E1E` (RGB: 143, 30, 30)
- **Color Secundario (Dorado Institucional)**: `#D5CBA0` (RGB: 213, 203, 160)
- **Color de Fondo (Dorado Claro)**: `#F5F2EB` (RGB: 245, 242, 235)

---

## 🔗 Endpoint Principal

### **GET** `/reports/global/modalities/pdf`

**Descripción**: Genera y descarga un reporte completo en formato PDF de todas las modalidades activas del programa académico del usuario autenticado.

### Autenticación
- **Requerida**: Sí
- **Tipo**: Bearer Token (JWT)
- **Permiso requerido**: `PERM_VIEW_REPORT`

### Headers de Solicitud
```http
Authorization: Bearer <token_jwt>
```

### Parámetros
**Este endpoint NO recibe parámetros en el body ni query params**. 

El reporte se genera automáticamente basándose en:
- El **usuario autenticado** (extraído del token JWT)
- El **programa académico** al que pertenece el usuario (obtenido de `program_authorities`)

### Respuesta Exitosa

**Código HTTP**: `200 OK`

**Headers de Respuesta**:
```http
Content-Type: application/pdf
Content-Disposition: attachment; filename=Reporte_Global_Modalidades_2026-02-18_143025.pdf
X-Report-Generated-At: 2026-02-18T14:30:25
X-Total-Records: 42
Content-Length: 125678
```

**Body**: Archivo PDF binario

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

#### Sin Permisos (403)
```json
{
  "error": "Forbidden",
  "message": "Access Denied"
}
```

---

## 📄 Estructura del Reporte PDF

El PDF generado contiene **6 secciones principales**:

### 1️⃣ Encabezado Institucional
- Logo textual "SIGMA"
- Nombre del sistema
- Universidad Surcolombiana
- Diseño con fondo dorado claro y línea roja institucional

### 2️⃣ Información del Reporte
- **Título**: "REPORTE GENERAL DE MODALIDADES ACTIVAS"
- **Programa académico**: Nombre y código
- **Fecha de generación**: Formato DD/MM/YYYY HH:mm
- **Generado por**: Nombre completo del usuario
- **Total de registros**: Cantidad de modalidades

### 3️⃣ Sección 1: Resumen Ejecutivo
Contiene métricas clave en tabla con colores alternos (rojo/dorado):

| Métrica | Descripción |
|---------|-------------|
| **Total de Modalidades Activas** | Cantidad total de modalidades en curso |
| **Total de Estudiantes Activos** | Estudiantes participando en modalidades |
| **Total de Directores Asignados** | Profesores dirigiendo modalidades |
| **Modalidades Individuales** | Modalidades con un solo estudiante |
| **Modalidades Grupales** | Modalidades con múltiples estudiantes |
| **En Proceso de Revisión** | Modalidades pendientes de aprobación |

**Subsecciones**:
- **1.1 Indicadores de Eficiencia**
  - Promedio de estudiantes por modalidad grupal
  - ⚠ Modalidades sin director asignado (alerta)
  - Tasa de progreso general

- **1.2 Distribución por Tipo de Modalidad**
  - Tabla con categoría, cantidad y barra de progreso visual
  - Ordenado por cantidad (mayor a menor)
  
- **1.3 Distribución por Estado**
  - Tabla con estado, cantidad y barra de progreso visual

### 4️⃣ Sección 2: Indicadores de Gestión

**2.1 Alertas y Observaciones**:
- Modalidad más antigua (días en proceso)
- Promedio de días de las top 5 modalidades más largas
- ⚠ Modalidades que requieren director

**2.2 Eficiencia Operativa**:
- Promedio de días en proceso
- Ratio estudiantes/director

**2.3 Modalidades con Mayor Tiempo Activo**:
- Top 5 modalidades con más días activos
- Tabla con: Modalidad, Estudiante, Estado, Días Activo

### 5️⃣ Sección 3: Análisis Visual de Distribución

**3.1 Comparativa Individual vs Grupal**:
- Tabla comparativa con cantidades y porcentajes
- Codificación por colores institucionales

### 6️⃣ Sección 4: Análisis de Directores

**4.1 Directores con Mayor Carga de Trabajo**:
- Top 5 directores con más modalidades asignadas
- Barra visual de carga relativa

**4.2 Estadísticas de Distribución**:
- Total de directores activos
- Promedio de modalidades por director
- Director con mayor carga

### 7️⃣ Sección 5: Detalle de Modalidades Activas

Tabla completa con las siguientes columnas:

| Columna | Descripción |
|---------|-------------|
| **ID** | ID de la modalidad del estudiante |
| **Modalidad** | Nombre del tipo de modalidad |
| **Estudiante(s)** | Nombres completos (L = Líder) |
| **Programa** | Programa académico |
| **Estado** | Estado descriptivo de la modalidad |
| **Director** | Nombre del director o "No requerido"/"Sin asignar" |
| **Días desde Inicio** | Cantidad de días desde que inició |

**Nota importante**: Las siguientes modalidades muestran "No requerido" en el campo Director:
- Plan Complementario Posgrado
- Producción Académica de Alto Nivel
- Seminario de Grado

### 8️⃣ Sección 6: Observaciones y Notas

- Nota sobre el filtrado por programa académico
- Información de contacto para más detalles
- Footer con:
  - Sistema SIGMA
  - Universidad Surcolombiana
  - Fecha y hora de generación

---

## 🔐 Seguridad y Autorización

### Roles Permitidos
El acceso está restringido a usuarios con el permiso `PERM_VIEW_REPORT`, típicamente:
- ✅ Jefatura de Programa (`PROGRAM_HEAD`)
- ✅ Consejo de Programa
- ✅ Secretaría
- ✅ Comité de Programa

### Filtrado Automático por Programa
El reporte **solo muestra modalidades del programa académico** al que pertenece el usuario autenticado. Esto garantiza:
- Privacidad de datos entre programas
- Información relevante y contextualizada
- Cumplimiento de políticas de acceso a información

---

## 📊 Datos del Reporte

### Fuentes de Información

El reporte obtiene datos de las siguientes entidades principales:

1. **StudentModality**: Modalidades activas de estudiantes
2. **ModalityType**: Tipos de modalidades disponibles
3. **User**: Información de estudiantes y directores
4. **AcademicProgram**: Programas académicos
5. **StudentProfile**: Perfiles académicos de estudiantes
6. **ProgramAuthority**: Relación usuario-programa-rol

### Estados de Modalidades Consideradas

El reporte incluye modalidades en los siguientes estados:
- ✅ `APROBADA` (Approved)
- ✅ `EN_PROCESO_REVISION` (In Review Process)
- ✅ `APROBADA_SECRETARIA` (Approved by Secretary)
- ✅ `APROBADA_CONSEJO` (Approved by Council)
- ✅ Otros estados activos según configuración

---

## 💻 Ejemplo de Uso

### Desde una aplicación Frontend (JavaScript)

```javascript
// Obtener el token de autenticación
const token = localStorage.getItem('auth_token');

// Hacer la petición al endpoint
fetch('http://localhost:8080/reports/global/modalities/pdf', {
  method: 'GET',
  headers: {
    'Authorization': `Bearer ${token}`
  }
})
.then(response => {
  if (!response.ok) {
    throw new Error('Error al generar el reporte');
  }
  return response.blob();
})
.then(blob => {
  // Crear un enlace de descarga
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `Reporte_Modalidades_${new Date().toISOString()}.pdf`;
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

### Desde Postman o herramientas similares

1. **Método**: GET
2. **URL**: `http://localhost:8080/reports/global/modalities/pdf`
3. **Headers**:
   - `Authorization: Bearer <tu_token_jwt>`
4. **Send to**: Guardar respuesta como archivo
5. **Resultado**: Archivo PDF descargado

### Desde cURL (PowerShell)

```powershell
$token = "tu_token_jwt_aqui"
$headers = @{
    "Authorization" = "Bearer $token"
}

Invoke-WebRequest -Uri "http://localhost:8080/reports/global/modalities/pdf" `
    -Method Get `
    -Headers $headers `
    -OutFile "Reporte_Modalidades.pdf"
```

---

## 🎯 Casos de Uso

### Caso de Uso 1: Jefatura de Programa revisa modalidades activas
1. Jefatura inicia sesión en SIGMA
2. Navega a la sección de reportes
3. Selecciona "Reporte de Modalidades Activas"
4. Sistema genera PDF con todas las modalidades de su programa
5. Jefatura descarga y analiza el reporte

### Caso de Uso 2: Consejo de Programa evalúa distribución
1. Miembro del consejo solicita reporte
2. Sistema filtra automáticamente por su programa
3. Reporte muestra estadísticas de distribución
4. Consejo identifica tipos de modalidad más/menos populares
5. Consejo toma decisiones informadas

### Caso de Uso 3: Secretaría monitorea directores
1. Secretaría genera reporte mensual
2. Revisa sección de análisis de directores
3. Identifica directores con sobrecarga
4. Toma acciones correctivas para balancear carga

---

## 📐 Características Técnicas

### Tecnologías Utilizadas
- **iText PDF**: Generación de documentos PDF
- **Spring Framework**: Servicios y controladores
- **Lombok**: Reducción de código boilerplate
- **JPA/Hibernate**: Acceso a datos

### Formato de Página
- **Tamaño**: A4 (210mm × 297mm)
- **Márgenes**: 50 puntos en todos los lados
- **Orientación**: Vertical (Portrait)

### Fuentes Tipográficas
- **Familia**: Helvetica
- **Tamaños**:
  - Título: 20pt (Negrita, Rojo)
  - Subtítulo: 16pt (Negrita, Rojo)
  - Sección: 14pt (Negrita, Rojo)
  - Normal: 10pt (Regular, Negro)
  - Pequeña: 8pt (Regular, Gris)
  - Encabezado de tabla: 10pt (Negrita, Blanco)

---

## 🔄 Flujo de Procesamiento

```
Usuario solicita reporte
        ↓
Autenticación JWT → Extrae email del usuario
        ↓
Obtiene programa académico del usuario (via program_authorities)
        ↓
ReportService.generateGlobalReport()
        ↓
Consulta base de datos (solo modalidades del programa)
        ↓
Construye DTO con estadísticas y detalles
        ↓
PdfReport.generatePDF(report)
        ↓
Genera documento PDF con diseño institucional
        ↓
Retorna ByteArrayOutputStream
        ↓
Convierte a ByteArrayResource
        ↓
Envía respuesta HTTP con PDF adjunto
```

---

## 📋 Estructura de Datos del Reporte

### GlobalModalityReportDTO

```java
{
  "academicProgramId": Long,              // ID del programa académico
  "academicProgramName": String,          // Nombre del programa (ej: "Ingeniería de Software")
  "academicProgramCode": String,          // Código del programa (ej: "IS-2020")
  "generatedAt": LocalDateTime,           // Fecha/hora de generación
  "generatedBy": String,                  // Nombre completo del usuario
  "metadata": {
    "totalRecords": Integer,              // Total de modalidades en el reporte
    "reportType": String                  // Tipo de reporte
  },
  "executiveSummary": ExecutiveSummaryDTO,
  "modalities": List<ModalityDetailReportDTO>
}
```

### ExecutiveSummaryDTO

```java
{
  "totalActiveModalities": Integer,       // Total de modalidades activas
  "totalActiveStudents": Integer,         // Total de estudiantes en modalidades
  "totalActiveDirectors": Integer,        // Total de directores asignados
  "individualModalities": Integer,        // Modalidades individuales (1 estudiante)
  "groupModalities": Integer,             // Modalidades grupales (2+ estudiantes)
  "modalitiesInReview": Integer,          // Modalidades en revisión
  "averageStudentsPerGroup": Double,      // Promedio de estudiantes por grupo
  "modalitiesWithoutDirector": Integer,   // Modalidades sin director (alerta)
  "overallProgressRate": Double,          // Tasa general de progreso (%)
  
  "modalitiesByType": Map<String, Long>,  // Distribución por tipo
  // Ejemplo:
  // {
  //   "Proyecto de Grado": 15,
  //   "Pasantía": 8,
  //   "Práctica Profesional": 5
  // }
  
  "modalitiesByStatus": Map<String, Long> // Distribución por estado
  // Ejemplo:
  // {
  //   "Aprobada": 20,
  //   "En Proceso de Revisión": 5,
  //   "Aprobada por Secretaría": 3
  // }
}
```

### ModalityDetailReportDTO

```java
{
  "studentModalityId": Long,              // ID de la modalidad del estudiante
  "modalityName": String,                 // Nombre del tipo de modalidad
  "academicProgram": String,              // Programa académico
  "statusDescription": String,            // Descripción del estado
  "daysSinceStart": Long,                 // Días desde inicio
  
  "students": List<StudentInfo> [
    {
      "fullName": String,                 // Nombre completo del estudiante
      "code": String,                     // Código estudiantil
      "isLeader": Boolean                 // Es líder del grupo (true/false)
    }
  ],
  
  "director": DirectorInfo {              // Puede ser null
    "fullName": String,                   // Nombre completo del director
    "email": String,                      // Correo del director
    "department": String                  // Departamento
  }
}
```

---

## 🎨 Secciones del PDF Generado

### Sección 1: RESUMEN EJECUTIVO
- **Tabla de métricas principales** con 6 indicadores clave
- **Subsección 1.1**: Indicadores de Eficiencia
  - Incluye alertas visuales para modalidades sin director
- **Subsección 1.2**: Distribución por Tipo de Modalidad
  - Tabla con barras de progreso visuales
  - Porcentajes calculados automáticamente
- **Subsección 1.3**: Distribución por Estado
  - Similar a 1.2 pero agrupado por estados

### Sección 2: INDICADORES DE GESTIÓN (Nueva Página)
- **2.1 Alertas y Observaciones**
  - Modalidad más antigua (días en proceso)
  - Promedio de las top 5 modalidades
  - Alertas de modalidades sin director
  
- **2.2 Eficiencia Operativa**
  - Promedio de días en proceso general
  - Ratio estudiantes/director
  
- **2.3 Modalidades con Mayor Tiempo Activo**
  - Top 5 modalidades con más tiempo
  - Tabla detallada con estudiante, estado y días

### Sección 3: ANÁLISIS VISUAL DE DISTRIBUCIÓN
- **3.1 Comparativa Individual vs Grupal**
  - Tabla comparativa con cantidades y porcentajes
  - Visualización con colores diferenciados

### Sección 4: ANÁLISIS DE DIRECTORES
- **4.1 Directores con Mayor Carga de Trabajo**
  - Top 5 directores más cargados
  - Barras visuales de carga relativa
  
- **4.2 Estadísticas de Distribución**
  - Total de directores activos
  - Promedio de modalidades por director
  - Identificación del director con mayor carga

### Sección 5: DETALLE DE MODALIDADES ACTIVAS (Nueva Página)
- Tabla completa con 7 columnas
- Todas las modalidades del programa listadas
- Información completa por modalidad

### Sección 6: OBSERVACIONES Y NOTAS (Nueva Página)
- Notas sobre el filtrado por programa
- Información de contacto
- Footer institucional con SIGMA y Universidad Surcolombiana

---

## 🔍 Reglas Especiales

### 1. Campo "Director" en Modalidades

El sistema aplica la siguiente lógica para el campo Director:

| Condición | Valor Mostrado |
|-----------|----------------|
| Tiene director asignado | Nombre completo del director |
| No tiene director Y modalidad NO requiere | **"No requerido"** |
| No tiene director Y modalidad SÍ requiere | **"Sin asignar"** |

**Modalidades que NO requieren director**:
- Plan Complementario Posgrado
- Producción Académica de Alto Nivel
- Seminario de Grado

### 2. Identificación de Líderes en Grupos

En modalidades grupales, el estudiante líder se marca con `(L)` después de su nombre.

**Ejemplo**: 
```
Juan Pérez (L), María García, Carlos López
```

### 3. Barras de Progreso Visuales

Las tablas de distribución incluyen barras visuales con las siguientes características:
- **Color rojo institucional**: Para porcentajes > 50%
- **Color dorado institucional**: Para porcentajes ≤ 50%
- **Ancho proporcional**: Representa el porcentaje visualmente

---

## 📊 Métricas e Indicadores Calculados

### Indicadores Básicos
1. **Total de Modalidades Activas**: COUNT de `student_modalities` con estados activos
2. **Total de Estudiantes**: COUNT DISTINCT de estudiantes en modalidades activas
3. **Total de Directores**: COUNT DISTINCT de directores asignados

### Indicadores de Eficiencia
1. **Promedio Estudiantes/Grupo**: AVG de estudiantes en modalidades grupales
2. **Modalidades sin Director**: COUNT donde `director_id IS NULL` Y `requiereDirector = true`
3. **Tasa de Progreso**: Calculada según estados avanzados vs iniciales

### Indicadores de Tiempo
1. **Días desde Inicio**: `CURRENT_DATE - start_date` por modalidad
2. **Promedio Días en Proceso**: AVG de días de todas las modalidades
3. **Modalidad Más Antigua**: MAX de días desde inicio

### Indicadores de Carga
1. **Ratio Estudiantes/Director**: Total estudiantes / Total directores
2. **Carga por Director**: COUNT de modalidades por director
3. **Carga Relativa**: Porcentaje respecto al director con mayor carga

---

## 📈 Visualizaciones Incluidas

### 1. Barras de Distribución Horizontal
- Se muestran en las tablas de distribución por tipo y estado
- Ancho proporcional al porcentaje
- Colores institucionales según el valor

### 2. Tablas con Fondo Alternado
- Filas alternas con fondo dorado claro
- Mejora la legibilidad de tablas largas

### 3. Tarjetas de Métricas
- Diseño destacado para indicadores clave
- Combinación de colores rojo y dorado

### 4. Alertas Visuales
- ⚠ Símbolo para alertas (modalidades sin director)
- Colores de fondo diferenciados según nivel de alerta

---

## 🚀 Optimizaciones y Mejores Prácticas

### Paginación Inteligente
- Nueva página para secciones principales
- Evita cortes abruptos en medio de tablas
- Mejora la presentación profesional

### Ordenamiento de Datos
- Distribuciones ordenadas por cantidad (descendente)
- Top N ordenados por relevancia
- Facilita identificación de patrones

### Manejo de Valores Nulos
- Verificaciones exhaustivas de campos opcionales
- Valores por defecto apropiados ("N/A", "Sin asignar", "No requerido")
- Previene errores de generación

### Rendimiento
- Query optimizadas con JPA
- Transacciones de solo lectura (`@Transactional(readOnly = true)`)
- Cálculos en memoria para métricas derivadas

---

## 🛠️ Mantenimiento y Extensibilidad

### Agregar Nueva Métrica al Resumen Ejecutivo

1. Agregar campo en `ExecutiveSummaryDTO`
2. Calcular valor en `ReportService.generateGlobalReport()`
3. Agregar fila en `PdfReport.addExecutiveSummary()`:

```java
addMetricRow(metricsTable, "Nueva Métrica",
    summary.getNuevaMetrica().toString(), INSTITUTIONAL_RED);
```

### Agregar Nueva Sección al PDF

1. Crear método privado en `PdfReport`:

```java
private void addNuevaSeccion(Document document, ...) throws DocumentException {
    addSectionTitle(document, "N. NUEVA SECCIÓN");
    // ... contenido de la sección
}
```

2. Invocar en `generatePDF()`:

```java
addNuevaSeccion(document, report.getNuevoDato());
```

### Modificar Colores Institucionales

Cambiar las constantes al inicio de la clase:

```java
private static final BaseColor INSTITUTIONAL_RED = new BaseColor(R, G, B);
private static final BaseColor INSTITUTIONAL_GOLD = new BaseColor(R, G, B);
```

---

## ❓ Preguntas Frecuentes

### ¿Por qué no veo modalidades de otros programas?
El reporte está filtrado automáticamente por el programa académico del usuario autenticado. Esto garantiza privacidad y relevancia de la información.

### ¿Puedo filtrar por fecha o estado específico?
Este endpoint genera el reporte completo de modalidades activas. Para filtros específicos, utiliza el endpoint `/reports/modalities/filtered/pdf` (RF-46).

### ¿Con qué frecuencia debo generar este reporte?
Se recomienda:
- **Mensualmente**: Para seguimiento general
- **Antes de reuniones de consejo**: Para toma de decisiones
- **Bajo demanda**: Cuando se requiera información actualizada

### ¿El reporte incluye modalidades completadas o canceladas?
No. Este reporte solo incluye modalidades **activas**. Para modalidades completadas, utiliza el endpoint `/reports/completed-modalities/pdf`.

### ¿Qué significa "Días desde Inicio"?
Es el número de días transcurridos desde la fecha de inicio (`start_date`) de la modalidad hasta la fecha actual. Ayuda a identificar modalidades que llevan mucho tiempo en proceso.

---

## 🔗 Endpoints Relacionados

| Endpoint | Descripción | Request Body |
|----------|-------------|--------------|
| `GET /reports/global/modalities` | Versión JSON del reporte | No |
| `POST /reports/modalities/filtered/pdf` | Reporte filtrado por criterios | Sí (ModalityReportFilterDTO) |
| `POST /reports/modalities/comparison/pdf` | Comparativa por tipos | Sí (ModalityComparisonFilterDTO) |
| `GET /reports/directors/assigned-modalities/pdf` | Modalidades por director | Opcional (DirectorReportFilterDTO) |
| `GET /reports/modalities/{id}/historical/pdf` | Histórico de una modalidad | No (PathVariable) |

---

## 📞 Contacto y Soporte

Para preguntas sobre este reporte o solicitudes de nuevas funcionalidades:
- **Sistema**: SIGMA - Sistema de Gestión de Modalidades de Grado
- **Institución**: Universidad Surcolombiana
- **Documentación técnica**: Ver código fuente en `com.SIGMA.USCO.report.service.PdfReport`

---

## 📅 Historial de Versiones

| Versión | Fecha | Cambios |
|---------|-------|---------|
| 1.0 | 2026-02-18 | Versión inicial del reporte |
| 1.1 | 2026-02-18 | Agregados indicadores de gestión y análisis de directores |
| 1.2 | 2026-02-18 | Implementados gráficos visuales y barras de progreso |

---

## ✅ Checklist de Validación

Antes de generar el reporte, verificar:

- [ ] Usuario autenticado con token JWT válido
- [ ] Usuario tiene permiso `PERM_VIEW_REPORT`
- [ ] Usuario pertenece a un programa académico
- [ ] Existen modalidades activas en el programa
- [ ] Base de datos accesible y consistente

---

**Generado por**: SIGMA - Sistema de Gestión de Modalidades de Grado  
**Última actualización**: 18 de Febrero de 2026

