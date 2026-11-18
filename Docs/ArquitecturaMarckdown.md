# Saber Pro — Arquitectura y especificaciones

Documento de arquitectura C4 y guía de implementación · Versión 0.1 · 18 de noviembre de 2026

## 1. Propósito y cómo leer este documento

Este documento describe la arquitectura de la aplicación **Saber Pro** (diagramas C1, C2 y C3), lo que debe hacer cada microservicio y las reglas de código que debe seguir cualquier persona o IA que lo implemente.

Cada decisión lleva una marca:

- **\[DECIDIDO\]**: lo definió el equipo.
- **\[PROPUESTA\]**: la sugerí yo para completar un hueco. Se puede cambiar.
- **\[PENDIENTE\]**: falta una decisión. No se debe implementar hasta resolverla.

Saber Pro es una aplicación web orientada a la gestión y preparación de estudiantes para las pruebas Saber Pro. Está formada por microservicios desacoplados, con arquitectura hexagonal dentro de cada uno, y está pensada para crecer: más adelante se añadirá un microservicio de quizzes sin que eso afecte lo que se construya ahora.

## 2. Convenciones de código

Los nombres de componentes, clases y métodos van en **inglés**. La documentación (Javadoc, comentarios, mensajes de error y textos de la UI) va en **español**. **\[DECIDIDO\]**

| Prefijo | Se usa para | Ejemplo |
| --- | --- | --- |
| `cls` | Clase | `clsUserService` |
| `I` | Interfaz | `IUserUseCase` |
| `prm` | Parámetro | `prmUser` |
| `att` | Atributo | `attUsername` |
| `var` | Variable local | `varUser` |
| `op` | Método o función | `opRegister` |
| `rqt` | Request (casi siempre `record`) | `rqtUser` |
| `rps` | Response (casi siempre `record`) | `rpsUser` |
| `pkg` | Paquete | `pkgdomain` |
| `enm` | Enum **\[PROPUESTA\]** | `enmRole` |

Ejemplo de documentación esperada en cada método:

```java
/**
 * Registro de usuarios
 * @param prmUser request con los datos de usuario a crear
 * @return response con los datos de usuario creado
 */
rpsUser opRegister(rqtUser prmUser);
```

Reglas adicionales:

- Toda clase, interfaz y método público lleva Javadoc en español.
- Los componentes de los records (`rqt` y `rps`) se escriben sin prefijo: `record rqtUser(String username, String password)`. **\[PROPUESTA\]**
- Los nombres de los diagramas se traducen al código así: `UserService` se implementa como `clsUserService`, `UserUseCase` como `IUserUseCase`, `UserRepositoryPort` como `IUserRepositoryPort`, `UserRestController` como `clsUserRestController`, y los adaptadores como `clsUserJpaAdapter` y `clsRabbitAdapter`.
- Los identificadores son `UUID` y las fechas son `Instant` en UTC. **\[PROPUESTA\]**
- No se usa Lombok. Los records cubren los DTO y el modelo de dominio es Java plano. **\[PROPUESTA\]**
- No se usa MapStruct ni librerías de mapeo. Los mapeos entre `rqt`/`rps` y el modelo son métodos `op` escritos a mano. **\[PROPUESTA\]**

## 3. Stack tecnológico

| Tema | Decisión |
| --- | --- |
| Lenguaje | Java 25 (LTS) **\[DECIDIDO\]** |
| Framework | Spring Boot 4.1.2 **\[DECIDIDO\]** |
| Build | Maven **\[DECIDIDO\]** |
| Gateway | Spring Cloud Gateway reactivo (WebFlux), con el starter `spring-cloud-starter-gateway-server-webflux` **\[DECIDIDO\]** |
| Versión de Spring Cloud | La compatible con Spring Boot 4.1.x **\[PENDIENTE\]**: verificar en start.spring.io |
| Base de datos | PostgreSQL 18 en Docker, una base por microservicio **\[DECIDIDO\]** |
| Mensajería | RabbitMQ con Spring AMQP, mensajes en JSON **\[DECIDIDO\]** |
| Persistencia | Spring Data JPA con Hibernate |
| Cliente HTTP entre servicios | Spring `RestClient` (síncrono) |
| Seguridad | Spring Security (OAuth2 Resource Server en el Gateway, JOSE para generar el JWT en MCUser) |
| Correo | Por definir. El candidato natural es Spring Mail **\[PENDIENTE\]** |
| Pruebas | JUnit 5 y Mockito, solo pruebas unitarias **\[DECIDIDO\]** |
| Despliegue | Docker Compose, lo configura el equipo **\[DECIDIDO\]** |
| Frontend | HTML y CSS básicos, una UI simple **\[DECIDIDO\]** |

Principio general: pocas librerías y nada rebuscado. Spring y Java estándar.

## 4. C1 — Contexto del sistema

El sistema **Aplicación Saber Pro** lo usan cinco tipos de persona. Cada usuario tiene **un solo rol**, que se usa únicamente para autorización. **\[DECIDIDO\]**

| Rol | Qué hace |
| --- | --- |
| ADMIN | Gestiona usuarios (los registra, los edita y los desactiva). |
| AUTHOR | Crea y edita preguntas. |
| REVIEWER | Revisa preguntas. |
| STUDENT | Responde quizzes. Depende del futuro microservicio de quizzes. |
| TEACHER | Realiza quizzes y ve reportes de desempeño. Depende del futuro microservicio de quizzes. |

Sistema externo: un **proveedor de email**, por definir. Aparece en C1 y C2 como pendiente.

### Diagrama C1

&#91;embedded content: C1 · 5 roles, 1 sistema, 1 proveedor externo\]

STUDENT y TEACHER dependen del futuro microservicio de quizzes; hoy el sistema no los atiende.

## 5. C2 — Contenedores

