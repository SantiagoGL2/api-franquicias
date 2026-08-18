package com.franquicias.nequi.infrastructure.postgresql.mapper;

import com.franquicias.nequi.infrastructure.postgresql.entity.SucursalEntity;
import com.franquicias.nequi.model.Sucursal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ProductoEntityMapper.class)
public interface SucursalEntityMapper {

	Sucursal toDomain(SucursalEntity entity);

	@Mapping(target = "franquicia", ignore = true)
	SucursalEntity toEntity(Sucursal sucursal);

}
