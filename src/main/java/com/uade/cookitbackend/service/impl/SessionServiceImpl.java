package com.uade.cookitbackend.service.impl;

import com.uade.cookitbackend.entity.UserSession;
import com.uade.cookitbackend.entity.Usuario;
import com.uade.cookitbackend.repository.db.UserSessionRepository;
import com.uade.cookitbackend.service.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SessionServiceImpl  implements SessionService {

    @Autowired
    private UserSessionRepository userSessionRepository;

    public void newSession(String fcm, Usuario usuario) {
        log.info("New session: {}",usuario.getIdUsuario());
        UserSession userSession = new UserSession();
        userSession.setUser(usuario);
        userSession.setFmc(fcm);
        userSessionRepository.save(userSession);
    }
}
