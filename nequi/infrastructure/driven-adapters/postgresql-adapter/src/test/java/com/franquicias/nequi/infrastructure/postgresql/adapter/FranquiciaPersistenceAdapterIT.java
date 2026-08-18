package com.franquicias.nequi.infrastructure.postgresql.adapter;

import com.franquicias.nequi.model.Franquicia;
import com.franquicias.nequi.model.Producto;
import com.franquicias.nequi.model.Sucursal;
import com.franquicias.nequi.ports.IFranquiciaPersistencePort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test against a real Postgres (Testcontainers), not H2 — the
 * point is to exercise the actual Flyway migration, the fetch-join +
 * SUBSELECT repository queries, and orphanRemoval end to end.
 */
@SpringBootTest
@Testcontainers
class FranquiciaPersistenceAdapterIT {

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

	@Autowired
	private IFranquiciaPersistencePort franquiciaPersistencePort;

	@Test
	void guardaYRecuperaUnaFranquiciaConSucursalesYProductos() {
		Producto camiseta = new Producto(null, "Camiseta", 10);
		Producto gorra = new Producto(null, "Gorra", 30);
		Sucursal centro = new Sucursal(null, "Centro", List.of(camiseta, gorra));
		Franquicia franquicia = new Franquicia(null, "Nequi", List.of(centro));

		Franquicia guardada = franquiciaPersistencePort.guardar(franquicia);

		assertThat(guardada.id()).isNotNull();
		assertThat(guardada.sucursales()).hasSize(1);
		Sucursal sucursalGuardada = guardada.sucursales().get(0);
		assertThat(sucursalGuardada.id()).isNotNull();
		assertThat(sucursalGuardada.productos()).hasSize(2);
		assertThat(sucursalGuardada.productos()).allSatisfy(p -> assertThat(p.id()).isNotNull());

		Optional<Franquicia> encontrada = franquiciaPersistencePort.buscarPorId(guardada.id());

		assertThat(encontrada).isPresent();
		assertThat(encontrada.get().nombre()).isEqualTo("Nequi");
		assertThat(encontrada.get().sucursales()).hasSize(1);
		assertThat(encontrada.get().sucursales().get(0).nombre()).isEqualTo("Centro");
		assertThat(encontrada.get().sucursales().get(0).productos())
			.extracting(Producto::nombre)
			.containsExactlyInAnyOrder("Camiseta", "Gorra");
	}

	@Test
	void listarTodasIncluyeLaFranquiciaGuardada() {
		Franquicia franquicia = franquiciaPersistencePort.guardar(
			new Franquicia(null, "Nequi Listar", List.of()));

		List<Franquicia> todas = franquiciaPersistencePort.listarTodas();

		assertThat(todas).extracting(Franquicia::id).contains(franquicia.id());
	}

	@Test
	void actualizarQuitandoUnProductoBorraElHuerfanoEnBaseDeDatos() {
		Producto camiseta = new Producto(null, "Camiseta", 10);
		Producto gorra = new Producto(null, "Gorra", 30);
		Sucursal centro = new Sucursal(null, "Centro", List.of(camiseta, gorra));
		Franquicia franquicia = franquiciaPersistencePort.guardar(
			new Franquicia(null, "Nequi Orphan", List.of(centro)));

		Sucursal sucursalGuardada = franquicia.sucursales().get(0);
		Producto productoAQuitar = sucursalGuardada.productos().stream()
			.filter(p -> p.nombre().equals("Gorra"))
			.findFirst()
			.orElseThrow();

		Sucursal sucursalSinGorra = sucursalGuardada.sinProducto(productoAQuitar.id());
		Franquicia franquiciaActualizada = franquicia.conSucursalActualizada(sucursalSinGorra);
		franquiciaPersistencePort.guardar(franquiciaActualizada);

		Franquicia recargada = franquiciaPersistencePort.buscarPorId(franquicia.id()).orElseThrow();

		assertThat(recargada.sucursales()).hasSize(1);
		assertThat(recargada.sucursales().get(0).productos())
			.extracting(Producto::nombre)
			.containsExactly("Camiseta");
	}

	@Test
	void actualizarAgregandoUnaSucursalNuevaLaPersiste() {
		Franquicia franquicia = franquiciaPersistencePort.guardar(
			new Franquicia(null, "Nequi Agregar", List.of()));

		Sucursal sucursalNueva = new Sucursal(null, "Norte", List.of());
		Franquicia actualizada = franquiciaPersistencePort.guardar(
			franquicia.conNuevaSucursal(sucursalNueva));

		assertThat(actualizada.sucursales()).hasSize(1);
		assertThat(actualizada.sucursales().get(0).id()).isNotNull();
		assertThat(actualizada.sucursales().get(0).nombre()).isEqualTo("Norte");
	}

}
