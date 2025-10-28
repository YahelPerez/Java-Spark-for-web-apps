# Project Technical Decision Log

This document records key decisions made during development to support team learning, as required by Sprint 1.

## Sprint 1

### Decision 1: Backend Framework
* **Decision:** Use **SparkJava (version 2.9.4)**.
* **Justification:** This is a lightweight micro-framework explicitly required by the challenge context. It allows for rapid and simple route definition, ideal for a RESTful API.

### Decision 2: JSON Library
* **Decision:** Use **Gson (version 2.10.1)**.
* **Justification:** Gson is a robust library from Google for JSON serialization and deserialization. It was a specific dependency requested in the Sprint 1 instructions.

### Decision 3: Data Persistence
* **Decision:** Use a static `HashMap` in memory to simulate the user database.
* **Justification:** The goal of Sprint 1 is to build and test the API layer (routes and request handling). An in-memory map is the fastest way to get a functional API without the overhead of configuring a real database at this early stage.

### Decision 4: Dependency Vulnerability Management
* **Decision:** Override SparkJava's transitive dependencies for **Jetty** to a more secure version (`9.4.55.v20240627`) using `<dependencyManagement>`. Accept the (2) remaining low-risk vulnerabilities for **Logback** (`1.5.6`), as it is the latest available version.
* **Justification:** The default dependencies from Spark (`spark-core:2.9.4`) are old and trigger multiple critical security warnings in modern IDEs.
    * The **Jetty** vulnerabilities were resolved by forcing Maven to use a newer, compatible version.
    * The **Logback** vulnerabilities are present even in the latest published version, so the risk is documented and accepted to avoid blocking development.

### Decision 5: User Creation Route (POST)
* **Decision:** Implement `POST /users/:id` for user creation, where the client provides the ID.
* **Justification:** This strictly follows the specification from **Requirement 3c**. Although a common REST pattern is to `POST /users` (letting the server generate the ID), we are adhering to the explicit instructions provided in the challenge.