| Contenedor | Tecnología | Responsabilidad |
| --- | --- | --- |
| API Gateway | Spring Cloud Gateway (WebFlux), Spring Security | Único punto de entrada. Valida el JWT, aplica las reglas de rol y enruta. |
| MCUser | Spring Boot, Spring Data JPA, Spring Security, Spring AMQP | Usuarios, login y emisión de JWT. Base `DB_USER`. |
| MCQuestion | Spring Boot, Spring Data JPA, Spring AMQP | Banco de preguntas y su ciclo de vida. Base `DB_QUESTION`. |
| MCReview | Spring Boot, Spring Data JPA, Spring AMQP, RestClient | Revisiones de preguntas. Base `DB_REVIEW`. |
| MCAudit | Spring Boot, Spring Data JPA, Spring AMQP | Guarda los eventos auditables y los expone solo en lectura. Base `DB_AUDIT`. |
| MCMessaging | Spring Boot, Spring AMQP | Convierte eventos de aviso en emails y los envía. Sin base de datos ni REST. |
| PostgreSQL 18 | Docker | Un solo contenedor con cuatro bases creadas con `CREATE DATABASE`: `DB_USER`, `DB_QUESTION`, `DB_REVIEW` y `DB_AUDIT`. |
| RabbitMQ | Docker | Un solo broker con el exchange `app.events` y las colas `audit.queue` y `messaging.queue`. |

Comunicación entre contenedores:

| Origen | Destino | Tipo |
| --- | --- | --- |
| Usuario | Gateway | HTTPS. Valida el JWT. |
| Gateway | MCUser, MCQuestion, MCReview, MCAudit | HTTP/REST. En MCAudit solo GET. |
| Gateway | MCUser | Obtiene la clave pública (JWKS). |
| MCReview | MCQuestion | REST síncrono. |
| MCUser, MCQuestion, MCReview | RabbitMQ | Publican eventos (AMQP/JSON). |
| RabbitMQ | MCAudit, MCMessaging | Consumen `audit.queue` y `messaging.queue`. |
| Cada MC con base | Su propia base | JDBC/JPA. Ningún MC accede a la base de otro. |
| MCMessaging | Proveedor de email | Por definir. |

Decisiones de despliegue:

- Los microservicios internos (MCUser, MCQuestion, MCReview, MCAudit, MCMessaging) **no se exponen fuera de la red de Docker**. Solo el Gateway es accesible desde fuera. **\[DECIDIDO\]**
- Cada MC usa su propio usuario de base de datos con permisos solo sobre su base. **\[PROPUESTA\]**
- MCAudit usa un usuario con permisos solo de `INSERT` y `SELECT`, para que lo auditado no se pueda modificar. **\[PROPUESTA\]**
- Migraciones de esquema: no definidas. Se sugiere Flyway por microservicio. **\[PENDIENTE\]**
- El frontend no aparece en el C2 actual. Dónde se sirve (por ejemplo desde el Gateway o un contenedor aparte) **\[PENDIENTE\]**.

### Diagrama C2

&#91;embedded content: C2 · 6 contenedores y 2 de infraestructura en Docker\]

MCUser, MCQuestion y MCReview publican en RabbitMQ; MCAudit y MCMessaging consumen. Entre microservicios solo existe la llamada REST de MCReview a MCQuestion.

## 6. Seguridad

**Autenticación \[DECIDIDO\]**

- El inicio de sesión es con `username` y contraseña.
- Solo el ADMIN registra usuarios. No hay registro abierto.
- **MCUser genera** el JWT y **solo el Gateway lo valida**. Los demás microservicios confían en el Gateway.
- El JWT dura **1 día**. No hay refresh token ni logout.

**Detalles propuestos \[PROPUESTA\]**

- Firma asimétrica RS256. MCUser firma con la clave privada y publica la clave pública en un endpoint JWKS. El Gateway la usa para validar.
- La clave privada viene de configuración (archivo o variable de entorno montada por Docker Compose). Si se genera al arrancar, los tokens dejarían de ser válidos con cada reinicio.
- Claims del token: `sub` (id del usuario), `username`, `role`, `iat` y `exp`.
- Después de validar el token, el Gateway **elimina** cualquier cabecera `X-User-Id` y `X-User-Role` que traiga la petición y **añade las suyas** con los datos del token. Así los MC saben quién actúa (para propiedad de preguntas y para auditoría) sin validar JWT.
- Contraseñas con BCrypt. Nunca se envían ni se registran en eventos o logs.
- El primer ADMIN se crea con un dato inicial al arrancar (semilla) **\[PENDIENTE\]**.

**Reglas de rol en el Gateway \[PROPUESTA\]**

Cada usuario tiene un solo rol. Las reglas se aplican en el Gateway por ruta:

| Ruta | Quién puede |
| --- | --- |
| `POST /api/auth/login` | Público |
| `GET /.well-known/jwks.json` | Público. Solo lo necesita el Gateway, puede quedar interno. |
| `/api/users/**` | ADMIN |
| `POST /api/questions` y `PUT /api/questions/{id}` | AUTHOR |
| `POST /api/questions/{id}/submit` y `/reopen` | AUTHOR |
| `POST /api/questions/{id}/publish` y `/archive` | ADMIN |
| `GET /api/questions/**` | AUTHOR, REVIEWER, ADMIN, TEACHER. Para STUDENT **\[PENDIENTE\]**. |
| `POST /api/reviews` | REVIEWER |
| `GET /api/reviews/**` | REVIEWER, ADMIN y AUTHOR (solo sobre sus preguntas) |
| `GET /api/audit/**` | ADMIN. Si también TEACHER u otro rol **\[PENDIENTE\]**. |
| `PATCH /api/questions/{id}/status` | **No se enruta**. Es un endpoint interno que solo llama MCReview. |

Las reglas de propiedad (por ejemplo, que un autor solo edite sus preguntas) las aplica cada MC con la cabecera `X-User-Id`.

## 7. Mensajería y eventos

**Topología \[DECIDIDO\]**

Un solo broker RabbitMQ con un exchange tipo `topic` llamado `app.events`. Los MC publican una sola vez al exchange y no conocen las colas.

| Cola | Binding | Recibe |
| --- | --- | --- |
| `audit.queue` | `auditable.#` | Todos los eventos (todos son auditables) |
| `messaging.queue` | `auditable.aviso.#` | Solo los eventos de aviso a usuarios |

Los eventos de aviso son un subconjunto de los auditables. Por eso su routing key empieza por `auditable.aviso.` y llegan a las dos colas: se guardan en auditoría y además se envían por correo.

**Métodos obligatorios en MCUser, MCQuestion y MCReview \[DECIDIDO\]**

Los tres exponen estos métodos en su puerto `IEventPublisherPort`, para llamarlos desde el servicio cuando se necesite:

