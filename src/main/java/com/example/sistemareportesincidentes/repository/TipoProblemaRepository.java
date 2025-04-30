package com.example.sistemareportesincidentes.repository;

import com.example.sistemareportesincidentes.entity.TipoProblema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TipoProblemaRepository extends JpaRepository<TipoProblema, Long> {
    Optional<TipoProblema> findTipoProblemaByNombre(String nombre);
    boolean existsTipoProblemaByNombre(String nombre);

    @Query("SELECT tp FROM TipoProblema tp JOIN tp.especialidadesRequeridas e WHERE e.idEspecialidad = :idEspecialidad")
    List<TipoProblema> findTiposProblemaByEspecialidadId(Long idEspecialidad);

    @Query("SELECT COUNT(id) > 0 FROM IncidenteDetalle id WHERE id.tipoProblema.idTipoProblema = :idTipoProblema")
    boolean existsIncidenteDetalleByTipoProblemaId(Long idTipoProblema);

    TipoProblema findTipoProblemasByIdTipoProblema(Long idTipoProblema);
}
