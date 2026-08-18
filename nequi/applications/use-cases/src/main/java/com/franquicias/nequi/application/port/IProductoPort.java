package com.franquicias.nequi.application.port;

import com.franquicias.nequi.model.Franquicia;


public interface IProductoPort {

	Franquicia agregarProducto(Long franquiciaId, Long sucursalId, String nombreProducto, int stockInicial);

	Franquicia eliminarProducto(Long franquiciaId, Long sucursalId, Long productoId);

	Franquicia actualizarStock(Long franquiciaId, Long sucursalId, Long productoId, int nuevoStock);

	Franquicia actualizarNombreProducto(Long franquiciaId, Long sucursalId, Long productoId, String nuevoNombre);

}
