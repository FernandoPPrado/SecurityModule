# SECURITY MODULE – PLUG & PLAY GUIDE

Este módulo fornece:

- Autenticação via Google OAuth2 / OIDC
- Autenticação via login local (email + senha)
- JWT para autenticação e autorização
- Resource Server (Bearer Token)
- Arquitetura limpa, desacoplada e reutilizável

O módulo funciona em QUALQUER projeto Spring Boot.  
Para integrar, siga os passos abaixo.

----------------------------------------------------------
1. COPIAR AS PASTAS
----------------------------------------------------------

Copie este diretório inteiro para o seu projeto:

/security
/jwt
JwtConfig.java
JwtService.java

    /oauthgoogle
        GoogleAuthService.java
        GoogleOAuth2SuccessHandler.java

    /config
        SecurityConfig.java

/dto
AuthResponseDto.java

Nenhuma dessas classes precisa ser alterada ao reutilizar o módulo.

----------------------------------------------------------
2. CONFIGURAR application.properties
----------------------------------------------------------

# JWT
security.jwt.secret=SUA_CHAVE_SECRETA_GRANDE_AQUI
security.jwt.expiration-seconds=3600

# Google OAuth2
spring.security.oauth2.client.registration.google.client-id=SEU_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=SEU_CLIENT_SECRET
spring.security.oauth2.client.registration.google.scope=openid,profile,email

----------------------------------------------------------
3. IMPLEMENTAR GoogleAuthService (login Google)
----------------------------------------------------------

O módulo security define esta interface:

AuthResponseDto authenticateWithGoogle(OidcUser oidcUser);

Você deve criar UMA classe no seu projeto que implemente essa interface:

- Recebe OidcUser (Google)
- Extrai email/nome/foto
- Chama UserService para:
    - buscar usuário no banco
    - ou criar usuário se não existir
- Gera o JWT com JwtService.generateToken
- Retorna AuthResponseDto (id, email, token)

Toda regra de negócio (roles, flags, nomes, permissões, validações etc.)  
fica no UserService. O módulo de segurança não deve conhecer isso.

----------------------------------------------------------
4. CRIAR AuthService + AuthController (login local)
----------------------------------------------------------

Esse módulo não cria login por email/senha automaticamente.  
Você precisa criar:

/auth
AuthService.java
AuthController.java

Função do AuthService:

1. Receber email e password
2. Autenticar usando AuthenticationManager
3. Carregar UserDetails
4. Buscar User para pegar o ID
5. Gerar JWT com JwtService
6. Retornar AuthResponseDto

Fluxo do Controller:

POST /auth/login
{
"email": "...",
"password": "..."
}

Retorna:
{
"id": 1,
"email": "...",
"jwt": "eyJhbGciOiJIUzI1NiJ9..."
}

----------------------------------------------------------
5. GARANTIR QUE USER IMPLEMENTE UserDetails (ou montar manualmente)
----------------------------------------------------------

Opção A (recomendada):
Sua entidade User implementa UserDetails.

Opção B:
Você monta um UserDetails manual:

UserDetails details = User.withUsername(user.getEmail())
.password("")
.authorities("ROLE_USER")
.build();

----------------------------------------------------------
6. ROTAS
----------------------------------------------------------

Públicas automaticamente:
- /auth/login
- /auth/register (se existir)
- /oauth2/**
- /error

Protegidas:
- toda rota não listada acima exige JWT válido no header:
  Authorization: Bearer <token>

----------------------------------------------------------
7. FLUXO DO LOGIN GOOGLE
----------------------------------------------------------

1. Usuário clica "Login com Google"
2. Spring Security redireciona para Google
3. Google autentica e devolve para sua aplicação
4. Spring chama GoogleOAuth2SuccessHandler
5. O handler chama GoogleAuthService.authenticateWithGoogle
6. A implementação:
    - busca/cria o usuário
    - monta UserDetails
    - gera JWT via JwtService
    - retorna AuthResponseDto
7. O handler devolve JSON com id + email + jwt para o frontend

----------------------------------------------------------
8. O QUE VOCÊ PRECISA FAZER EM CADA PROJETO
----------------------------------------------------------

APENAS 3 ITENS:

1. Implementar GoogleAuthServiceImpl
2. Criar AuthService + AuthController (login local)
3. Preencher application.properties

Todo o resto já está pronto no módulo.

----------------------------------------------------------
9. BENEFÍCIOS
----------------------------------------------------------

- Não reescreve SecurityConfig nunca mais
- Não cria filtros manuais
- Não configura OAuth2 na mão
- JWT pronto e seguro
- Módulo totalmente reutilizável
- Arquitetura limpa e desacoplada
- Integração fácil com qualquer domínio (User / Repository)

----------------------------------------------------------
10. CONCLUSÃO
----------------------------------------------------------

Este módulo permite adicionar autenticação moderna (JWT + Google OAuth2)
em qualquer microserviço Spring Boot apenas copiando o módulo
e implementando UM serviço (GoogleAuthServiceImpl) e UM login local
(AuthService).

Fim do README.
