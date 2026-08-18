package com.franquicias.nequi.application.adapter;

import com.franquicias.nequi.application.port.IReporteStockPort;
import com.franquicias.nequi.exception.FranquiciaNoEncontradaException;
import com.franquicias.nequi.model.Franquicia;
import com.franquicias.nequi.model.ProductoPorSucursal;
import com.franquicias.nequi.model.Sucursal;
import com.franquicias.nequi.ports.IFranquiciaPersistencePort;

import java.util.ArrayList;
import java.util.List;

public class ReporteStockUseCase implements IReporteStockPort {

	private final IFranquiciaPersistencePort franquiciaPersistencePort;

	public ReporteStockUseCase(IFranquiciaPersistencePort franquiciaPersistencePort) {
		this.franquiciaPersistencePort = franquiciaPersistencePort;
	}

	@Override
	public List<ProductoPorSucursal> obtenerProductoConMayorStockPorSucursal(Long franquiciaId) {
		Franquicia franquicia = franquiciaPersistencePort.buscarPorId(franquiciaId)
			.orElseThrow(() -> new FranquiciaNoEncontradaException(
				"No existe una franquicia con id " + franquiciaId));

		List<ProductoPorSucursal> resultado = new ArrayList<>();
		for (Sucursal sucursal : franquicia.sucursales()) {
			sucursal.productoConMayorStock().ifPresent(producto ->
				resultado.add(new ProductoPorSucursal(sucursal.id(), sucursal.nombre(), producto)));
		}
		return resultado;
	}

}
