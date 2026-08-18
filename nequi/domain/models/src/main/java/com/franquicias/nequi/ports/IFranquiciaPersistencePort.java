package com.franquicias.nequi.ports;

import com.franquicias.nequi.model.Franquicia;

import java.util.List;
import java.util.Optional;


public interface IFranquiciaPersistencePort {

	Franquicia guardar(Franquicia franquicia);

	Optional<Franquicia> buscarPorId(Long id);

	List<Franquicia> listarTodas();

}
