package com.franquicias.nequi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;

public record ActualizarStockRequest(
		@PositiveOrZero @Schema(description = "Nuevo stock del producto", example = "25") int stock) {
}
