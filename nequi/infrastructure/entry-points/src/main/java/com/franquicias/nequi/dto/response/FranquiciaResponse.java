package com.franquicias.nequi.dto.response;

import com.franquicias.nequi.model.Franquicia;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record FranquiciaResponse(
		@Schema(description = "Id de la franquicia", example = "1") Long id,
		@Schema(description = "Nombre de la franquicia", example = "Nequi") String nombre,
		@Schema(description = "Sucursales de la franquicia") List<SucursalResponse> sucursales) {

	public static FranquiciaResponse from(Franquicia franquicia) {
		List<SucursalResponse> sucursales = franquicia.sucursales().stream()
			.map(SucursalResponse::from)
			.toList();
		return new FranquiciaResponse(franquicia.id(), franquicia.nombre(), sucursales);
	}

}
