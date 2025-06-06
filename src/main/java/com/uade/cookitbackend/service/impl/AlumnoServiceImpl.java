package com.uade.cookitbackend.service.impl;

import com.uade.cookitbackend.dto.AlumnoCreateDTO;
import com.uade.cookitbackend.dto.AlumnoResponseDTO;
import com.uade.cookitbackend.dto.AlumnoUpdateDTO;
import com.uade.cookitbackend.entity.Alumno;
import com.uade.cookitbackend.entity.Usuario;
import com.uade.cookitbackend.exception.ErrorCode;
import com.uade.cookitbackend.exception.ResourceNotFoundException;
import com.uade.cookitbackend.repository.db.AlumnoRepository;
import com.uade.cookitbackend.repository.db.UsuarioRepository;
import com.uade.cookitbackend.service.AlumnoService;
import com.uade.cookitbackend.service.mappers.AlumnoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlumnoServiceImpl implements AlumnoService {

    private final AlumnoRepository alumnoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlumnoMapper alumnoMapper;

    @Override
    public AlumnoResponseDTO createAlumno(AlumnoCreateDTO dto) {
        // Buscamos el usuario relacionado
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USUARIO_NOT_FOUND,
                        "Usuario not found with id: " + dto.getUsuarioId()));

        // Creamos el Alumno usando MapStruct
        Alumno alumno = alumnoMapper.toEntity(dto, usuario);
        alumno = alumnoRepository.save(alumno);

        return alumnoMapper.toResponseDTO(alumno);
    }

    @Override
    public AlumnoResponseDTO getAlumnoById(Integer id) {
        Alumno alumno = alumnoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USUARIO_NOT_FOUND,
                        "Alumno not found with id: " + id));
        return alumnoMapper.toResponseDTO(alumno);
    }

    @Override
    public List<AlumnoResponseDTO> getAllAlumnos() {
        List<Alumno> alumnos = alumnoRepository.findAll();
        return alumnos.stream()
                .map(alumnoMapper::toResponseDTO)
                .toList();
        // O directamente si agregás el método en el mapper:
        // return alumnoMapper.toResponseDTOList(alumnos);
    }

    @Override
    public AlumnoResponseDTO updateAlumno(Integer id, AlumnoUpdateDTO dto) {
        Alumno alumno = alumnoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USUARIO_NOT_FOUND,
                        "Alumno not found with id: " + id));
        alumnoMapper.updateEntityFromDTO(dto, alumno);
        alumno = alumnoRepository.save(alumno);
        return alumnoMapper.toResponseDTO(alumno);
    }

    @Override
    public void deleteAlumno(Integer id) {
        if (!alumnoRepository.existsById(id)) {
            throw new ResourceNotFoundException(ErrorCode.USUARIO_NOT_FOUND,
                    "Alumno not found with id: " + id);
        }
        alumnoRepository.deleteById(id);
    }
}
