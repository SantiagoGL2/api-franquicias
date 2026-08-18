package com.franquicias.nequi.application.adapter;

import com.franquicias.nequi.exception.FranquiciaNoEncontradaException;
import com.franquicias.nequi.exception.SucursalNoEncontradaException;
import com.franquicias.nequi.model.Franquicia;
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
class FranquiciaUseCaseTest {

	@Mock
	private IFranquiciaPersistencePort franquiciaPersistencePort;

	private FranquiciaUseCase franquiciaUseCase;

	@BeforeEach
	void setUp() {
		franquiciaUseCase = new FranquiciaUseCase(franquiciaPersistencePort);
	}

	@Test
	void crearGuardaUnaFranquiciaNuevaSinSucursales() {
		when(franquiciaPersistencePort.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

		Franquicia resultado = franquiciaUseCase.crear("Nequi");

		ArgumentCaptor<Franquicia> captor = ArgumentCaptor.forClass(Franquicia.class);
		verify(franquiciaPersistencePort).guardar(captor.capture());
		Franquicia guardada = captor.getValue();
		assertThat(guardada.id()).isNull();
		assertThat(guardada.nombre()).isEqualTo("Nequi");
		assertThat(guardada.sucursales()).isEmpty();
		assertThat(resultado).isEqualTo(guardada);
	}

	@Test
	void actualizarNombreGuardaLaFranquiciaConElNuevoNombre() {
		Franquicia existente = new Franquicia(1L, "Nequi", List.of());
		when(franquiciaPersistencePort.buscarPorId(1L)).thenReturn(Optional.of(existente));
		when(franquiciaPersistencePort.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

		franquiciaUseCase.actualizarNombre(1L, "Nequi Colombia");

		ArgumentCaptor<Franquicia> captor = ArgumentCaptor.forClass(Franquicia.class);
		verify(franquiciaPersistencePort).guardar(captor.capture());
		assertThat(captor.getValue().nombre()).isEqualTo("Nequi Colombia");
	}

	@Test
	void actualizarNombreConFranquiciaInexistenteLanzaExcepcionYNoGuarda() {
		when(franquiciaPersistencePort.buscarPorId(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> franquiciaUseCase.actualizarNombre(1L, "Nequi Colombia"))
			.isInstanceOf(FranquiciaNoEncontradaException.class);

		verify(franquiciaPersistencePort, never()).guardar(any());
	}

	@Test
	void agregarSucursalGuardaLaFranquiciaConLaNuevaSucursal() {
		Franquicia existente = new Franquicia(1L, "Nequi", List.of());
		when(franquiciaPersistencePort.buscarPorId(1L)).thenReturn(Optional.of(existente));
		when(franquiciaPersistencePort.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

		franquiciaUseCase.agregarSucursal(1L, "Centro");

		ArgumentCaptor<Franquicia> captor = ArgumentCaptor.forClass(Franquicia.class);
		verify(franquiciaPersistencePort).guardar(captor.capture());
		assertThat(captor.getValue().sucursales()).hasSize(1);
		assertThat(captor.getValue().sucursales().get(0).nombre()).isEqualTo("Centro");
		assertThat(captor.getValue().sucursales().get(0).id()).isNull();
	}

	@Test
	void agregarSucursalConFranquiciaInexistenteLanzaExcepcionYNoGuarda() {
		when(franquiciaPersistencePort.buscarPorId(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> franquiciaUseCase.agregarSucursal(1L, "Centro"))
			.isInstanceOf(FranquiciaNoEncontradaException.class);

		verify(franquiciaPersistencePort, never()).guardar(any());
	}

	@Test
	void actualizarNombreSucursalGuardaLaFranquiciaConLaSucursalRenombrada() {
		Sucursal centro = new Sucursal(10L, "Centro", List.of());
		Franquicia existente = new Franquicia(1L, "Nequi", List.of(centro));
		when(franquiciaPersistencePort.buscarPorId(1L)).thenReturn(Optional.of(existente));
		when(franquiciaPersistencePort.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

		franquiciaUseCase.actualizarNombreSucursal(1L, 10L, "Centro Principal");

		ArgumentCaptor<Franquicia> captor = ArgumentCaptor.forClass(Franquicia.class);
		verify(franquiciaPersistencePort).guardar(captor.capture());
		assertThat(captor.getValue().sucursales()).hasSize(1);
		assertThat(captor.getValue().sucursales().get(0).nombre()).isEqualTo("Centro Principal");
	}

	@Test
	void actualizarNombreSucursalConSucursalInexistenteLanzaExcepcionYNoGuarda() {
		Franquicia existente = new Franquicia(1L, "Nequi", List.of());
		when(franquiciaPersistencePort.buscarPorId(1L)).thenReturn(Optional.of(existente));

		assertThatThrownBy(() -> franquiciaUseCase.actualizarNombreSucursal(1L, 999L, "Otro"))
			.isInstanceOf(SucursalNoEncontradaException.class);

		verify(franquiciaPersistencePort, never()).guardar(any());
	}

}
