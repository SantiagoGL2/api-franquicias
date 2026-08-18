package com.franquicias.nequi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AgregarSucursalRequest(
		@NotBlank @Schema(description = "Nombre de la sucursal", example = "Centro") String nombre) {
}
