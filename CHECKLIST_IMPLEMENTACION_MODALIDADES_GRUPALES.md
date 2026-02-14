# ✅ CHECKLIST DE IMPLEMENTACIÓN - MODALIDADES GRUPALES

**Proyecto:** Sistema SIGMA - Modalidades Grupales  
**Versión:** 1.0  
**Fecha Inicio:** __________  
**Fecha Estimada de Finalización:** __________

---

## 📋 FASE 1: PREPARACIÓN (Día 1-2)

### Base de Datos y Migraciones

- [ ] Crear branch `feature/group-modalities` desde `develop`
- [ ] Crear script de migración para tabla `student_modality_members`
  - [ ] Columnas: id, student_modality_id, student_id, is_leader, status, joined_at, left_at, leave_reason
  - [ ] Índices: student_modality_id, student_id, status
  - [ ] Foreign keys configuradas
- [ ] Crear script de migración para tabla `modality_invitations`
  - [ ] Columnas: id, student_modality_id, inviter_id, invitee_id, status, invited_at, responded_at, message, rejection_reason
  - [ ] Índices: student_modality_id, inviter_id, invitee_id, status
  - [ ] Foreign keys configuradas
- [ ] Modificar tabla `student_modalities`
  - [ ] Agregar columna `modality_type` (VARCHAR(50))
  - [ ] Renombrar columna `student_id` a `leader_id`
  - [ ] Crear índice en `modality_type`
- [ ] Migrar datos existentes
  - [ ] Script para convertir modalidades actuales a INDIVIDUAL
  - [ ] Script para crear StudentModalityMember para cada StudentModality existente
- [ ] Ejecutar migraciones en ambiente de desarrollo
- [ ] Verificar integridad de datos
- [ ] Rollback y retry si hay problemas

**Responsable:** __________  
**Fecha Completada:** __________

---

## 📋 FASE 2: CAPA DE DATOS (Día 3-5)

### Repositorios

- [ ] Crear `StudentModalityMemberRepository.java`
  - [ ] Extender JpaRepository
  - [ ] Query: `findByStudentModalityIdAndStatus`
  - [ ] Query: `countByStudentModalityIdAndStatus`
  - [ ] Query: `findByStudentIdAndStatus`
  - [ ] Query: `findByStudentModalityIdAndStudentId`
  - [ ] Query: `existsByStudentIdAndStatus`
- [ ] Crear `ModalityInvitationRepository.java`
  - [ ] Extender JpaRepository
  - [ ] Query: `findByInviteeIdAndStatus`
  - [ ] Query: `findByStudentModalityIdAndStatus`
  - [ ] Query: `countByStudentModalityIdAndStatus`
  - [ ] Query: `findByStudentModalityIdAndInviteeIdAndStatus`
  - [ ] Query: `existsByStudentModalityIdAndInviteeId`
- [ ] Modificar `StudentModalityRepository.java`
  - [ ] Agregar query para buscar por líder
  - [ ] Agregar query para buscar por tipo de modalidad
  - [ ] Agregar query para buscar modalidades grupales activas

### Tests de Repositorio

- [ ] Tests para `StudentModalityMemberRepository`
  - [ ] Test: crear miembro
  - [ ] Test: buscar por modalidad y estado
  - [ ] Test: contar miembros activos
  - [ ] Test: verificar si estudiante tiene modalidad activa
- [ ] Tests para `ModalityInvitationRepository`
  - [ ] Test: crear invitación
  - [ ] Test: buscar invitaciones pendientes de un estudiante
  - [ ] Test: contar invitaciones de una modalidad
  - [ ] Test: buscar invitación específica

**Responsable:** __________  
**Fecha Completada:** __________

---

## 📋 FASE 3: DTOs (Día 6-7)

### Request DTOs

- [ ] Crear `StartModalityRequestDTO.java`
  - [ ] Campo: programDegreeModalityId
  - [ ] Campo: modalityType (INDIVIDUAL/GROUP)
  - [ ] Validaciones con @NotNull, @NotBlank
