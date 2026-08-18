package com.franquicias.nequi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CrearFranquiciaRequest(
		@NotBlank @Schema(description = "Nombre de la franquicia", example = "Nequi") String nombre) {
}
