package com.franquicias.nequi.model;

import com.franquicias.nequi.exception.NombreInvalidoException;
import com.franquicias.nequi.exception.ProductoNoEncontradoException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SucursalTest {

	private static final Producto CAMISETA = new Producto(1L, "Camiseta", 10);
	private static final Producto GORRA = new Producto(2L, "Gorra", 30);

	@Test
	void rechazaNombreInvalido() {
		assertThatThrownBy(() -> new Sucursal(1L, "  ", List.of()))
			.isInstanceOf(NombreInvalidoException.class);
	}

	@Test
	void laListaDeProductosEsInmutable() {
		Sucursal sucursal = new Sucursal(1L, "Centro", List.of(CAMISETA));

		assertThatThrownBy(() -> sucursal.productos().add(GORRA))
			.isInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	void conNuevoProductoAgregaAlFinalSinMutarLaOriginal() {
		Sucursal original = new Sucursal(1L, "Centro", List.of(CAMISETA));

		Sucursal actualizada = original.conNuevoProducto(GORRA);

		assertThat(original.productos()).containsExactly(CAMISETA);
		assertThat(actualizada.productos()).containsExactly(CAMISETA, GORRA);
	}

	@Test
	void sinProductoQuitaElProductoConEseId() {
		Sucursal original = new Sucursal(1L, "Centro", List.of(CAMISETA, GORRA));

		Sucursal actualizada = original.sinProducto(CAMISETA.id());

		assertThat(original.productos()).containsExactly(CAMISETA, GORRA);
		assertThat(actualizada.productos()).containsExactly(GORRA);
	}

	@Test
	void sinProductoConIdInexistenteLanzaExcepcion() {
		Sucursal sucursal = new Sucursal(1L, "Centro", List.of(CAMISETA));

		assertThatThrownBy(() -> sucursal.sinProducto(999L))
			.isInstanceOf(ProductoNoEncontradoException.class);
	}

	@Test
	void conProductoActualizadoReemplazaElProductoConElMismoId() {
		Sucursal original = new Sucursal(1L, "Centro", List.of(CAMISETA, GORRA));
		Producto camisetaConMasStock = CAMISETA.conStock(50);

		Sucursal actualizada = original.conProductoActualizado(camisetaConMasStock);

		assertThat(original.productos()).containsExactly(CAMISETA, GORRA);
		assertThat(actualizada.productos()).containsExactly(camisetaConMasStock, GORRA);
	}

	@Test
	void conProductoActualizadoConIdInexistenteLanzaExcepcion() {
		Sucursal sucursal = new Sucursal(1L, "Centro", List.of(CAMISETA));
		Producto productoDesconocido = new Producto(999L, "Fantasma", 1);

		assertThatThrownBy(() -> sucursal.conProductoActualizado(productoDesconocido))
			.isInstanceOf(ProductoNoEncontradoException.class);
	}

	@Test
	void productoConMayorStockRetornaElDeMasStock() {
		Sucursal sucursal = new Sucursal(1L, "Centro", List.of(CAMISETA, GORRA));

		Optional<Producto> resultado = sucursal.productoConMayorStock();

		assertThat(resultado).contains(GORRA);
	}

	@Test
	void productoConMayorStockConListaVaciaRetornaOptionalVacio() {
		Sucursal sucursal = new Sucursal(1L, "Centro", List.of());

		assertThat(sucursal.productoConMayorStock()).isEmpty();
	}

}
