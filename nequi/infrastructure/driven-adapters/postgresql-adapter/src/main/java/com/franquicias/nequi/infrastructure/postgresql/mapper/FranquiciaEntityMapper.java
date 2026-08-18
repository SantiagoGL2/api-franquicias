package com.franquicias.nequi.infrastructure.postgresql.mapper;

import com.franquicias.nequi.infrastructure.postgresql.entity.FranquiciaEntity;
import com.franquicias.nequi.model.Franquicia;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = SucursalEntityMapper.class)
public interface FranquiciaEntityMapper {

	Franquicia toDomain(FranquiciaEntity entity);

	FranquiciaEntity toEntity(Franquicia franquicia);

}
