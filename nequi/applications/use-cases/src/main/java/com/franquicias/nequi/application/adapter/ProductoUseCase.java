package com.franquicias.nequi.application.adapter;

import com.franquicias.nequi.application.port.IProductoPort;
import com.franquicias.nequi.exception.FranquiciaNoEncontradaException;
import com.franquicias.nequi.model.Franquicia;
import com.franquicias.nequi.model.Producto;
import com.franquicias.nequi.model.Sucursal;
import com.franquicias.nequi.ports.IFranquiciaPersistencePort;

public class ProductoUseCase implements IProductoPort {

	private final IFranquiciaPersistencePort franquiciaPersistencePort;

	public ProductoUseCase(IFranquiciaPersistencePort franquiciaPersistencePort) {
		this.franquiciaPersistencePort = franquiciaPersistencePort;
	}

	@Override
	public Franquicia agregarProducto(Long franquiciaId, Long sucursalId, String nombreProducto, int stockInicial) {
		Franquicia franquicia = buscarFranquicia(franquiciaId);
		Sucursal sucursal = franquicia.buscarSucursal(sucursalId);
		Producto producto = new Producto(null, nombreProducto, stockInicial);
		Sucursal sucursalActualizada = sucursal.conNuevoProducto(producto);
		return franquiciaPersistencePort.guardar(franquicia.conSucursalActualizada(sucursalActualizada));
	}

	@Override
	public Franquicia eliminarProducto(Long franquiciaId, Long sucursalId, Long productoId) {
		Franquicia franquicia = buscarFranquicia(franquiciaId);
		Sucursal sucursal = franquicia.buscarSucursal(sucursalId);
		Sucursal sucursalActualizada = sucursal.sinProducto(productoId);
		return franquiciaPersistencePort.guardar(franquicia.conSucursalActualizada(sucursalActualizada));
	}

	@Override
	public Franquicia actualizarStock(Long franquiciaId, Long sucursalId, Long productoId, int nuevoStock) {
		Franquicia franquicia = buscarFranquicia(franquiciaId);
		Sucursal sucursal = franquicia.buscarSucursal(sucursalId);
		Producto producto = sucursal.buscarProducto(productoId);
		Sucursal sucursalActualizada = sucursal.conProductoActualizado(producto.conStock(nuevoStock));
		return franquiciaPersistencePort.guardar(franquicia.conSucursalActualizada(sucursalActualizada));
	}

	@Override
	public Franquicia actualizarNombreProducto(Long franquiciaId, Long sucursalId, Long productoId, String nuevoNombre) {
		Franquicia franquicia = buscarFranquicia(franquiciaId);
		Sucursal sucursal = franquicia.buscarSucursal(sucursalId);
		Producto producto = sucursal.buscarProducto(productoId);
		Sucursal sucursalActualizada = sucursal.conProductoActualizado(producto.conNombre(nuevoNombre));
		return franquiciaPersistencePort.guardar(franquicia.conSucursalActualizada(sucursalActualizada));
	}

	private Franquicia buscarFranquicia(Long franquiciaId) {
		return franquiciaPersistencePort.buscarPorId(franquiciaId)
			.orElseThrow(() -> new FranquiciaNoEncontradaException(
				"No existe una franquicia con id " + franquiciaId));
	}

}
