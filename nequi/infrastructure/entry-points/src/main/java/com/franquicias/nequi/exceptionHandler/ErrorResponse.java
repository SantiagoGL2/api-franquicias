package com.franquicias.nequi.exceptionHandler;

import java.time.Instant;

public record ErrorResponse(String mensaje, int status, Instant timestamp) {
}
