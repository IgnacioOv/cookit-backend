package com.uade.cookitbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cursos")
public class CursosController {

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Crear un curso", description = "Crea un curso con datos mockeados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Curso creado correctamente")
    })
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateCursoResponse> crearCurso(
        @Valid @RequestBody(
            description = "Datos para crear curso",
            required = true,
            content = @Content(schema = @Schema(implementation = CreateCursoRequest.class))
        ) CreateCursoRequest request) {
        CreateCursoResponse response = new CreateCursoResponse();
        response.setId(1);
        response.setNombreCurso(request.getNombreCurso());
        response.setMensaje("Curso creado (mock)");
        return ResponseEntity.status(201).body(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Sumarse a un curso", description = "Se añade el usuario al curso indicado de forma mockeada")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Se sumó al curso correctamente")
    })
    @PostMapping(value = "/join", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JoinCursoResponse> joinCurso(
        @Valid @RequestBody(
            description = "Datos para sumarse a un curso",
            required = true,
            content = @Content(schema = @Schema(implementation = JoinCursoRequest.class))
        ) JoinCursoRequest request) {
        JoinCursoResponse response = new JoinCursoResponse();
        response.setCursoId(request.getCursoId());
        response.setMensaje("Te has sumado al curso (mock)");
        return ResponseEntity.ok(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Ver detalles del curso", description = "Obtiene detalles del curso por ID (mock)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Detalles del curso"),
        @ApiResponse(responseCode = "404", description = "Curso no encontrado")
    })
    @GetMapping(value = "/detail/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CursoDetailResponse> getCursoDetail(@PathVariable("id") Integer id) {
        CursoDetailResponse response = new CursoDetailResponse();
        response.setId(id);
        response.setNombreCurso("Nombre del curso mock");
        response.setDescripcion("Descripción del curso mock");
        return ResponseEntity.ok(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Ver mis cursos", description = "Obtiene la lista de cursos del usuario (mock)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listado de cursos del usuario")
    })
    @GetMapping(value = "/my-courses", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CursoResponse>> getMisCursos() {
        CursoResponse curso = new CursoResponse();
        curso.setId(1);
        curso.setNombreCurso("Curso mock");
        return ResponseEntity.ok(List.of(curso));
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Ver cursos terminados", description = "Obtiene la lista de cursos finalizados del usuario (mock)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listado de cursos finalizados")
    })
    @GetMapping(value = "/completed", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CursoResponse>> getCursosTerminados() {
        CursoResponse curso = new CursoResponse();
        curso.setId(2);
        curso.setNombreCurso("Curso terminado mock");
        return ResponseEntity.ok(List.of(curso));
    }

    @Operation(summary = "Ver todos los cursos", description = "Obtiene la lista de todos los cursos disponibles (mock)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listado de cursos disponibles")
    })
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CursoResponse>> getAllCursos() {
        CursoResponse curso1 = new CursoResponse();
        curso1.setId(1);
        curso1.setNombreCurso("Cocina italiana");

        CursoResponse curso2 = new CursoResponse();
        curso2.setId(2);
        curso2.setNombreCurso("Repostería básica");

        CursoResponse curso3 = new CursoResponse();
        curso3.setId(3);
        curso3.setNombreCurso("Parrilla argentina");

        return ResponseEntity.ok(List.of(curso1, curso2, curso3));
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Pagar un curso", description = "Realiza el pago de un curso de forma mockeada")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pago realizado correctamente")
    })
    @PostMapping(value = "/pay", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaymentCursoResponse> payCurso(
        @Valid @RequestBody(
            description = "Datos de pago para un curso",
            required = true,
            content = @Content(schema = @Schema(implementation = PaymentCursoRequest.class))
        ) PaymentCursoRequest request) {
        PaymentCursoResponse response = new PaymentCursoResponse();
        response.setCursoId(request.getCursoId());
        response.setMensaje("Pago realizado (mock)");
        return ResponseEntity.ok(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Dar presente", description = "dar el presente en un curso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "presente dado correctamente")
    })
    @PostMapping(value = "/presente/{cursoId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity scanQRPresente(@RequestParam int cursoId) {
        PaymentCursoResponse response = new PaymentCursoResponse();
        response.setMensaje("presente (mock)");
        return ResponseEntity.ok().build();
    }

    public static class CreateCursoRequest {
        private String nombreCurso;
        private String descripcion;
        // ...getters/setters...
        public String getNombreCurso() { return nombreCurso; }
        public void setNombreCurso(String nombreCurso) { this.nombreCurso = nombreCurso; }
        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    }

    public static class CreateCursoResponse {
        private Integer id;
        private String nombreCurso;
        private String mensaje;
        // ...getters/setters...
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        public String getNombreCurso() { return nombreCurso; }
        public void setNombreCurso(String nombreCurso) { this.nombreCurso = nombreCurso; }
        public String getMensaje() { return mensaje; }
        public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    }

    public static class JoinCursoRequest {
        private Integer cursoId;
        // ...getters/setters...
        public Integer getCursoId() { return cursoId; }
        public void setCursoId(Integer cursoId) { this.cursoId = cursoId; }
    }

    public static class JoinCursoResponse {
        private Integer cursoId;
        private String mensaje;
        // ...getters/setters...
        public Integer getCursoId() { return cursoId; }
        public void setCursoId(Integer cursoId) { this.cursoId = cursoId; }
        public String getMensaje() { return mensaje; }
        public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    }

    public static class CursoDetailResponse {
        private Integer id;
        private String nombreCurso;
        private String descripcion;
        // ...getters/setters...
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        public String getNombreCurso() { return nombreCurso; }
        public void setNombreCurso(String nombreCurso) { this.nombreCurso = nombreCurso; }
        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    }

    public static class CursoResponse {
        private Integer id;
        private String nombreCurso;
        // ...getters/setters...
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        public String getNombreCurso() { return nombreCurso; }
        public void setNombreCurso(String nombreCurso) { this.nombreCurso = nombreCurso; }
    }

    public static class PaymentCursoRequest {
        private Integer cursoId;
        private String metodoPago;
        // ...getters/setters...
        public Integer getCursoId() { return cursoId; }
        public void setCursoId(Integer cursoId) { this.cursoId = cursoId; }
        public String getMetodoPago() { return metodoPago; }
        public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    }

    public static class PaymentCursoResponse {
        private Integer cursoId;
        private String mensaje;
        // ...getters/setters...
        public Integer getCursoId() { return cursoId; }
        public void setCursoId(Integer cursoId) { this.cursoId = cursoId; }
        public String getMensaje() { return mensaje; }
        public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    }


}
