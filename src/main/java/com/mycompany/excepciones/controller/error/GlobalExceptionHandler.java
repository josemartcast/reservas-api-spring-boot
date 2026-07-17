package com.mycompany.excepciones.controller.error;

import com.mycompany.excepciones.MesaOcupadaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MesaOcupadaException.class)
    public ResponseEntity<ApiError> manejarMesaOcupada(MesaOcupadaException exception) {
        HttpStatus status = HttpStatus.CONFLICT;

        ApiError apiError = new ApiError(status.value(), status.getReasonPhrase(), exception.getMessage());

        return ResponseEntity.status(status).body(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationApiError> manejarErroresDeValidacion(MethodArgumentNotValidException exception) {
        Map<String, String> fieldErrors = new HashMap<>();
        HttpStatus status = HttpStatus.BAD_REQUEST;
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            fieldErrors.put(
                    fieldError.getField(),
                    fieldError.getDefaultMessage()
            );
        }
        ValidationApiError validationApiError = new ValidationApiError(status.value(), status.getReasonPhrase(), fieldErrors);
        return ResponseEntity.status(status).body(validationApiError);
    }
}