- [ ] Crear `SendInvitationRequestDTO.java`
  - [ ] Campo: inviteeId
  - [ ] Campo: message (opcional)
  - [ ] Validaciones
- [ ] Crear `InvitationResponseRequestDTO.java`
  - [ ] Campo: reason (para rechazos, opcional)
  - [ ] Validaciones
- [ ] Crear `LeaveGroupRequestDTO.java`
  - [ ] Campo: reason
  - [ ] Validaciones

### Response DTOs

- [ ] Crear `StudentModalityMemberDTO.java`
  - [ ] Campos: id, student (UserBasicDTO), isLeader, status, joinedAt, leftAt
  - [ ] Mapper desde entidad
- [ ] Crear `ModalityInvitationDTO.java`
  - [ ] Campos: id, modalityId, modalityName, inviter, invitee, status, message, invitedAt, respondedAt
  - [ ] Mapper desde entidad
- [ ] Crear `EligibleStudentDTO.java`
  - [ ] Campos: id, fullName, studentCode, gradeAverage, approvedCredits
  - [ ] Mapper desde User
- [ ] Actualizar `StudentModalityDTO.java`
  - [ ] Agregar campo: modalityType
  - [ ] Agregar campo: leader
  - [ ] Agregar campo: members (lista)
  - [ ] Agregar campo: memberCount

**Responsable:** __________  
**Fecha Completada:** __________

---

## 📋 FASE 4: SERVICIOS - PARTE 1 (Día 8-10)

### StudentModalityMemberService

- [ ] Crear `StudentModalityMemberService.java`
  - [ ] Método: `createMember(modalityId, studentId, isLeader)`
  - [ ] Método: `getActiveMembers(modalityId)`
  - [ ] Método: `countActiveMembers(modalityId)`
  - [ ] Método: `isActiveMember(modalityId, studentId)`
  - [ ] Método: `leaveGroup(modalityId, studentId, reason)`
  - [ ] Validaciones en cada método
- [ ] Tests unitarios
  - [ ] Test: crear miembro con validaciones
  - [ ] Test: obtener miembros activos
  - [ ] Test: contar miembros
  - [ ] Test: verificar membresía
  - [ ] Test: abandonar grupo

**Responsable:** __________  
**Fecha Completada:** __________

---

## 📋 FASE 5: SERVICIOS - PARTE 2 (Día 11-13)

### ModalityInvitationService

- [ ] Crear `ModalityInvitationService.java`
  - [ ] Método: `sendInvitation(modalityId, inviteeId, message)`
    - [ ] Validar que el usuario es líder
    - [ ] Validar límite de miembros
    - [ ] Validar mismo programa académico
    - [ ] Validar que invitado no tiene modalidad activa
    - [ ] Crear invitación con status PENDING
    - [ ] Publicar evento `InvitationSentEvent`
  - [ ] Método: `acceptInvitation(invitationId, userId)`
    - [ ] Validar que la invitación es del usuario
    - [ ] Validar requisitos académicos (créditos, promedio)
    - [ ] Actualizar invitación a ACCEPTED
    - [ ] Crear StudentModalityMember
    - [ ] Publicar evento `InvitationAcceptedEvent`
  - [ ] Método: `rejectInvitation(invitationId, userId, reason)`
    - [ ] Validar que la invitación es del usuario
    - [ ] Actualizar invitación a REJECTED
    - [ ] Publicar evento `InvitationRejectedEvent`
  - [ ] Método: `cancelInvitation(invitationId, userId)`
    - [ ] Validar que el usuario es líder
    - [ ] Actualizar invitación a CANCELLED
    - [ ] Publicar evento `InvitationCancelledEvent`
  - [ ] Método: `getPendingInvitations(userId)`
  - [ ] Método: `getInvitationsByModality(modalityId)`

### Tests Unitarios

- [ ] Tests para cada método de ModalityInvitationService
  - [ ] Test: enviar invitación válida
  - [ ] Test: enviar invitación con validaciones fallidas
  - [ ] Test: aceptar invitación cumpliendo requisitos
  - [ ] Test: rechazar invitación
  - [ ] Test: cancelar invitación
  - [ ] Test: obtener invitaciones pendientes

