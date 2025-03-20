package com.uade.cookitbackend.service;

import com.uade.cookitbackend.dto.CreateUsuarioDTO;
import com.uade.cookitbackend.entity.Usuario;

public interface UsuarioService {
    Usuario createUsuario(CreateUsuarioDTO createUsuarioDTO);
} 