```java
/**
 * Envío de un evento auditable a la cola de auditoría
 * @param prmEvent request con los datos del evento
 */
void opSendAudit(rqtAuditEvent prmEvent);

/**
 * Envío de un evento de aviso a usuarios (también queda auditado)
 * @param prmEvent request con los datos del aviso
 */
void opSendEmail(rqtEmailEvent prmEvent);
```

- `opSendAudit` publica con la routing key `auditable.<eventType>`, por ejemplo `auditable.question.created`.
- `opSendEmail` publica con `auditable.aviso.<eventType>`. **No** hay que llamar además a `opSendAudit` para el mismo hecho, porque ese mensaje ya llega a `audit.queue`.

**Formato del mensaje \[PROPUESTA\]**

```json
{
  "eventId": "uuid",
  "eventType": "question.status-changed",
  "occurredAt": "2026-11-18T15:30:00Z",
  "sourceService": "MCQuestion",
  "actorId": "uuid",
  "entityType": "QUESTION",
  "entityId": "uuid",
  "detail": { "from": "BORRADOR", "to": "PENDIENTE_REVISION" }
}
```

Los eventos de aviso añaden un bloque `recipient` y una `template` con sus `variables`.

**Reglas de publicación \[PROPUESTA\]**

- Exchange y colas durables, mensajes persistentes y serialización JSON con Jackson.
- Si RabbitMQ no está disponible, el microservicio registra el error y **no** falla la operación de negocio. No se implementa patrón outbox.
- En el consumidor, un mismo `eventId` no debe procesarse dos veces (idempotencia).

**Eventos iniciales \[PROPUESTA\]** (los definitivos no están cerrados)

| Evento | Origen | ¿Aviso por email? |
| --- | --- | --- |
| `user.registered` | MCUser | Sí, al usuario creado (sin contraseña) |
| `user.updated`, `user.deactivated`, `user.login` | MCUser | No |
| `question.created`, `question.updated` | MCQuestion | No |
| `question.status-changed` | MCQuestion | No |
| `question.submitted` | MCQuestion | A los revisores **\[PENDIENTE\]** |
| `review.created` | MCReview | No |
| `question.approved`, `question.rejected` | MCReview | Sí, al autor |

**Problema abierto \[PENDIENTE\]: el correo del destinatario.** El email de un usuario solo lo conoce MCUser, pero MCQuestion y MCReview también envían avisos (por ejemplo al autor). Hay dos opciones: (A) el evento trae solo `recipientUserId` y MCMessaging consulta el correo a MCUser por REST interno (requiere una flecha nueva en C2 y un puerto y adaptador de salida en MCMessaging); (B) cada productor incluye el correo en el evento. La opción B obliga a MCQuestion y MCReview a obtener el correo antes de publicar, así que la A queda más limpia.

## 8. C3 — API Gateway

El Gateway valida el token, aplica la regla de rol de la ruta y enruta; no tiene lógica de negocio ni base de datos. No es hexagonal: son tres piezas de infraestructura en cadena.

&#91;embedded content: C3 API Gateway · 3 piezas y 4 destinos\]

La petición pasa por `SecurityWebFilterChain`, luego `ReactiveJwtDecoder` valida el token y por último `RouteLocator` la envía al microservicio. La línea punteada es la descarga de la clave pública desde MCUser.

| Componente | `[Component: …]` | Qué hace |
| --- | --- | --- |
| `SecurityWebFilterChain` | Java, Spring Security WebFlux | Declara las rutas públicas y la regla de rol de cada ruta (sección 6). Borra las cabeceras `X-User-Id` y `X-User-Role` que traiga el cliente y escribe las suyas con los datos del token. |
| `ReactiveJwtDecoder` | Java, Spring Security OAuth2 Resource Server | Valida firma y expiración con la clave pública de MCUser (JWKS). Guarda la clave en caché. |
| `RouteLocator` | Java, Spring Cloud Gateway | Enruta por prefijo: `/api/auth` y `/api/users` a MCUser, `/api/questions` a MCQuestion, `/api/reviews` a MCReview y `/api/audit` (solo GET) a MCAudit. |

**Reglas de implementación**

- Token ausente, inválido o vencido: responde `401`. Rol no permitido en la ruta: responde `403`.
- Las rutas públicas son `POST /api/auth/login` y el JWKS. Todo lo demás exige token.
- A MCAudit solo se enruta el método GET. Cualquier otro método se rechaza en el Gateway.
- `PATCH /api/questions/{id}/status` no se enruta: es interno y lo llama MCReview.
- Las direcciones de los microservicios salen de configuración (nombres de servicio de Docker Compose), no del código.
- Se usa el starter `spring-cloud-starter-gateway-server-webflux` y el modelo reactivo de Spring Security.

## 9. C3 — MCUser

MCUser registra usuarios, valida credenciales y firma el JWT de un día. Es el único microservicio que conoce contraseñas y roles.

&#91;embedded content: C3 MCUser · 2 entradas, 4 salidas\]

El flujo de login y gestión es `clsUserRestController` → `IUserUseCase` → `clsUserService` → puertos de salida → adaptadores. El flujo de la clave pública es `clsJwksRestController` → `IPublicKeyUseCase` → `clsUserService` → `ITokenPort`. Los puertos y el dominio son solo Java; las etiquetas entre corchetes del diagrama son las tecnologías de los adaptadores.

### Modelo

`clsUser`: `attId` (UUID), `attUsername` (único), `attPasswordHash`, `attEmail` **\[PROPUESTA: hace falta para los avisos\]**, `attRole` (`enmRole`), `attActive` y `attCreatedAt`. `enmRole` vale `ADMIN`, `STUDENT`, `REVIEWER`, `AUTHOR` o `TEACHER`.

### Componentes

