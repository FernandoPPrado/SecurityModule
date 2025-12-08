package com.example.demo.security.oauthgoogle;

import com.example.demo.security.dto.AuthResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Handler chamado quando o login via Google OAuth2/OIDC é bem sucedido.
 * Ele:
 * - pega o OidcUser
 * - delega para um serviço da aplicação (GoogleAuthService)
 * - recebe um DTO com id, email, jwt
 * - escreve esse DTO em JSON na resposta
 */
@Component
public class GoogleOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final GoogleAuthService googleAuthService;
    private final tools.jackson.databind.ObjectMapper objectMapper;

    public GoogleOAuth2SuccessHandler(GoogleAuthService googleAuthService,
                                      ObjectMapper objectMapper) {
        this.googleAuthService = googleAuthService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        // O principal aqui é o usuário do Google (OIDC)
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();

        // Delegamos a regra de negócio para a aplicação
        AuthResponseDto authResponse = googleAuthService.authenticateWithGoogle(oidcUser);

        // Monta resposta JSON com id, email e jwt
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), authResponse);
    }
}
