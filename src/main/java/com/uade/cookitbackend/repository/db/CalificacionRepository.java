package com.uade.cookitbackend.repository.db;

import com.uade.cookitbackend.entity.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Integer> {
    List<Calificacion> findByRecetaIdReceta(Integer idReceta);
    Optional<Calificacion> findByUsuarioIdUsuarioAndRecetaIdReceta(Integer idUsuario, Integer idReceta);

    @Query("SELECT c FROM Calificacion c JOIN CalificacionApproval ca ON c.idCalificacion = ca.idCalificacion WHERE ca.approved = true AND c.receta.idReceta = :idReceta")
    List<Calificacion> findApprovedByRecetaIdReceta(@Param("idReceta") Integer idReceta);

    @Query("SELECT c, CASE WHEN ca.approved = true THEN c.comentarios ELSE null END as comentariosAprobados FROM Calificacion c LEFT JOIN CalificacionApproval ca ON c.idCalificacion = ca.idCalificacion WHERE c.receta.idReceta = :idReceta")
    List<Object[]> findByRecetaIdRecetaWithApprovalStatus(@Param("idReceta") Integer idReceta);
}
