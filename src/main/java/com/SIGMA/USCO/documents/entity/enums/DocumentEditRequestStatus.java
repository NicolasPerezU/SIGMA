package com.SIGMA.USCO.documents.entity.enums;

public enum DocumentEditRequestStatus {

    /** Solicitud pendiente – esperando votos de los jurados primarios */
    PENDING,

    /** Los jurados primarios tienen votos divididos – se requiere jurado de desempate */
    TIEBREAKER_REQUIRED,

    /** Solicitud aprobada – el estudiante puede resubir el documento */
    APPROVED,

    /** Solicitud rechazada */
    REJECTED
}
