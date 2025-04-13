package com.example.sistemareportesincidentes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "incidente")
public class Incidente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idIncidente;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDateTime fechaCreacion;

    @Column(columnDefinition = "DATE")
    private LocalDateTime fechaResolucion;

    @Column(nullable = false)
    private Integer tiempoEstimadoResolucion;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Estado estado;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idCliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idTecnico", nullable = false)
    private Tecnico tecnicoAsignado;

    @OneToMany(mappedBy = "incidente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IncidenteDetalle> incidentesDetalles = new ArrayList<>();

    public enum Estado {
        ABIERTO,
        RESUELTO
    }

    public void addDetalle(IncidenteDetalle incidenteDetalle) {
        incidentesDetalles.add(incidenteDetalle);
        incidenteDetalle.setIncidente(this);
    }

    public void removeDetalle(IncidenteDetalle incidenteDetalle) {
        incidentesDetalles.remove(incidenteDetalle);
        incidenteDetalle.setIncidente(null);
    }
}
