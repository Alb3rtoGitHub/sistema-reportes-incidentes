package com.example.sistemareportesincidentes.service;

import com.example.sistemareportesincidentes.entity.Cliente;
import com.example.sistemareportesincidentes.entity.Incidente;
import com.example.sistemareportesincidentes.entity.Tecnico;

public interface NotificacionService {
    void notificarTecnicoAsignacion(Tecnico tecnico, Incidente incidente);
    void notificarClienteResolucion(Cliente cliente, Incidente incidente);
    void enviarEmail(String destinatario, String asunto, String cuerpo);
    void enviarWhatsapp(String numeroDestinatario, String mensaje);
    String generarCuerpoEmailAsignacionTecnico(Tecnico tecnico, Incidente incidente);
    String generarMensajeWhatsappAsignacionTecnico(Tecnico tecnico, Incidente incidente);
    String generarCuerpoEmailResolucionCliente(Cliente cliente, Incidente incidente);
}
