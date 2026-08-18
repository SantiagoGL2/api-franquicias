package com.franquicias.nequi.application.adapter;

import com.franquicias.nequi.exception.FranquiciaNoEncontradaException;
import com.franquicias.nequi.model.Franquicia;
import com.franquicias.nequi.model.Producto;
import com.franquicias.nequi.model.ProductoPorSucursal;
import com.franquicias.nequi.model.Sucursal;
import com.franquicias.nequi.ports.IFranquiciaPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReporteStockUseCaseTest {

	@Mock
	private IFranquiciaPersistencePort franquiciaPersistencePort;

	private ReporteStockUseCase reporteStockUseCase;

	@BeforeEach
	void setUp() {
		reporteStockUseCase = new ReporteStockUseCase(franquiciaPersistencePort);
	}

	@Test
	void retornaElProductoConMasStockPorCadaSucursal() {
		Producto camiseta = new Producto(1L, "Camiseta", 10);
		Producto gorra = new Producto(2L, "Gorra", 30);
		Producto media = new Producto(3L, "Media", 5);
		Sucursal centro = new Sucursal(10L, "Centro", List.of(camiseta, gorra));
		Sucursal norte = new Sucursal(20L, "Norte", List.of(media));
		Franquicia franquicia = new Franquicia(1L, "Nequi", List.of(centro, norte));
		when(franquiciaPersistencePort.buscarPorId(1L)).thenReturn(Optional.of(franquicia));

		List<ProductoPorSucursal> resultado = reporteStockUseCase.obtenerProductoConMayorStockPorSucursal(1L);

		assertThat(resultado).containsExactly(
			new ProductoPorSucursal(10L, "Centro", gorra),
			new ProductoPorSucursal(20L, "Norte", media));
	}

	@Test
	void unaSucursalSinProductosNoAparaceEnElResultado() {
		Producto camiseta = new Producto(1L, "Camiseta", 10);
		Sucursal centro = new Sucursal(10L, "Centro", List.of(camiseta));
		Sucursal norte = new Sucursal(20L, "Norte", List.of());
		Franquicia franquicia = new Franquicia(1L, "Nequi", List.of(centro, norte));
		when(franquiciaPersistencePort.buscarPorId(1L)).thenReturn(Optional.of(franquicia));

		List<ProductoPorSucursal> resultado = reporteStockUseCase.obtenerProductoConMayorStockPorSucursal(1L);

		assertThat(resultado).containsExactly(new ProductoPorSucursal(10L, "Centro", camiseta));
	}

	@Test
	void franquiciaInexistenteLanzaExcepcion() {
		when(franquiciaPersistencePort.buscarPorId(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> reporteStockUseCase.obtenerProductoConMayorStockPorSucursal(1L))
			.isInstanceOf(FranquiciaNoEncontradaException.class);
	}

}
