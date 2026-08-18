package com.franquicias.nequi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record AgregarProductoRequest(
		@NotBlank @Schema(description = "Nombre del producto", example = "Camiseta") String nombre,
		@PositiveOrZero @Schema(description = "Stock inicial del producto", example = "10") int stock) {
}
