package com.uade.cookitbackend.repository.db;

import com.uade.cookitbackend.entity.Receta;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, Integer> {
    List<Receta> findByNombreRecetaContainingIgnoreCaseOrderByIdRecetaDesc(String nombreReceta);

    List<Receta> findRecetaByUsuario_IdUsuario(Integer idUsuario);

    @Query("""
        SELECT r
        FROM Receta r
        WHERE NOT EXISTS (
            SELECT iu
            FROM IngredienteUtilizado iu
            WHERE iu.receta = r
              AND LOWER(iu.ingrediente.nombre) = LOWER(:nombreIngrediente)
        )
        """)
    List<Receta> findRecetasSinIngrediente(
            @Param("nombreIngrediente") String nombreIngrediente,
            Sort sort
    );
    @Query("""
        SELECT DISTINCT r
        FROM Receta r
        JOIN r.ingredientesUtilizados iu
        WHERE LOWER(iu.ingrediente.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))
        ORDER BY r.idReceta DESC
        """)
    List<Receta> findRecetasConIngrediente(
            @Param("nombre") String nombre, Sort sort
    );

    @Query("SELECT r FROM Receta r " +
           "LEFT JOIN FETCH r.ingredientesUtilizados iu " +
           "LEFT JOIN FETCH iu.ingrediente " +
           "LEFT JOIN FETCH iu.unidad " +
           "WHERE r.idReceta = :idReceta")
    Optional<Receta> findByIdWithIngredientes(@Param("idReceta") Integer idReceta);

    @Query("SELECT r FROM Receta r JOIN RecetaApproval ra ON r.idReceta = ra.idReceta WHERE ra.approved = true ORDER BY r.idReceta DESC")
    List<Receta> findAllApproved();

    @Query("SELECT r FROM Receta r JOIN RecetaApproval ra ON r.idReceta = ra.idReceta WHERE ra.approved = true AND LOWER(r.nombreReceta) LIKE LOWER(CONCAT('%', :nombre, '%')) ORDER BY r.idReceta DESC")
    List<Receta> findApprovedByNombreRecetaContainingIgnoreCaseOrderByIdRecetaDesc(@Param("nombre") String nombre);

    @Query("SELECT r FROM Receta r JOIN RecetaApproval ra ON r.idReceta = ra.idReceta WHERE ra.approved = true AND r.usuario.idUsuario = :idUsuario")
    List<Receta> findApprovedRecetaByUsuario_IdUsuario(@Param("idUsuario") Integer idUsuario);

    @Query("""
        SELECT r
        FROM Receta r
        JOIN RecetaApproval ra ON r.idReceta = ra.idReceta
        WHERE ra.approved = true
          AND NOT EXISTS (
            SELECT iu
            FROM IngredienteUtilizado iu
            WHERE iu.receta = r
              AND LOWER(iu.ingrediente.nombre) = LOWER(:nombreIngrediente)
        )
        """)
    List<Receta> findApprovedRecetasSinIngrediente(@Param("nombreIngrediente") String nombreIngrediente, Sort sort);

    @Query("""
        SELECT DISTINCT r
        FROM Receta r
        JOIN RecetaApproval ra ON r.idReceta = ra.idReceta
        JOIN r.ingredientesUtilizados iu
        WHERE ra.approved = true
          AND LOWER(iu.ingrediente.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))
        ORDER BY r.idReceta DESC
        """)
    List<Receta> findApprovedRecetasConIngrediente(@Param("nombre") String nombre, Sort sort);

    @Query("SELECT r FROM Receta r " +
           "JOIN RecetaApproval ra ON r.idReceta = ra.idReceta " +
           "LEFT JOIN FETCH r.ingredientesUtilizados iu " +
           "LEFT JOIN FETCH iu.ingrediente " +
           "LEFT JOIN FETCH iu.unidad " +
           "WHERE ra.approved = true AND r.idReceta = :idReceta")
    Optional<Receta> findApprovedByIdWithIngredientes(@Param("idReceta") Integer idReceta);

    @Query("SELECT r FROM Receta r JOIN RecetaApproval ra ON r.idReceta = ra.idReceta WHERE ra.approved = true AND r.tipoReceta.idTipo = :idTipo")
    List<Receta> findApprovedByTipoReceta(@Param("idTipo") Integer idTipo, Sort sort);

    boolean existsByNombreRecetaAndUsuario_IdUsuario(String nombreReceta, Integer idUsuario);

    Optional<Receta> findByNombreRecetaAndUsuario_IdUsuario(String nombreReceta, Integer idUsuario);
}
