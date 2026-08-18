package com.franquicias.nequi.application.port;

import com.franquicias.nequi.model.ProductoPorSucursal;

import java.util.List;

public interface IReporteStockPort {

	List<ProductoPorSucursal> obtenerProductoConMayorStockPorSucursal(Long franquiciaId);

}
