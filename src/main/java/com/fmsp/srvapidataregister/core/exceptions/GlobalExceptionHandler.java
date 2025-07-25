package com.fmsp.srvapidataregister.core.exceptions;

import com.fmsp.srvapidataregister.core.payload.ApiResponse;
import com.fmsp.srvapidataregister.core.payload.ErrorItemDTO;
import com.fmsp.srvapidataregister.core.payload.ResponseHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.Collections;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomServiceException.class)
    public ResponseEntity<ApiResponse<String>> handleBadRequest(CustomServiceException e) {
        ErrorItemDTO err = new ErrorItemDTO(e.getIdTx(), e.getCodError(), e.getDescError(), e.getMessage());
        return ResponseHandler.badRequestResponse(Collections.singletonList(err), e.getIdTx());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(Exception e) {
        ErrorItemDTO err = new ErrorItemDTO(
                null, "ERROR", e.getMessage(), "Error in service!"
        );
        return ResponseHandler.internalServerResponse(Collections.singletonList(err), null);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<String>> handleUnauthorized(UnauthorizedException e) {
        ErrorItemDTO err = new ErrorItemDTO(
                null, "UNAUTHORIZED", e.getMessage(), "Acceso no autorizado"
        );
        return ResponseHandler.unauthorizedResponse(Collections.singletonList(err), null);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse<String>> handleForbidden(ForbiddenException e) {
        ErrorItemDTO err = new ErrorItemDTO(
                null, "FORBIDDEN", e.getMessage(), "No tienes permisos para este recurso"
        );
        return ResponseHandler.forbiddenResponse(Collections.singletonList(err), null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<String>> handleAccessDenied(AccessDeniedException e) {
        ErrorItemDTO error = new ErrorItemDTO(
                null,
                "FORBIDDEN",
                "Acceso denegado",
                "No tienes permisos para este recurso"
        );
        return ResponseHandler.forbiddenResponse(Collections.singletonList(error), null);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiResponse<String>> handleAuthorizationDeniedException(AuthorizationDeniedException e) {
        ErrorItemDTO error = new ErrorItemDTO(
                null,
                "FORBIDDEN",
                "Acceso denegado",
                "No tienes permisos para este recurso"
        );
        return ResponseHandler.forbiddenResponse(Collections.singletonList(error), null);
    }

}
