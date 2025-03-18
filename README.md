# LeverX Final Project

## Description
This is the final project for LeverX.

## Technologies Used
The project uses the following technologies:

- **Spring Boot** (JPA, Redis, OAuth2 Resource Server, Security, Web, Validation, Mail)
- **SpringDoc OpenAPI** (Swagger UI)
- **MapStruct** (Object Mapping)
- **Jedis** (Redis Client)
- **JWT (io.jsonwebtoken)** (Authentication)
- **Lombok** (Boilerplate Reduction)
- **PostgreSQL** (Database)
- **Testcontainers** (Integration Testing)
- **WireMock** (Mocking API Responses)
- **JSON Unit** (JSON Testing)

## Running Locally

1. Navigate to the `docker` folder and run:
   ```sh
   docker compose up
   ```
2. Build the project:
   ```sh
   ./gradlew build
   ```
3. Run the application:
   ```sh
   java -jar build/libs/rating-system-0.0.1-SNAPSHOT.jar
   ```

## Deployment to SAP BTP

1. Create service instances:
   - **PostgreSQL** instance named `postgresqlbtp`
   - **Redis** instance named `redisbtp`
2. Build the project:
   ```sh
   ./gradlew build
   ```
3. Deploy using Cloud Foundry:
   ```sh
   cf push
   ```

## API Documentation
The API documentation is available at:
[Swagger UI](https://leverx-final-petrachkou.cfapps.us10-001.hana.ondemand.com/swagger-ui/index.html)
