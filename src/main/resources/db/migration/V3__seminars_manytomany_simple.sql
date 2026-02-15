-- ============================================
-- SQL SIMPLIFICADO - Seminarios con ManyToMany
-- Implementación simplificada sin tabla de enrollment
-- ============================================

-- ============================================
-- 1. ELIMINAR TABLAS ANTIGUAS SI EXISTEN
-- ============================================

DROP TABLE IF EXISTS seminar_enrollments;
DROP TABLE IF EXISTS seminar_students;
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
-- 3. CREAR TABLA DE RELACIÓN MUCHOS A MUCHOS
-- ============================================

CREATE TABLE seminar_students (
    seminar_id BIGINT NOT NULL COMMENT 'ID del seminario',
    student_id BIGINT NOT NULL COMMENT 'ID del estudiante (student_profile.user_id)',

    PRIMARY KEY (seminar_id, student_id),

    -- Foreign Keys
    CONSTRAINT fk_seminar_students_seminar
        FOREIGN KEY (seminar_id)
        REFERENCES seminars(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_seminar_students_student
        FOREIGN KEY (student_id)
        REFERENCES student_profiles(user_id)
        ON DELETE CASCADE

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Relación ManyToMany entre seminarios y estudiantes inscritos';

-- ============================================
-- 4. CREAR ÍNDICES PARA OPTIMIZAR CONSULTAS
-- ============================================

-- Índices para la tabla seminars
CREATE INDEX idx_seminar_academic_program ON seminars(academic_program_id);
CREATE INDEX idx_seminar_active ON seminars(active);
CREATE INDEX idx_seminar_status ON seminars(status);
CREATE INDEX idx_seminar_dates ON seminars(start_date, end_date);
CREATE INDEX idx_seminar_active_status ON seminars(active, status);
CREATE INDEX idx_seminar_cupos ON seminars(current_participants, max_participants);

-- Índices para la tabla seminar_students
CREATE INDEX idx_seminar_students_seminar ON seminar_students(seminar_id);
CREATE INDEX idx_seminar_students_student ON seminar_students(student_id);

-- ============================================
-- 5. TRIGGER PARA ACTUALIZAR current_participants
-- ============================================

DELIMITER $$

-- Trigger al insertar un estudiante
CREATE TRIGGER after_seminar_student_insert
AFTER INSERT ON seminar_students
FOR EACH ROW
BEGIN
    UPDATE seminars
    SET current_participants = (
        SELECT COUNT(*)
        FROM seminar_students
        WHERE seminar_id = NEW.seminar_id
    ),
    updated_at = NOW()
    WHERE id = NEW.seminar_id;
END$$

-- Trigger al eliminar un estudiante
CREATE TRIGGER after_seminar_student_delete
AFTER DELETE ON seminar_students
FOR EACH ROW
BEGIN
    UPDATE seminars
    SET current_participants = (
        SELECT COUNT(*)
        FROM seminar_students
        WHERE seminar_id = OLD.seminar_id
    ),
    updated_at = NOW()
    WHERE id = OLD.seminar_id;
END$$

DELIMITER ;

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
    AND TABLE_NAME IN ('seminars', 'seminar_students');

-- Verificar los constraints
SELECT
    CONSTRAINT_NAME,
    TABLE_NAME,
    CONSTRAINT_TYPE
FROM
    INFORMATION_SCHEMA.TABLE_CONSTRAINTS
WHERE
    TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME IN ('seminars', 'seminar_students')
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
    AND TABLE_NAME IN ('seminars', 'seminar_students')
ORDER BY
    TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX;

-- Verificar triggers
SELECT
    TRIGGER_NAME,
    EVENT_MANIPULATION,
    EVENT_OBJECT_TABLE,
    ACTION_TIMING
FROM
    INFORMATION_SCHEMA.TRIGGERS
WHERE
    TRIGGER_SCHEMA = DATABASE()
    AND EVENT_OBJECT_TABLE = 'seminar_students';

-- ============================================
-- 7. CONSULTAS ÚTILES
-- ============================================

-- Ver estudiantes inscritos en un seminario
/*
SELECT
    s.name AS seminario,
    u.name AS estudiante_nombre,
    u.last_name AS estudiante_apellido,
    u.email AS estudiante_email,
    ap.name AS programa
FROM seminar_students ss
JOIN seminars s ON ss.seminar_id = s.id
JOIN student_profiles sp ON ss.student_id = sp.user_id
JOIN user u ON sp.user_id = u.id
JOIN academic_programs ap ON sp.academic_program_id = ap.id
WHERE s.id = 1
ORDER BY u.last_name, u.name;
*/

-- Ver seminarios de un estudiante
/*
SELECT
    s.name AS seminario,
    s.status,
    s.current_participants,
    s.max_participants,
    s.start_date,
    s.end_date
FROM seminar_students ss
JOIN seminars s ON ss.seminar_id = s.id
WHERE ss.student_id = 1
ORDER BY s.start_date DESC;
*/

-- Ver cupos disponibles por seminario
/*
SELECT
    id,
    name,
    current_participants,
    max_participants,
    (max_participants - current_participants) AS cupos_disponibles,
    status,
    active
FROM seminars
WHERE active = TRUE
  AND status = 'OPEN'
ORDER BY start_date;
*/

-- ============================================
-- 8. NOTAS IMPORTANTES
-- ============================================

/*
VENTAJAS DE ESTA IMPLEMENTACIÓN:

1. ✅ SIMPLICIDAD
   - Solo 2 tablas (seminars, seminar_students)
   - Relación ManyToMany directa
   - Menos complejidad en el código

2. ✅ INTEGRIDAD AUTOMÁTICA
   - Triggers actualizan current_participants automáticamente
   - Constraints garantizan reglas del reglamento
   - CASCADE elimina inscripciones si se borra seminario/estudiante

3. ✅ RENDIMIENTO
   - Índices optimizados para consultas frecuentes
   - PRIMARY KEY compuesta en tabla de relación
   - Menos JOINs necesarios

4. ✅ MANTENIMIENTO
   - Fácil de entender y modificar
   - Sin lógica de negocio en la BD
   - Compatible con JPA/Hibernate ManyToMany

LIMITACIONES:

- ❌ No almacena fecha de inscripción individual
- ❌ No almacena calificaciones por estudiante
- ❌ No almacena observaciones por inscripción
- ❌ No diferencia estados (enrolled, completed, withdrawn)

CUÁNDO USAR ESTA IMPLEMENTACIÓN:
- Cuando solo necesitas saber quién está inscrito
- Cuando no necesitas tracking detallado de inscripciones
- Cuando la simplicidad es más importante que las funcionalidades avanzadas

CUÁNDO USAR LA IMPLEMENTACIÓN COMPLETA:
- Cuando necesitas calificaciones por estudiante
- Cuando necesitas historial de inscripciones
- Cuando necesitas estados de inscripción
- Cuando necesitas observaciones o comentarios
*/

-- ============================================
-- FIN DEL SCRIPT
-- ============================================

