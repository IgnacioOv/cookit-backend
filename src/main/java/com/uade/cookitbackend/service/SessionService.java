package com.uade.cookitbackend.service;

import com.uade.cookitbackend.entity.Usuario;

public interface SessionService {
    void newSession(String fcm, Usuario usuario);
}
