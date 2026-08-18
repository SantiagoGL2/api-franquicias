package com.franquicias.nequi.controller;

import com.franquicias.nequi.dto.response.ProductoPorSucursalResponse;
import com.franquicias.nequi.exceptionHandler.ErrorResponse;
import com.franquicias.nequi.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/franquicias/{franquiciaId}/reportes")
@Tag(name = "Reportes", description = "Consultas de stock por sucursal")
public class ReporteController {

	private final ReporteService reporteService;

	public ReporteController(ReporteService reporteService) {
		this.reporteService = reporteService;
	}

	@GetMapping("/producto-mayor-stock-por-sucursal")
	@Operation(summary = "Producto con mayor stock por sucursal",
			description = "Devuelve, por cada sucursal de la franquicia, el producto con más stock. "
				+ "Las sucursales sin productos no aparecen en el resultado.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Reporte generado",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductoPorSucursalResponse.class)))),
			@ApiResponse(responseCode = "404", description = "No existe una franquicia con ese id",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public List<ProductoPorSucursalResponse> obtenerProductoConMayorStockPorSucursal(
			@Parameter(description = "Id de la franquicia a consultar") @PathVariable Long franquiciaId) {
		return reporteService.obtenerProductoConMayorStockPorSucursal(franquiciaId).stream()
			.map(ProductoPorSucursalResponse::from)
			.toList();
	}

}
