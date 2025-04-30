package com.example.sistemareportesincidentes.repository;

import com.example.sistemareportesincidentes.entity.Especialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EspecialidadRepository extends JpaRepository<Especialidad, Long> {
    Optional<Especialidad> findEspecialidadByNombre(String nombre);
    boolean existsEspecialidadByNombre(String nombre);

    @Query("SELECT COUNT(t) > 0 FROM Tecnico t JOIN t.especialidades e WHERE e.idEspecialidad = :idEspecialidad")
    boolean existsTecnicoByEspecialidadId(Long idEspecialidad);

    @Query("SELECT COUNT(tp) > 0 FROM TipoProblema tp JOIN tp.especialidadesRequeridas e WHERE e.idEspecialidad = :idEspecialidad")
    boolean existsTipoProblemaByEspecialidadId(Long idEspecialidad);
}
