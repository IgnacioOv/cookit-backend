package com.uade.cookitbackend.repository.db;

import com.uade.cookitbackend.entity.RecetaApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecetaApprovalRepository extends JpaRepository<RecetaApproval, Integer> {
    
    @Query("SELECT ra FROM RecetaApproval ra JOIN FETCH ra.receta r JOIN FETCH r.usuario WHERE ra.approved = false")
    List<RecetaApproval> findUnapprovedRecetas();
    
    @Query("SELECT ra FROM RecetaApproval ra JOIN FETCH ra.receta r JOIN FETCH r.usuario WHERE ra.approved = false AND r.usuario.idUsuario = :idUsuario")
    List<RecetaApproval> findUnapprovedRecetasByUsuario(Integer idUsuario);
}