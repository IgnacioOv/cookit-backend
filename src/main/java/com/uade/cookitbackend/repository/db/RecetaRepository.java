package com.uade.cookitbackend.repository.db;

import com.uade.cookitbackend.entity.Receta;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}
