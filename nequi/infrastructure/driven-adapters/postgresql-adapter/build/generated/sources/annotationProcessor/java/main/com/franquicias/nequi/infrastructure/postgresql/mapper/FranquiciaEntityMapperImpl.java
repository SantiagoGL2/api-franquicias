package com.franquicias.nequi.infrastructure.postgresql.mapper;

import com.franquicias.nequi.infrastructure.postgresql.entity.FranquiciaEntity;
import com.franquicias.nequi.infrastructure.postgresql.entity.SucursalEntity;
import com.franquicias.nequi.model.Franquicia;
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
public class FranquiciaEntityMapperImpl implements FranquiciaEntityMapper {

    @Autowired
    private SucursalEntityMapper sucursalEntityMapper;

    @Override
    public Franquicia toDomain(FranquiciaEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Long id = null;
        String nombre = null;
        List<Sucursal> sucursales = null;

        id = entity.getId();
        nombre = entity.getNombre();
        sucursales = sucursalEntityListToSucursalList( entity.getSucursales() );

        Franquicia franquicia = new Franquicia( id, nombre, sucursales );

        return franquicia;
    }

    @Override
    public FranquiciaEntity toEntity(Franquicia franquicia) {
        if ( franquicia == null ) {
            return null;
        }

        FranquiciaEntity.FranquiciaEntityBuilder franquiciaEntity = FranquiciaEntity.builder();

        franquiciaEntity.id( franquicia.id() );
        franquiciaEntity.nombre( franquicia.nombre() );
        franquiciaEntity.sucursales( sucursalListToSucursalEntityList( franquicia.sucursales() ) );

        return franquiciaEntity.build();
    }

    protected List<Sucursal> sucursalEntityListToSucursalList(List<SucursalEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<Sucursal> list1 = new ArrayList<Sucursal>( list.size() );
        for ( SucursalEntity sucursalEntity : list ) {
            list1.add( sucursalEntityMapper.toDomain( sucursalEntity ) );
        }

        return list1;
    }

    protected List<SucursalEntity> sucursalListToSucursalEntityList(List<Sucursal> list) {
        if ( list == null ) {
            return null;
        }

        List<SucursalEntity> list1 = new ArrayList<SucursalEntity>( list.size() );
        for ( Sucursal sucursal : list ) {
            list1.add( sucursalEntityMapper.toEntity( sucursal ) );
        }

        return list1;
    }
}