| Diagrama | Código | `[Component: …]` | Responsabilidad |
| --- | --- | --- | --- |
| UserRestController | `clsUserRestController` | Java, Spring Web | Convierte HTTP en `rqt` y `rps`. Sin lógica de negocio. |
| JwksRestController | `clsJwksRestController` | Java, Spring Web | Publica la clave pública en formato JWKS. |
| UserUseCase | `IUserUseCase` | Java | Puerto de entrada de usuarios y login. |
| PublicKeyUseCase | `IPublicKeyUseCase` | Java | Puerto de entrada de la clave pública. |
| UserService | `clsUserService` | Java | Implementa ambos casos de uso y orquesta los puertos. Sin anotaciones de Spring. |
| User | `clsUser` | Java | Entidad de dominio. |
| UserRepositoryPort | `IUserRepositoryPort` | Java | Guardar y consultar usuarios. |
| TokenPort | `ITokenPort` | Java | Generar el JWT y entregar la clave pública. |
| PasswordHashPort | `IPasswordHashPort` | Java | Calcular y comparar hashes. |
| EventPublisherPort | `IEventPublisherPort` | Java | `opSendAudit` y `opSendEmail`. |
| UserJpaAdapter | `clsUserJpaAdapter` | Java, Spring Data JPA, Hibernate | Usa una entidad JPA propia (`clsUserEntity`) para no ensuciar el dominio. **\[PROPUESTA\]** |
| JwtAdapter | `clsJwtAdapter` | Java, Spring Security OAuth2 JOSE | Firma el JWT con RS256. |
| BCryptAdapter | `clsBCryptAdapter` | Java, Spring Security Crypto | Hash BCrypt. |
| RabbitAdapter | `clsRabbitAdapter` | Java, Spring AMQP | Publica en el exchange `app.events`. |

### Operaciones y records

```java
// IUserUseCase
rpsUser opRegister(rqtUser prmUser);
rpsToken opLogin(rqtLogin prmLogin);
rpsUser opFindById(UUID prmId);
List<rpsUser> opFindAll();
rpsUser opUpdate(UUID prmId, rqtUser prmUser);
void opDeactivate(UUID prmId);

// IPublicKeyUseCase
rpsJwks opGetPublicKeys();

// IUserRepositoryPort
clsUser opSave(clsUser prmUser);
Optional<clsUser> opFindById(UUID prmId);
Optional<clsUser> opFindByUsername(String prmUsername);
List<clsUser> opFindAll();
boolean opExistsByUsername(String prmUsername);

// ITokenPort
rpsToken opGenerate(clsUser prmUser);
rpsJwks opGetPublicKeys();

// IPasswordHashPort
String opHash(String prmPassword);
boolean opMatches(String prmPassword, String prmHash);

// Records
record rqtUser(String username, String password, String email, enmRole role) {}
record rpsUser(UUID id, String username, String email, enmRole role, boolean active) {}
record rqtLogin(String username, String password) {}
record rpsToken(String token, Instant expiresAt) {}
record rpsJwks(List<Map<String, Object>> keys) {}
```

### Endpoints \[PROPUESTA\]

| Método y ruta | Rol | Request | Response | Códigos |
| --- | --- | --- | --- | --- |
| `POST /api/auth/login` | Público | `rqtLogin` | `rpsToken` | 200, 401 |
| `POST /api/users` | ADMIN | `rqtUser` | `rpsUser` | 201, 400, 409 |
| `GET /api/users` | ADMIN | — | lista de `rpsUser` | 200 |
| `GET /api/users/{id}` | ADMIN | — | `rpsUser` | 200, 404 |
| `PUT /api/users/{id}` | ADMIN | `rqtUser` (contraseña opcional) | `rpsUser` | 200, 400, 404, 409 |
| `PATCH /api/users/{id}/deactivate` | ADMIN | — | — | 204, 404 |
| `GET /.well-known/jwks.json` | Público | — | `rpsJwks` | 200 |

### Reglas

- El `username` es único: si ya existe se responde `409`. Cada usuario tiene un solo rol.
- Login con usuario inexistente, contraseña errónea o usuario desactivado responde `401` con el mismo mensaje.
- El token usa RS256, vale 1 día y lleva `sub`, `username`, `role`, `iat` y `exp`. No hay refresh ni logout. **\[DECIDIDO\]**
- La contraseña se guarda con BCrypt y nunca aparece en un `rps`, un evento ni un log. Política mínima de contraseña **\[PENDIENTE\]** (se sugiere 8 caracteres).
- Desactivar no borra al usuario: marca `attActive` en falso.
- Eventos: `user.registered` con `opSendEmail`, y `user.updated`, `user.deactivated` y `user.login` con `opSendAudit`. El `actorId` es el ADMIN de la cabecera `X-User-Id`, o el propio usuario en el login.
- Pruebas unitarias de `clsUserService` con los cuatro puertos simulados: registro correcto, `username` repetido, login correcto, contraseña errónea, usuario desactivado y publicación de eventos.

## 10. C3 — MCQuestion

MCQuestion guarda el banco de preguntas, valida su estructura y controla su estado. Recibe las peticiones del Gateway y las llamadas REST internas de MCReview.

&#91;embedded content: C3 MCQuestion · Factory, Decorator y 2 salidas\]

El flujo es `clsQuestionRestController` → `IQuestionUseCase` → `clsQuestionService`, que usa `clsQuestionFactory` y `clsQuestion`, y sale por `IQuestionRepositoryPort` (a `DB_QUESTION`) y `IEventPublisherPort` (a RabbitMQ).

### Modelo

`clsQuestion` guarda los campos que definiste: `attContext`, `attDirectQuestion`, `attDistractors` (exactamente 4), `attCorrectAnswer`, `attJustification`, `attBibliography`, `attCompetence`, `attTopic`, `attSubtopic` y `attDifficulty`. Se añaden `attId`, `attAuthorId`, `attStatus` (`enmQuestionStatus`), `attContextType` (`enmContextType`: `MULTIPLE_CHOICE`, `CASE`, `MULTIMEDIA`), `attMediaUrl` (solo multimedia), `attCreatedAt` y `attUpdatedAt`.

- Los valores de `enmDifficulty` no están definidos **\[PENDIENTE\]**; se sugiere `BAJA`, `MEDIA` y `ALTA`.
- Competencia, tema y subtema son texto libre por ahora. Un catálogo cerrado **\[PENDIENTE\]**.

### Estados y transiciones

Los siete estados son `BORRADOR`, `PENDIENTE_REVISION`, `EN_REVISION`, `APROBADA`, `RECHAZADA`, `PUBLICADA` y `ARCHIVADA`. **\[DECIDIDO\]** que existen; la lista puede cambiar. Las transiciones son **\[PROPUESTA\]**:

