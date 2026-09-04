#  Dental Project - API REST Backend

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)

Backend robusto y API RESTful diseñado para soportar toda la lógica de negocio y gestión de datos de una clínica dental integral.

Este proyecto funciona en conjunto con su cliente móvil. Puedes ver el repositorio del frontend aquí: [dental-project-frontend](https://github.com/Dianathecoder/dental-project-frontend).

##  Arquitectura y Lógica de Negocio

El núcleo de este sistema destaca por su compleja arquitectura de permisos y la gestión de entidades interconectadas. La API está diseñada para servir datos de manera eficiente y segura al cliente Android.

###  Seguridad y Autenticación (Spring Security)
*   **Role-Based Access Control (RBAC):** Sistema de seguridad estricto que protege los endpoints dependiendo de 4 roles definidos (Administrador, Auxiliar, Doctor, Paciente).
*   **Flujos de acceso híbridos:** Soporte para autenticación mediante Google y acceso tradicional.
*   **Gestión de credenciales seguras:** Lógica para la generación de contraseñas temporales por parte del Administrador para el primer inicio de sesión del personal médico.

###  Endpoints y Módulos Principales
*   **Módulo de Usuarios y RRHH:** CRUD de usuarios, asignación de roles y sistema de registro de jornada laboral (fichajes de entrada y salida).
*   **Módulo Clínico:** Gestión del estado del paciente, historiales médicos y almacenamiento estructurado de Odontogramas.
*   **Módulo de Logística:** Control y asignación de Boxes (salas), protocolos médicos e inventario de materiales.
*   **Módulo de Agendas:** Gestión de citas, calendarios segmentados por doctor y asignación de espacios.
*   **Módulo de Comunicación (WebSockets):** Infraestructura en tiempo real para soportar un chat interno segmentado con reglas de comunicación estrictas según el rol.

## Contexto del Proyecto y Mis Aportes

Este repositorio es una evolución de un proyecto académico grupal (https://github.com/cassiuste/dynalar_backend)). 

**Mi mayor contribución en esta versión individual ha sido la creación desde cero de toda la capa de seguridad y gestión de usuarios**, la cual no existía en el proyecto original. He rediseñado la arquitectura lógica para convertirla en un sistema integral con control de acceso basado en roles (RBAC).

###  Seguridad y Autenticación (Construida desde cero)
*   **Integración de Spring Security:** Configuración de filtros, protección de rutas y manejo de sesiones/tokens para toda la API.
*   **Autenticación Híbrida:** Implementación completa del flujo de registro e inicio de sesión integrando **Google Login (OAuth2)** junto con un sistema de acceso con credenciales tradicionales.
*   **Gestión de credenciales seguras:** Lógica para la generación de contraseñas temporales por parte del Administrador para el primer inicio de sesión del personal médico.


##  Tecnologías Utilizadas

*   **Lenguaje:** Java
*   **Framework Principal:** Spring Boot
*   **Seguridad:** Spring Security *(Añade aquí si usas JWT (JSON Web Tokens) o sesiones)*
*   **Persistencia de Datos:** Spring Data JPA / Hibernate
*   **Base de Datos:** *(Ej: MySQL o PostgreSQL - ¡Cambia esto por la que uses!)*
*   **Comunicación en tiempo real:** *(Ej: Spring WebSockets / STOMP para el chat)*
  
### ⚙️ Funcionalidades y Control de Roles (RBAC)
He implementado una separación estricta de permisos e interfaces dependiendo del tipo de usuario:

*   ** Administrador (Control Total):** Creación de usuarios, configuración de horarios/fichajes, gestión clínica (Odontogramas, materiales, boxes) y administración del calendario.
*   ** Auxiliar (Gestión Operativa):** Acceso a herramientas clínicas y calendario, fichaje de jornada, pero sin permisos de configuración global.
*   ** Doctor (Enfoque Clínico):** Visualización de citas propias, boxes asignados y acceso directo a las fichas de sus pacientes correspondientes.
*   ** Paciente (Portal de Usuario):** Autogestión de citas y visualización de box/doctor asignado.
##  Estructura de la Base de Datos

El proyecto cuenta con un modelo relacional complejo para unir toda la operativa de la clínica. Algunas de las relaciones clave incluyen:
*   Usuarios (1:N) Citas
*   Citas (1:1) Boxes
*   Pacientes (1:1) Odontogramas
*   *(Nota para Diana: Si tienes un diagrama Entidad-Relación (ER), este es el lugar perfecto para añadir la imagen)*

##  Configuración y Ejecución local

1. Asegúrate de tener instalado Java 17+ y *(tu base de datos, ej: MySQL)* funcionando.
2. Clona el repositorio: `git clone https://github.com/Dianathecoder/dental-project-backend.git`
3. Configura las variables de entorno o el archivo `application.properties` / `application.yml` con tus credenciales locales de base de datos.
4. Ejecuta el proyecto mediante Maven o Gradle:
   ```bash
   ./mvnw spring-boot:run
   # o si usas gradle:
   ./gradlew bootRun
