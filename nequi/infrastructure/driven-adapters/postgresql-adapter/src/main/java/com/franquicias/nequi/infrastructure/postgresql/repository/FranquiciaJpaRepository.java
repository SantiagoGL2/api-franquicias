package com.franquicias.nequi.infrastructure.postgresql.repository;

import com.franquicias.nequi.infrastructure.postgresql.entity.FranquiciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FranquiciaJpaRepository extends JpaRepository<FranquiciaEntity, Long> {

	@Query("SELECT DISTINCT f FROM FranquiciaEntity f LEFT JOIN FETCH f.sucursales WHERE f.id = :id")
	Optional<FranquiciaEntity> findByIdConSucursalesYProductos(@Param("id") Long id);

	@Query("SELECT DISTINCT f FROM FranquiciaEntity f LEFT JOIN FETCH f.sucursales")
	List<FranquiciaEntity> findAllConSucursalesYProductos();

}
