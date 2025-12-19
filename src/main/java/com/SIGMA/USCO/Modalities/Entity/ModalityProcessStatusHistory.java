package com.SIGMA.USCO.Modalities.Entity;

import com.SIGMA.USCO.Modalities.Entity.enums.ModalityProcessStatus;
import com.SIGMA.USCO.Users.Entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "modality_process_status_history")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModalityProcessStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private StudentModality studentModality;

    @Enumerated(EnumType.STRING)
    private ModalityProcessStatus status;

    private LocalDateTime changeDate;

    @ManyToOne
    private User responsible;

    @Column(length = 5000)
    private String observations;

}
