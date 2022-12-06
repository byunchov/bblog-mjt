package net.byunchov.bblog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import net.byunchov.bblog.users.providers.CustomAuthenticationProvider;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {
	@Autowired
	private CustomAuthenticationProvider authProvider;

	private static final String[] WHITELIST = {
			"/",
			"/users/create",
			"/users/{^[\\d]$}",
	};

	private static final String[] ANT_PATTERNS = {
			"/users/**",
			"/posts/**",
	};

	public static final String ROLE_ADMIN = "ADMIN";
	public static final String ROLE_USER = "USER";

	@Bean
	public AuthenticationManager authManager(HttpSecurity http) throws Exception  {
		AuthenticationManagerBuilder authenticationManagerBuilder = http
				.getSharedObject(AuthenticationManagerBuilder.class);
		authenticationManagerBuilder.authenticationProvider(authProvider);
		return authenticationManagerBuilder.build();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity
				.csrf(csrf -> csrf.disable())
				.authorizeRequests(auth -> {
					auth
							.antMatchers(WHITELIST).permitAll()
							// .antMatchers("/users/**/delete").hasRole(ROLE_ADMIN)
							// .antMatchers("/users/**").hasAnyRole(ROLE_USER, ROLE_ADMIN)
							.antMatchers(HttpMethod.GET, "/posts/**").permitAll()
							.antMatchers(HttpMethod.GET, "/users/**").hasRole(ROLE_ADMIN)
							// .antMatchers(HttpMethod.GET, "/users/{^[\\d]$}").hasAnyRole(ROLE_USER, ROLE_ADMIN)
							.antMatchers(HttpMethod.POST, ANT_PATTERNS).hasAnyRole(ROLE_USER, ROLE_ADMIN)
							.antMatchers(HttpMethod.PATCH, ANT_PATTERNS).hasAnyRole(ROLE_USER, ROLE_ADMIN)
							.antMatchers(HttpMethod.DELETE, ANT_PATTERNS).hasRole(ROLE_ADMIN)
							.anyRequest().authenticated();
				})
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				// .formLogin()
				// .and()
				.httpBasic(Customizer.withDefaults())
				.build();
	}
}