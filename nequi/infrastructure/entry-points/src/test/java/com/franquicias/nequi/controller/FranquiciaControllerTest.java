package com.franquicias.nequi.controller;

import com.franquicias.nequi.exception.FranquiciaNoEncontradaException;
import com.franquicias.nequi.exception.SucursalNoEncontradaException;
import com.franquicias.nequi.model.Franquicia;
import com.franquicias.nequi.model.Sucursal;
import com.franquicias.nequi.service.FranquiciaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FranquiciaController.class)
class FranquiciaControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private FranquiciaService franquiciaService;

	@Test
	void crear_devuelve201ConLaFranquiciaCreada() throws Exception {
		Franquicia franquicia = new Franquicia(1L, "Nequi", List.of());
		when(franquiciaService.crear("Nequi")).thenReturn(franquicia);

		mockMvc.perform(post("/api/franquicias")
				.contentType("application/json")
				.content("{\"nombre\":\"Nequi\"}"))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").value(1))
			.andExpect(jsonPath("$.nombre").value("Nequi"));
	}

	@Test
	void crear_conNombreVacioDevuelve400() throws Exception {
		mockMvc.perform(post("/api/franquicias")
				.contentType("application/json")
				.content("{\"nombre\":\"\"}"))
			.andExpect(status().isBadRequest());
	}

	@Test
	void actualizarNombre_devuelve200ConLaFranquiciaActualizada() throws Exception {
		Franquicia franquicia = new Franquicia(1L, "Nequi Colombia", List.of());
		when(franquiciaService.actualizarNombre(eq(1L), anyString())).thenReturn(franquicia);

		mockMvc.perform(patch("/api/franquicias/1/nombre")
				.contentType("application/json")
				.content("{\"nombre\":\"Nequi Colombia\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.nombre").value("Nequi Colombia"));
	}

	@Test
	void actualizarNombre_franquiciaInexistenteDevuelve404() throws Exception {
		when(franquiciaService.actualizarNombre(eq(999L), anyString()))
			.thenThrow(new FranquiciaNoEncontradaException("No existe una franquicia con id 999"));

		mockMvc.perform(patch("/api/franquicias/999/nombre")
				.contentType("application/json")
				.content("{\"nombre\":\"Nequi Colombia\"}"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status").value(404));
	}

	@Test
	void agregarSucursal_devuelve201ConLaFranquiciaActualizada() throws Exception {
		Sucursal centro = new Sucursal(10L, "Centro", List.of());
		Franquicia franquicia = new Franquicia(1L, "Nequi", List.of(centro));
		when(franquiciaService.agregarSucursal(eq(1L), anyString())).thenReturn(franquicia);

		mockMvc.perform(post("/api/franquicias/1/sucursales")
				.contentType("application/json")
				.content("{\"nombre\":\"Centro\"}"))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.sucursales[0].nombre").value("Centro"));
	}

	@Test
	void actualizarNombreSucursal_devuelve200() throws Exception {
		Sucursal centro = new Sucursal(10L, "Centro Principal", List.of());
		Franquicia franquicia = new Franquicia(1L, "Nequi", List.of(centro));
		when(franquiciaService.actualizarNombreSucursal(eq(1L), eq(10L), anyString())).thenReturn(franquicia);

		mockMvc.perform(patch("/api/franquicias/1/sucursales/10/nombre")
				.contentType("application/json")
				.content("{\"nombre\":\"Centro Principal\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.sucursales[0].nombre").value("Centro Principal"));
	}

	@Test
	void actualizarNombreSucursal_sucursalInexistenteDevuelve404() throws Exception {
		when(franquiciaService.actualizarNombreSucursal(eq(1L), eq(999L), anyString()))
			.thenThrow(new SucursalNoEncontradaException("No existe una sucursal con id 999"));

		mockMvc.perform(patch("/api/franquicias/1/sucursales/999/nombre")
				.contentType("application/json")
				.content("{\"nombre\":\"Otro\"}"))
			.andExpect(status().isNotFound());
	}

}
