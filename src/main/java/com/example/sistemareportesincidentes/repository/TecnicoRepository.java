package com.example.sistemareportesincidentes.repository;

import com.example.sistemareportesincidentes.entity.Tecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TecnicoRepository extends JpaRepository<Tecnico, Long> {
    Optional<Tecnico> findTecnicoByNombre(String nombre);

    // Continuar luego con los Queries metodos adicionales...
    Optional<Tecnico> findByEmail(String email);

    @Query("SELECT t FROM Tecnico t JOIN t.especialidades e WHERE e.idEspecialidad = :idEspecialidad")
    List<Tecnico> findTecnicosByEspecialidadId(Long especialidadId);

    @Query("SELECT i.tecnicoAsignado, COUNT(i) as incidentesResueltos FROM Incidente i WHERE i.estado = 'RESUELTO' AND i.fechaResolucion >= :fechaInicio GROUP BY i.tecnicoAsignado ORDER BY incidentesResueltos DESC")
    List<Object[]> findTecnicosConMasIncidentesResueltos(LocalDateTime fechaInicio);

    @Query("SELECT i.tecnicoAsignado, COUNT(i) as incidentesResueltos FROM Incidente i JOIN i.incidentesDetalles d JOIN d.tipoProblema tp JOIN tp.especialidadesRequeridas e WHERE i.estado = 'RESUELTO' AND i.fechaResolucion >= :fechaInicio AND e.idEspecialidad = :especialidadId GROUP BY i.tecnicoAsignado ORDER BY incidentesResueltos DESC")
    List<Object[]> findTecnicosConMasIncidentesResueltosporEspecialidad(LocalDateTime fechaInicio, Long especialidadId);

    @Query("SELECT i.tecnicoAsignado, AVG(TIMESTAMPDIFF(SECOND, i.fechaCreacion, i.fechaResolucion)) as tiempoPromedioResolucion FROM Incidente i WHERE i.estado = 'RESUELTO' GROUP BY i.tecnicoAsignado ORDER BY tiempoPromedioResolucion ASC")
    List<Object[]> findTecnicosConMenorTiempoResolucion();
}
