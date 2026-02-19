-- Agregar columna invitation_id a la tabla notification
-- Esta columna almacena el ID de la invitación asociada cuando la notificación
-- es de tipo MODALITY_INVITATION_RECEIVED

ALTER TABLE notification
ADD COLUMN invitation_id BIGINT;

-- Crear índice para mejorar rendimiento de búsquedas por invitation_id
CREATE INDEX idx_notification_invitation_id ON notification(invitation_id);

-- Comentarios explicativos
COMMENT ON COLUMN notification.invitation_id IS 'ID de la invitación asociada (tabla modality_invitations). Se usa para notificaciones de tipo MODALITY_INVITATION_RECEIVED';

