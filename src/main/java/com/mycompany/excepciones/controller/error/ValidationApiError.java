package com.mycompany.excepciones.controller.error;

import java.util.Map;

public record ValidationApiError(int status, String error, Map<String, String> fieldErrors) {
}
