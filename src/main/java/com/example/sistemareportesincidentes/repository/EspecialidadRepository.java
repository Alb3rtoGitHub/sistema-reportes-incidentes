package com.example.sistemareportesincidentes.repository;

import com.example.sistemareportesincidentes.entity.Especialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EspecialidadRepository extends JpaRepository<Especialidad, Long> {
    Optional<Especialidad> findEspecialidadByNombre(String nombre);

    boolean existsEspecialidadByNombre(String nombre);
}
