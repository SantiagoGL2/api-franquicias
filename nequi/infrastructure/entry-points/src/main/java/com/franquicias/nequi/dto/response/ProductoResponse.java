package com.franquicias.nequi.dto.response;

import com.franquicias.nequi.model.Producto;
import io.swagger.v3.oas.annotations.media.Schema;

public record ProductoResponse(
		@Schema(description = "Id del producto", example = "1") Long id,
		@Schema(description = "Nombre del producto", example = "Camiseta") String nombre,
		@Schema(description = "Stock actual del producto", example = "10") int stock) {

	public static ProductoResponse from(Producto producto) {
		return new ProductoResponse(producto.id(), producto.nombre(), producto.stock());
	}

}
