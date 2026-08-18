package com.franquicias.nequi.exceptionHandler;

import com.franquicias.nequi.exception.FranquiciaNoEncontradaException;
import com.franquicias.nequi.exception.NombreInvalidoException;
import com.franquicias.nequi.exception.ProductoNoEncontradoException;
import com.franquicias.nequi.exception.StockInvalidoException;
import com.franquicias.nequi.exception.SucursalNoEncontradaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler({
			FranquiciaNoEncontradaException.class,
			SucursalNoEncontradaException.class,
			ProductoNoEncontradoException.class
	})
	public ResponseEntity<ErrorResponse> manejarNoEncontrado(RuntimeException exception) {
		return construirRespuesta(HttpStatus.NOT_FOUND, exception.getMessage());
	}

	@ExceptionHandler({NombreInvalidoException.class, StockInvalidoException.class})
	public ResponseEntity<ErrorResponse> manejarSolicitudInvalida(RuntimeException exception) {
		return construirRespuesta(HttpStatus.BAD_REQUEST, exception.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> manejarValidacionFallida(MethodArgumentNotValidException exception) {
		String mensaje = exception.getBindingResult().getFieldErrors().stream()
			.map(error -> error.getField() + ": " + error.getDefaultMessage())
			.collect(Collectors.joining("; "));
		return construirRespuesta(HttpStatus.BAD_REQUEST, mensaje);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> manejarErrorInesperado(Exception exception) {
		return construirRespuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado");
	}

	private ResponseEntity<ErrorResponse> construirRespuesta(HttpStatus status, String mensaje) {
		return ResponseEntity.status(status).body(new ErrorResponse(mensaje, status.value(), Instant.now()));
	}

}