| De | A | Quién | Cómo |
| --- | --- | --- | --- |
| BORRADOR | PENDIENTE\_REVISION | AUTHOR dueño | `POST /api/questions/{id}/submit` |
| PENDIENTE\_REVISION | EN\_REVISION | MCReview | Al registrarse la primera revisión (`PATCH` interno) |
| EN\_REVISION | APROBADA | MCReview | Cuando las 2 revisiones aprueban |
| EN\_REVISION | RECHAZADA | MCReview | Cuando hay 2 revisiones y al menos una rechaza |
| RECHAZADA | BORRADOR | AUTHOR dueño | `POST /api/questions/{id}/reopen` |
| APROBADA | PUBLICADA | ADMIN **\[PENDIENTE: quién publica\]** | `POST /api/questions/{id}/publish` |
| PUBLICADA | ARCHIVADA | ADMIN | `POST /api/questions/{id}/archive` |

Cualquier otra transición se rechaza con `409`. Una pregunta solo se puede modificar en `BORRADOR` (RF-06).

### Factory y Decorator

- `IQuestionGenerator` declara `clsQuestion opGenerate(rqtQuestion prmQuestion)`.
- `clsMultipleChoiceQuestionGenerator` (GeneradorPreguntaSeleccionMultiple) es el generador base y arma la pregunta con un contexto de texto.
- `clsQuestionGeneratorDecorator` es la clase abstracta que guarda el generador envuelto (`attWrapped`) y delega en él.
- `clsCaseQuestionGenerator` (GeneradorPreguntaCaso) envuelve a otro y exige que el contexto sea un caso o situación. Marca `CASE`. Longitud mínima del caso **\[PENDIENTE\]**.
- `clsMultimediaQuestionGenerator` (GeneradorPreguntaMultimedia) envuelve a otro, exige `attMediaUrl` y añade la referencia al contexto. Marca `MULTIMEDIA`. Solo guarda la URL, sin almacenar archivos. **\[PROPUESTA\]**
- `clsQuestionFactory` con `opCreate(enmContextType, rqtQuestion)` arma la cadena de generadores según el tipo y devuelve una pregunta ya validada.
- `ImportadorPreguntasExcel` y `GeneradorPreguntasIA` **no se implementan ahora**. El diseño permite añadirlos sin tocar los existentes.

### Validación estructural (HU03, RF-08 a RF-12)

`clsQuestion.opValidate()` se ejecuta al crear y al modificar. Si algo falla lanza `clsDomainException` y la respuesta es `400` con la lista de errores.

- Contexto no vacío (RF-08).
- Pregunta directa presente, no vacía y una sola (RF-09).
- Exactamente cuatro distractores, no vacíos y distintos entre sí (RF-10). "Distintos" **\[PROPUESTA\]**.
- Una única respuesta correcta, distinta de los distractores (RF-11).
- Ninguna opción puede ser ni contener "Todas las anteriores" o "Ninguna de las anteriores", sin distinguir mayúsculas ni tildes (RF-12).

### Componentes

| Diagrama | Código | `[Component: …]` | Responsabilidad |
| --- | --- | --- | --- |
| QuestionRestController | `clsQuestionRestController` | Java, Spring Web | HTTP a `rqt`/`rps`. Lee `X-User-Id` y lo pasa como actor. |
| QuestionUseCase | `IQuestionUseCase` | Java | Puerto de entrada. |
| QuestionService | `clsQuestionService` | Java | Reglas de estado, propiedad y eventos. |
| QuestionFactory | `clsQuestionFactory` | Java, patrón Factory | Crea preguntas válidas. |
| QuestionDecorator | `clsQuestionGeneratorDecorator` y sus hijos | Java, patrón Decorator | Enriquece el contexto. |
| Question | `clsQuestion` | Java | Entidad y validaciones. |
| QuestionRepositoryPort | `IQuestionRepositoryPort` | Java | Guardar, buscar por id y buscar con filtros. |
| EventPublisherPort | `IEventPublisherPort` | Java | `opSendAudit` y `opSendEmail`. |
| QuestionJpaAdapter | `clsQuestionJpaAdapter` | Java, Spring Data JPA, Hibernate | Entidad JPA propia y consultas con filtros. |
| RabbitAdapter | `clsRabbitAdapter` | Java, Spring AMQP | Publica en `app.events`. |

### Operaciones

```java
// IQuestionUseCase
rpsQuestion opCreate(rqtQuestion prmQuestion, UUID prmActorId);
rpsQuestion opUpdate(UUID prmId, rqtQuestion prmQuestion, UUID prmActorId);
rpsQuestion opFindById(UUID prmId);
rpsPage<rpsQuestion> opSearch(rqtQuestionFilter prmFilter, int prmPage, int prmSize);
rpsQuestion opSubmit(UUID prmId, UUID prmActorId);
rpsQuestion opReopen(UUID prmId, UUID prmActorId);
rpsQuestion opPublish(UUID prmId, UUID prmActorId);
rpsQuestion opArchive(UUID prmId, UUID prmActorId);
rpsQuestion opChangeStatus(UUID prmId, rqtStatusChange prmChange);

// IQuestionRepositoryPort
clsQuestion opSave(clsQuestion prmQuestion);
Optional<clsQuestion> opFindById(UUID prmId);
clsPage<clsQuestion> opSearch(rqtQuestionFilter prmFilter, int prmPage, int prmSize);

// Records
record rqtQuestion(enmContextType contextType, String context, String mediaUrl,
    String directQuestion, List<String> distractors, String correctAnswer,
    String justification, String bibliography, String competence,
    String topic, String subtopic, enmDifficulty difficulty) {}
record rqtQuestionFilter(String competence, String topic, String subtopic,
    enmDifficulty difficulty, enmQuestionStatus status, UUID authorId) {}
record rqtStatusChange(enmQuestionStatus status) {}
record rpsPage<T>(List<T> content, int page, int size, long totalElements) {}
```

`rpsQuestion` repite los campos de `rqtQuestion` y añade `id`, `authorId`, `status`, `createdAt` y `updatedAt`. `clsPage<T>` es el equivalente de dominio de `rpsPage<T>`.

### Endpoints \[PROPUESTA\]

