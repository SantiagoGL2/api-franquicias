package com.franquicias.nequi.model;

import com.franquicias.nequi.exception.NombreInvalidoException;
import com.franquicias.nequi.exception.StockInvalidoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductoTest {

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {"", "   "})
	void rechazaNombreInvalido(String nombreInvalido) {
		assertThatThrownBy(() -> new Producto(1L, nombreInvalido, 10))
			.isInstanceOf(NombreInvalidoException.class);
	}

	@Test
	void rechazaStockNegativo() {
		assertThatThrownBy(() -> new Producto(1L, "Camiseta", -1))
			.isInstanceOf(StockInvalidoException.class);
	}

	@Test
	void aceptaStockCero() {
		Producto producto = new Producto(1L, "Camiseta", 0);

		assertThat(producto.stock()).isZero();
	}

	@Test
	void conNombreRetornaNuevaInstanciaSinMutarLaOriginal() {
		Producto original = new Producto(1L, "Camiseta", 10);

		Producto renombrado = original.conNombre("Gorra");

		assertThat(original.nombre()).isEqualTo("Camiseta");
		assertThat(renombrado.nombre()).isEqualTo("Gorra");
		assertThat(renombrado.id()).isEqualTo(original.id());
		assertThat(renombrado.stock()).isEqualTo(original.stock());
		assertThat(renombrado).isNotSameAs(original);
	}

	@Test
	void conStockRetornaNuevaInstanciaSinMutarLaOriginal() {
		Producto original = new Producto(1L, "Camiseta", 10);

		Producto conMasStock = original.conStock(25);

		assertThat(original.stock()).isEqualTo(10);
		assertThat(conMasStock.stock()).isEqualTo(25);
		assertThat(conMasStock.id()).isEqualTo(original.id());
		assertThat(conMasStock.nombre()).isEqualTo(original.nombre());
		assertThat(conMasStock).isNotSameAs(original);
	}

}
