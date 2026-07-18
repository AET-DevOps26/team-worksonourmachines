# System Overview

At the core are three **server** Spring Boot Microservices. They are each responsible for their own logic and each exposes a REST API.

- Communication Service
- Marketplace Service
- Student Service

Each of them communicates with a logical DB from our single Postgres **DB** instance. Only the responsible service can write to the logical DB, and cross-service access to any other logical DB is not allowed.

The **GenAI** Service is written in Python and consists of a LangChain component and a FastAPI HTTP component. It will communicate with (a subset of) the three Server Microservices. Communication to AI models will include an external online model (Logos) and a self deployed local model (Ollama).

The **web client** uses React Router. The service consists of the in-browser Frontend (FE) and a Backend-for-frontend (BFF). The FE communicates with the BFF via actions and loaders (React Router concepts). The BFF will communicate directly with the Server Microservices via the provided REST APIs.

A **Public Gateway** will expose only the client-facing services and endpoints, while hiding internal services/communication. Due to the React Router BFF, the server Microservices are not directly accessible to any user and are not exposed to the Internet.

In addition to the core services, we deploy UIs for Observability and OpenAPI Specs. Further supporting services are Keycloak as IAM provider and services necessary for the Observability stack.
