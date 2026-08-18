# Franquicias API

API REST para gestionar franquicias, sus sucursales y los productos de cada sucursal, con un
reporte del producto de mayor stock por sucursal. Arquitectura hexagonal en un monorepo Gradle
multi-módulo.

## Tabla de contenido

- [Stack técnico](#stack-técnico)
- [Arquitectura](#arquitectura)
- [Patrones de diseño y decisiones](#patrones-de-diseño-y-decisiones)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Requisitos previos](#requisitos-previos)
- [Cómo levantar el proyecto](#cómo-levantar-el-proyecto)
- [Variables de entorno](#variables-de-entorno)
- [Endpoints](#endpoints)
- [Documentación de la API (Swagger)](#documentación-de-la-api-swagger)
- [Pruebas](#pruebas)

## Stack técnico

| Tecnología | Uso |
|---|---|
| Java 25 | Lenguaje base de los 4 módulos (toolchain fijado en el `build.gradle` raíz) |
| Spring Boot 4.1.0 (Spring Framework 7) | Framework de la capa `infrastructure/entry-points` únicamente |
| Gradle multi-módulo, con wrapper | El build se maneja con `./gradlew`; no hace falta tener Gradle instalado globalmente, a diferencia de proyectos que sí lo requieren |
| PostgreSQL 16 (`postgres:16-alpine`) | Base de datos relacional |
| Flyway | Migraciones de esquema (`V1__crear_tablas_franquicias.sql`) |
| Spring Data JPA + Hibernate | Persistencia del agregado `Franquicia` |
| MapStruct 1.6.3 | Mapeo `Entity <-> dominio`, generado en compilación |
| Lombok | Solo en `infrastructure/driven-adapters/postgresql-adapter` (entidades JPA) — nunca en el dominio |
| springdoc-openapi 3.0.3 | Genera el contrato OpenAPI y sirve Swagger UI |
| JUnit 5 + Mockito + Testcontainers | Pruebas unitarias, de casos de uso y de integración con Postgres real |
| Docker + Docker Compose | Levantar Postgres (y opcionalmente la app) sin instalar nada aparte |

## Arquitectura

Arquitectura hexagonal implementada como **4 módulos Gradle reales** (no solo paquetes), para
que el propio build imponga la dirección de las dependencias:

```
franquicias-api (root)
├── domain/models                                    → dominio puro
├── applications/use-cases                           → casos de uso (sin Spring)
├── infrastructure/driven-adapters/postgresql-adapter → JPA + Postgres
└── infrastructure/entry-points                       → controllers, service, Swagger, main
```

**Regla de dependencias:** `domain/models` no depende de nada (ni de Spring, ni de JPA, ni de
Lombok). Los demás módulos dependen de él en cascada — nunca al revés:

```
domain/models  <──  applications/use-cases  <──  infrastructure/entry-points
domain/models  <──  infrastructure/driven-adapters/postgresql-adapter  <──  infrastructure/entry-points
```

`infrastructure/entry-points` es el único módulo ejecutable (tiene la clase
`@SpringBootApplication` y empaqueta el jar); los otros tres son librerías.

**Persistencia por raíz de agregado:** `Sucursal` y `Producto` nunca se persisten ni se
modifican sueltos. Toda mutación pasa por `Franquicia` — se busca la franquicia completa, se
aplica el cambio sobre la sucursal/producto correspondiente, y se guarda la franquicia entera.
Por eso `IFranquiciaPersistencePort` es el único puerto de persistencia que existe, y por eso
`FranquiciaEntity`/`SucursalEntity` usan `cascade = CascadeType.ALL, orphanRemoval = true` en
sus `@OneToMany`: al guardar una franquicia con una lista de sucursales/productos distinta a la
que había en base de datos, Hibernate borra los hijos que ya no están en la lista nueva.

## Patrones de diseño y decisiones

- **Dominio inmutable con records de Java.** `Franquicia`, `Sucursal` y `Producto` son
  `record`s sin setters. Cada cambio de estado retorna una instancia nueva:
  `Producto.conNombre(...)`/`conStock(...)`, `Sucursal.conNombre(...)`/`conNuevoProducto(...)`/
  `sinProducto(...)`/`conProductoActualizado(...)`, `Franquicia.conNombre(...)`/
  `conNuevaSucursal(...)`/`conSucursalActualizada(...)`. Las validaciones de negocio
  (nombre no vacío, stock no negativo) viven en el constructor compacto de cada record, así
  que es imposible construir un `Producto` inválido.

- **Aggregate root.** Ya descrito en Arquitectura: todas las mutaciones de `Sucursal`/`Producto`
  pasan por `Franquicia.buscarSucursal(...)` + el método `con...`/`sin...` correspondiente, y se
  persisten guardando la franquicia completa (`cascade` + `orphanRemoval` del lado de JPA).

- **Inversión de dependencias vía ports.** `domain.ports` (`IFranquiciaPersistencePort`) es el
  puerto de salida — lo define el dominio, lo implementa `FranquiciaPersistenceAdapter` en
  `postgresql-adapter`. `application.port` (`IFranquiciaPort`, `IProductoPort`,
  `IReporteStockPort`) son los puertos de entrada — los define `applications/use-cases`, los
  llaman los controllers de `entry-points` (a través de la capa de service, ver abajo). El
  dominio nunca importa Spring, JPA ni nada de `jakarta.servlet`/HTTP.

- **Capa de servicio transaccional en `entry-points`.** `applications/use-cases` es
  deliberadamente framework-free (sin Spring, para que los casos de uso se puedan probar y
  reusar sin atarse a ningún framework), así que no puede tener `@Transactional`. Por eso
  `FranquiciaService`/`ProductoService`/`ReporteService` (paquete `com.franquicias.nequi.service`
  de `entry-points`) existen como frontera transaccional: cada método envuelve una llamada al
  port correspondiente en `@Transactional(rollbackFor = Exception.class)`
  (`@Transactional(readOnly = true)` en el de reportes). Los controllers inyectan estos
  services, nunca los ports directamente.

- **MapStruct para `Entity <-> dominio`.** `FranquiciaEntityMapper`, `SucursalEntityMapper` y
  `ProductoEntityMapper` generan el código de mapeo en tiempo de compilación (sin reflection).
  Los back-references bidireccionales (`SucursalEntity.franquicia`, `ProductoEntity.sucursal`)
  se ignoran en el mapeo (`@Mapping(target = "...", ignore = true)`) porque no existen en el
  dominio inmutable; `FranquiciaPersistenceAdapter` los fija a mano antes de guardar.

- **Módulo ejecutable único.** Solo `infrastructure/entry-points` aplica el plugin
  `org.springframework.boot` y tiene la clase `NequiApplication`
  (`@SpringBootApplication`, en el paquete raíz `com.franquicias.nequi` para que el
  component scan por defecto cubra `application.adapter`, `service`, `controller`, etc. de
  todos los módulos). `domain/models`, `applications/use-cases` e
  `infrastructure/driven-adapters/postgresql-adapter` solo aportan clases al classpath — no
  generan un jar ejecutable.

## Estructura del proyecto

> Nota: la mayoría de paquetes de este proyecto no tienen `package-info.java` (se retiraron a
> propósito). La única excepción es `com.franquicias.nequi.service`, cuyo `package-info.java`
> sí existe — el resumen de ese paquete está tomado literalmente de ahí; el resto de
> descripciones abajo son un resumen fiel del código real, no de un javadoc de paquete.

### `domain/models` — paquete base `com.franquicias.nequi`

| Paquete | Contenido |
|---|---|
| `model` | Los records del dominio: `Franquicia`, `Sucursal`, `Producto`, `ProductoPorSucursal` (proyección de solo lectura para el reporte) |
| `exception` | `DomainException` (abstracta) y sus subtipos: `NombreInvalidoException`, `StockInvalidoException`, `FranquiciaNoEncontradaException`, `SucursalNoEncontradaException`, `ProductoNoEncontradoException` |
| `ports` | `IFranquiciaPersistencePort` — único puerto de persistencia (`guardar`, `buscarPorId`, `listarTodas`) |

### `applications/use-cases` — paquete base `com.franquicias.nequi.application`

| Paquete | Contenido |
|---|---|
| `port` | Puertos de entrada: `IFranquiciaPort`, `IProductoPort`, `IReporteStockPort` |
| `adapter` | Implementación de esos puertos: `FranquiciaUseCase`, `ProductoUseCase`, `ReporteStockUseCase` (se llama "adapter" por convención, aunque contiene la lógica de negocio) |

### `infrastructure/driven-adapters/postgresql-adapter` — paquete base `com.franquicias.nequi.infrastructure.postgresql`

| Paquete | Contenido |
|---|---|
| `entity` | Entidades JPA + Lombok: `FranquiciaEntity`, `SucursalEntity`, `ProductoEntity` |
| `repository` | `FranquiciaJpaRepository` (Spring Data JPA), con `findByIdConSucursalesYProductos` y `findAllConSucursalesYProductos` |
| `mapper` | Mappers MapStruct: `FranquiciaEntityMapper`, `SucursalEntityMapper`, `ProductoEntityMapper` |
| `adapter` | `FranquiciaPersistenceAdapter`, implementación de `IFranquiciaPersistencePort` |
| `resources/db/migration` | `V1__crear_tablas_franquicias.sql` (tablas `franquicias`, `sucursales`, `productos`) |

### `infrastructure/entry-points` — paquete base `com.franquicias.nequi`

| Paquete | Contenido |
|---|---|
| *(raíz)* | `NequiApplication`, la clase `@SpringBootApplication` |
| `config` | `UseCaseConfig` (instancia los casos de uso como beans de Spring) y `OpenApiConfig` (metadata de Swagger) |
| `service` | *(del `package-info.java` real)* "Transactional boundary between the controllers and application.port use cases" — `FranquiciaService`, `ProductoService`, `ReporteService` |
| `controller` | `FranquiciaController`, `ProductoController`, `ReporteController` |
| `dto.request` | `CrearFranquiciaRequest`, `AgregarSucursalRequest`, `AgregarProductoRequest`, `ActualizarStockRequest`, `ActualizarNombreRequest` |
| `dto.response` | `FranquiciaResponse`, `SucursalResponse`, `ProductoResponse`, `ProductoPorSucursalResponse` |
| `exceptionHandler` | `ErrorResponse` y `GlobalExceptionHandler` (`@RestControllerAdvice`) |

## Requisitos previos

- JDK 25
- Docker y Docker Compose (el proyecto no requiere Postgres instalado aparte — corre en un contenedor)
- (Opcional) IntelliJ IDEA u otro IDE con soporte para Gradle

## Cómo levantar el proyecto

### 1. Desarrollo local (recarga rápida, sin reconstruir imagen Docker)

```bash
cp .env.example .env
docker compose up -d postgres   # solo levanta la base de datos
./gradlew :infrastructure:entry-points:bootRun
```

Al arrancar, Flyway aplica automáticamente las migraciones de
`infrastructure/driven-adapters/postgresql-adapter/src/main/resources/db/migration`. Este flujo
es el más rápido para iterar, porque no hay que reconstruir una imagen Docker en cada cambio.

### 2. Stack completo en Docker (app + base de datos, un solo comando)

```bash
cp .env.example .env
docker compose up --build
```

Esto construye la imagen de la app (`infrastructure/entry-points/Dockerfile`, multi-stage:
compila con Gradle sobre `eclipse-temurin:25-jdk-jammy` y corre el jar sobre
`eclipse-temurin:25-jre-jammy`) y levanta `postgres` + `app`, con `app` esperando a que
`postgres` esté `healthy` (`depends_on: condition: service_healthy`).

Ambos flujos exponen la API en `http://localhost:8080` (o el puerto que definas en `APP_PORT`).

## Variables de entorno

Definidas en `.env` (no se sube a git) a partir de la plantilla `.env.example` (sí se sube):

| Variable | Controla |
|---|---|
| `POSTGRES_DB` | Nombre de la base de datos |
| `POSTGRES_USER` | Usuario de Postgres |
| `POSTGRES_PASSWORD` | Contraseña de Postgres |
| `POSTGRES_PORT` | Puerto de host mapeado al `5432` del contenedor de Postgres |
| `APP_PORT` | Puerto de host mapeado al `8080` del contenedor de la app |

`docker-compose.yml` las lee automáticamente del `.env` (misma carpeta, sin necesitar
`--env-file`) tanto para configurar el contenedor `postgres` como para armar
`SPRING_DATASOURCE_URL`/`SPRING_DATASOURCE_USERNAME`/`SPRING_DATASOURCE_PASSWORD` del contenedor
`app`. `infrastructure/entry-points/src/main/resources/application.yml` usa esos mismos nombres
como variables de entorno con esos mismos valores por defecto
(`${POSTGRES_DB:franquicias}`, etc.), así que `./gradlew bootRun` sin Docker funciona sin
exportar nada.

## Endpoints

Base: `http://localhost:8080`. Todos los cuerpos de petición/respuesta son JSON.

### Franquicias

| Método | Ruta | Qué hace |
|---|---|---|
| `POST` | `/api/franquicias` | Crea una franquicia nueva, sin sucursales |
| `PATCH` | `/api/franquicias/{franquiciaId}/nombre` | Renombra una franquicia |
| `POST` | `/api/franquicias/{franquiciaId}/sucursales` | Agrega una sucursal nueva, sin productos, a la franquicia |
| `PATCH` | `/api/franquicias/{franquiciaId}/sucursales/{sucursalId}/nombre` | Renombra una sucursal |

### Productos

| Método | Ruta | Qué hace |
|---|---|---|
| `POST` | `/api/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos` | Agrega un producto a la sucursal |
| `DELETE` | `/api/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}` | Elimina un producto de la sucursal |
| `PATCH` | `/api/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}/stock` | Actualiza el stock de un producto |
| `PATCH` | `/api/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}/nombre` | Renombra un producto |

### Reportes

| Método | Ruta | Qué hace |
|---|---|---|
| `GET` | `/api/franquicias/{franquiciaId}/reportes/producto-mayor-stock-por-sucursal` | Devuelve el producto con más stock de cada sucursal de la franquicia (las sucursales sin productos no aparecen) |

Todas las mutaciones (`POST`/`PATCH`/`DELETE` de franquicias/productos) devuelven el
`FranquiciaResponse` completo y actualizado, no solo el recurso tocado — es el reflejo directo
de que la persistencia siempre guarda la raíz del agregado entera.

**Ejemplo real, flujo completo** (así se probó end-to-end en desarrollo):

```bash
# 1. Crear franquicia
curl -X POST http://localhost:8080/api/franquicias \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Nequi"}'
# → {"id":1,"nombre":"Nequi","sucursales":[]}

# 2. Agregar sucursal
curl -X POST http://localhost:8080/api/franquicias/1/sucursales \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Centro"}'
# → {"id":1,"nombre":"Nequi","sucursales":[{"id":1,"nombre":"Centro","productos":[]}]}

# 3. Agregar dos productos con distinto stock
curl -X POST http://localhost:8080/api/franquicias/1/sucursales/1/productos \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Camiseta","stock":10}'

curl -X POST http://localhost:8080/api/franquicias/1/sucursales/1/productos \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Gorra","stock":30}'
# → {"id":1,"nombre":"Nequi","sucursales":[{"id":1,"nombre":"Centro",
#     "productos":[{"id":1,"nombre":"Camiseta","stock":10},{"id":2,"nombre":"Gorra","stock":30}]}]}

# 4. Reporte: producto con más stock por sucursal
curl http://localhost:8080/api/franquicias/1/reportes/producto-mayor-stock-por-sucursal
# → [{"sucursalId":1,"sucursalNombre":"Centro","producto":{"id":2,"nombre":"Gorra","stock":30}}]
```

Un error de negocio o de "no encontrado" responde con el `ErrorResponse` del
`GlobalExceptionHandler`, por ejemplo al buscar una franquicia inexistente:

```json
{
  "mensaje": "No existe una franquicia con id 999",
  "status": 404,
  "timestamp": "2026-08-18T14:00:00Z"
}
```

## Documentación de la API (Swagger)

Con la app corriendo (por cualquiera de los dos flujos de arriba), Swagger UI está en:

```
http://localhost:8080/swagger-ui/index.html
```

Ahí aparecen los 3 tags (**Franquicias**, **Productos**, **Reportes**) agrupando las 9
operaciones, cada una con su resumen, descripción, ejemplos de request/response (`@Schema` en
cada DTO) y los códigos de respuesta documentados (200/201 de éxito, 400 de validación/regla de
negocio, 404 de recurso no encontrado — todos con el esquema de `ErrorResponse`). El JSON crudo
del contrato OpenAPI está en `http://localhost:8080/v3/api-docs`.

## Pruebas

```bash
./gradlew test    # todos los módulos
./gradlew build   # test + compilación + empaquetado de todos los módulos
```

Cada módulo prueba una capa distinta:

| Módulo | Tipo de prueba | Qué cubre |
|---|---|---|
| `domain/models` | Unitarias puras (JUnit 5, sin mocks) | Invariantes de los records (`ProductoTest`, `SucursalTest`, `FranquiciaTest`): validaciones, inmutabilidad, `productoConMayorStock`, excepciones de dominio |
| `applications/use-cases` | Unitarias con Mockito sobre el port | `FranquiciaUseCaseTest`, `ProductoUseCaseTest`, `ReporteStockUseCaseTest` — mockean `IFranquiciaPersistencePort`, verifican camino feliz y que los errores de dominio no lleguen a llamar `guardar()` |
| `infrastructure/driven-adapters/postgresql-adapter` | Integración con Testcontainers (Postgres real, no H2) | `FranquiciaPersistenceAdapterIT` — guarda/busca una franquicia con sucursales y productos, confirma que `orphanRemoval` borra lo que sale de la lista |
| `infrastructure/entry-points` | `@WebMvcTest` + `MockMvc` sobre los controllers | `FranquiciaControllerTest`, `ProductoControllerTest`, `ReporteControllerTest` — mockean el `Service` correspondiente con `@MockitoBean`, cubren caso exitoso y errores mapeados por `GlobalExceptionHandler`; más `NequiApplicationTests`, el smoke test de contexto completo |

`infrastructure/driven-adapters/postgresql-adapter` y `infrastructure/entry-points` necesitan
Postgres accesible para correr sus pruebas (el primero lo levanta él mismo vía Testcontainers;
el segundo usa el `postgres` de `docker compose up -d postgres`, igual que en desarrollo local).
