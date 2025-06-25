// CursoController.java
package com.uade.cookitbackend.controller;

import com.uade.cookitbackend.dto.*;
import com.uade.cookitbackend.service.CursoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cursos")
@RequiredArgsConstructor
public class CursoController {

    private final CursoService cursoService;

    @GetMapping
    public ResponseEntity<List<CursoResponseDTO>> getAllCursos() {
        return ResponseEntity.ok(cursoService.getAllCursosDisponibles());
    }

    @GetMapping("/sede/{idSede}")
    public ResponseEntity<List<CursoResponseDTO>> getCursosBySede(@PathVariable Integer idSede) {
        return ResponseEntity.ok(cursoService.getCursosBySede(idSede));
    }

    @PostMapping("/inscripcion")
    public ResponseEntity<Void> inscribirAlumno(@RequestBody CursoInscripcionRequestDTO dto) {
        cursoService.inscribirAlumnoACurso(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/baja")
    public ResponseEntity<Void> darDeBaja(@RequestBody BajaCursoRequestDTO dto) {
        cursoService.darDeBajaDeCurso(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/mis-cursos/{idAlumno}")
    public ResponseEntity<List<MisCursosResponseDTO>> getMisCursos(@PathVariable Integer idAlumno) {
        return ResponseEntity.ok(cursoService.getCursosContratadosPorAlumno(idAlumno));
    }

    @PostMapping("/asistencia/qr")
    public ResponseEntity<String> registrarAsistenciaQR(@RequestBody AsistenciaQRRequestDTO dto) {
        cursoService.registrarAsistenciaQR(dto);
        return ResponseEntity.ok("Asistencia registrada correctamente");
    }

    @GetMapping("/asistencia/{idAlumno}/{idCronograma}")
    public ResponseEntity<AsistenciaReportDTO> getReporteAsistencia(@PathVariable Integer idAlumno, @PathVariable Integer idCronograma) {
        return ResponseEntity.ok(cursoService.getReporteAsistencia(idAlumno, idCronograma));
    }
}
