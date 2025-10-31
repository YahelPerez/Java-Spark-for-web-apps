# Java Spark Collectibles Store# Project: Java Spark for Web Apps (Collectibles Store)



This is a web application built with Java Spark framework that simulates an online collectibles store. The application features both a REST API and server-side rendered views using Mustache templates.This repository contains the source code for the "Java Spark for web apps" challenge. The project is a RESTful API built using Java and the SparkJava framework, simulating the backend for Ramón's collectible items store.



## Features## Purpose of the Repository



- **User Management**: REST API endpoints for managing usersThis repository holds all source code, Maven configuration, and documentation for the project's 3 Sprints. The goal of **Sprint 1** is to establish the project foundation, configure dependencies, and build the basic API endpoints for user management.

- **Collectible Items**: Browse and view detailed information about rare collectibles

- **Bidding System**: Users can place bids on items## Sprint 1 Deliverables

- **Server-side Rendering**: Uses Mustache templates for a consistent user interface

- **Exception Handling**: Comprehensive error handling with custom exceptions* `pom.xml`: Maven configuration with all dependencies (Spark, Gson, Logback) and security vulnerability mitigations (Jetty version management).

- **Static Assets**: CSS styling and JavaScript functionality* `src/main/java/com/rafael/collectibles/Main.java`: Main API source code, including all user routes (GET, POST, PUT, DELETE, OPTIONS).

* `README.md`: This file.

## Tech Stack* `DECISIONS.md`: The shared log for key technical decisions.



- Java 17## Steps to Run the Project (Requirement 4d)

- Spark Java Framework

- Mustache TemplatesYou must have the following installed:

- Gson for JSON serialization* Java JDK 17 (or higher)

- Maven for dependency management* Apache Maven

- Logback for logging

### 1. Clone the Repository

## Project Structure

```bash

```# Replace with your own repository URL

src/main/git clone [https://github.com/](https://github.com/)YahelPerez/Java-Spark-for-web-apps.git

├── java/com/rafael/collectibles/cd Java-Spark-for-web-apps
│   ├── Main.java                 # Application entry point and routes
│   ├── Item.java                 # Item model
│   ├── User.java                 # User model
│   └── exception/                # Custom exception handlers
├── resources/
│   ├── public/                   # Static assets (CSS, JS)
│   └── templates/                # Mustache templates
│       ├── layout.mustache       # Base template
│       ├── list.mustache         # Items listing
│       └── detail.mustache       # Item detail view
```

## Running the Application

### Prerequisites

- Java JDK 17 or higher
- Apache Maven

### Steps to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/YahelPerez/Java-Spark-for-web-apps.git
   cd Java-Spark-for-web-apps
   ```

2. Build the project:
   ```bash
   mvn package
   ```

3. Run the application:
   ```bash
   java -jar target/Java-Spark-for-web-apps-1.0-SNAPSHOT-shaded.jar
   ```

4. Visit http://localhost:8080/items in your browser to see the application

## API Endpoints

### Users API (JSON)
- `GET /users` - List all users
- `GET /users/:id` - Get user by ID
- `POST /users` - Create new user
- `PUT /users/:id` - Update user
- `DELETE /users/:id` - Delete user

### Items Views
- `GET /items` - View all items
- `GET /items/:id` - View item details

## Contributing

Please read `DECISIONS.md` for details on our code decisions and the processes we follow.

## License

This project is part of a learning exercise and demonstration.