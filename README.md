🔐 SecurityModule – Spring Security 7 + JWT + OAuth2 Google

* Este repositório contém um módulo de segurança plug-and-play para projetos Spring Boot 3+, com autenticação baseada em:
  * Login com email e senha (autenticação local)
  * Login com Google (OAuth2 + OIDC)
  * Geração e validação de JWT usando a stack moderna do Spring Security 7 (JwtEncoder / JwtDecoder)

* A ideia é ser um módulo reutilizável: copie a pasta de segurança para outro projeto, ajuste o User/UserService e você já tem um sistema de autenticação pronto.

----------

🚀 Funcionalidades

* 🔑 Autenticação com email e senha usando AuthenticationManager
* 🌐 Autenticação com Google OAuth2 + OIDC
* 🧾 Geração de JWT com:
  * sub (subject) = email/username
  * id (id do usuário)
  * scope (roles/authorities)
  * iat (data de emissão)
  * exp (data de expiração)
* 🔒 Validação do token JWT com JwtDecoder
* ✂ Módulo desacoplado, fácil de integrar em qualquer API REST
* ✅ Compatível com Spring Boot 3.x e Spring Security 7.x

----------

🧱 Estrutura do módulo

* Pacote base de segurança (exemplo):
  * src/main/java/com/example/demo/security/

* Estrutura sugerida:
  * SecurityConfig.java – configuração principal do Spring Security

  * jwt/
    * JwtConfig.java – beans de JwtEncoder e JwtDecoder
    * JwtService.java – geração e validação de tokens JWT

  * oauthgoogle/
    * GoogleAuthService.java – contrato/interface de autenticação via Google
    * GoogleAuthServiceImpl.java – implementação da autenticação via Google + geração do JWT

  * dto/
    * AuthResponseDto.java – DTO de resposta com id, email e jwt

* Os nomes de pacotes podem ser ajustados conforme a estrutura do seu projeto.

----------

🔧 Tecnologias utilizadas

* Java 17+
* Spring Boot 3+
* Spring Security 7
* Spring Security OAuth2 Client (para login com Google)
* JWT (Json Web Token) com NimbusJwtEncoder e NimbusJwtDecoder

----------

🔑 JWT – Detalhes de implementação

* Uso da abordagem moderna do Spring Security:
  * JwtEncoder para gerar tokens
  * JwtDecoder para validar tokens
  * Algoritmo de assinatura HMAC SHA-256 (HS256)
  * Segredo configurado via application.properties ou variáveis de ambiente

* Exemplo de payload gerado:

  * sub: usuario@example.com
  * id: 3
  * scope: ROLE_USER
  * iat: timestamp de emissão
  * exp: timestamp de expiração

----------

🌐 Fluxo de login local (email + senha)

* 1. O cliente envia email e senha para o endpoint de login.
* 2. O AuthenticationManager autentica as credenciais.
* 3. O sistema carrega o usuário (User) e extrai id e roles.
* 4. O JwtService gera um JWT com as claims principais.
* 5. O backend retorna um DTO com:
  * id
  * email
  * jwt

* Esse fluxo é exposto por um serviço de autenticação (ex.: AuthService) e, opcionalmente, por um controller HTTP no projeto que consumir o módulo.

----------

🌐 Fluxo de login com Google OAuth2

* 1. O usuário clica em “Login com Google” no frontend.
* 2. O Google autentica o usuário e redireciona de volta para a aplicação.
* 3. O Spring Security converte o retorno em um OidcUser autenticado.
* 4. O GoogleAuthServiceImpl:
  * cria ou localiza o usuário no banco (via UserService)
  * gera um JWT com o mesmo padrão do login local
* 5. O backend retorna para o frontend um DTO contendo:
  * id
  * email
  * jwt

----------

⚙️ Configuração – application.properties

* Este módulo utiliza configurações externas para segredos e credenciais do Google.
* A recomendação é usar variáveis de ambiente, por exemplo:

```
* JWT:
  * security.jwt.secret=${JWT_SECRET}
  * security.jwt.expiration-seconds=3600

* Google OAuth2:
  * spring.security.oauth2.client.registration.google.client-id=${GOOGLE_CLIENT_ID}
  * spring.security.oauth2.client.registration.google.client-secret=${GOOGLE_CLIENT_SECRET}
  * spring.security.oauth2.client.registration.google.scope=openid,profile,email
  * spring.security.oauth2.client.registration.google.redirect-uri={baseUrl}/login/oauth2/code/{registrationId}

* Provider do Google:
  * spring.security.oauth2.client.provider.google.authorization-uri=https://accounts.google.com/o/oauth2/v2/auth
  * spring.security.oauth2.client.provider.google.token-uri=https://oauth2.googleapis.com/token
  * spring.security.oauth2.client.provider.google.user-info-uri=https://openidconnect.googleapis.com/v1/userinfo
  * spring.security.oauth2.client.provider.google.user-name-attribute=sub

* Arquivos como application-dev.properties (com segredos reais) devem ser ignorados via .gitignore.

```
----------

🧩 Integração em outro projeto

* Para reutilizar este módulo em outro projeto Spring Boot:

  * Copie a pasta security/ (e subpacotes) para o novo projeto.
  * Ajuste os pacotes (por exemplo, de com.example.demo para com.seuprojeto).
  * Integre com suas entidades de usuário:
    * adapte User, UserService e repositórios usados no módulo para as suas entidades reais.
  * Configure o application.properties do novo projeto com:
    * security.jwt.secret
    * security.jwt.expiration-seconds
    * credenciais do Google OAuth2 (se desejar login com Google).

* Proteja as rotas desejadas na SecurityConfig, por exemplo:
  * liberar /auth/login e endpoints públicos
  * exigir autenticação nas demais rotas
  * adicionar regras de autorização baseadas em roles/authorities

* Após a integração, o projeto passa a aceitar:
  * Login local + retorno de JWT
  * Login Google + retorno de JWT
  * Uso de JWT em rotas protegidas via header Authorization: Bearer <token>

----------

🔒 Boas práticas de segurança

* Nunca commitar valores reais de:
  * security.jwt.secret
  * GOOGLE_CLIENT_SECRET
* Utilizar variáveis de ambiente ou arquivos locais ignorados pelo Git.
* Configurar um tempo de expiração adequado em security.jwt.expiration-seconds.
* Usar segredos longos e aleatórios em produção.
* Opcionalmente, adicionar:
  * refresh tokens
  * rotação de chaves JWT (kid)
  * logs e monitoramento de tentativas de login

----------

✅ Status

* Login local: funcional
* Login com Google: funcional
* Geração de JWT: funcional
* Validação de JWT: funcional
* Estrutura pronta para ser reutilizada como módulo de segurança em outros projetos.

----------

👤 Autor

* Fernando Prado  
* Desenvolvedor Backend Java / Spring Boot  
* Focado em criar módulos reutilizáveis, APIs bem estruturadas e projetos de portfólio próximos da realidade de produção.  
* LinkedIn: https://www.linkedin.com/in/fernando-prado  
* GitHub: https://github.com/FernandoPPrado
