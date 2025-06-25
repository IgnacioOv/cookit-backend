package com.uade.cookitbackend.repository.db;

import com.uade.cookitbackend.entity.RecetaApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecetaApprovalRepository extends JpaRepository<RecetaApproval, Integer> {
}