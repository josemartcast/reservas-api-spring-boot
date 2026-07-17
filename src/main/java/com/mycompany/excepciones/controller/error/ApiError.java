package com.mycompany.excepciones.controller.error;

public record ApiError(int status, String error, String message) {
}
