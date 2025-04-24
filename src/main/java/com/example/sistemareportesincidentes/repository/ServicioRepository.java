package com.example.sistemareportesincidentes.repository;

import com.example.sistemareportesincidentes.entity.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {
    Optional<Servicio> findServicioByNombre(String nombre);
    boolean existsServicioByNombre(String nombre);
}
