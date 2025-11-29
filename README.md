# 🛡️ User Management Microservice

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-6.0-47A248?style=for-the-badge&logo=mongodb&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Security](https://img.shields.io/badge/OAuth2-Resource_Server-red?style=for-the-badge&logo=spring-security&logoColor=white)

Este proyecto es un microservicio encargado de la **gestión centralizada de usuarios** y la validación de identidad. Actúa como una pieza fundamental en una arquitectura distribuida, proporcionando endpoints seguros para el registro, consulta y administración de perfiles, integrándose con un frontend en **React**.

## 🚀 Arquitectura

El proyecto sigue una **Arquitectura Hexagonal (Puertos y Adaptadores)** para desacoplar la lógica de negocio de la infraestructura.

- **Dominio:** Entidades y lógica pura de negocio (User, Role).
- **Aplicación:** Casos de uso y servicios (UserCreateUseCase, UserAuthService).
- **Infraestructura:**
    - *Input Adapters:* Controladores REST (`@RestController`).
    - *Output Adapters:* Repositorios MongoDB (`MongoRepository`) y Productores de Eventos (RabbitMQ).

Además, funciona como un **OAuth2 Resource Server**, validando tokens JWT mediante llaves criptográficas (RSA) gestionadas con Bouncy Castle.

## 📋 Prerrequisitos

Antes de iniciar, asegúrate de tener instalado:

* **Java 17** (JDK)
* **Docker & Docker Compose** (V3.8+)
* **Maven** (Opcional, si usas el wrapper incluido)

## 🔧 Instalación y Configuración

### 1. Clonar el repositorio
```bash
git clone [[https://github.com/tu-usuario/microservicio_usuarios.git](https://github.com/Turismo-sostenible/User_Microservice.git)]([https://github.com/tu-usuario/microservicio_usuarios.git](https://github.com/Turismo-sostenible/User_Microservice.git))
cd microservicio_usuarios
```
2. Configurar Variables de Entorno (.env)
Crea un archivo .env en la raíz del proyecto. Estas variables son consumidas por docker-compose.yml:

Properties

MONGO_INITDB_ROOT_USERNAME=root
MONGO_INITDB_ROOT_PASSWORD=secretpassword
SPRING_DATA_MONGODB_DATABASE=users_db
3. Configuración de Volúmenes (Importante ⚠️)
El archivo docker-compose.yml requiere montar las llaves RSA para la firma de tokens. Nota: Verifica en el docker-compose.yml que la ruta del volumen de las llaves apunte a tu directorio local correcto o usa una ruta relativa:

YAML

# Ejemplo en docker-compose.yml:
volumes:
  - ./keys:/app/keys
Asegúrate de que la carpeta keys contenga tus pares de claves RSA (private.pem, public.pem).

🐳 Ejecución con Docker
Para levantar la base de datos MongoDB y el microservicio simultáneamente:

Bash

docker-compose up --build -d
Verifica que los contenedores estén corriendo:

Bash

docker ps
El servicio estará disponible en: http://localhost:8080

🔌 Endpoints Principales
Aquí tienes algunos ejemplos de consumo de la API basados en los DTOs actuales.

1. Crear un Usuario (Público)
POST /api/users

JSON

{
  "username": "juanperez",
  "name": "Juan",
  "lastName": "Pérez",
  "age": 22,
  "email": "juan.perez@unicauca.edu.co",
  "password": "PasswordSeguro123!",
  "role": "ADMINISTRATOR"
}
2. Obtener Perfil (Requiere Auth)
GET /api/users/{id} Headers: Authorization: Bearer <tu_token_jwt>

3. Health Check
GET /actuator/health (Verifica si el servicio y la conexión a Mongo están activos)

🛠️ Stack Tecnológico
Framework: Spring Boot 3.5.6

Base de Datos: MongoDB

Seguridad: Spring Security, OAuth2 Resource Server

Mapeo: MapStruct & ModelMapper

Utilidades: Lombok, Spring Dotenv

Testing: Mockito, Spring Security Test

Desarrollado para el curso de Microservicios - Universidad del Cauca 🎓
