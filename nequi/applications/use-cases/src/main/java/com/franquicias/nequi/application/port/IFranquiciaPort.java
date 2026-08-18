package com.franquicias.nequi.application.port;

import com.franquicias.nequi.model.Franquicia;


public interface IFranquiciaPort {

	Franquicia crear(String nombre);

	Franquicia actualizarNombre(Long franquiciaId, String nuevoNombre);

	Franquicia agregarSucursal(Long franquiciaId, String nombreSucursal);

	Franquicia actualizarNombreSucursal(Long franquiciaId, Long sucursalId, String nuevoNombre);

}
