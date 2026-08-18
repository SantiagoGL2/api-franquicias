package com.franquicias.nequi.service;

import com.franquicias.nequi.application.port.IProductoPort;
import com.franquicias.nequi.model.Franquicia;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductoService {

	private final IProductoPort productoPort;

	public ProductoService(IProductoPort productoPort) {
		this.productoPort = productoPort;
	}

	@Transactional(rollbackFor = Exception.class)
	public Franquicia agregarProducto(Long franquiciaId, Long sucursalId, String nombreProducto, int stockInicial) {
		return productoPort.agregarProducto(franquiciaId, sucursalId, nombreProducto, stockInicial);
	}

	@Transactional(rollbackFor = Exception.class)
	public Franquicia eliminarProducto(Long franquiciaId, Long sucursalId, Long productoId) {
		return productoPort.eliminarProducto(franquiciaId, sucursalId, productoId);
	}

	@Transactional(rollbackFor = Exception.class)
	public Franquicia actualizarStock(Long franquiciaId, Long sucursalId, Long productoId, int nuevoStock) {
		return productoPort.actualizarStock(franquiciaId, sucursalId, productoId, nuevoStock);
	}

	@Transactional(rollbackFor = Exception.class)
	public Franquicia actualizarNombreProducto(Long franquiciaId, Long sucursalId, Long productoId,
			String nuevoNombre) {
		return productoPort.actualizarNombreProducto(franquiciaId, sucursalId, productoId, nuevoNombre);
	}

}
