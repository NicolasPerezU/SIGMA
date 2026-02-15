-- ============================================
-- SQL PARA ACTUALIZAR LA BASE DE DATOS
-- Implementación de Seminario de Grado
-- Capítulo VIII del reglamento (Artículos 39-47)
-- ============================================

-- ============================================
-- 1. ELIMINAR TABLA ANTIGUA SI EXISTE
-- ============================================

DROP TABLE IF EXISTS seminar_enrollments;
DROP TABLE IF EXISTS seminars;

-- ============================================
-- 2. CREAR TABLA DE SEMINARIOS
-- ============================================

CREATE TABLE seminars (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- Relación con programa académico
    academic_program_id BIGINT NOT NULL,

    -- Información básica del seminario
    name VARCHAR(200) NOT NULL COMMENT 'Nombre del seminario',
    description VARCHAR(4000) COMMENT 'Descripción detallada del seminario',

    -- Costos (Art. 43 - autofinanciado)
    total_cost DECIMAL(10, 2) NOT NULL COMMENT 'Costo total del seminario',

    -- Participantes (Art. 43 - mínimo 15, máximo 35)
    min_participants INT NOT NULL COMMENT 'Mínimo de participantes (Art. 43: 15)',
    max_participants INT NOT NULL COMMENT 'Máximo de participantes (Art. 43: 35)',
    current_participants INT NOT NULL DEFAULT 0 COMMENT 'Número actual de inscritos',

    -- Intensidad horaria (Art. 42 - mínimo 160 horas)
    total_hours INT NOT NULL DEFAULT 160 COMMENT 'Intensidad horaria total (Art. 42: mínimo 160 horas)',

    -- Estado del seminario
    active BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Si el seminario está activo',
    status VARCHAR(50) NOT NULL DEFAULT 'OPEN' COMMENT 'Estado: OPEN, CLOSED, IN_PROGRESS, COMPLETED',

    -- Fechas
    start_date DATETIME COMMENT 'Fecha de inicio del seminario',
    end_date DATETIME COMMENT 'Fecha de finalización del seminario',
    created_at DATETIME NOT NULL COMMENT 'Fecha de creación',
    updated_at DATETIME NOT NULL COMMENT 'Fecha de última actualización',

    -- Foreign Keys
    CONSTRAINT fk_seminar_academic_program
        FOREIGN KEY (academic_program_id)
        REFERENCES academic_programs(id)
        ON DELETE RESTRICT,

    -- Constraints de validación según el reglamento
    CONSTRAINT chk_min_participants
        CHECK (min_participants >= 15),

    CONSTRAINT chk_max_participants
        CHECK (max_participants <= 35),

    CONSTRAINT chk_participants_range
        CHECK (min_participants <= max_participants),

    CONSTRAINT chk_total_hours
        CHECK (total_hours >= 160),

    CONSTRAINT chk_current_participants
        CHECK (current_participants >= 0 AND current_participants <= max_participants),

    CONSTRAINT chk_total_cost
        CHECK (total_cost > 0)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Tabla de seminarios de grado según Capítulo VIII del reglamento';

-- ============================================
-- 3. CREAR TABLA DE INSCRIPCIONES A SEMINARIOS
-- ============================================

CREATE TABLE seminar_enrollments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- Relaciones
    seminar_id BIGINT NOT NULL COMMENT 'ID del seminario',
    student_id BIGINT NOT NULL COMMENT 'ID del estudiante (student_profile)',

    -- Estado de la inscripción
    status VARCHAR(50) NOT NULL DEFAULT 'ENROLLED' COMMENT 'Estado: ENROLLED, COMPLETED, WITHDRAWN, FAILED',

    -- Observaciones
    observations VARCHAR(2000) COMMENT 'Observaciones o comentarios',

    -- Fechas importantes
    enrollment_date DATETIME COMMENT 'Fecha de inscripción',
    completion_date DATETIME COMMENT 'Fecha de finalización',

    -- Calificación (Art. 44 - evaluación por módulos)
    final_grade DECIMAL(5, 2) COMMENT 'Nota final del seminario',
    passed BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Si aprobó el seminario',

    -- Auditoría
    created_at DATETIME NOT NULL COMMENT 'Fecha de creación del registro',
    updated_at DATETIME NOT NULL COMMENT 'Fecha de última actualización',

    -- Foreign Keys
    CONSTRAINT fk_enrollment_seminar
        FOREIGN KEY (seminar_id)
        REFERENCES seminars(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_enrollment_student
        FOREIGN KEY (student_id)
        REFERENCES student_profiles(user_id)
        ON DELETE RESTRICT,

    -- Constraint de unicidad: un estudiante solo puede inscribirse una vez en un seminario
    CONSTRAINT uk_seminar_student
        UNIQUE (seminar_id, student_id)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Inscripciones de estudiantes a seminarios de grado';

-- ============================================
-- 4. CREAR ÍNDICES PARA OPTIMIZAR CONSULTAS
-- ============================================

-- Índices para la tabla seminars
CREATE INDEX idx_seminar_academic_program ON seminars(academic_program_id);
CREATE INDEX idx_seminar_active ON seminars(active);
CREATE INDEX idx_seminar_status ON seminars(status);
CREATE INDEX idx_seminar_dates ON seminars(start_date, end_date);
CREATE INDEX idx_seminar_active_status ON seminars(active, status);

-- Índices para la tabla seminar_enrollments
CREATE INDEX idx_enrollment_seminar ON seminar_enrollments(seminar_id);
CREATE INDEX idx_enrollment_student ON seminar_enrollments(student_id);
CREATE INDEX idx_enrollment_status ON seminar_enrollments(status);
CREATE INDEX idx_enrollment_seminar_status ON seminar_enrollments(seminar_id, status);

-- ============================================
-- 5. DATOS DE EJEMPLO (OPCIONAL - COMENTADO)
-- ============================================

/*
-- Ejemplo de seminario para Ingeniería de Software
INSERT INTO seminars (
    academic_program_id,
    name,
    description,
    total_cost,
    min_participants,
    max_participants,
    current_participants,
    total_hours,
    active,
    status,
    start_date,
    end_date,
    created_at,
    updated_at
) VALUES (
    1, -- Asume que 1 es el ID de Ingeniería de Software
    'SEMINARIO DE ACTUALIZACIÓN EN ARQUITECTURAS MODERNAS DE SOFTWARE',
    'Seminario de profundización en arquitecturas de software modernas, incluyendo microservicios, arquitecturas serverless, event-driven architecture y patrones de diseño contemporáneos. Incluye talleres prácticos y casos de estudio de la industria.',
    1500000.00,
    15,
    30,
    0,
    180,
    TRUE,
    'OPEN',
    '2026-03-01 08:00:00',
    '2026-06-30 18:00:00',
    NOW(),
    NOW()
);

-- Ejemplo de seminario para Ingeniería Civil
INSERT INTO seminars (
    academic_program_id,
    name,
    description,
    total_cost,
    min_participants,
    max_participants,
    current_participants,
    total_hours,
    active,
    status,
    start_date,
    end_date,
    created_at,
    updated_at
) VALUES (
    2, -- Asume que 2 es el ID de Ingeniería Civil
    'SEMINARIO DE GESTIÓN DE PROYECTOS DE CONSTRUCCIÓN',
    'Seminario de actualización en gestión y administración de proyectos de construcción civil, con énfasis en metodologías ágiles aplicadas a la construcción, uso de BIM, y gestión de riesgos.',
    1800000.00,
    15,
    35,
    0,
    200,
    TRUE,
    'OPEN',
    '2026-04-01 08:00:00',
    '2026-07-31 18:00:00',
    NOW(),
    NOW()
);
*/

-- ============================================
-- 6. VERIFICACIONES POST-INSTALACIÓN
-- ============================================

-- Verificar que las tablas se crearon correctamente
SELECT
    TABLE_NAME,
    TABLE_COMMENT
FROM
    INFORMATION_SCHEMA.TABLES
WHERE
    TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME IN ('seminars', 'seminar_enrollments');

-- Verificar los constraints
SELECT
    CONSTRAINT_NAME,
    TABLE_NAME,
    CONSTRAINT_TYPE
FROM
    INFORMATION_SCHEMA.TABLE_CONSTRAINTS
WHERE
    TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME IN ('seminars', 'seminar_enrollments')
ORDER BY
    TABLE_NAME, CONSTRAINT_TYPE;

-- Verificar los índices
SELECT
    TABLE_NAME,
    INDEX_NAME,
    COLUMN_NAME,
    SEQ_IN_INDEX
FROM
    INFORMATION_SCHEMA.STATISTICS
WHERE
    TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME IN ('seminars', 'seminar_enrollments')
ORDER BY
    TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX;

-- ============================================
-- 7. NOTAS Y REFERENCIAS
-- ============================================

/*
REFERENCIAS AL REGLAMENTO:

Artículo 39 - Definición:
- Los seminarios son actividades de actualización o profundización

Artículo 40 - Objetivo:
- Profundizar en un área específica de ingeniería

Artículo 41 - Requisitos:
a) Haber aprobado el 90% de los créditos académicos
b) Presentar solicitud por escrito al Consejo del Programa

Artículo 42 - Intensidad Horaria:
- Mínimo 160 horas de trabajo presencial
- Estructurado por módulos temáticos

Artículo 43 - Conformación de Grupos:
- Mínimo: 15 estudiantes
- Máximo: 35 estudiantes
- Autofinanciado

Artículo 44 - Evaluación:
- Cada módulo evaluado por separado
- Informar resultados dentro de 5 días hábiles

Artículo 45 - Asistencia:
- Presencial
- Mínimo 80% de asistencia por módulo
- Reprobación de un módulo = pérdida del seminario

Artículo 46 - Reporte Final:
- Coordinador presenta reporte al Consejo de Facultad
- Estudiantes presentan certificado/diploma

Artículo 47 - Oferta y Programación:
a) Cada programa define si ofrece seminario semestralmente
b-e) Criterios de aprobación y evaluación configurables

ESTADOS DEL SEMINARIO (status):
- OPEN: Abierto para inscripciones
- CLOSED: Cerrado (sin cupos o fecha límite alcanzada)
- IN_PROGRESS: En curso
- COMPLETED: Finalizado

ESTADOS DE INSCRIPCIÓN (enrollment status):
- ENROLLED: Inscrito activamente
- COMPLETED: Completó el seminario exitosamente
- WITHDRAWN: Se retiró del seminario
- FAILED: Reprobó el seminario
*/

-- ============================================
-- FIN DEL SCRIPT
-- ============================================

