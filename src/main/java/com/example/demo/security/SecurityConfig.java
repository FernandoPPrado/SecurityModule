package com.example.demo.security;

import com.example.demo.security.oauthgoogle.GoogleOAuth2SuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final GoogleOAuth2SuccessHandler googleOAuth2SuccessHandler;

    public SecurityConfig(GoogleOAuth2SuccessHandler googleOAuth2SuccessHandler) {
        this.googleOAuth2SuccessHandler = googleOAuth2SuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // API stateless, sem sessão e sem CSRF
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Regras de autorização
                .authorizeHttpRequests(auth -> auth
                        // rotas públicas de autenticação / cadastro
                        .requestMatchers(
                                "/auth/login",
                                "/auth/register",
                                "/auth/create",
                                "/error"
                        ).permitAll()
                        // callback do OAuth2 (Google) normalmente também fica público
                        .requestMatchers("/oauth2/**").permitAll()
                        // qualquer outra rota precisa estar autenticada
                        .anyRequest().authenticated()
                )

                // Login via OAuth2 (Google) usando o nosso success handler
                .oauth2Login(oauth -> oauth
                        .successHandler(googleOAuth2SuccessHandler)
                )

                // Resource Server JWT: valida o token vindo no Authorization: Bearer
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                );

        return http.build();
    }

    // Encoder padrão para senhas (login local)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Expondo o AuthenticationManager para uso no login local (email/senha)
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
