package com.franquicias.nequi.config;

import com.franquicias.nequi.application.adapter.FranquiciaUseCase;
import com.franquicias.nequi.application.adapter.ProductoUseCase;
import com.franquicias.nequi.application.adapter.ReporteStockUseCase;
import com.franquicias.nequi.application.port.IFranquiciaPort;
import com.franquicias.nequi.application.port.IProductoPort;
import com.franquicias.nequi.application.port.IReporteStockPort;
import com.franquicias.nequi.ports.IFranquiciaPersistencePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class UseCaseConfig {

	@Bean
	public IFranquiciaPort franquiciaPort(IFranquiciaPersistencePort franquiciaPersistencePort) {
		return new FranquiciaUseCase(franquiciaPersistencePort);
	}

	@Bean
	public IProductoPort productoPort(IFranquiciaPersistencePort franquiciaPersistencePort) {
		return new ProductoUseCase(franquiciaPersistencePort);
	}

	@Bean
	public IReporteStockPort reporteStockPort(IFranquiciaPersistencePort franquiciaPersistencePort) {
		return new ReporteStockUseCase(franquiciaPersistencePort);
	}

}
