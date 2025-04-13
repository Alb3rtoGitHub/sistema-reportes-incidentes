package com.example.sistemareportesincidentes.service;

import com.example.sistemareportesincidentes.dto.ServicioDTO;
import com.example.sistemareportesincidentes.entity.Servicio;

import java.util.List;

public interface ServicioService {

    List<ServicioDTO> findAllServicios();

    ServicioDTO findServicioById(Long id);

    ServicioDTO saveServicio(ServicioDTO servicioDTO);

    ServicioDTO updateServicio(Long idServicio, ServicioDTO servicioDTO);

    void deleteServicioById(Long id);

    // Metodo para obtener entidades por IDs
    List<Servicio> obtenerServiciosPorIds(List<Long> ids);
}