package com.uade.cookitbackend.service.impl;

import com.uade.cookitbackend.dto.InscripcionCursoRequestDTO;
import com.uade.cookitbackend.dto.InscripcionCursoResponseDTO;
import com.uade.cookitbackend.entity.*;
import com.uade.cookitbackend.exception.BadRequestException;
import com.uade.cookitbackend.exception.ErrorCode;
import com.uade.cookitbackend.exception.ResourceNotFoundException;
import com.uade.cookitbackend.repository.db.AlumnoRepository;
import com.uade.cookitbackend.repository.db.CronogramaCursoRepository;
import com.uade.cookitbackend.repository.db.InscripcionCursoRepository;
import com.uade.cookitbackend.service.InscripcionCursoService;
import com.uade.cookitbackend.service.mappers.InscripcionCursoMapper;
import com.uade.cookitbackend.service.impl.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InscripcionCursoServiceImpl implements InscripcionCursoService {

    private final InscripcionCursoRepository inscripcionRepo;
    private final AlumnoRepository alumnoRepo;
    private final CronogramaCursoRepository cronogramaRepo;
    private final InscripcionCursoMapper inscripcionMapper;
    private final EmailService emailService;

    @Override
    @Transactional
    public InscripcionCursoResponseDTO inscribirAlumno(InscripcionCursoRequestDTO dto) {
        if (inscripcionRepo.existsByAlumno_IdAlumnoAndCronograma_IdCronograma(dto.getIdAlumno(), dto.getIdCronograma())) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST,"El alumno ya está inscripto en ese cronograma");
        }
        Alumno alumno = alumnoRepo.findById(dto.getIdAlumno())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ALUMNO_NOT_FOUND,"Alumno no encontrado"));
        CronogramaCurso cronograma = cronogramaRepo.findById(dto.getIdCronograma())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CRONOGRAMA_CURSO_NOT_FOUND,"Cronograma no encontrado"));

        if (cronograma.getVacantesDisponibles() == null || cronograma.getVacantesDisponibles() < 1) {
            throw new BadRequestException(ErrorCode.VACANTE_NOT_AVAILABLE,"No hay vacantes disponibles para este curso");
        }

        // Calcular precio real (aplicar promoción de sede si existe)
        BigDecimal precioCurso = cronograma.getCurso().getPrecio();
        BigDecimal precioFinal = precioCurso;
        Sede sede = cronograma.getSede();
        if (sede.getPromocionCursos() != null) {
            precioFinal = precioFinal.subtract(sede.getPromocionCursos());
        }
        if (sede.getBonificacionCursos() != null) {
            // Si tipo bonificacion es porcentaje
            if ("porcentaje".equalsIgnoreCase(sede.getTipoBonificacion())) {
                precioFinal = precioFinal.subtract(precioCurso.multiply(sede.getBonificacionCursos().divide(new BigDecimal("100"))));
            } else {
                precioFinal = precioFinal.subtract(sede.getBonificacionCursos());
            }
        }
        if (precioFinal.compareTo(BigDecimal.ZERO) < 0) precioFinal = BigDecimal.ZERO;

        // Validar método de pago y procesar
        String tipoPago;
        if (dto.getPagarConCuentaCorriente()) {
            // Validar que tenga saldo suficiente en cuenta corriente
            BigDecimal saldoActual = alumno.getCuentaCorriente() != null ? alumno.getCuentaCorriente() : BigDecimal.ZERO;
            if (saldoActual.compareTo(precioFinal) < 0) {
                throw new BadRequestException(ErrorCode.BAD_REQUEST,
                        "Saldo insuficiente en cuenta corriente. Saldo actual: $" + saldoActual + ", Precio del curso: $" + precioFinal);
            }
            
            // Descontar el monto de la cuenta corriente
            BigDecimal nuevoSaldo = saldoActual.subtract(precioFinal);
            alumno.setCuentaCorriente(nuevoSaldo);
            alumnoRepo.save(alumno);
            tipoPago = "cuenta_corriente";
        } else {
            // Pago con tarjeta (mockeado desde el front)
            if (alumno.getNumeroTarjeta() == null || alumno.getNumeroTarjeta().isBlank()) {
                throw new BadRequestException(ErrorCode.BAD_REQUEST,"El alumno no tiene tarjeta registrada para pago");
            }
            tipoPago = "tarjeta_credito";
        }

        // Actualizar vacantes
        cronograma.setVacantesDisponibles(cronograma.getVacantesDisponibles() - 1);
        cronogramaRepo.save(cronograma);

        // Crear inscripción
        InscripcionCurso insc = new InscripcionCurso();
        insc.setAlumno(alumno);
        insc.setCronograma(cronograma);
        insc.setFechaInscripcion(LocalDate.now());
        insc.setEstado("inscripto");
        insc.setMontoPagado(precioFinal);
        insc.setMontoReintegrado(null);
        insc.setTipoPago(tipoPago);
        insc.setNumeroTransaccion("TXN-" + System.currentTimeMillis());
        insc.setSaldoCuentaCorriente(dto.getPagarConCuentaCorriente() ? alumno.getCuentaCorriente() : BigDecimal.ZERO);
        insc.setFacturaEnviada(false);
        insc = inscripcionRepo.save(insc);

        // Enviar email de confirmación de pago
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("nombreAlumno", alumno.getUsuario().getNombre() != null ? alumno.getUsuario().getNombre() : alumno.getUsuario().getNickname());
            variables.put("nombreCurso", cronograma.getCurso().getDescripcion());
            variables.put("fechaInicio", cronograma.getFechaInicio().toString());
            variables.put("sede", sede.getNombreSede());
            variables.put("montoPagado", precioFinal.toString());
            variables.put("metodoPago", dto.getPagarConCuentaCorriente() ? "Cuenta Corriente" : "Tarjeta de Crédito");
            variables.put("numeroTransaccion", insc.getNumeroTransaccion());
            
            emailService.sendHtmlMessage(
                alumno.getUsuario().getMail(),
                "Confirmación de Inscripción - Pago Exitoso",
                "payment-success",
                variables
            );
        } catch (Exception e) {
            // Log error pero no fallar la inscripción
            System.err.println("Error al enviar email de confirmación: " + e.getMessage());
        }

        return inscripcionMapper.toDTO(insc);
    }

    @Override
    @Transactional
    public InscripcionCursoResponseDTO darDeBaja(Integer idInscripcion, Boolean reintegroEnCuentaCorriente) {
        InscripcionCurso insc = inscripcionRepo.findById(idInscripcion)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.INSCRIPCION_CURSO_NOT_FOUND,"Inscripción no encontrada"));
        if (!"inscripto".equals(insc.getEstado())) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST,"Solo se puede dar de baja una inscripción activa");
        }

        CronogramaCurso cronograma = insc.getCronograma();
        LocalDate hoy = LocalDate.now();
        LocalDate inicio = cronograma.getFechaInicio();

        long diasRestantes = ChronoUnit.DAYS.between(hoy, inicio);
        BigDecimal montoReintegro;
        BigDecimal montoPagado = insc.getMontoPagado();

        if (diasRestantes > 10) {
            montoReintegro = montoPagado; // 100%
        } else if (diasRestantes >= 1 && diasRestantes <= 9) {
            montoReintegro = montoPagado.multiply(new BigDecimal("0.7")); // 70%
        } else if (diasRestantes == 0) {
            montoReintegro = montoPagado.multiply(new BigDecimal("0.5")); // 50%
        } else {
            montoReintegro = BigDecimal.ZERO; // ya empezó, sin reintegro
        }

        // Procesar el reintegro según el flag
        if (reintegroEnCuentaCorriente != null && reintegroEnCuentaCorriente) {
            // Reintegro real a cuenta corriente
            Alumno alumno = insc.getAlumno();
            alumno.setCuentaCorriente(alumno.getCuentaCorriente().add(montoReintegro));
            alumnoRepo.save(alumno);
            insc.setMotivoBaja("Solicitud del alumno - Reintegro a cuenta corriente: $" + montoReintegro);
        } else {
            // Simulación de reintegro a tarjeta
            insc.setMotivoBaja("Solicitud del alumno - Reintegro simulado a tarjeta: $" + montoReintegro);
        }

        insc.setEstado("baja");
        insc.setMontoReintegrado(montoReintegro);
        insc.setFechaBaja(LocalDate.now());

        // Devolver la vacante solo si la baja fue antes de iniciar
        if (diasRestantes >= 0) {
            cronograma.setVacantesDisponibles(cronograma.getVacantesDisponibles() + 1);
            cronogramaRepo.save(cronograma);
        }
        insc = inscripcionRepo.save(insc);

        return inscripcionMapper.toDTO(insc);
    }

    @Override
    public List<InscripcionCursoResponseDTO> getInscripcionesAlumno(Integer idAlumno) {
        return inscripcionRepo.findByAlumno_IdAlumno(idAlumno)
                .stream()
                .map(inscripcionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public InscripcionCursoResponseDTO getInscripcionById(Integer idInscripcion) {
        InscripcionCurso insc = inscripcionRepo.findById(idInscripcion)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.INSCRIPCION_CURSO_NOT_FOUND,"Inscripción no encontrada"));
        return inscripcionMapper.toDTO(insc);
    }
}