| Método y ruta | Rol | Códigos |
| --- | --- | --- |
| `POST /api/questions` | AUTHOR | 201, 400 |
| `PUT /api/questions/{id}` | AUTHOR dueño | 200, 400, 403, 404, 409 (no está en BORRADOR) |
| `GET /api/questions/{id}` | AUTHOR, REVIEWER, ADMIN, TEACHER | 200, 404 |
| `GET /api/questions?competence=&topic=&subtopic=&difficulty=&status=&authorId=&page=&size=` | Igual | 200 (RF-07) |
| `POST /api/questions/{id}/submit` y `/reopen` | AUTHOR dueño | 200, 403, 404, 409 |
| `POST /api/questions/{id}/publish` y `/archive` | ADMIN | 200, 404, 409 |
| `PATCH /api/questions/{id}/status` | Interno, solo MCReview | 200, 404, 409 |

### Reglas y pruebas

- Solo el autor dueño (`X-User-Id`) modifica, envía o reabre su pregunta. Otro usuario recibe `403`.
- Eventos con `opSendAudit`: `question.created`, `question.updated` y `question.status-changed` (con `from` y `to`). `question.submitted` también avisa a los revisores **\[PENDIENTE\]**.
- Pruebas unitarias: `clsQuestion.opValidate` con un caso por cada RF-08 a RF-12; cada generador y la fábrica; y `clsQuestionService` con cada transición válida e inválida, usando los puertos simulados.

## 11. C3 — MCReview

MCReview registra las dos revisiones de cada pregunta y, según su resultado, pide a MCQuestion que cambie el estado. No guarda preguntas: las consulta a MCQuestion.

&#91;embedded content: C3 MCReview · 3 salidas\]

El flujo es `clsReviewRestController` → `IReviewUseCase` → `clsReviewService`, que sale por tres puertos: `IReviewRepositoryPort` (a `DB_REVIEW`), `IQuestionClientPort` (REST síncrono a MCQuestion) e `IEventPublisherPort` (a RabbitMQ).

### Modelo

`clsReview`: `attId`, `attQuestionId`, `attReviewerId`, `attDecision` (`enmDecision`: `APPROVED` o `REJECTED`), `attJustification` y `attCreatedAt`. La justificación del revisor es obligatoria. **\[DECIDIDO\]**

### Reglas de revisión

- Cada pregunta necesita **2 revisores distintos**. **\[DECIDIDO\]**
- Solo se revisa una pregunta en `PENDIENTE_REVISION` o `EN_REVISION`. MCReview lo comprueba con `GET` a MCQuestion y responde `409` en otro estado.
- El autor no puede revisar su propia pregunta (`403`). **\[PROPUESTA\]**
- Un revisor no puede revisar dos veces la misma pregunta (`409`). **\[PROPUESTA\]**
- Con la primera revisión, MCReview pide pasar la pregunta a `EN_REVISION`.
- Con la segunda: si las dos aprueban, pide `APROBADA`; si al menos una rechaza, pide `RECHAZADA`. **\[PROPUESTA\]**
- Eventos: `review.created` con `opSendAudit`, y `question.approved` o `question.rejected` con `opSendEmail` para avisar al autor.
- Riesgo conocido: no hay transacción distribuida. Si falla la llamada a MCQuestion, la revisión queda guardada y se responde `502`. Para que se recupere, el estado se calcula siempre a partir de las revisiones guardadas. **\[PROPUESTA\]**
- Cada microservicio tiene su propia copia de `enmQuestionStatus`. No se comparte una librería entre servicios. **\[PROPUESTA\]**

### Componentes

| Diagrama | Código | `[Component: …]` | Responsabilidad |
| --- | --- | --- | --- |
| ReviewRestController | `clsReviewRestController` | Java, Spring Web | HTTP a `rqt`/`rps`. Pasa `X-User-Id` como revisor. |
| ReviewUseCase | `IReviewUseCase` | Java | Puerto de entrada. |
| ReviewService | `clsReviewService` | Java | Aplica las reglas de revisión y decide el nuevo estado. |
| Review | `clsReview` | Java | Entidad de dominio. |
| ReviewRepositoryPort | `IReviewRepositoryPort` | Java | Guardar y consultar revisiones. |
| QuestionClientPort | `IQuestionClientPort` | Java | Consultar una pregunta y cambiar su estado. |
| EventPublisherPort | `IEventPublisherPort` | Java | `opSendAudit` y `opSendEmail`. |
| ReviewJpaAdapter | `clsReviewJpaAdapter` | Java, Spring Data JPA, Hibernate | Entidad JPA propia. |
| QuestionRestAdapter | `clsQuestionRestAdapter` | Java, Spring RestClient | Llama a los endpoints internos de MCQuestion. |
| RabbitAdapter | `clsRabbitAdapter` | Java, Spring AMQP | Publica en `app.events`. |

### Operaciones

```java
// IReviewUseCase
rpsReview opCreate(rqtReview prmReview, UUID prmReviewerId);
rpsReview opFindById(UUID prmId, UUID prmActorId, String prmActorRole);
List<rpsReview> opFindByQuestionId(UUID prmQuestionId, UUID prmActorId, String prmActorRole);

// IReviewRepositoryPort
clsReview opSave(clsReview prmReview);
Optional<clsReview> opFindById(UUID prmId);
List<clsReview> opFindByQuestionId(UUID prmQuestionId);
boolean opExistsByQuestionIdAndReviewerId(UUID prmQuestionId, UUID prmReviewerId);

// IQuestionClientPort
rpsQuestionSummary opGetQuestion(UUID prmQuestionId);
void opChangeStatus(UUID prmQuestionId, enmQuestionStatus prmStatus);

// Records
record rqtReview(UUID questionId, enmDecision decision, String justification) {}
record rpsReview(UUID id, UUID questionId, UUID reviewerId,
    enmDecision decision, String justification, Instant createdAt) {}
record rpsQuestionSummary(UUID id, UUID authorId, enmQuestionStatus status) {}
```

### Endpoints \[PROPUESTA\]

| Método y ruta | Rol | Códigos |
| --- | --- | --- |
| `POST /api/reviews` | REVIEWER | 201, 400, 403, 404 (pregunta inexistente), 409, 502 |
| `GET /api/reviews/{id}` | REVIEWER, ADMIN, AUTHOR de la pregunta | 200, 403, 404 |
| `GET /api/reviews?questionId=` | REVIEWER, ADMIN, AUTHOR de la pregunta | 200, 403 |

