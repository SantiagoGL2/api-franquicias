package com.franquicias.nequi.infrastructure.postgresql.mapper;

import com.franquicias.nequi.infrastructure.postgresql.entity.ProductoEntity;
import com.franquicias.nequi.model.Producto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-18T13:45:38-0500",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-java-compiler-worker-9.5.1.jar, environment: Java 25.0.2 (Eclipse Adoptium)"
)
@Component
public class ProductoEntityMapperImpl implements ProductoEntityMapper {

    @Override
    public Producto toDomain(ProductoEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Long id = null;
        String nombre = null;
        int stock = 0;

        id = entity.getId();
        nombre = entity.getNombre();
        stock = entity.getStock();

        Producto producto = new Producto( id, nombre, stock );

        return producto;
    }

    @Override
    public ProductoEntity toEntity(Producto producto) {
        if ( producto == null ) {
            return null;
        }

        ProductoEntity.ProductoEntityBuilder productoEntity = ProductoEntity.builder();

        productoEntity.id( producto.id() );
        productoEntity.nombre( producto.nombre() );
        productoEntity.stock( producto.stock() );

        return productoEntity.build();
    }
}
