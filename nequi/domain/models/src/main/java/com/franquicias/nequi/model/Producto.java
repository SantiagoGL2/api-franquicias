package com.franquicias.nequi.model;

import com.franquicias.nequi.exception.NombreInvalidoException;
import com.franquicias.nequi.exception.StockInvalidoException;

public record Producto(Long id, String nombre, int stock) {

	public Producto {
		if (nombre == null || nombre.isBlank()) {
			throw new NombreInvalidoException("El nombre del producto no puede estar vacío");
		}
		if (stock < 0) {
			throw new StockInvalidoException("El stock del producto no puede ser negativo");
		}
	}

	public Producto conNombre(String nuevoNombre) {
		return new Producto(id, nuevoNombre, stock);
	}

	public Producto conStock(int nuevoStock) {
		return new Producto(id, nombre, nuevoStock);
	}

}
