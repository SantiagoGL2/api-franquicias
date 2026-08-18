package com.franquicias.nequi.infrastructure.postgresql.mapper;

import com.franquicias.nequi.infrastructure.postgresql.entity.ProductoEntity;
import com.franquicias.nequi.model.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductoEntityMapper {

	Producto toDomain(ProductoEntity entity);

	@Mapping(target = "sucursal", ignore = true)
	ProductoEntity toEntity(Producto producto);

}
