package com.uade.cookitbackend.repository.db;

import com.uade.cookitbackend.entity.RecetaFavorita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecetaFavoritaRepository extends JpaRepository<RecetaFavorita, Integer> {
    
    @Query("SELECT rf FROM RecetaFavorita rf WHERE rf.usuario.idUsuario = :idUsuario ORDER BY rf.fechaAgregada DESC")
    List<RecetaFavorita> findByUsuario_IdUsuarioOrderByFechaAgregadaDesc(@Param("idUsuario") Integer idUsuario);
    
    boolean existsByUsuario_IdUsuarioAndReceta_IdReceta(Integer idUsuario, Integer idReceta);
    
    Optional<RecetaFavorita> findByUsuario_IdUsuarioAndReceta_IdReceta(Integer idUsuario, Integer idReceta);
    
    long countByUsuario_IdUsuario(Integer idUsuario);
}