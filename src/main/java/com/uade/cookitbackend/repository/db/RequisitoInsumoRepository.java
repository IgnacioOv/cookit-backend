package com.uade.cookitbackend.repository.db;

import com.uade.cookitbackend.entity.RequisitoInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequisitoInsumoRepository extends JpaRepository<RequisitoInsumo, Integer> {
    
    List<RequisitoInsumo> findByIdCursoOrderByCategoriaAscObligatorioDescNombreInsumoAsc(Integer idCurso);
    
    List<RequisitoInsumo> findByIdCursoAndObligatorioTrueOrderByNombreInsumoAsc(Integer idCurso);
    
    List<RequisitoInsumo> findByIdCursoAndCategoriaOrderByNombreInsumoAsc(Integer idCurso, String categoria);
    
    @Query("SELECT DISTINCT r.categoria FROM RequisitoInsumo r WHERE r.idCurso = :idCurso ORDER BY r.categoria")
    List<String> findCategoriasByIdCurso(@Param("idCurso") Integer idCurso);
    
    boolean existsByIdCursoAndNombreInsumoIgnoreCase(Integer idCurso, String nombreInsumo);
}