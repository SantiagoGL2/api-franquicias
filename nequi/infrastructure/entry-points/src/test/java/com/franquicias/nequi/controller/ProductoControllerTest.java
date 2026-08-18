package com.franquicias.nequi.controller;

import com.franquicias.nequi.exception.ProductoNoEncontradoException;
import com.franquicias.nequi.model.Franquicia;
import com.franquicias.nequi.model.Producto;
import com.franquicias.nequi.model.Sucursal;
import com.franquicias.nequi.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

	private static final String BASE_URL = "/api/franquicias/1/sucursales/10/productos";

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ProductoService productoService;

	private static Franquicia franquiciaConProducto(Producto producto) {
		Sucursal centro = new Sucursal(10L, "Centro", List.of(producto));
		return new Franquicia(1L, "Nequi", List.of(centro));
	}

	@Test
	void agregarProducto_devuelve201ConLaFranquiciaActualizada() throws Exception {
		Producto camiseta = new Producto(100L, "Camiseta", 10);
		when(productoService.agregarProducto(eq(1L), eq(10L), anyString(), eq(10)))
			.thenReturn(franquiciaConProducto(camiseta));

		mockMvc.perform(post(BASE_URL)
				.contentType("application/json")
				.content("{\"nombre\":\"Camiseta\",\"stock\":10}"))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.sucursales[0].productos[0].nombre").value("Camiseta"));
	}

	@Test
	void agregarProducto_conStockNegativoDevuelve400() throws Exception {
		mockMvc.perform(post(BASE_URL)
				.contentType("application/json")
				.content("{\"nombre\":\"Camiseta\",\"stock\":-1}"))
			.andExpect(status().isBadRequest());
	}

	@Test
	void eliminarProducto_devuelve200ConLaFranquiciaActualizada() throws Exception {
		Franquicia franquiciaSinProducto = new Franquicia(1L, "Nequi", List.of(new Sucursal(10L, "Centro", List.of())));
		when(productoService.eliminarProducto(1L, 10L, 100L)).thenReturn(franquiciaSinProducto);

		mockMvc.perform(delete(BASE_URL + "/100"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.sucursales[0].productos").isEmpty());
	}

	@Test
	void eliminarProducto_inexistenteDevuelve404() throws Exception {
		when(productoService.eliminarProducto(1L, 10L, 999L))
			.thenThrow(new ProductoNoEncontradoException("No existe un producto con id 999"));

		mockMvc.perform(delete(BASE_URL + "/999"))
			.andExpect(status().isNotFound());
	}

	@Test
	void actualizarStock_devuelve200ConElStockActualizado() throws Exception {
		Producto camiseta = new Producto(100L, "Camiseta", 99);
		when(productoService.actualizarStock(1L, 10L, 100L, 99)).thenReturn(franquiciaConProducto(camiseta));

		mockMvc.perform(patch(BASE_URL + "/100/stock")
				.contentType("application/json")
				.content("{\"stock\":99}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.sucursales[0].productos[0].stock").value(99));
	}

	@Test
	void actualizarNombreProducto_devuelve200ConElNombreActualizado() throws Exception {
		Producto gorra = new Producto(100L, "Gorra", 10);
		when(productoService.actualizarNombreProducto(1L, 10L, 100L, "Gorra")).thenReturn(franquiciaConProducto(gorra));

		mockMvc.perform(patch(BASE_URL + "/100/nombre")
				.contentType("application/json")
				.content("{\"nombre\":\"Gorra\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.sucursales[0].productos[0].nombre").value("Gorra"));
	}

}
