package hu.syscode.profile.config;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

	@Bean
	public WebClient webClient(@Value("${my.security.auth-details}") final String authDetails) {
		final var encodedAuthDetails = Base64.getEncoder().encodeToString(authDetails.getBytes(UTF_8));
		return WebClient.builder() //
				.defaultHeader(AUTHORIZATION, "Basic " + encodedAuthDetails) //
				.build();
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
