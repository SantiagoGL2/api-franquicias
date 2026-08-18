package com.franquicias.nequi.dto.response;

import com.franquicias.nequi.model.ProductoPorSucursal;
import io.swagger.v3.oas.annotations.media.Schema;

public record ProductoPorSucursalResponse(
		@Schema(description = "Id de la sucursal", example = "1") Long sucursalId,
		@Schema(description = "Nombre de la sucursal", example = "Centro") String sucursalNombre,
		@Schema(description = "Producto con mayor stock en la sucursal") ProductoResponse producto) {

	public static ProductoPorSucursalResponse from(ProductoPorSucursal productoPorSucursal) {
		return new ProductoPorSucursalResponse(
			productoPorSucursal.sucursalId(),
			productoPorSucursal.sucursalNombre(),
			ProductoResponse.from(productoPorSucursal.producto()));
	}

}
