package com.example.sistemareportesincidentes.repository;

import com.example.sistemareportesincidentes.entity.IncidenteDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidenteDetalleRepository extends JpaRepository<IncidenteDetalle, Long> {
    List<IncidenteDetalle> findByTipoProblemaIdTipoProblema(Long idTipoProblema);

    List<IncidenteDetalle> findByIncidenteIdIncidente(Long idIncidente);

    @Query("SELECT COUNT(id) > 0 FROM IncidenteDetalle id WHERE id.tipoProblema.idTipoProblema = :idTipoProblema")
    boolean existsIncidenteDetalleByTipoProblemaId(Long idTipoProblema);
}
