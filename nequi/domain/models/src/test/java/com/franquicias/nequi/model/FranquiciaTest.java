package com.franquicias.nequi.model;

import com.franquicias.nequi.exception.NombreInvalidoException;
import com.franquicias.nequi.exception.SucursalNoEncontradaException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FranquiciaTest {

	private static final Sucursal CENTRO = new Sucursal(1L, "Centro", List.of());
	private static final Sucursal NORTE = new Sucursal(2L, "Norte", List.of());

	@Test
	void rechazaNombreInvalido() {
		assertThatThrownBy(() -> new Franquicia(1L, "", List.of()))
			.isInstanceOf(NombreInvalidoException.class);
	}

	@Test
	void laListaDeSucursalesEsInmutable() {
		Franquicia franquicia = new Franquicia(1L, "Nequi", List.of(CENTRO));

		assertThatThrownBy(() -> franquicia.sucursales().add(NORTE))
			.isInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	void conNuevaSucursalAgregaSinMutarLaOriginal() {
		Franquicia original = new Franquicia(1L, "Nequi", List.of(CENTRO));

		Franquicia actualizada = original.conNuevaSucursal(NORTE);

		assertThat(original.sucursales()).containsExactly(CENTRO);
		assertThat(actualizada.sucursales()).containsExactly(CENTRO, NORTE);
	}

	@Test
	void conSucursalActualizadaReemplazaLaSucursalConElMismoId() {
		Franquicia original = new Franquicia(1L, "Nequi", List.of(CENTRO, NORTE));
		Sucursal centroRenombrada = CENTRO.conNombre("Centro Principal");

		Franquicia actualizada = original.conSucursalActualizada(centroRenombrada);

		assertThat(original.sucursales()).containsExactly(CENTRO, NORTE);
		assertThat(actualizada.sucursales()).containsExactly(centroRenombrada, NORTE);
	}

	@Test
	void conSucursalActualizadaConIdInexistenteLanzaExcepcion() {
		Franquicia franquicia = new Franquicia(1L, "Nequi", List.of(CENTRO));
		Sucursal sucursalDesconocida = new Sucursal(999L, "Fantasma", List.of());

		assertThatThrownBy(() -> franquicia.conSucursalActualizada(sucursalDesconocida))
			.isInstanceOf(SucursalNoEncontradaException.class);
	}

	@Test
	void buscarSucursalExitoso() {
		Franquicia franquicia = new Franquicia(1L, "Nequi", List.of(CENTRO, NORTE));

		assertThat(franquicia.buscarSucursal(NORTE.id())).isEqualTo(NORTE);
	}

	@Test
	void buscarSucursalConIdInexistenteLanzaExcepcion() {
		Franquicia franquicia = new Franquicia(1L, "Nequi", List.of(CENTRO));

		assertThatThrownBy(() -> franquicia.buscarSucursal(999L))
			.isInstanceOf(SucursalNoEncontradaException.class);
	}

}
