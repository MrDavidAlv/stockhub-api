# StockHub API

Backend del reto tecnico Lite Thinking. API REST con Spring Boot 3.5 y PostgreSQL para gestionar empresas, productos multi-moneda, categorias, clientes, ordenes, y generar / enviar inventarios en PDF.

- URL produccion: http://3.93.170.171:8080
- Swagger UI: http://3.93.170.171:8080/swagger-ui/index.html
- Repo frontend: https://github.com/MrDavidAlv/stockhub-web

## Stack

| Capa | Tecnologia |
|---|---|
| Runtime | Java 17 (Eclipse Temurin) |
| Framework | Spring Boot 3.5.14 |
| Persistencia | Spring Data JPA + Hibernate 6 |
| BD | PostgreSQL 16 + Flyway |
| Seguridad | Spring Security 6 + JWT (jjwt 0.12) + BCrypt cost 12 |
| Mapeo | MapStruct 1.6 |
| PDF | iText 8 |
| Email | Spring Mail (JavaMailSender) |
| Docs API | springdoc-openapi |
| Tests | JUnit 5 + Mockito + Testcontainers PostgreSQL 16 |
| Build | Maven (mvnw) |
| Empaquetado | Docker multi-stage, usuario no-root |

## Arquitectura

Hexagonal con cuatro capas:

- `domain` entidades, puertos (interfaces de repositorios y de salida como PdfPort, MailPort), excepciones de negocio
- `application` services, DTOs (records con Bean Validation), mappers MapStruct
- `infrastructure` adapters JPA, security (JwtService, RefreshTokenService, JwtAuthFilter, UserDetailsServiceImpl), config, pdf (iText), mail, seeder
- `interfaces.rest` controllers y manejador global de excepciones

## Modelo de datos

Empresa (NIT como llave primaria), Producto (codigo unico), PrecioMoneda (N:1 con producto, unique por moneda), Categoria, ProductoCategoria (N:M), Cliente, Orden, OrdenProducto (N:M con cantidad y precio unitario). Usuarios y RefreshTokens en sus propias tablas.

## Correr en local

Requiere Docker, Docker Compose y Java 17+.

```bash
docker compose up -d      # postgres en :5433 + mailpit en :8025
./mvnw spring-boot:run    # API en :8080
```

- BD por defecto en `localhost:5433` (el 5432 puede estar ocupado por un Postgres nativo).
- Mailpit UI en http://localhost:8025 para inspeccionar correos enviados durante desarrollo.
- Swagger UI en http://localhost:8080/swagger-ui/index.html
- Migraciones Flyway aplicadas al arrancar.
- Usuarios `admin` y `externo` creados automaticamente por `DataSeeder` con BCrypt cost 12.

## Variables de entorno

El archivo `.env` (gitignored) se carga automaticamente via `spring.config.import` y Docker Compose. Plantilla en `.env.example`.

| Variable | Requerida | Descripcion |
|---|---|---|
| SPRING_DATASOURCE_URL | no (default `jdbc:postgresql://localhost:5433/stockhub_db`) | URL JDBC |
| SPRING_DATASOURCE_USERNAME | no (default `stockhub`) | Usuario BD |
| POSTGRES_PASSWORD | si | Password BD (Postgres container y Spring Boot) |
| JWT_SECRET | si | Clave HS384, minimo 32 caracteres |
| CORS_ORIGINS | no (default `http://localhost:9000`) | Origenes permitidos por coma |
| SEED_ADMIN_PASSWORD | si | Password inicial del usuario ADMIN |
| SEED_EXTERNO_PASSWORD | si | Password inicial del usuario EXTERNO |
| MAIL_HOST | no (default `localhost` Mailpit) | Host SMTP |
| MAIL_PORT | no (default `1025`) | Puerto SMTP |
| MAIL_USERNAME | no | Usuario SMTP |
| MAIL_PASSWORD | no | Password SMTP (App Password en Gmail) |
| MAIL_AUTH | no (default `false`) | Habilitar auth SMTP |
| MAIL_TLS | no (default `false`) | Habilitar STARTTLS |
| MAIL_FROM | no (default `no-reply@stockhub.local`) | Remitente |

## Endpoints

```
POST   /api/auth/login              { email, password }
POST   /api/auth/refresh            { refreshToken }
POST   /api/auth/logout             { refreshToken }

GET    /api/empresas                publico
GET    /api/empresas/{nit}          publico
POST   /api/empresas                ADMIN
PUT    /api/empresas/{nit}          ADMIN
DELETE /api/empresas/{nit}          ADMIN

GET    /api/productos               ADMIN
GET    /api/productos/{id}          ADMIN
GET    /api/productos/por-empresa/{nit}
POST   /api/productos               ADMIN
PUT    /api/productos/{id}          ADMIN
DELETE /api/productos/{id}          ADMIN

GET    /api/categorias              autenticado
POST   /api/categorias              ADMIN
PUT    /api/categorias/{id}         ADMIN
DELETE /api/categorias/{id}         ADMIN

GET    /api/inventario              ADMIN
GET    /api/inventario/pdf          ADMIN (descarga PDF)
POST   /api/inventario/enviar-email ADMIN

GET    /actuator/health             publico
```

Catalogo completo en Swagger UI.

## Tests

```bash
./mvnw verify
```

38 tests:
- 27 unit (Mockito): EmpresaServiceImpl, ProductoServiceImpl, JwtService, RefreshTokenService, PdfAdapter
- 11 integration (Testcontainers PostgreSQL 16): AuthFlow, EmpresaCrud via MockMvc con `@WithMockUser`

## Empaquetado y despliegue

Imagen Docker multi-stage publicada en `ghcr.io/mrdavidalv/stockhub-api`. La pipeline `.github/workflows/ci.yml` corre los tests, publica `:latest` y `:${sha}` en ghcr.io y despliega por SSH al EC2.

El stack productivo lo orquesta `docker-compose.prod.yml` con cuatro servicios:
- `postgres` con volume persistente
- `api` (esta imagen)
- `web` (imagen del repo stockhub-web)
- `nginx` reverse-proxy que publica el puerto `8080` y rutea `/` al frontend y `/api/` al backend

El EC2 ya hospeda otro proyecto en :80, por eso StockHub usa el 8080.

Provisionar un EC2 nuevo:
```bash
sudo bash deploy/setup-ec2.sh
```

Deploy manual una vez configurado el `.env` con secrets:
```bash
bash deploy/deploy.sh
```

El script hace `pull`, `up -d`, `prune` y un loop de healthcheck de 6 intentos por 10 segundos contra `/actuator/health`.

## Credenciales

Los usuarios admin y externo se crean en el primer arranque por `DataSeeder` con BCrypt cost 12. Sus passwords vienen de las variables de entorno `SEED_ADMIN_PASSWORD` y `SEED_EXTERNO_PASSWORD`.

- **App desplegada:** las credenciales de evaluacion se entregan por correo, no se publican en este README.
- **Dev local:** copia `.env.example` a `.env` y ajusta los valores de seed. Por defecto los emails son `admin@stockhub.local` y `externo@stockhub.local`.

## Autor

Mario David Alvarez Vallejo - https://github.com/MrDavidAlv
