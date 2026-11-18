Universidad del Cauca   
Laboratorio de Ingeniería de Software II   
Ing. Paola Andrea Bedoya Toro   
2026.2 

**TALLER 13: ENTREGA FINAL INTEGRADORA (FASE II)** *Guía Unificada: DDD, Arquitectura Hexagonal, Contenerización con Docker y  Solucionario Técnico* 

**PARTE 1: ESPECIFICACIÓN INSTITUCIONAL Y REQUERIMIENTOS DE  EVALUACIÓN** 

El Taller 13 representa la evaluación final integradora del curso de Laboratorio de Ingeniería de  Software II. En esta práctica, los estudiantes consolidan la refactorización arquitectónica de la Fase  I (DDD y Arquitectura Hexagonal iniciada en el Taller 12\) y completan la Fase II de Infraestructura  mediante la contenerización con Dockerfile y la orquestación multi-servicio con Docker Compose  sobre el sistema del Banco de Preguntas Saber PRO. 

**1\. Objetivos de Aprendizaje Integrados** 

• Consolidación DDD y Hexagonal: Encapsular el dominio del Agregado Pregunta y Objetos de  Valor en Java puro, aislando completamente las reglas de negocio de cualquier framework  (Spring/JPA). 

• Puertos y Adaptadores: Estructurar los puertos de entrada (Casos de Uso) y salida (Persistencia  y Notificaciones), demostrando la intercambiabilidad mediante la prueba de  InMemoryQuestionAdapter vs JpaQuestionAdapter. 

• Contenerización con Dockerfile: Crear imágenes livianas Docker con Dockerfile basado en  openjdk:17-jdk-slim para cada uno de los microservicios del ecosistema. 

• Orquestación con Docker Compose: Configurar el archivo docker-compose.yml para levantar y  comunicar en una red privada virtual (saberpro-net) los servicios Validation Service, Question  Service, Lifecycle Service, RabbitMQ y PostgreSQL/H2. 

• Trazabilidad Semestral: Demostrar la evolución del proyecto desde el monolito limpio (Corte I)  hasta los microservicios distribuidos contenerizados (Corte III). 

**2\. Requerimientos Técnicos de la Entrega Final** 

• Núcleo de Dominio Puro: Ubicado en domain.model, contiene las clases Pregunta, Enunciado,  Justificacion y OpcionRespuesta sin anotaciones @Entity ni librerías de Spring. 

• Contenerización con Dockerfile: Cada microservicio (Validation Service :8082, Question Service  :8083 y Lifecycle Service :8084) debe incluir un Dockerfile funcional en su raíz. 

• Orquestación Multi-Contenedor: El archivo docker-compose.yml en la raíz debe compilar y  ejecutar todo el ecosistema con el comando docker compose up \-d \--build. 

• Red Virtual saberpro-net: Definición explícita de la red bridge para garantizar la comunicación  aislada entre contenedores. 

• Exposición de Puertos Públicos: Configuración de mapeo de puertos (8082, 8083, 8084, 5672 y  15672\) para posibilitar las pruebas integradas con Postman o Thunder Client.

Universidad del Cauca 

Facultad de Ingeniería Electrónica y Telecomunicaciones Departamento de Sistemas \- Programa de Ingeniería de Sistemas 

Universidad del Cauca   
Laboratorio de Ingeniería de Software II   
Ing. Paola Andrea Bedoya Toro   
2026.2 

**3\. Estructura Oficial de Encarpetado del Proyecto.** 

El repositorio final entregado en GitHub debe organizar estrictamente la siguiente arborescencia de  archivos y paquetes: 

saberpro-banco-preguntas/ \<-- Raíz del Repositorio 

├── docker-compose.yml \<-- Orquestador unificado multi-contenedor ├── README.md \<-- Guía de ejecución del proyecto │ 

├── validation-service/ \<-- Microservicio de Validación (:8082) │ ├── Dockerfile \<-- Configuración Docker del servicio │ ├── pom.xml \<-- Dependencias Maven 

│ └── src/main/java/co/edu/unicauca/saberpro/validation/ 

│ ├── ValidationServiceApplication.java 

│ └── api/ValidationRestController.java 

│ 

├── question-service/ \<-- Microservicio Principal (:8083) │ ├── Dockerfile \<-- Configuración Docker del servicio │ ├── pom.xml \<-- Dependencias Maven 

│ └── src/main/java/co/edu/unicauca/saberpro/question/ 

