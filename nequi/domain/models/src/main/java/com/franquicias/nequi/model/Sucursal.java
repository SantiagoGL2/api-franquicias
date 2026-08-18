package com.franquicias.nequi.model;

import com.franquicias.nequi.exception.NombreInvalidoException;
import com.franquicias.nequi.exception.ProductoNoEncontradoException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record Sucursal(Long id, String nombre, List<Producto> productos) {

	public Sucursal {
		if (nombre == null || nombre.isBlank()) {
			throw new NombreInvalidoException("El nombre de la sucursal no puede estar vacío");
		}
		productos = List.copyOf(productos);
	}

	public Sucursal conNombre(String nuevoNombre) {
		return new Sucursal(id, nuevoNombre, productos);
	}

	public Sucursal conNuevoProducto(Producto producto) {
		List<Producto> conNuevo = new ArrayList<>(productos);
		conNuevo.add(producto);
		return new Sucursal(id, nombre, conNuevo);
	}

	public Sucursal sinProducto(Long productoId) {
		buscarProducto(productoId);
		List<Producto> restantes = productos.stream()
			.filter(producto -> !Objects.equals(producto.id(), productoId))
			.toList();
		return new Sucursal(id, nombre, restantes);
	}

	public Sucursal conProductoActualizado(Producto productoActualizado) {
		buscarProducto(productoActualizado.id());
		List<Producto> actualizados = productos.stream()
			.map(producto -> Objects.equals(producto.id(), productoActualizado.id())
				? productoActualizado
				: producto)
			.toList();
		return new Sucursal(id, nombre, actualizados);
	}

	public Optional<Producto> productoConMayorStock() {
		return productos.stream().max(Comparator.comparingInt(Producto::stock));
	}

	private Producto buscarProducto(Long productoId) {
		return productos.stream()
			.filter(producto -> Objects.equals(producto.id(), productoId))
			.findFirst()
			.orElseThrow(() -> new ProductoNoEncontradoException(
				"No existe un producto con id " + productoId + " en la sucursal " + id));
	}

}
