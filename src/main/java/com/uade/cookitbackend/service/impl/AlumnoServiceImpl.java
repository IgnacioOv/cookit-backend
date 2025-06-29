package com.uade.cookitbackend.service.impl;


import com.uade.cookitbackend.dto.AlumnoResponseDTO;
import com.uade.cookitbackend.dto.AlumnoUpdateDTO;
import com.uade.cookitbackend.dto.AlumnoWithUsuarioDTO;
import com.uade.cookitbackend.dto.CreateUsuarioDTO;
import com.uade.cookitbackend.dto.UsuarioToAlumnoConversionDTO;
import com.uade.cookitbackend.entity.Alumno;
import com.uade.cookitbackend.entity.Usuario;
import com.uade.cookitbackend.exception.DuplicateResourceException;
import com.uade.cookitbackend.exception.ErrorCode;
import com.uade.cookitbackend.exception.ResourceNotFoundException;
import com.uade.cookitbackend.repository.db.AlumnoRepository;
import com.uade.cookitbackend.service.AlumnoService;
import com.uade.cookitbackend.service.mappers.AlumnoMapper;
import com.uade.cookitbackend.service.mappers.UsuarioMapper;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlumnoServiceImpl implements AlumnoService {

    private final AlumnoRepository alumnoRepository;
    private final UsuarioServiceImpl usuarioService;
    private final AlumnoMapper alumnoMapper;
    private final UsuarioMapper usuarioMapper;

    @Transactional
    public AlumnoResponseDTO createAlumnoWithUsuario(AlumnoWithUsuarioDTO dto) {
        CreateUsuarioDTO usuarioDTO = usuarioMapper.fromComposedDTO(dto);
        Usuario usuario = usuarioService.createUsuario(usuarioDTO);

        Alumno alumno = alumnoMapper.toEntityFromComposedDTO(dto, usuario);
        alumno = alumnoRepository.save(alumno);

        return alumnoMapper.toResponseDTO(alumno);
    }
    @Override
    @Transactional(readOnly = true)
    public AlumnoResponseDTO getAlumnoById(Integer id) {
        Alumno alumno = alumnoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.ALUMNO_NOT_FOUND,
                        "Alumno no encontrado con id: " + id
                ));
        return alumnoMapper.toResponseDTO(alumno);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlumnoResponseDTO> getAllAlumnos() {
        List<Alumno> alumnos = alumnoRepository.findAll();
        return alumnos.stream()
                .map(alumnoMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public AlumnoResponseDTO updateAlumno(Integer id, AlumnoUpdateDTO dto) {
        Alumno alumno = alumnoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.ALUMNO_NOT_FOUND,
                        "Alumno no encontrado con id: " + id
                ));
        alumnoMapper.updateEntityFromDTO(dto, alumno);
        alumno = alumnoRepository.save(alumno);
        return alumnoMapper.toResponseDTO(alumno);
    }

    @Override
    @Transactional
    public void deleteAlumno(Integer id) {
        if (!alumnoRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    ErrorCode.ALUMNO_NOT_FOUND,
                    "Alumno no encontrado con id: " + id
            );
        }
        alumnoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public AlumnoResponseDTO convertUsuarioToAlumno(Integer userId, UsuarioToAlumnoConversionDTO dto) {
        // Verificar que el usuario existe
        Usuario usuario = usuarioService.getUsuarioById(userId);
        
        // Verificar que el usuario no sea ya un alumno
        if (alumnoRepository.existsById(userId)) {
            throw new DuplicateResourceException(
                    ErrorCode.ALUMNO_ALREADY_REGISTERED,
                    "El usuario ya está registrado como alumno"
            );
        }

        // Crear nuevo alumno usando el usuario existente
        Alumno alumno = new Alumno();
        alumno.setUsuario(usuario);
        alumno.setNumeroTarjeta(dto.getNumeroTarjeta());
        alumno.setDniFrente(dto.getDniFrente());
        alumno.setDniFondo(dto.getDniFondo());
        alumno.setTramite(dto.getTramite());
        alumno.setCuentaCorriente(java.math.BigDecimal.ZERO); // Inicializar en 0

        alumno = alumnoRepository.save(alumno);
        return alumnoMapper.toResponseDTO(alumno);
    }

    @Override
    public boolean isUsuarioAlumno(Integer userId) {
        return alumnoRepository.findByUsuarioIdUsuario(userId).isPresent();
    }
}
