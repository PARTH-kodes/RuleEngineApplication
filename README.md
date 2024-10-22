# Rule Engine Application

## Overview

The **Rule Engine Application** is a Spring Boot project that parses complex rules into an Abstract Syntax Tree (AST), evaluates them, and combines multiple rules efficiently. The application also supports saving and loading rules using a database. The rules can be evaluated against dynamic data, making it suitable for decision-making scenarios.

## Features

- Parse rules into AST.
- Combine multiple rules using logical operators.
- Evaluate rules against provided data.
- Save and load rules from a database.
- REST API to interact with the system.

## Project Structure

```bash
.
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.example.RuleEngine
│   │   └── resources
│   └── test
├── pom.xml
└── README.md
```

## Dependencies

- **Spring Boot 3.x**
- **Spring Data JPA**
- **H2 Database (or switch to any other like MySQL, Postgres)**
- **Lombok (for reducing boilerplate code)**
- **Jackson (for JSON processing)**

Ensure to install the required dependencies via Maven:

```bash
mvn clean install
```

## Build Instructions

1. Clone the repository:

    ```bash
    git clone https://github.com/PARTH-kodes/RuleEngineApplication.git
    cd RuleEngineApplication
    ```

2. Install dependencies:

    ```bash
    mvn install
    ```

3. Build the project:

    ```bash
    mvn clean package
    ```

4. Run the application:

    ```bash
    mvn spring-boot:run
    ```

5. The application will start on **`localhost:8080`**. You can interact with the API using tools like Postman.

## REST API Endpoints

- **Create a rule**:
    - `POST /api/rules/create`
    - Example:
      ```json
      {
        "rule": "age > 30 AND department = 'Sales'"
      }
      ```

- **Combine multiple rules**:
    - `POST /api/rules/combine`
    - Example:
      ```json
      {
        "rules": ["rule1", "rule2"],
        "operator": "AND"
      }
      ```

- **Evaluate a rule**:
    - `POST /api/rules/evaluate`
    - Example:
      ```json
      {
        "rule": "age > 30",
        "data": {
          "age": 35
        }
      }
      ```

- **Save a rule**:
    - `POST /api/rules/save`
    - Saves the rule and its AST to the database.

- **Load rules**:
    - `GET /api/rules/load`
    - Retrieves all saved rules from the database.

## Design Choices

1. **AST Representation**: Each rule is parsed into a tree-like structure (AST), where each node represents an operand or operator. This structure allows for complex rule parsing and easy evaluation.
   
2. **Rule Evaluation**: The evaluation leverages the parsed AST and processes rules like `AND`, `OR`, and operands using custom logic. This provides flexibility to handle various data types like integers and strings.

3. **Modular Services**: The logic is split into services and controllers, with a clear separation of concerns between parsing, rule combination, and persistence.

4. **Persistence with JPA**: The rules and their ASTs are stored in the database as `RuleEntity`. This makes the rules persistable and retrievable at any time.

## How to Run with Docker/Podman

You can containerize the application using Docker or Podman to ensure consistent environment setup. Below is a basic setup:

1. Create a `Dockerfile` in the project root:

    ```dockerfile
    # Start with a Maven image to build the project
    FROM maven:3.8.1-openjdk-17 AS build
    COPY . /app
    WORKDIR /app
    RUN mvn clean package -DskipTests

    # Use an OpenJDK image to run the application
    FROM openjdk:17-jdk-slim
    COPY --from=build /app/target/RuleEngine-0.0.1-SNAPSHOT.jar /app/RuleEngine.jar
    WORKDIR /app
    EXPOSE 8080
    CMD ["java", "-jar", "RuleEngine.jar"]
    ```

2. Build the Docker image:

    ```bash
    docker build -t rule-engine-app .
    ```

3. Run the container:

    ```bash
    docker run -p 8080:8080 rule-engine-app
    ```

4. The application will now be accessible at **`localhost:8080`**.

## How to Run with Database in Docker

To run the application with a database like MySQL:

1. Set up a `docker-compose.yml` file:

    ```yaml
    version: '3'
    services:
      db:
        image: mysql:8
        environment:
          MYSQL_ROOT_PASSWORD: rootpassword
          MYSQL_DATABASE: rule_engine_db
          MYSQL_USER: user
          MYSQL_PASSWORD: password
        ports:
          - "3306:3306"
      app:
        image: rule-engine-app
        ports:
          - "8080:8080"
        depends_on:
          - db
    ```

2. Start the services:

    ```bash
    docker-compose up
    ```

3. Update the application properties to point to the MySQL database in the `application.properties`:

    ```properties
    spring.datasource.url=jdbc:mysql://db:3306/rule_engine_db
    spring.datasource.username=user
    spring.datasource.password=password
    ```

## License

This project is open-source and available under the MIT License.
