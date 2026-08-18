package com.franquicias.nequi.infrastructure.postgresql.adapter;

import com.franquicias.nequi.infrastructure.postgresql.entity.FranquiciaEntity;
import com.franquicias.nequi.infrastructure.postgresql.entity.ProductoEntity;
import com.franquicias.nequi.infrastructure.postgresql.entity.SucursalEntity;
import com.franquicias.nequi.infrastructure.postgresql.mapper.FranquiciaEntityMapper;
import com.franquicias.nequi.infrastructure.postgresql.repository.FranquiciaJpaRepository;
import com.franquicias.nequi.model.Franquicia;
import com.franquicias.nequi.ports.IFranquiciaPersistencePort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@Transactional
public class FranquiciaPersistenceAdapter implements IFranquiciaPersistencePort {

	private final FranquiciaJpaRepository franquiciaJpaRepository;
	private final FranquiciaEntityMapper franquiciaEntityMapper;

	public FranquiciaPersistenceAdapter(FranquiciaJpaRepository franquiciaJpaRepository,
			FranquiciaEntityMapper franquiciaEntityMapper) {
		this.franquiciaJpaRepository = franquiciaJpaRepository;
		this.franquiciaEntityMapper = franquiciaEntityMapper;
	}

	@Override
	public Franquicia guardar(Franquicia franquicia) {
		FranquiciaEntity franquiciaEntity = franquiciaEntityMapper.toEntity(franquicia);
		fijarBackReferences(franquiciaEntity);
		FranquiciaEntity guardada = franquiciaJpaRepository.save(franquiciaEntity);
		return franquiciaEntityMapper.toDomain(guardada);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Franquicia> buscarPorId(Long id) {
		return franquiciaJpaRepository.findByIdConSucursalesYProductos(id)
			.map(franquiciaEntityMapper::toDomain);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Franquicia> listarTodas() {
		return franquiciaJpaRepository.findAllConSucursalesYProductos().stream()
			.map(franquiciaEntityMapper::toDomain)
			.toList();
	}

	private void fijarBackReferences(FranquiciaEntity franquiciaEntity) {
		for (SucursalEntity sucursalEntity : franquiciaEntity.getSucursales()) {
			sucursalEntity.setFranquicia(franquiciaEntity);
			for (ProductoEntity productoEntity : sucursalEntity.getProductos()) {
				productoEntity.setSucursal(sucursalEntity);
			}
		}
	}

}
