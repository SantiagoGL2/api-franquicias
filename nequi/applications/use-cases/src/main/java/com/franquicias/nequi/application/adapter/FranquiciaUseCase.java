package com.franquicias.nequi.application.adapter;

import com.franquicias.nequi.application.port.IFranquiciaPort;
import com.franquicias.nequi.exception.FranquiciaNoEncontradaException;
import com.franquicias.nequi.model.Franquicia;
import com.franquicias.nequi.model.Sucursal;
import com.franquicias.nequi.ports.IFranquiciaPersistencePort;

import java.util.List;

public class FranquiciaUseCase implements IFranquiciaPort {

	private final IFranquiciaPersistencePort franquiciaPersistencePort;

	public FranquiciaUseCase(IFranquiciaPersistencePort franquiciaPersistencePort) {
		this.franquiciaPersistencePort = franquiciaPersistencePort;
	}

	@Override
	public Franquicia crear(String nombre) {
		Franquicia franquicia = new Franquicia(null, nombre, List.of());
		return franquiciaPersistencePort.guardar(franquicia);
	}

	@Override
	public Franquicia actualizarNombre(Long franquiciaId, String nuevoNombre) {
		Franquicia franquicia = buscarFranquicia(franquiciaId);
		return franquiciaPersistencePort.guardar(franquicia.conNombre(nuevoNombre));
	}

	@Override
	public Franquicia agregarSucursal(Long franquiciaId, String nombreSucursal) {
		Franquicia franquicia = buscarFranquicia(franquiciaId);
		Sucursal sucursal = new Sucursal(null, nombreSucursal, List.of());
		return franquiciaPersistencePort.guardar(franquicia.conNuevaSucursal(sucursal));
	}

	@Override
	public Franquicia actualizarNombreSucursal(Long franquiciaId, Long sucursalId, String nuevoNombre) {
		Franquicia franquicia = buscarFranquicia(franquiciaId);
		Sucursal sucursal = franquicia.buscarSucursal(sucursalId);
		Sucursal sucursalActualizada = sucursal.conNombre(nuevoNombre);
		return franquiciaPersistencePort.guardar(franquicia.conSucursalActualizada(sucursalActualizada));
	}

	private Franquicia buscarFranquicia(Long franquiciaId) {
		return franquiciaPersistencePort.buscarPorId(franquiciaId)
			.orElseThrow(() -> new FranquiciaNoEncontradaException(
				"No existe una franquicia con id " + franquiciaId));
	}

}
