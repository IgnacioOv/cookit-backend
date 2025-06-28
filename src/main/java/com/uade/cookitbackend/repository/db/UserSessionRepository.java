package com.uade.cookitbackend.repository.db;

import com.uade.cookitbackend.entity.UserSession;
import com.uade.cookitbackend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {
    List<UserSession> findUserSessionByUser(Usuario user);

    List<UserSession> findLastUserSessionByUser(Usuario user);
}
