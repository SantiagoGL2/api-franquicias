package com.franquicias.nequi.model;

import com.franquicias.nequi.exception.NombreInvalidoException;
import com.franquicias.nequi.exception.SucursalNoEncontradaException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public record Franquicia(Long id, String nombre, List<Sucursal> sucursales) {

	public Franquicia {
		if (nombre == null || nombre.isBlank()) {
			throw new NombreInvalidoException("El nombre de la franquicia no puede estar vacío");
		}
		sucursales = List.copyOf(sucursales);
	}

	public Franquicia conNombre(String nuevoNombre) {
		return new Franquicia(id, nuevoNombre, sucursales);
	}

	public Franquicia conNuevaSucursal(Sucursal sucursal) {
		List<Sucursal> conNueva = new ArrayList<>(sucursales);
		conNueva.add(sucursal);
		return new Franquicia(id, nombre, conNueva);
	}

	public Franquicia conSucursalActualizada(Sucursal sucursalActualizada) {
		buscarSucursal(sucursalActualizada.id());
		List<Sucursal> actualizadas = sucursales.stream()
			.map(sucursal -> Objects.equals(sucursal.id(), sucursalActualizada.id())
				? sucursalActualizada
				: sucursal)
			.toList();
		return new Franquicia(id, nombre, actualizadas);
	}

	public Sucursal buscarSucursal(Long sucursalId) {
		return sucursales.stream()
			.filter(sucursal -> Objects.equals(sucursal.id(), sucursalId))
			.findFirst()
			.orElseThrow(() -> new SucursalNoEncontradaException(
				"No existe una sucursal con id " + sucursalId + " en la franquicia " + id));
	}

}
