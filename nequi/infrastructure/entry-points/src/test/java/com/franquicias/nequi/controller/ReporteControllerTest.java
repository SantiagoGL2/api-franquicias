package com.franquicias.nequi.controller;

import com.franquicias.nequi.exception.FranquiciaNoEncontradaException;
import com.franquicias.nequi.model.Producto;
import com.franquicias.nequi.model.ProductoPorSucursal;
import com.franquicias.nequi.service.ReporteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReporteController.class)
class ReporteControllerTest {

	private static final String URL = "/api/franquicias/1/reportes/producto-mayor-stock-por-sucursal";

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ReporteService reporteService;

	@Test
	void obtenerReporte_devuelve200ConElProductoDeMayorStockPorSucursal() throws Exception {
		ProductoPorSucursal centro = new ProductoPorSucursal(10L, "Centro", new Producto(2L, "Gorra", 30));
		ProductoPorSucursal norte = new ProductoPorSucursal(20L, "Norte", new Producto(3L, "Media", 5));
		when(reporteService.obtenerProductoConMayorStockPorSucursal(1L)).thenReturn(List.of(centro, norte));

		mockMvc.perform(get(URL))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].sucursalNombre").value("Centro"))
			.andExpect(jsonPath("$[0].producto.nombre").value("Gorra"))
			.andExpect(jsonPath("$[1].sucursalNombre").value("Norte"))
			.andExpect(jsonPath("$[1].producto.nombre").value("Media"));
	}

	@Test
	void obtenerReporte_franquiciaInexistenteDevuelve404() throws Exception {
		when(reporteService.obtenerProductoConMayorStockPorSucursal(999L))
			.thenThrow(new FranquiciaNoEncontradaException("No existe una franquicia con id 999"));

		mockMvc.perform(get("/api/franquicias/999/reportes/producto-mayor-stock-por-sucursal"))
			.andExpect(status().isNotFound());
	}

}
