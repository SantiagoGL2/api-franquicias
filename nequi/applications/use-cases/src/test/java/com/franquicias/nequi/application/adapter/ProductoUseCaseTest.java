package com.franquicias.nequi.application.adapter;

import com.franquicias.nequi.exception.FranquiciaNoEncontradaException;
import com.franquicias.nequi.exception.ProductoNoEncontradoException;
import com.franquicias.nequi.exception.SucursalNoEncontradaException;
import com.franquicias.nequi.model.Franquicia;
import com.franquicias.nequi.model.Producto;
import com.franquicias.nequi.model.Sucursal;
import com.franquicias.nequi.ports.IFranquiciaPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoUseCaseTest {

	private static final Producto CAMISETA = new Producto(100L, "Camiseta", 10);

	@Mock
	private IFranquiciaPersistencePort franquiciaPersistencePort;

	private ProductoUseCase productoUseCase;

	@BeforeEach
	void setUp() {
		productoUseCase = new ProductoUseCase(franquiciaPersistencePort);
	}

	@Test
	void agregarProductoGuardaLaFranquiciaConElProductoNuevo() {
		Sucursal centro = new Sucursal(10L, "Centro", List.of());
		Franquicia existente = new Franquicia(1L, "Nequi", List.of(centro));
		when(franquiciaPersistencePort.buscarPorId(1L)).thenReturn(Optional.of(existente));
		when(franquiciaPersistencePort.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

		productoUseCase.agregarProducto(1L, 10L, "Camiseta", 10);

		ArgumentCaptor<Franquicia> captor = ArgumentCaptor.forClass(Franquicia.class);
		verify(franquiciaPersistencePort).guardar(captor.capture());
		List<Producto> productos = captor.getValue().sucursales().get(0).productos();
		assertThat(productos).hasSize(1);
		assertThat(productos.get(0).nombre()).isEqualTo("Camiseta");
		assertThat(productos.get(0).stock()).isEqualTo(10);
	}

	@Test
	void agregarProductoConFranquiciaInexistenteLanzaExcepcionYNoGuarda() {
		when(franquiciaPersistencePort.buscarPorId(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> productoUseCase.agregarProducto(1L, 10L, "Camiseta", 10))
			.isInstanceOf(FranquiciaNoEncontradaException.class);

		verify(franquiciaPersistencePort, never()).guardar(any());
	}

	@Test
	void agregarProductoConSucursalInexistenteLanzaExcepcionYNoGuarda() {
		Franquicia existente = new Franquicia(1L, "Nequi", List.of());
		when(franquiciaPersistencePort.buscarPorId(1L)).thenReturn(Optional.of(existente));

		assertThatThrownBy(() -> productoUseCase.agregarProducto(1L, 999L, "Camiseta", 10))
			.isInstanceOf(SucursalNoEncontradaException.class);

		verify(franquiciaPersistencePort, never()).guardar(any());
	}

	@Test
	void eliminarProductoGuardaLaFranquiciaSinEseProducto() {
		Sucursal centro = new Sucursal(10L, "Centro", List.of(CAMISETA));
		Franquicia existente = new Franquicia(1L, "Nequi", List.of(centro));
		when(franquiciaPersistencePort.buscarPorId(1L)).thenReturn(Optional.of(existente));
		when(franquiciaPersistencePort.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

		productoUseCase.eliminarProducto(1L, 10L, CAMISETA.id());

		ArgumentCaptor<Franquicia> captor = ArgumentCaptor.forClass(Franquicia.class);
		verify(franquiciaPersistencePort).guardar(captor.capture());
		assertThat(captor.getValue().sucursales().get(0).productos()).isEmpty();
	}

	@Test
	void eliminarProductoConIdInexistenteLanzaExcepcionYNoGuarda() {
		Sucursal centro = new Sucursal(10L, "Centro", List.of(CAMISETA));
		Franquicia existente = new Franquicia(1L, "Nequi", List.of(centro));
		when(franquiciaPersistencePort.buscarPorId(1L)).thenReturn(Optional.of(existente));

		assertThatThrownBy(() -> productoUseCase.eliminarProducto(1L, 10L, 999L))
			.isInstanceOf(ProductoNoEncontradoException.class);

		verify(franquiciaPersistencePort, never()).guardar(any());
	}

	@Test
	void actualizarStockGuardaLaFranquiciaConElProductoActualizado() {
		Sucursal centro = new Sucursal(10L, "Centro", List.of(CAMISETA));
		Franquicia existente = new Franquicia(1L, "Nequi", List.of(centro));
		when(franquiciaPersistencePort.buscarPorId(1L)).thenReturn(Optional.of(existente));
		when(franquiciaPersistencePort.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

		productoUseCase.actualizarStock(1L, 10L, CAMISETA.id(), 99);

		ArgumentCaptor<Franquicia> captor = ArgumentCaptor.forClass(Franquicia.class);
		verify(franquiciaPersistencePort).guardar(captor.capture());
		assertThat(captor.getValue().sucursales().get(0).productos().get(0).stock()).isEqualTo(99);
	}

	@Test
	void actualizarStockConSucursalInexistenteLanzaExcepcionYNoGuarda() {
		Franquicia existente = new Franquicia(1L, "Nequi", List.of());
		when(franquiciaPersistencePort.buscarPorId(1L)).thenReturn(Optional.of(existente));

		assertThatThrownBy(() -> productoUseCase.actualizarStock(1L, 999L, CAMISETA.id(), 99))
			.isInstanceOf(SucursalNoEncontradaException.class);

		verify(franquiciaPersistencePort, never()).guardar(any());
	}

	@Test
	void actualizarNombreProductoGuardaLaFranquiciaConElNombreActualizado() {
		Sucursal centro = new Sucursal(10L, "Centro", List.of(CAMISETA));
		Franquicia existente = new Franquicia(1L, "Nequi", List.of(centro));
		when(franquiciaPersistencePort.buscarPorId(1L)).thenReturn(Optional.of(existente));
		when(franquiciaPersistencePort.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

		productoUseCase.actualizarNombreProducto(1L, 10L, CAMISETA.id(), "Gorra");

		ArgumentCaptor<Franquicia> captor = ArgumentCaptor.forClass(Franquicia.class);
		verify(franquiciaPersistencePort).guardar(captor.capture());
		assertThat(captor.getValue().sucursales().get(0).productos().get(0).nombre()).isEqualTo("Gorra");
	}

	@Test
	void actualizarNombreProductoConProductoInexistenteLanzaExcepcionYNoGuarda() {
		Sucursal centro = new Sucursal(10L, "Centro", List.of());
		Franquicia existente = new Franquicia(1L, "Nequi", List.of(centro));
		when(franquiciaPersistencePort.buscarPorId(1L)).thenReturn(Optional.of(existente));

		assertThatThrownBy(() -> productoUseCase.actualizarNombreProducto(1L, 10L, 999L, "Gorra"))
			.isInstanceOf(ProductoNoEncontradoException.class);

		verify(franquiciaPersistencePort, never()).guardar(any());
	}

}
