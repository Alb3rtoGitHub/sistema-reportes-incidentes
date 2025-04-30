package com.example.sistemareportesincidentes.repository;

import com.example.sistemareportesincidentes.entity.Incidente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IncidenteRepository extends JpaRepository<Incidente, Long> {
    @Query("SELECT i FROM Incidente i WHERE i.tecnicoAsignado.idTecnico = :idTecnico AND i.fechaCreacion >= :fecha")
    List<Incidente> findIncidentesPorTecnicoYFecha(Long idTecnico, LocalDateTime fecha);

    @Query("SELECT  i FROM Incidente i JOIN i.incidentesDetalles d WHERE d.servicio.idServicio = :idServicio")
    List<Incidente> findIncidentesPorServicioId(Long idServicio);

    @Query("SELECT i FROM Incidente i JOIN i.incidentesDetalles d WHERE d.tipoProblema.idTipoProblema = :idTipoProblema")
    List<Incidente> findIncidentesPorTipoProblemaId(Long idTipoProblema);
}
