package com.example.demo.security.oauthgoogle;

import com.example.demo.security.dto.AuthResponseDto;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public interface GoogleAuthService {

    /**
     * Processo de autenticação via Google.
     * Responsabilidade:
     * - Receber o usuário do Google (OidcUser) após autenticação bem-sucedida.
     * - Delegar para a camada de domínio da aplicação a resolução do usuário
     * (ex.: buscar existente ou criar se necessário).
     * - Delegar para o JwtService a geração do token.
     * - Retornar um AuthResponseDto contendo as informações necessárias para o front.
     * OBS: Esta interface não define regra de negócio de criação/atualização de usuários.
     * Essa lógica deve estar no UserService da aplicação.
     */
    AuthResponseDto authenticateWithGoogle(OidcUser oidcUser);
}
