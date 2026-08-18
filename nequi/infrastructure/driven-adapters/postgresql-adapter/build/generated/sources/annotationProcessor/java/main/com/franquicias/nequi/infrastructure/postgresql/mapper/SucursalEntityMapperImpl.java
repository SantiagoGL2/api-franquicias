package com.franquicias.nequi.infrastructure.postgresql.mapper;

import com.franquicias.nequi.infrastructure.postgresql.entity.ProductoEntity;
import com.franquicias.nequi.infrastructure.postgresql.entity.SucursalEntity;
import com.franquicias.nequi.model.Producto;
import com.franquicias.nequi.model.Sucursal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-18T13:45:38-0500",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-java-compiler-worker-9.5.1.jar, environment: Java 25.0.2 (Eclipse Adoptium)"
)
@Component
public class SucursalEntityMapperImpl implements SucursalEntityMapper {

    @Autowired
    private ProductoEntityMapper productoEntityMapper;

    @Override
    public Sucursal toDomain(SucursalEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Long id = null;
        String nombre = null;
        List<Producto> productos = null;

        id = entity.getId();
        nombre = entity.getNombre();
        productos = productoEntityListToProductoList( entity.getProductos() );

        Sucursal sucursal = new Sucursal( id, nombre, productos );

        return sucursal;
    }

    @Override
    public SucursalEntity toEntity(Sucursal sucursal) {
        if ( sucursal == null ) {
            return null;
        }

        SucursalEntity.SucursalEntityBuilder sucursalEntity = SucursalEntity.builder();

        sucursalEntity.id( sucursal.id() );
        sucursalEntity.nombre( sucursal.nombre() );
        sucursalEntity.productos( productoListToProductoEntityList( sucursal.productos() ) );

        return sucursalEntity.build();
    }

    protected List<Producto> productoEntityListToProductoList(List<ProductoEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<Producto> list1 = new ArrayList<Producto>( list.size() );
        for ( ProductoEntity productoEntity : list ) {
            list1.add( productoEntityMapper.toDomain( productoEntity ) );
        }

        return list1;
    }

    protected List<ProductoEntity> productoListToProductoEntityList(List<Producto> list) {
        if ( list == null ) {
            return null;
        }

        List<ProductoEntity> list1 = new ArrayList<ProductoEntity>( list.size() );
        for ( Producto producto : list ) {
            list1.add( productoEntityMapper.toEntity( producto ) );
        }

        return list1;
    }
}
