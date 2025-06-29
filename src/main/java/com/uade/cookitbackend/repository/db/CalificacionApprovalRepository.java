package com.uade.cookitbackend.repository.db;

import com.uade.cookitbackend.entity.CalificacionApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalificacionApprovalRepository extends JpaRepository<CalificacionApproval, Integer> {
    
    @Query("SELECT ca FROM CalificacionApproval ca WHERE ca.approved = false")
    List<CalificacionApproval> findByApprovedFalse();
}