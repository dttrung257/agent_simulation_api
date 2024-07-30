package com.uet.agent_simulation_api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Table(name = "experiments")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Experiment extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT")
    private BigInteger id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer finalStep;

    @Column(name = "model_id", nullable = false, insertable = false, updatable = false, columnDefinition = "BIGINT")
    private BigInteger modelId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", referencedColumnName = "id")
    @JsonIgnore
    private Model model;
}
