# CampusAura Backend

CampusAura is a modern campus engagement platform that brings together event discovery, ticketing, fundraising, and a student marketplace in one system.

This repository contains the **Spring Boot backend**, which serves as the core API for the platform. It handles secure user authentication, role-based access control, business logic for the marketplace, and seamless integration with Firebase Firestore.

## Overview

The backend is built with security and scalability in mind. It uses Firebase Authentication to issue and validate JWT tokens, ensuring that all API endpoints are secure. Users are automatically synced to Firestore, and roles are dynamically assigned based on university email domains.

## Key Features

- **Secure RESTful API**: Protected endpoints with Firebase JWT token validation.
- **Role-Based Access Control**: Granular permissions for 4 distinct roles (`ADMIN`, `COORDINATOR`, `STUDENT`, `EXTERNAL_USER`).
- **Smart Role Assignment**: Automatic role provisioning based on registered email domains (e.g., `@std.uwu.ac.lk` for students).
- **Marketplace Business Rules**: Access control logic (e.g., restricting external users from selling items).
- **Firestore Integration**: Seamless data storage and retrieval using Google Cloud Firestore.
- **Health Monitoring**: Spring Boot Actuator endpoints configured for Azure deployment probes.

## Live Environment

🔗 **Frontend Application**: https://campus-aura-frontend.vercel.app  
🔗 **Backend API**: https://campusaura-backend.lemontree-0868690c.centralindia.azurecontainerapps.io  

## Tech Stack

- **Java 17**
- **Spring Boot 3**
- **Firebase Admin SDK** (Authentication & Firestore)
- **Maven** for dependency management
- **Docker** for containerization
- **Azure Container Apps** for hosting

## Project Structure

- `src/main/java/.../config/` - Firebase initialization and Spring Security configurations.
- `src/main/java/.../controller/` - RESTful API endpoints for Auth, Users, Events, and Marketplace.
- `src/main/java/.../model/` - Data models and entities representing the business domain.
- `src/main/java/.../repository/` - Firestore data access implementations.
- `src/main/java/.../security/` - Custom JWT authentication filters and role constants.
- `src/main/java/.../service/` - Core business logic and service layers.
- `src/main/java/.../util/` - Utility classes, including email domain validation.

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Firebase project with Authentication and Firestore enabled
- Firebase service account credentials (`firebase-service-account.json`)

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd CampusAura-backend
   ```

2. **Configure Firebase**
   Place your `firebase-service-account.json` in the `src/main/resources/` directory.

3. **Environment Setup**
   Update `src/main/resources/application.properties` or use environment variables:
   ```properties
   firebase.service-account-key=classpath:firebase-service-account.json
   firebase.database-url=https://your-project.firebaseio.com
   ```

### Development

Run the application locally using the Maven wrapper:

```bash
./mvnw spring-boot:run
```

The server will start at `http://localhost:8080`.

## Docker Configuration

The application is fully containerized for seamless development and production deployment. The repository includes a `Dockerfile` for building the application image and a `docker-compose.yml` for local orchestration.

### Building the Image

To build the Docker image locally, run the following command from the project root:

```bash
docker build -t campusaura-backend .
```

### Running with Docker Compose

For a complete local setup, you can start the backend using Docker Compose. Make sure your `firebase-service-account.json` is correctly placed so it can be mounted securely into the container.

```bash
docker-compose up -d --build
```

The container will expose the API on port `8080`. To stop the container, use `docker-compose down`.

## Deployment

This backend is containerized and ready to deploy on **Azure Container Apps**.

- **Docker**: The project includes a `Dockerfile` for building production-ready images.
- **CI/CD**: Automated deployment is managed via GitHub Actions (`.github/workflows/deploy-backend.yml`).
- **Secrets Management**: Sensitive configuration, such as the Firebase service account key and CORS allowed origins, should be securely passed to the container environment during deployment.
