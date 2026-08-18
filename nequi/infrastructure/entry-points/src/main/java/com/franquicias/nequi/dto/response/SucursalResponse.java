package com.franquicias.nequi.dto.response;

import com.franquicias.nequi.model.Sucursal;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record SucursalResponse(
		@Schema(description = "Id de la sucursal", example = "1") Long id,
		@Schema(description = "Nombre de la sucursal", example = "Centro") String nombre,
		@Schema(description = "Productos de la sucursal") List<ProductoResponse> productos) {

	public static SucursalResponse from(Sucursal sucursal) {
		List<ProductoResponse> productos = sucursal.productos().stream()
			.map(ProductoResponse::from)
			.toList();
		return new SucursalResponse(sucursal.id(), sucursal.nombre(), productos);
	}

}
