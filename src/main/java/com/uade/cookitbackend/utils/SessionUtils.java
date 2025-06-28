package com.uade.cookitbackend.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SessionUtils {

    public static <T> T getCurrenteUser(Class<T> clazz) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (clazz.isInstance(authentication.getPrincipal())) {
            Object principal = authentication.getPrincipal();
            return clazz.cast(principal);
        }
        // This method should return the current user from the session or security context.
        // The actual implementation will depend on your security setup (e.g., Spring Security).
        // For example, you might retrieve the user from the SecurityContextHolder.
        return null; // Placeholder for actual user retrieval logic
    }
}