│ ├── QuestionServiceApplication.java 

│ ├── domain/ \<-- DOMINIO PURO (Sin @Entity ni Spring) │ │ ├── model/ (Question.java, AnswerOption.java, Enunciado.java, Justificacion.java) │ │ ├── exception/ (ReglaNegocioException.java) 

│ │ └── ports/ 

│ │ ├── inbound/ (CrearPreguntaUseCase.java, EvaluarPreguntaUseCase.java) │ │ └── outbound/ (PreguntaRepositoryPort.java, NotificacionPort.java) │ ├── application/ \<-- SERVICIOS DE APLICACIÓN 

│ │ └── QuestionApplicationService.java 

│ ├── infrastructure/ \<-- ADAPTADORES (INFRAESTRUCTURA) │ │ ├── jpa/ (QuestionRecord.java, QuestionDomainMapper.java, JpaQuestionAdapter.java) │ │ ├── memory/ (InMemoryQuestionAdapter.java \<-- Prueba de Intercambiabilidad) │ │ └── messaging/ (RabbitMQPublisher.java) 

│ └── api/ (QuestionRestController.java) 

│ 

└── lifecycle-service/ \<-- Microservicio de Ciclo de Vida (:8084)  ├── Dockerfile \<-- Configuración Docker del servicio  ├── pom.xml \<-- Dependencias Maven 

 └── src/main/java/co/edu/unicauca/saberpro/lifecycle/ 

 ├── LifecycleServiceApplication.java 

 └── messaging/QuestionReviewListener.java 

**4\. Matriz de Trazabilidad del Proyecto Semestral.** 

La siguiente tabla resume la evolución técnica del proyecto integrador a lo largo de las tres  iteraciones evaluativas del semestre: 

**Corte Evaluado Enfoque  Arquitectónico**   
**Hitos Técnicos e Infraestructura Integrada Ponde ración**

Universidad del Cauca 

Facultad de Ingeniería Electrónica y Telecomunicaciones Departamento de Sistemas \- Programa de Ingeniería de Sistemas 

Universidad del Cauca   
Laboratorio de Ingeniería de Software II Ing. Paola Andrea Bedoya Toro 2026.2 

Corte I Monolito Limpio &  SOLID   
Refactorización SOLID, capas MVC limpias, pipeline  de validación HU03 (Pipes & Filters) y soporte de  plugins (Microkernel).   
25% 

Corte II Microservicios & GoF Bounded Contexts autónomos, peticiones síncronas    
35% 

REST, eventos AMQP RabbitMQ, patrones GoF  

(Builder, State, Adapter) y JPA. 

Corte III DDD, Hexagonal &  Docker   
Dominio puro en Java, Puertos/Adaptadores,  intercambiabilidad de repositorios (InMemory vs  JPA), Dockerfile y Docker Compose.   
40% 

**4\. Entregables y Rúbrica Final de Evaluación** 

Los estudiantes deben presentar los siguientes artefactos en el espacio habilitado de la plataforma: 

• Repositorio GitHub: Estructura organizada con el código fuente de los microservicios, sus  Dockerfile y el archivo docker-compose.yml en la raíz. 

• Informe Técnico Final: Documento consolidado utilizando la plantilla Formato-Documento.docx  con diagramas C4 (Nivel 1 al 4\) y capturas de pantalla de la consola Docker y RabbitMQ. 

**Criterio de Evaluación**    
**Final Porcentaje Indicador de Logro Esperado** 

Arquitectura DDD y Hexagonal 35% Refactorización limpia en capas puras de dominio,  encapsulamiento de invariantes, puertos y adaptadores  

intercambiables (InMemory vs JPA). 

Contenerización y Docker  Compose   
30% Construcción correcta de Dockerfile por servicio,  empaquetado JAR ejecutable y despliegue exitoso  

mediante docker compose up \-d. 

Flujo Integrador de Servicios 20% Comprobación end-to-end de peticiones REST síncronas  (201/400) y procesamiento asíncrono en RabbitMQ (202  

Accepted) sobre contenedores. 

Informe Técnico y Diagramas  C4   
15% Informe final unificado con trazabilidad semestral,  diagramas C4 (Nivel 1 al 4\) y evidencias de ejecución.

Universidad del Cauca 

Facultad de Ingeniería Electrónica y Telecomunicaciones Departamento de Sistemas \- Programa de Ingeniería de Sistemas 