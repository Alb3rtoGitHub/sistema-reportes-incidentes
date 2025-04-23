package com.example.sistemareportesincidentes.service.impl;

import com.example.sistemareportesincidentes.dto.ServicioDTO;
import com.example.sistemareportesincidentes.entity.Servicio;
import com.example.sistemareportesincidentes.exception.ResourceAlreadyExistsException;
import com.example.sistemareportesincidentes.exception.ResourceNotFoundException;
import com.example.sistemareportesincidentes.repository.ServicioRepository;
import com.example.sistemareportesincidentes.service.ServicioService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServicioServiceImpl implements ServicioService {

//    private final ServicioRepository servicioRepository;
//
//    public ServicioServiceImpl(ServicioRepository servicioRepository) {
//        this.servicioRepository = servicioRepository;
//    }

    @Autowired
    private ServicioRepository servicioRepository;

    @Override
    public List<ServicioDTO> findAllServicios() {
        return servicioRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ServicioDTO findServicioById(Long idServicio) {
        Servicio servicio = servicioRepository.findById(idServicio)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio", "id", idServicio));
        return convertToDTO(servicio);
    }

    @Override
    @Transactional
    public ServicioDTO saveServicio(ServicioDTO servicioDTO) {
        // Verificar si ya existe un servicio con el mismo nombre
        if (servicioRepository.existsServicioByNombre(servicioDTO.getNombre())) {
            throw new ResourceAlreadyExistsException("Servicio", "nombre", servicioDTO.getNombre());
        }

        Servicio servicio = Servicio.builder()
                .nombre(servicioDTO.getNombre())
                .build();

        Servicio servicioGuardado = servicioRepository.save(servicio);
        return convertToDTO(servicioGuardado);
    }

    @Override
    @Transactional
    public ServicioDTO updateServicio(Long idServicio, ServicioDTO servicioDTO) {
        Servicio servicio = servicioRepository.findById(idServicio).orElseThrow(() -> new ResourceNotFoundException("Servicio", "id", idServicio));

        // Verificar si el nuevo nombre ya existe en otro servicio
        if (!servicio.getNombre().equals(servicioDTO.getNombre()) && servicioRepository.existsServicioByNombre(servicioDTO.getNombre())) {
            throw new ResourceAlreadyExistsException("Servicio", "nombre", servicioDTO.getNombre());
        }

        servicio.setNombre(servicioDTO.getNombre());

        Servicio servicioActualizado = servicioRepository.save(servicio);
        return convertToDTO(servicioActualizado);
    }

    @Override
    @Transactional
    public void deleteServicioById(Long idServicio) {
        if (!servicioRepository.existsById(idServicio)){
            throw new ResourceNotFoundException("Servicio", "id", idServicio);
        }
        servicioRepository.deleteById(idServicio);
    }

    @Override
    public List<Servicio> obtenerServiciosPorIds(List<Long> ids) {
        List<Servicio> servicios = servicioRepository.findAllById(ids);
        if (servicios.size() != ids.size()) {
            throw new ResourceNotFoundException("Uno o más servicios no fueron encontrados");
        }
        return servicios;
    }

    // Metodo privado auxiliar para convertir entidad a DTO
    private ServicioDTO convertToDTO(Servicio servicio) {
        ServicioDTO servicioDTO = new ServicioDTO();
        servicioDTO.setId(servicio.getIdServicio());
        servicioDTO.setNombre(servicio.getNombre());
        return servicioDTO;
    }
}
