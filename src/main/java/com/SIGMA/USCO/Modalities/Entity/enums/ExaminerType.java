package com.SIGMA.USCO.Modalities.Entity.enums;


public enum ExaminerType {
    PRIMARY_EXAMINER_1,
    PRIMARY_EXAMINER_2,
    TIEBREAKER_EXAMINER;

    public String toSpanish() {
        return switch (this) {
            case PRIMARY_EXAMINER_1 -> "Jurado Principal 1";
            case PRIMARY_EXAMINER_2 -> "Jurado Principal 2";
            case TIEBREAKER_EXAMINER -> "Jurado de Desempate";
        };
    }
}
