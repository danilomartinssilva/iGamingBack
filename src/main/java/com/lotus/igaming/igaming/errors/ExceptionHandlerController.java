package com.lotus.igaming.igaming.errors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException exception) {

        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(e -> {
            errors.put(e.getField(), e.getDefaultMessage());
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException e) {

        Throwable rootCause = e.getRootCause();
        String causeMessage = rootCause != null ? rootCause.getMessage() : e.getMessage();

        if (causeMessage != null &&
                (causeMessage.toLowerCase().contains("unique constraint") ||
                        causeMessage.toLowerCase().contains("clients_email_key"))) {

            return new ResponseEntity<>("Erro: O e-mail fornecido já está registrado.", HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>("Erro nao especificado, nao procure ninguem.", HttpStatus.CONFLICT);
    }

}