Para el AUTHOR, MCReview comprueba con `opGetQuestion` que `authorId` coincide con `X-User-Id`. Los revisores consultan las preguntas pendientes con `GET /api/questions?status=PENDIENTE_REVISION` en MCQuestion.

### Pruebas

Pruebas unitarias de `clsReviewService` con los tres puertos simulados: primera revisión, segunda aprobatoria, segunda con rechazo, autor que se revisa a sí mismo, revisor repetido, estado no revisable y fallo del cliente de MCQuestion.

## 12. C3 — MCAudit

MCAudit guarda cada evento auditable que publican los demás servicios y permite consultarlo solo con `GET`. Lo guardado no se modifica. **\[DECIDIDO\]**

&#91;embedded content: C3 MCAudit · 2 entradas, 1 salida\]

El flujo de escritura es `clsAuditEventListener` → `IRecordAuditUseCase` → `clsAuditService` → `IAuditRepositoryPort` → `DB_AUDIT`. El de lectura es `clsAuditRestController` → `IQueryAuditUseCase` → `clsAuditService` → el mismo puerto.

### Modelo

`clsAuditRecord`: `attId`, `attEventId` (único), `attActorId`, `attAction` (el `eventType`), `attEntityType`, `attEntityId`, `attDetail` (JSON como texto), `attSourceService`, `attOccurredAt` y `attRecordedAt`. Los filtros de consulta son por actor o por entidad. **\[DECIDIDO\]**

### Reglas

- **Solo inserción y consulta.** `IQueryAuditUseCase` no tiene métodos de escritura y `IAuditRepositoryPort` no tiene `update` ni `delete`. El usuario de base de datos de MCAudit solo tiene permisos `INSERT` y `SELECT`. **\[PROPUESTA\]**
- Idempotencia: `attEventId` es único. Si el mismo evento llega dos veces, la segunda se ignora.
- Un mensaje inválido (JSON mal formado o sin campos obligatorios) se registra en el log y se envía a una cola de mensajes muertos, sin reintentos infinitos. **\[PROPUESTA\]**
- La consulta se pagina y se ordena por fecha, de más nueva a más antigua. Se añade filtro por rango de fechas. **\[PROPUESTA\]**
- Quién puede consultar: ADMIN **\[PENDIENTE\]**.
- Nunca se guardan contraseñas ni datos secretos dentro de `attDetail`.

### Componentes

| Diagrama | Código | `[Component: …]` | Responsabilidad |
| --- | --- | --- | --- |
| AuditEventListener | `clsAuditEventListener` | Java, Spring AMQP (`@RabbitListener` sobre `audit.queue`) | Convierte el mensaje en `rqtAuditEvent`. |
| AuditRestController | `clsAuditRestController` | Java, Spring Web (solo `@GetMapping`) | Consulta de auditoría. |
| RecordAuditUseCase | `IRecordAuditUseCase` | Java | Puerto de entrada de escritura. |
| QueryAuditUseCase | `IQueryAuditUseCase` | Java | Puerto de entrada de lectura. |
| AuditService | `clsAuditService` | Java | Implementa ambos casos de uso. |
| AuditRecord | `clsAuditRecord` | Java | Entidad de dominio, inmutable. |
| AuditRepositoryPort | `IAuditRepositoryPort` | Java | Insertar y consultar. |
| AuditJpaAdapter | `clsAuditJpaAdapter` | Java, Spring Data JPA, Hibernate | Entidad JPA propia. |

### Operaciones

```java
// IRecordAuditUseCase
void opRecord(rqtAuditEvent prmEvent);

// IQueryAuditUseCase
rpsAuditRecord opFindById(UUID prmId);
rpsPage<rpsAuditRecord> opSearch(rqtAuditFilter prmFilter, int prmPage, int prmSize);

// IAuditRepositoryPort (solo insertar y consultar)
clsAuditRecord opSave(clsAuditRecord prmRecord);
boolean opExistsByEventId(UUID prmEventId);
Optional<clsAuditRecord> opFindById(UUID prmId);
clsPage<clsAuditRecord> opSearch(rqtAuditFilter prmFilter, int prmPage, int prmSize);

// Records
record rqtAuditEvent(UUID eventId, String eventType, Instant occurredAt,
    String sourceService, UUID actorId, String entityType, UUID entityId,
    Map<String, Object> detail) {}
record rqtAuditFilter(UUID actorId, String entityType, UUID entityId,
    Instant from, Instant to) {}
record rpsAuditRecord(UUID id, UUID eventId, UUID actorId, String action,
    String entityType, UUID entityId, String detail, String sourceService,
    Instant occurredAt, Instant recordedAt) {}
```

### Endpoints \[PROPUESTA\]

| Método y ruta | Rol | Códigos |
| --- | --- | --- |
| `GET /api/audit?actorId=&entityType=&entityId=&from=&to=&page=&size=` | ADMIN | 200 |
| `GET /api/audit/{id}` | ADMIN | 200, 404 |

No existe ningún `POST`, `PUT`, `PATCH` ni `DELETE`. El Gateway tampoco los enruta.

### Pruebas

Pruebas unitarias de `clsAuditService` con el puerto simulado: registro de un evento nuevo, evento duplicado, consulta por actor, consulta por entidad y consulta de un id inexistente.

## 13. C3 — MCMessaging

MCMessaging recibe los eventos de aviso, los convierte en un email y lo envía. No tiene controlador REST ni base de datos. **\[DECIDIDO\]**

&#91;embedded content: C3 MCMessaging · 1 entrada, 1 salida por definir\]

El flujo es `clsMessagingEventListener` → `ISendNotificationUseCase` → `clsNotificationService` → `IEmailSenderPort` → `clsEmailSenderAdapter` → proveedor de email. Los bordes punteados marcan lo que aún no está definido.

### Modelo

`clsEmailMessage`: `attTo`, `attSubject` y `attBody`.

### Reglas

