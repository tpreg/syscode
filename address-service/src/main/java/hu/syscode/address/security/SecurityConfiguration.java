package hu.syscode.address.security;

import static org.springframework.http.HttpMethod.GET;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.Assert;

import hu.syscode.address.security.filter.CustomFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	private final PasswordEncoder passwordEncoder;

	private final RestAuthenticationEntryPoint authenticationEntryPoint;

	@Value("${credentials.user}")
	private String username;

	@Value("${credentials.password}")
	private String password;

	public SecurityConfiguration(final PasswordEncoder passwordEncoder, final RestAuthenticationEntryPoint authenticationEntryPoint) {
		Assert.notNull(passwordEncoder, "passwordEncoder is null");
		Assert.notNull(authenticationEntryPoint, "authenticationEntryPoint is null");
		this.passwordEncoder = passwordEncoder;
		this.authenticationEntryPoint = authenticationEntryPoint;
	}

	@Autowired
	public void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser(this.username).password(this.passwordEncoder.encode(this.password)).authorities("ROLE_USER");
	}

	@Bean
	public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
		return http.authorizeHttpRequests(registry -> registry.requestMatchers(GET, "/api/addresses/**", "/v3/**", "/swagger-ui/**") //
						.permitAll() //
						.anyRequest() //
						.authenticated()) //
				.httpBasic(configurer -> configurer.authenticationEntryPoint(this.authenticationEntryPoint)) //
				.addFilterAfter(new CustomFilter(), BasicAuthenticationFilter.class) //
				.build();
	}

}
