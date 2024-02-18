package hu.syscode.address.config;

import static io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfiguration {

	private static final String BASIC_AUTHENTICATION = "basicAuth";

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI() //
				.addSecurityItem(new SecurityRequirement().addList(BASIC_AUTHENTICATION)) //
				.components(createSecurityComponents());
	}

	private Components createSecurityComponents() {
		return new Components().addSecuritySchemes(BASIC_AUTHENTICATION, createSecurityScheme());
	}

	private SecurityScheme createSecurityScheme() {
		return new SecurityScheme().name(BASIC_AUTHENTICATION).type(HTTP).scheme("basic");
	}

}