- Solo consume `messaging.queue`, que recibe los eventos `auditable.aviso.#`.
- `clsNotificationService` toma el `eventType`, la `template` y las `variables` del evento y arma el asunto y el cuerpo en español. Las plantillas viven en el código o en un archivo de propiedades. **\[PROPUESTA\]**
- El correo del destinatario no está resuelto **\[PENDIENTE\]** (ver el problema abierto de la sección 7).
- El proveedor y el adaptador de salida están **\[PENDIENTE\]**. El candidato natural es Spring Mail (`JavaMailSender`). Hasta decidirlo, el adaptador queda como `[Component: Java, por definir]`.
- Si el envío falla, se reintenta un número limitado de veces y luego el mensaje va a una cola de mensajes muertos, sin perderse. **\[PROPUESTA\]**
- Sin base de datos no hay deduplicación por `eventId`, así que un mensaje repetido podría enviar dos correos. Se acepta ese riesgo por ahora. **\[PROPUESTA\]**
- Nunca se incluyen contraseñas en un correo.

### Componentes

| Diagrama | Código | `[Component: …]` | Responsabilidad |
| --- | --- | --- | --- |
| MessagingEventListener | `clsMessagingEventListener` | Java, Spring AMQP (`@RabbitListener` sobre `messaging.queue`) | Convierte el mensaje en `rqtEmailEvent`. |
| SendNotificationUseCase | `ISendNotificationUseCase` | Java | Puerto de entrada. |
| NotificationService | `clsNotificationService` | Java | Formatea el evento y pide el envío. |
| EmailMessage | `clsEmailMessage` | Java | Entidad de dominio. |
| EmailSenderPort | `IEmailSenderPort` | Java | Puerto de salida de correo. |
| EmailSenderAdapter | `clsEmailSenderAdapter` | Java, por definir | Habla con el proveedor. |

### Operaciones

```java
// ISendNotificationUseCase
void opSend(rqtEmailEvent prmEvent);

// IEmailSenderPort
void opSend(clsEmailMessage prmMessage);

// Records
record rqtRecipient(UUID userId, String email) {}
record rqtEmailEvent(UUID eventId, String eventType, Instant occurredAt,
    String sourceService, UUID actorId, rqtRecipient recipient,
    String template, Map<String, Object> variables) {}
```

`rqtRecipient.email` es opcional hasta resolver el problema abierto de la sección 7.

### Pruebas

Pruebas unitarias de `clsNotificationService` con `IEmailSenderPort` simulado: un correo por tipo de evento, variables que faltan y fallo del puerto.

## 14. Ciclo de vida de una pregunta

Los siete estados son **\[DECIDIDO\]** y pueden cambiar; las transiciones y los responsables son **\[PROPUESTA\]**.

&#91;embedded content: Ciclo de vida de una pregunta · 7 estados\]

Solo `BORRADOR` permite modificar la pregunta (RF-06). El estado `EN_REVISION` empieza con la primera revisión y termina cuando hay dos. Si se cambian los estados, hay que actualizar a la vez `enmQuestionStatus` en MCQuestion y MCReview, la tabla de transiciones de la sección 10 y este diagrama.

## 15. Requisitos funcionales

Los números RF-01 a RF-05 y las historias HU01 y HU02 no estaban en el texto recibido **\[PENDIENTE\]**; aquí solo constan los que se enviaron. El RF-06 lleva ese número por deducción del orden.

**Requisitos definidos por el equipo \[DECIDIDO\]**

| ID | Requisito |
| --- | --- |
| (campos) | Una pregunta contiene: contexto, pregunta directa, cuatro distractores, respuesta correcta, justificación de la respuesta, bibliografía, competencia, tema, subtema y nivel de dificultad. |
| RF-06 | El sistema permitirá modificar preguntas mientras estén en estado "Borrador". |
| RF-07 | El sistema permitirá consultar preguntas mediante filtros. |
| HU03 | Validación estructural. |
| RF-08 | El sistema validará automáticamente que toda pregunta contenga un contexto. |
| RF-09 | El sistema validará que exista una única pregunta directa. |
| RF-10 | El sistema verificará que existan exactamente cuatro distractores. |
| RF-11 | El sistema validará que exista únicamente una respuesta correcta. |
| RF-12 | El sistema impedirá el uso de expresiones como "Todas las anteriores" y "Ninguna de las anteriores". |

**Requisitos derivados de las decisiones tomadas \[PROPUESTA\]**

| ID | Requisito |
| --- | --- |
| RFP-01 | El ADMIN registra usuarios con `username`, contraseña, correo y un solo rol. |
| RFP-02 | El inicio de sesión con `username` y contraseña entrega un JWT que dura 1 día. |
| RFP-03 | El ADMIN lista, consulta, edita y desactiva usuarios. |
| RFP-04 | El AUTHOR crea preguntas de selección múltiple, de caso o multimedia. |
| RFP-05 | El AUTHOR envía a revisión una pregunta en `BORRADOR` y reabre una `RECHAZADA`. |
| RFP-06 | Cada pregunta recibe dos revisiones de revisores distintos, cada una con su justificación. |
| RFP-07 | Con las dos revisiones, la pregunta queda `APROBADA` o `RECHAZADA`. |
| RFP-08 | El ADMIN publica una pregunta aprobada y archiva una publicada. |
| RFP-09 | Los eventos relevantes quedan guardados en auditoría y se consultan por actor o entidad, solo en lectura. |
| RFP-10 | Los eventos de aviso generan un correo al usuario correspondiente. |
| RFP-11 | El Gateway valida el token y el rol en cada petición. |

**Fuera del alcance actual**

- Quizzes, respuestas de estudiantes y reportes de desempeño (futuro microservicio de quizzes).
- Cierre de sesión, refresh token y registro abierto.
- `ImportadorPreguntasExcel` y `GeneradorPreguntasIA`.
- Almacenamiento de archivos multimedia: solo se guarda la URL.
- Requisitos de rendimiento, disponibilidad y protección de datos personales: no se definieron.

## 16. Interfaz de usuario

La UI es simple, con HTML y CSS básicos y textos en español. **\[DECIDIDO\]** El resto son **\[PROPUESTA\]**:

- Solo se usa JavaScript sin librerías para llamar a la API a través del Gateway y guardar el token en `sessionStorage`.
- Pantallas: inicio de sesión, gestión de usuarios (ADMIN), lista y formulario de preguntas (AUTHOR), cola y formulario de revisión (REVIEWER) y consulta de auditoría (ADMIN).
- El menú muestra solo lo que el rol del token permite. La seguridad real está en el Gateway, no en la UI.
- Sin logout: el token vence a las 24 horas y, si el Gateway responde `401`, la UI vuelve al inicio de sesión.
- Dónde se sirve la UI **\[PENDIENTE\]**.
