package com.example.sistemareportesincidentes.service.impl;

import com.example.sistemareportesincidentes.entity.Cliente;
import com.example.sistemareportesincidentes.entity.Incidente;
import com.example.sistemareportesincidentes.entity.Tecnico;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.example.sistemareportesincidentes.service.NotificacionService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class NotificacionServiceImpl implements NotificacionService {

    @Autowired
    private JavaMailSender emailSender;

    @Value("${twilio.account.sid}")
    private String twilioAccountSid;

    @Value("${twilio.auth.token}")
    private String twilioAuthToken;

    //    @Value("${twilio.whatsapp.number}")
    @Value("${twilio.whatsapp.sandbox.number}")
    private String twilioWhatsappNumber;

    @PostConstruct
    public void init() {
        // Inicializar Twilio
        Twilio.init(twilioAccountSid, twilioAuthToken);
    }

    // Notificar a un técnico sobre un nuevo incidente asignado
    @Override
    public void notificarTecnicoAsignacion(Tecnico tecnico, Incidente incidente) {
        if (tecnico.getMedioPreferido() == Tecnico.MedioNotificacion.EMAIL) {
            enviarEmail(
                    tecnico.getEmail(),
                    "Nuevo incidente asignado - #" + incidente.getIdIncidente(),
                    generarCuerpoEmailAsignacionTecnico(tecnico, incidente)
            );
        } else if (tecnico.getMedioPreferido() == Tecnico.MedioNotificacion.WHATSAPP) {
            enviarWhatsapp(
                    tecnico.getWhatsapp(),
                    generarMensajeWhatsappAsignacionTecnico(tecnico, incidente)
            );
        }
    }

    // Notifica al cliente que su incidente ha sido resuelto
    @Override
    public void notificarClienteResolucion(Cliente cliente, Incidente incidente) {
        enviarEmail(
                cliente.getEmail(),
                "Incidente #" + incidente.getIdIncidente() + " resuelto",
                generarCuerpoEmailResolucionCliente(cliente, incidente)
        );
    }

    // Envía un email
    @Override
    public void enviarEmail(String destinatario, String asunto, String cuerpo) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinatario);
        message.setSubject(asunto);
        message.setText(cuerpo);
        emailSender.send(message);
    }

    //  Envía un mensaje de WhatsApp usando Twilio
    @Override
    public void enviarWhatsapp(String numeroDestinatario, String mensaje) {
        try {
            System.out.println(numeroDestinatario + ": " + mensaje + twilioWhatsappNumber);
            Message.creator(
                    new PhoneNumber("whatsapp:" + numeroDestinatario),
                    new PhoneNumber("whatsapp:" + twilioWhatsappNumber),
                    mensaje
            ).create();
        } catch (Exception e) {
            // Loguear el error pero no interrumpir el flujo
            System.err.println("Error al enviar WhatsApp: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Genera el cuerpo del email para notificar a un técnico sobre un nuevo incidente
    @Override
    public String generarCuerpoEmailAsignacionTecnico(Tecnico tecnico, Incidente incidente) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        StringBuilder sb = new StringBuilder();
        sb.append("Hola ").append(tecnico.getNombre()).append(",\n\n ");
        sb.append("Se te ha asignado un nuevo incidente:\n\n");
        sb.append("ID del Incidente: ").append(incidente.getIdIncidente()).append("\n ");
        sb.append("Cliente: ").append(incidente.getCliente().getRazonSocial()).append("\n ");
        sb.append("Fecha de Creación: ").append(incidente.getFechaCreacion().format(formatter)).append("\n ");
        sb.append("Tiempo Estimado de Resolución: ").append(incidente.getTiempoEstimadoResolucion()).append(" minutos\n\n");

        sb.append("Detalles del Incidente:\n");
        incidente.getIncidentesDetalles().forEach(detalle -> {
            sb.append("- Servicio: ").append(detalle.getServicio().getNombre()).append("\n");
            sb.append("  Tipo de Problema: ").append(detalle.getTipoProblema().getNombre()).append("\n");
            sb.append("  Descripción: ").append(detalle.getDescripcion()).append("\n\n");
        });

        sb.append("Por favor, atiende este incidente a la brevedad posible.\n\n");
        sb.append("Saludos,\n");
        sb.append("Sistema de Reporte de Incidentes");

        return sb.toString();
    }

    // Genera el mensaje de WhatsApp para notificar a un técnico sobre un nuevo incidente
    @Override
    public String generarMensajeWhatsappAsignacionTecnico(Tecnico tecnico, Incidente incidente) {
        StringBuilder sb = new StringBuilder();
        sb.append("Hola ").append(tecnico.getNombre()).append(", ");
        sb.append("se te ha asignado el incidente #").append(incidente.getIdIncidente()).append(" ");
        sb.append("del cliente ").append(incidente.getCliente().getRazonSocial()).append(". ");
        sb.append("Tiempo estimado: ").append(incidente.getTiempoEstimadoResolucion()).append(" min. ");
        sb.append("Por favor revisa tu email para más detalles.");
        return sb.toString();
    }

    // Genera el cuerpo del email para notificar a un cliente sobre la resolución de su incidente
    @Override
    public String generarCuerpoEmailResolucionCliente(Cliente cliente, Incidente incidente) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        StringBuilder sb = new StringBuilder();
        sb.append("Estimado cliente ").append(cliente.getRazonSocial()).append(",\n\n");
        sb.append("Nos complace informarle que su incidente ha sido resuelto:\n\n");
        sb.append("ID del Incidente: ").append(incidente.getIdIncidente()).append("\n");
        sb.append("Fecha de Creación: ").append(incidente.getFechaCreacion().format(formatter)).append("\n");
        sb.append("Fecha de Resolución: ").append(incidente.getFechaResolucion().format(formatter)).append("\n");
        sb.append("Técnico Asignado: ").append(incidente.getTecnicoAsignado().getNombre()).append("\n\n");

        sb.append("Detalles del Incidente:\n");
        incidente.getIncidentesDetalles().forEach(detalle -> {
            sb.append("- Servicio: ").append(detalle.getServicio().getNombre()).append("\n");
            sb.append("  Tipo de Problema: ").append(detalle.getTipoProblema().getNombre()).append("\n");
            sb.append("  Descripción: ").append(detalle.getDescripcion()).append("\n\n");
        });

        sb.append("Agradecemos su confianza en nuestros servicios. Si tiene alguna consulta o si el problema persiste, no dude en contactarnos.\n\n");
        sb.append("Saludos cordiales,\n");
        sb.append("Equipo de Soporte\n");
        sb.append("Sistema de Reporte de Incidentes");
        return sb.toString();
    }
}
