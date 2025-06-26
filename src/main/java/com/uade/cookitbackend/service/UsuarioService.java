package com.uade.cookitbackend.service;

import com.uade.cookitbackend.dto.CreateUsuarioDTO;
import com.uade.cookitbackend.dto.PasswordResetCompleteDTO;
import com.uade.cookitbackend.dto.RegisterStage1DTO;
import com.uade.cookitbackend.dto.RegisterStage2DTO;
import com.uade.cookitbackend.entity.Usuario;

public interface UsuarioService {
    Usuario createUsuario(CreateUsuarioDTO createUsuarioDTO);
    Usuario getUsuarioById(Integer id);
    Usuario login(String mail, String password); // Autenticación de usuario
    Usuario getUsuarioByMail(String mail);
    Usuario updateUsuario(Usuario usuario);
    
    // Métodos para registro en dos etapas
    Usuario createUsuarioStage1(RegisterStage1DTO registerStage1DTO);
    void validateRegistrationCode(String mail, String codigo);
    Usuario completeUsuarioStage2(RegisterStage2DTO registerStage2DTO);
    
    // Métodos para completar usuario incompleto via reset password
    boolean isUserIncomplete(String mail);
    Usuario completeUserViaPasswordReset(PasswordResetCompleteDTO dto);
}