**Responsable:** __________  
**Fecha Completada:** __________

---

## 📋 FASE 6: SERVICIOS - PARTE 3 (Día 14-15)

### Modificar ModalityService

- [ ] Actualizar método `startStudentModality`
  - [ ] Aceptar parámetro `modalityType`
  - [ ] Si INDIVIDUAL: flujo actual + crear StudentModalityMember
  - [ ] Si GROUP: crear StudentModality, crear Member para líder, retornar
- [ ] Crear método `canStartModality(modalityId)`
  - [ ] Para modalidades grupales: verificar que hay al menos 2 miembros activos
  - [ ] Para individuales: validación actual
- [ ] Actualizar método `getModalityDetails`
  - [ ] Incluir lista de miembros
  - [ ] Incluir información de invitaciones
- [ ] Actualizar validaciones de permisos
  - [ ] Verificar que el usuario es miembro activo en operaciones de documentos

### Tests de Integración

- [ ] Test: iniciar modalidad individual
- [ ] Test: iniciar modalidad grupal
- [ ] Test: validar que solo miembros activos pueden subir documentos
- [ ] Test: validar que miembros LEFT no tienen permisos

**Responsable:** __________  
**Fecha Completada:** __________

---

## 📋 FASE 7: CONTROLADORES (Día 16-18)

### ModalityInvitationController

- [ ] Crear `ModalityInvitationController.java`
  - [ ] `GET /api/modalities/{id}/eligible-students`
    - [ ] Retorna estudiantes elegibles para invitar
    - [ ] Filtro por nombre
    - [ ] Paginación
  - [ ] `POST /api/modalities/{id}/invitations`
    - [ ] Enviar invitación
    - [ ] Validar permisos (solo líder)
  - [ ] `GET /api/invitations/pending`
    - [ ] Obtener invitaciones pendientes del usuario actual
  - [ ] `GET /api/modalities/{id}/invitations`
    - [ ] Obtener invitaciones de una modalidad
    - [ ] Validar permisos (solo miembros)
  - [ ] `POST /api/invitations/{id}/accept`
    - [ ] Aceptar invitación
  - [ ] `POST /api/invitations/{id}/reject`
    - [ ] Rechazar invitación
  - [ ] `DELETE /api/invitations/{id}`
    - [ ] Cancelar invitación (solo líder)

### StudentModalityMemberController

- [ ] Crear `StudentModalityMemberController.java`
  - [ ] `GET /api/modalities/{id}/members`
    - [ ] Listar miembros de la modalidad
    - [ ] Incluir información de estado
  - [ ] `POST /api/modalities/{id}/leave`
    - [ ] Abandonar grupo
    - [ ] Validar permisos

### Actualizar ModalityController

- [ ] Modificar `POST /api/modalities/start`
  - [ ] Aceptar parámetro `modalityType`
  - [ ] Ajustar respuesta según tipo

### Documentación Swagger

- [ ] Documentar todos los nuevos endpoints
- [ ] Agregar ejemplos de request/response
- [ ] Documentar códigos de error

**Responsable:** __________  
**Fecha Completada:** __________

---

## 📋 FASE 8: EVENTOS Y NOTIFICACIONES (Día 19-21)

### Eventos de Dominio

- [ ] Crear `InvitationSentEvent.java`
  - [ ] Campos: invitation, modality
- [ ] Crear `InvitationAcceptedEvent.java`
  - [ ] Campos: invitation, member, modality
- [ ] Crear `InvitationRejectedEvent.java`
  - [ ] Campos: invitation, modality
- [ ] Crear `InvitationCancelledEvent.java`
  - [ ] Campos: invitation, modality
- [ ] Crear `MemberJoinedEvent.java`
  - [ ] Campos: member, modality
- [ ] Crear `MemberLeftEvent.java`
  - [ ] Campos: member, modality
- [ ] Crear `GroupReadyEvent.java`
  - [ ] Campos: modality

### Listeners

