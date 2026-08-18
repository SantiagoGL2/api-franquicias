package com.franquicias.nequi.controller;

import com.franquicias.nequi.dto.request.ActualizarNombreRequest;
import com.franquicias.nequi.dto.request.AgregarSucursalRequest;
import com.franquicias.nequi.dto.request.CrearFranquiciaRequest;
import com.franquicias.nequi.dto.response.FranquiciaResponse;
import com.franquicias.nequi.exceptionHandler.ErrorResponse;
import com.franquicias.nequi.service.FranquiciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/franquicias")
@Tag(name = "Franquicias", description = "Gestión de franquicias y sus sucursales")
public class FranquiciaController {

	private final FranquiciaService franquiciaService;

	public FranquiciaController(FranquiciaService franquiciaService) {
		this.franquiciaService = franquiciaService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Crear una franquicia", description = "Crea una franquicia nueva, sin sucursales, con el nombre indicado.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Franquicia creada",
					content = @Content(schema = @Schema(implementation = FranquiciaResponse.class))),
			@ApiResponse(responseCode = "400", description = "Nombre inválido o solicitud mal formada",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public FranquiciaResponse crear(@Valid @RequestBody CrearFranquiciaRequest request) {
		return FranquiciaResponse.from(franquiciaService.crear(request.nombre()));
	}

	@PatchMapping("/{franquiciaId}/nombre")
	@Operation(summary = "Renombrar una franquicia", description = "Cambia el nombre de la franquicia indicada.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Franquicia actualizada",
					content = @Content(schema = @Schema(implementation = FranquiciaResponse.class))),
			@ApiResponse(responseCode = "400", description = "Nombre inválido o solicitud mal formada",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "No existe una franquicia con ese id",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public FranquiciaResponse actualizarNombre(
			@Parameter(description = "Id de la franquicia a renombrar") @PathVariable Long franquiciaId,
			@Valid @RequestBody ActualizarNombreRequest request) {
		return FranquiciaResponse.from(franquiciaService.actualizarNombre(franquiciaId, request.nombre()));
	}

	@PostMapping("/{franquiciaId}/sucursales")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Agregar una sucursal", description = "Agrega una sucursal nueva, sin productos, a la franquicia indicada.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Sucursal agregada",
					content = @Content(schema = @Schema(implementation = FranquiciaResponse.class))),
			@ApiResponse(responseCode = "400", description = "Nombre inválido o solicitud mal formada",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "No existe una franquicia con ese id",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public FranquiciaResponse agregarSucursal(
			@Parameter(description = "Id de la franquicia dueña de la nueva sucursal") @PathVariable Long franquiciaId,
			@Valid @RequestBody AgregarSucursalRequest request) {
		return FranquiciaResponse.from(franquiciaService.agregarSucursal(franquiciaId, request.nombre()));
	}

	@PatchMapping("/{franquiciaId}/sucursales/{sucursalId}/nombre")
	@Operation(summary = "Renombrar una sucursal", description = "Cambia el nombre de la sucursal indicada, dentro de la franquicia indicada.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Sucursal actualizada",
					content = @Content(schema = @Schema(implementation = FranquiciaResponse.class))),
			@ApiResponse(responseCode = "400", description = "Nombre inválido o solicitud mal formada",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "No existe la franquicia o la sucursal indicadas",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public FranquiciaResponse actualizarNombreSucursal(
			@Parameter(description = "Id de la franquicia dueña de la sucursal") @PathVariable Long franquiciaId,
			@Parameter(description = "Id de la sucursal a renombrar") @PathVariable Long sucursalId,
			@Valid @RequestBody ActualizarNombreRequest request) {
		return FranquiciaResponse.from(
			franquiciaService.actualizarNombreSucursal(franquiciaId, sucursalId, request.nombre()));
	}

}
