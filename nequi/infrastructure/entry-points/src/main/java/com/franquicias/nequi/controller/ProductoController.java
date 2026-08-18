package com.franquicias.nequi.controller;

import com.franquicias.nequi.dto.request.ActualizarNombreRequest;
import com.franquicias.nequi.dto.request.ActualizarStockRequest;
import com.franquicias.nequi.dto.request.AgregarProductoRequest;
import com.franquicias.nequi.dto.response.FranquiciaResponse;
import com.franquicias.nequi.exceptionHandler.ErrorResponse;
import com.franquicias.nequi.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos")
@Tag(name = "Productos", description = "Gestión de productos dentro de una sucursal")
public class ProductoController {

	private final ProductoService productoService;

	public ProductoController(ProductoService productoService) {
		this.productoService = productoService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Agregar un producto", description = "Agrega un producto nuevo a la sucursal indicada.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Producto agregado",
					content = @Content(schema = @Schema(implementation = FranquiciaResponse.class))),
			@ApiResponse(responseCode = "400", description = "Nombre o stock inválido, o solicitud mal formada",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "No existe la franquicia o la sucursal indicadas",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public FranquiciaResponse agregarProducto(
			@Parameter(description = "Id de la franquicia dueña de la sucursal") @PathVariable Long franquiciaId,
			@Parameter(description = "Id de la sucursal donde se agrega el producto") @PathVariable Long sucursalId,
			@Valid @RequestBody AgregarProductoRequest request) {
		return FranquiciaResponse.from(
			productoService.agregarProducto(franquiciaId, sucursalId, request.nombre(), request.stock()));
	}

	@DeleteMapping("/{productoId}")
	@Operation(summary = "Eliminar un producto", description = "Quita un producto de la sucursal indicada.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Producto eliminado",
					content = @Content(schema = @Schema(implementation = FranquiciaResponse.class))),
			@ApiResponse(responseCode = "404", description = "No existe la franquicia, la sucursal o el producto indicados",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public FranquiciaResponse eliminarProducto(
			@Parameter(description = "Id de la franquicia dueña de la sucursal") @PathVariable Long franquiciaId,
			@Parameter(description = "Id de la sucursal dueña del producto") @PathVariable Long sucursalId,
			@Parameter(description = "Id del producto a eliminar") @PathVariable Long productoId) {
		return FranquiciaResponse.from(productoService.eliminarProducto(franquiciaId, sucursalId, productoId));
	}

	@PatchMapping("/{productoId}/stock")
	@Operation(summary = "Actualizar el stock de un producto", description = "Cambia el stock del producto indicado.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Stock actualizado",
					content = @Content(schema = @Schema(implementation = FranquiciaResponse.class))),
			@ApiResponse(responseCode = "400", description = "Stock inválido o solicitud mal formada",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "No existe la franquicia, la sucursal o el producto indicados",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public FranquiciaResponse actualizarStock(
			@Parameter(description = "Id de la franquicia dueña de la sucursal") @PathVariable Long franquiciaId,
			@Parameter(description = "Id de la sucursal dueña del producto") @PathVariable Long sucursalId,
			@Parameter(description = "Id del producto a actualizar") @PathVariable Long productoId,
			@Valid @RequestBody ActualizarStockRequest request) {
		return FranquiciaResponse.from(
			productoService.actualizarStock(franquiciaId, sucursalId, productoId, request.stock()));
	}

	@PatchMapping("/{productoId}/nombre")
	@Operation(summary = "Renombrar un producto", description = "Cambia el nombre del producto indicado.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Nombre actualizado",
					content = @Content(schema = @Schema(implementation = FranquiciaResponse.class))),
			@ApiResponse(responseCode = "400", description = "Nombre inválido o solicitud mal formada",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "No existe la franquicia, la sucursal o el producto indicados",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public FranquiciaResponse actualizarNombreProducto(
			@Parameter(description = "Id de la franquicia dueña de la sucursal") @PathVariable Long franquiciaId,
			@Parameter(description = "Id de la sucursal dueña del producto") @PathVariable Long sucursalId,
			@Parameter(description = "Id del producto a renombrar") @PathVariable Long productoId,
			@Valid @RequestBody ActualizarNombreRequest request) {
		return FranquiciaResponse.from(
			productoService.actualizarNombreProducto(franquiciaId, sucursalId, productoId, request.nombre()));
	}

}
