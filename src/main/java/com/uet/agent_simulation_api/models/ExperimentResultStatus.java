package com.uet.agent_simulation_api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigInteger;

@Entity
@Table(name = "experiment_result_statuses")
@Getter
@Setter
@SuperBuilder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ExperimentResultStatus extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT")
    private BigInteger id;

    @Column(name = "experiment_id", nullable = false, insertable = false, updatable = false, columnDefinition = "BIGINT")
    private BigInteger experimentId;

    @Column(nullable = false, columnDefinition = "TINYINT")
    private Integer status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "experiment_id", referencedColumnName = "id")
    @JsonIgnore
    private Experiment experiment;
}
