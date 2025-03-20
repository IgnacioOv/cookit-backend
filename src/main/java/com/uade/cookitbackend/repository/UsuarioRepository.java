package com.uade.cookitbackend.repository;

import com.uade.cookitbackend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByMail(String mail);
    boolean existsByMail(String mail);
    boolean existsByNickname(String nickname);
} 