package com.example.sistemareportesincidentes.repository;

import com.example.sistemareportesincidentes.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
    Optional<Rol> findByNombre(Rol.nombreRol nombre);
}
