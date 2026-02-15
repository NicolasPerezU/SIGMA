-- Migración para crear la tabla de Seminarios de Grado
-- Capítulo VIII del reglamento: SEMINARIO DE GRADO (Artículos 39-47)

CREATE TABLE IF NOT EXISTS seminars (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    academic_program_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(4000),
    total_cost DECIMAL(10, 2) NOT NULL,
    min_participants INT NOT NULL,
    max_participants INT NOT NULL,
    current_participants INT NOT NULL DEFAULT 0,
    total_hours INT NOT NULL DEFAULT 160,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    status VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    start_date DATETIME,
    end_date DATETIME,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_seminar_academic_program FOREIGN KEY (academic_program_id)
        REFERENCES academic_programs(id) ON DELETE RESTRICT,
    CONSTRAINT chk_participants_min CHECK (min_participants >= 15),
    CONSTRAINT chk_participants_max CHECK (max_participants <= 35),
    CONSTRAINT chk_participants_range CHECK (min_participants <= max_participants),
    CONSTRAINT chk_total_hours CHECK (total_hours >= 160),
    CONSTRAINT chk_current_participants CHECK (current_participants >= 0 AND current_participants <= max_participants)
);

-- Índices para mejorar el rendimiento de las consultas
CREATE INDEX idx_seminar_academic_program ON seminars(academic_program_id);
CREATE INDEX idx_seminar_active ON seminars(active);
CREATE INDEX idx_seminar_status ON seminars(status);
CREATE INDEX idx_seminar_dates ON seminars(start_date, end_date);

-- Comentarios en la tabla
ALTER TABLE seminars COMMENT = 'Tabla de Seminarios de Grado según Capítulo VIII del reglamento';

