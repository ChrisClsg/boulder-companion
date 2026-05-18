package de.clsg.boulder_companion.security;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import de.clsg.boulder_companion.config.AppProperties;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  private final AppProperties appProperties;

  public SecurityConfig(AppProperties appProperties) {
    this.appProperties = appProperties;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    CookieCsrfTokenRepository csrfTokenRepository =
      CookieCsrfTokenRepository.withHttpOnlyFalse();

    XorCsrfTokenRequestAttributeHandler xorHandler =
      new XorCsrfTokenRequestAttributeHandler();

    CsrfTokenRequestAttributeHandler plainHandler =
      new CsrfTokenRequestAttributeHandler();

    return http
      .cors(cors -> {})
      .csrf(csrf -> csrf
        .csrfTokenRepository(csrfTokenRepository)
        .csrfTokenRequestHandler((request, response, deferredCsrfToken) -> {
          xorHandler.handle(request, response, deferredCsrfToken);

          Supplier<CsrfToken> csrfToken = deferredCsrfToken::get;
          plainHandler.handle(request, response, csrfToken);
        })
      )
      .authorizeHttpRequests(a -> a
        .requestMatchers("/api/auth/csrf").permitAll()
        .requestMatchers("/api/auth/me").authenticated()
        .requestMatchers("/api/users/**").authenticated()
        .anyRequest().permitAll()
      )
      .logout(l -> l
        .logoutUrl("/api/auth/logout")
        .logoutSuccessHandler((request, response, authentication) -> {
          response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        })
      )
      .oauth2Login(oauth -> oauth
        .defaultSuccessUrl(appProperties.getFrontendUrl(), true)
      )
      .build();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();

    config.setAllowCredentials(true);
    config.setAllowedOrigins(List.of(appProperties.getFrontendUrl()));

    config.setAllowedHeaders(List.of(
      "Content-Type",
      "X-XSRF-TOKEN"
    ));

    config.setAllowedMethods(List.of(
      "GET",
      "POST",
      "PUT",
      "PATCH",
      "DELETE",
      "OPTIONS"
    ));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);

    return source;
  }
}