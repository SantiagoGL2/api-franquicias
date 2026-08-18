package com.franquicias.nequi.service;

import com.franquicias.nequi.application.port.IReporteStockPort;
import com.franquicias.nequi.model.ProductoPorSucursal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReporteService {

	private final IReporteStockPort reporteStockPort;

	public ReporteService(IReporteStockPort reporteStockPort) {
		this.reporteStockPort = reporteStockPort;
	}

	@Transactional(readOnly = true)
	public List<ProductoPorSucursal> obtenerProductoConMayorStockPorSucursal(Long franquiciaId) {
		return reporteStockPort.obtenerProductoConMayorStockPorSucursal(franquiciaId);
	}

}