- [ ] Crear `ModalityInvitationListener.java`
  - [ ] Handler para `InvitationSentEvent`
    - [ ] Crear notificación MODALITY_INVITATION_RECEIVED para invitado
  - [ ] Handler para `InvitationAcceptedEvent`
    - [ ] Crear notificación MODALITY_INVITATION_ACCEPTED para líder
    - [ ] Crear notificación MODALITY_MEMBER_JOINED para todos los miembros
  - [ ] Handler para `InvitationRejectedEvent`
    - [ ] Crear notificación MODALITY_INVITATION_REJECTED para líder
  - [ ] Handler para `InvitationCancelledEvent`
    - [ ] Crear notificación MODALITY_INVITATION_CANCELLED para invitado
  - [ ] Handler para `MemberLeftEvent`
    - [ ] Crear notificación MODALITY_MEMBER_LEFT para miembros restantes
  - [ ] Handler para `GroupReadyEvent`
    - [ ] Crear notificación MODALITY_GROUP_READY para todos los miembros

### Tests

- [ ] Tests de eventos
- [ ] Tests de listeners
- [ ] Verificar que las notificaciones se crean correctamente

**Responsable:** __________  
**Fecha Completada:** __________

---

## 📋 FASE 9: SEGURIDAD Y VALIDACIONES (Día 22-23)

### ModalitySecurityService

- [ ] Crear `ModalitySecurityService.java`
  - [ ] Método: `isLeader(modalityId, userId)`
  - [ ] Método: `isActiveMember(modalityId, userId)`
  - [ ] Método: `isMember(modalityId, userId)`
  - [ ] Método: `canInvite(modalityId, userId)`
  - [ ] Método: `canAccessModality(modalityId, userId)`

### Anotaciones de Seguridad

- [ ] Agregar `@PreAuthorize` en endpoints de invitación
  - [ ] Solo líder puede enviar/cancelar invitaciones
  - [ ] Solo invitado puede aceptar/rechazar
- [ ] Agregar `@PreAuthorize` en endpoints de miembros
  - [ ] Solo miembros pueden ver detalles
  - [ ] Solo miembros activos pueden subir documentos
- [ ] Agregar `@PreAuthorize` en endpoints de modalidad
  - [ ] Verificar membresía para acciones sensibles

### Tests de Seguridad

- [ ] Test: usuario no autorizado no puede enviar invitaciones
- [ ] Test: no-líder no puede cancelar invitaciones
- [ ] Test: no-miembro no puede ver detalles de modalidad
- [ ] Test: miembro LEFT no puede subir documentos

**Responsable:** __________  
**Fecha Completada:** __________

---

## 📋 FASE 10: TESTS DE INTEGRACIÓN (Día 24-25)

### Escenarios Completos

- [ ] Test E2E: Flujo completo de modalidad grupal
  - [ ] Estudiante inicia modalidad grupal
  - [ ] Líder invita a 2 estudiantes
  - [ ] Estudiantes aceptan invitaciones
  - [ ] Grupo sube documentos
  - [ ] Modalidad se completa
  - [ ] Se generan 3 certificados
- [ ] Test E2E: Flujo con rechazo
  - [ ] Líder invita estudiante
  - [ ] Estudiante rechaza
  - [ ] Líder invita a otro
  - [ ] Segundo estudiante acepta
- [ ] Test E2E: Flujo con abandono
  - [ ] Grupo de 3 inicia modalidad
  - [ ] 1 miembro abandona
  - [ ] Grupo continúa con 2
  - [ ] Se completa con 2 certificados
- [ ] Test E2E: Validaciones de requisitos
  - [ ] Estudiante sin créditos suficientes no puede aceptar
  - [ ] Sistema notifica al líder del rechazo automático

### Tests de Performance

- [ ] Test: consulta de estudiantes elegibles con 10,000 estudiantes
- [ ] Test: creación de invitaciones en paralelo
- [ ] Test: carga de notificaciones con múltiples grupos

**Responsable:** __________  
**Fecha Completada:** __________

---

## 📋 FASE 11: CERTIFICADOS (Día 26-27)

### Modificar AcademicCertificateService

