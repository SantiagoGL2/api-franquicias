package com.franquicias.nequi.service;

import com.franquicias.nequi.application.port.IFranquiciaPort;
import com.franquicias.nequi.model.Franquicia;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FranquiciaService {

	private final IFranquiciaPort franquiciaPort;

	public FranquiciaService(IFranquiciaPort franquiciaPort) {
		this.franquiciaPort = franquiciaPort;
	}

	@Transactional(rollbackFor = Exception.class)
	public Franquicia crear(String nombre) {
		return franquiciaPort.crear(nombre);
	}

	@Transactional(rollbackFor = Exception.class)
	public Franquicia actualizarNombre(Long franquiciaId, String nuevoNombre) {
		return franquiciaPort.actualizarNombre(franquiciaId, nuevoNombre);
	}

	@Transactional(rollbackFor = Exception.class)
	public Franquicia agregarSucursal(Long franquiciaId, String nombreSucursal) {
		return franquiciaPort.agregarSucursal(franquiciaId, nombreSucursal);
	}

	@Transactional(rollbackFor = Exception.class)
	public Franquicia actualizarNombreSucursal(Long franquiciaId, Long sucursalId, String nuevoNombre) {
		return franquiciaPort.actualizarNombreSucursal(franquiciaId, sucursalId, nuevoNombre);
	}

}
