package com.uade.cookitbackend.repository.db;

import com.uade.cookitbackend.entity.CalificacionApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalificacionApprovalRepository extends JpaRepository<CalificacionApproval, Integer> {
}