- [ ] Actualizar método de generación de certificados
  - [ ] Si modalidad es INDIVIDUAL: 1 certificado (como siempre)
  - [ ] Si modalidad es GROUP:
    - [ ] Iterar sobre StudentModalityMembers con status ACTIVE
    - [ ] Generar 1 certificado por cada miembro activo
    - [ ] Incluir datos específicos de cada estudiante
- [ ] Actualizar plantillas de certificados
  - [ ] Indicar si fue modalidad grupal
  - [ ] Incluir nombres de compañeros (opcional)

### Tests

- [ ] Test: generar certificados para modalidad individual
- [ ] Test: generar 3 certificados para grupo de 3
- [ ] Test: generar 2 certificados si 1 miembro abandonó

**Responsable:** __________  
**Fecha Completada:** __________

---

## 📋 FASE 12: FRONTEND - BACKEND INTEGRATION (Día 28-29)

### Ajustes en Backend

- [ ] Configurar CORS para nuevos endpoints
- [ ] Agregar rate limiting para invitaciones
- [ ] Configurar WebSockets para notificaciones en tiempo real (si aplica)
- [ ] Verificar logs y métricas

### Documentación para Frontend

- [ ] Documentar todos los endpoints en Swagger
- [ ] Crear ejemplos de payloads
- [ ] Documentar flujos de estados
- [ ] Crear guía de integración

**Responsable:** __________  
**Fecha Completada:** __________

---

## 📋 FASE 13: QA Y REFINAMIENTO (Día 30+)

### Testing Manual

- [ ] Probar flujo completo en ambiente de desarrollo
- [ ] Verificar todas las notificaciones
- [ ] Probar casos edge
- [ ] Verificar permisos y seguridad
- [ ] Probar en diferentes navegadores (si aplica)

### Correcciones

- [ ] Corregir bugs encontrados
- [ ] Optimizar queries lentas
- [ ] Mejorar mensajes de error
- [ ] Refinar validaciones

### Code Review

- [ ] Revisión de código por pares
- [ ] Verificar cumplimiento de estándares
- [ ] Revisar tests coverage (objetivo: >80%)
- [ ] Validar documentación

**Responsable:** __________  
**Fecha Completada:** __________

---

## 📋 FASE 14: DEPLOYMENT (Día 31+)

### Preparación

- [ ] Ejecutar migraciones en staging
- [ ] Verificar datos migrados correctamente
- [ ] Smoke tests en staging
- [ ] Preparar plan de rollback

### Deployment a Producción

- [ ] Backup de base de datos
- [ ] Ejecutar migraciones
- [ ] Deploy de aplicación
- [ ] Verificar health checks
- [ ] Monitorear logs
- [ ] Verificar métricas

### Post-Deployment

- [ ] Smoke tests en producción
- [ ] Monitoreo de errores (24-48h)
- [ ] Comunicar a usuarios sobre nueva funcionalidad
- [ ] Estar disponible para soporte

**Responsable:** __________  
**Fecha Completada:** __________

---

## 📊 MÉTRICAS DE CALIDAD

### Cobertura de Tests
- [ ] Repositorios: > 80%
- [ ] Servicios: > 85%
- [ ] Controladores: > 75%
- [ ] General: > 80%

### Performance
- [ ] Endpoints responden en < 500ms (p95)
- [ ] Queries de listado < 200ms
- [ ] Sin N+1 queries

### Seguridad
- [ ] Todos los endpoints tienen autorización
- [ ] Validación de entrada en todos los DTOs
- [ ] Sin SQL injection posible
- [ ] Logs de auditoría implementados

---

## 🐛 BUGS Y PROBLEMAS

| ID | Descripción | Prioridad | Estado | Responsable |
|----|-------------|-----------|--------|-------------|
| | | | | |
| | | | | |
| | | | | |

---

## 📝 NOTAS Y OBSERVACIONES

_Espacio para notas durante la implementación:_

---

---

**Fecha de Inicio:** __________  
**Fecha de Finalización:** __________  
**Progreso General:** _____ / 100%

---

**Firmas:**

Tech Lead: ________________  
QA Lead: ________________  
Product Owner: ________________

