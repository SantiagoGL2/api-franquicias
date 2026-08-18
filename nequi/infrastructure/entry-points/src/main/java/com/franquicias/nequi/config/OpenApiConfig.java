package com.franquicias.nequi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI franquiciasOpenAPI() {
		return new OpenAPI()
			.info(new Info()
				.title("Franquicias API")
				.description("Gestión de franquicias, sucursales y productos, "
					+ "y reporte del producto con mayor stock por sucursal.")
				.version("1.0.0")
				.contact(new Contact()
					.name("Equipo Franquicias")
					.email("franquicias@nequi.com"))
				.license(new License()
					.name("Apache 2.0")
					.url("https://www.apache.org/licenses/LICENSE-2.0")));
	}

}
