package com.medipatient.auth.exception;

public class SessionNotFoundException extends RuntimeException {
    
    public SessionNotFoundException(String sessionId) {
        super("Session not found: " + sessionId);
    }
}