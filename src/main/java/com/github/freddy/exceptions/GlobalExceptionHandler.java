package com.github.freddy.exceptions;

import com.github.freddy.dtos.errors.StandardError;
import com.github.freddy.dtos.errors.ValidationError;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException; // IMPORT CORRIGIDO
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // --- 404 Not Found ---
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> handleEntityNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<StandardError> handleUsernameNotFound(UsernameNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    // --- 422 Unprocessable Entity (Regra de Negócio) ---
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<StandardError> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request);
    }

    // --- 409 Conflict (Duplicidade) ---
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<StandardError> handleConflictException(ConflictException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    // --- 400 Bad Request (Validação de Campos) ---
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        // Uso de Streams para ficar mais limpo e moderno
        List<ValidationError.FieldMessage> errors = e.getBindingResult().getFieldErrors().stream()
                .map(f -> new ValidationError.FieldMessage(f.getField(), f.getDefaultMessage()))
                .toList();

        ValidationError err = new ValidationError(
                Instant.now(),
                status.value(),
                "Erro de validação",
                request.getRequestURI(),
                errors
        );

        return ResponseEntity.status(status).body(err);
    }

    // --- 400 Bad Request (Tipo de dado errado na URL) ---
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<StandardError> handleTypeMismatch(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String requiredType = e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "desconhecido";
        String message = String.format("O parâmetro '%s' recebeu o valor '%s', mas esperava-se um tipo '%s'.",
                e.getName(), e.getValue(), requiredType);

        return buildResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    // --- 401 Unauthorized (Autenticação) ---
    @ExceptionHandler({AuthenticationException.class, JwtException.class})
    public ResponseEntity<StandardError> handleAuthenticationError(Exception ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Falha na autenticação: " + ex.getMessage(), request);
    }

    // --- 403 Forbidden (Autorização / Permissão) ---
    @ExceptionHandler(AccessDeniedException.class) // O do Spring Security
    public ResponseEntity<StandardError> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, "Acesso negado: Você não tem permissão para realizar essa ação.", request);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<StandardError> unauthorized(UnauthorizedException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED; // 401
        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    // --- 500 Internal Server Error (Genérico) ---
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardError> handleAnyException(Exception ex, HttpServletRequest request) {
        // IMPORTANTE: Logar o erro no servidor para você saber o que consertar
        log.error("Erro interno não tratado: ", ex);

        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno inesperado no servidor.", request);
    }

    private ResponseEntity<StandardError> buildResponse(HttpStatus status, String message, HttpServletRequest request) {
        StandardError error = new StandardError(
                Instant.now(),
                status.value(),
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(error);
    }
}