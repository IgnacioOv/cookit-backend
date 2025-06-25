package com.uade.cookitbackend.service.impl;


import com.uade.cookitbackend.dto.AlumnoResponseDTO;
import com.uade.cookitbackend.dto.AlumnoUpdateDTO;
import com.uade.cookitbackend.dto.AlumnoWithUsuarioDTO;
import com.uade.cookitbackend.dto.CreateUsuarioDTO;
import com.uade.cookitbackend.entity.Alumno;
import com.uade.cookitbackend.entity.Usuario;
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
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USUARIO_NOT_FOUND,
                        "Alumno not found with id: " + id));
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
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USUARIO_NOT_FOUND,
                        "Alumno not found with id: " + id));
        alumnoMapper.updateEntityFromDTO(dto, alumno);
        alumno = alumnoRepository.save(alumno);
        return alumnoMapper.toResponseDTO(alumno);
    }

    @Override
    @Transactional
    public void deleteAlumno(Integer id) {
        if (!alumnoRepository.existsById(id)) {
            throw new ResourceNotFoundException(ErrorCode.USUARIO_NOT_FOUND,
                    "Alumno not found with id: " + id);
        }
        alumnoRepository.deleteById(id);
    }
}
