# ♠ Blackjack Game

A web-based Blackjack application built with Java, Spring Boot and modern web technologies.

This project is currently **under construction** and actively being developed. The goal is to create a complete Blackjack platform with user authentication, game logic, persistent user data and a responsive web interface.

---

## 🚧 Project Status: Under Construction

This application is currently in an active development phase.

Implemented features:

✅ Spring Boot backend  
✅ MVC architecture  
✅ Thymeleaf frontend rendering  
✅ HTML, CSS and JavaScript user interface  
✅ REST API endpoints  
✅ User registration and login  
✅ JWT authentication setup  
✅ Password encryption with BCrypt  
✅ Database integration with JPA/Hibernate  
✅ Docker development environment  

Currently working on:

🚧 Complete Blackjack game mechanics  
🚧 Improved user experience  
🚧 Better error handling  
🚧 User balance management  
🚧 Game history and statistics  
🚧 Additional API improvements  

---

# 🛠 Technologies Used

## Backend

### Java

The main programming language used for backend development and application logic.

### Spring Boot

Used as the main framework for building the application.

Technologies included:

- Spring MVC
- Spring Security
- Spring Data JPA
- REST API
- Dependency Injection

### Spring Security + JWT

Authentication and authorization are implemented using:

- JWT tokens
- Stateless authentication
- Protected endpoints
- Role-based authorization

### BCrypt

User passwords are securely encrypted using BCrypt before being stored in the database.

Password flow:

```
User password
      |
      v
 BCrypt hashing
      |
      v
Encrypted password stored in database
```

---

# Frontend

## Thymeleaf

Used for server-side HTML rendering and connecting backend data with frontend views.

Features:

- Dynamic HTML pages
- MVC-based templates
- Backend-to-frontend data communication

## HTML5

Used for creating the structure and layout of the application.

## CSS3

Used for:

- Styling
- Layout
- User interface design

## JavaScript

Used for:

- Client-side interaction
- Game interface functionality
- Dynamic updates

---

# Database

## JPA / Hibernate

Used for object-relational mapping between Java objects and database tables.

Features:

- Entity management
- Repository pattern
- Database persistence

Stored data includes:

- Users
- User accounts
- Balance information
- Game-related data

---

# REST API

The application uses REST API endpoints for communication between the frontend and backend.

Example endpoints:

```
POST /api/users/register
```

Creates a new user account.

```
POST /api/users/login
```

Authenticates users and provides authentication access.

```
POST /game/start
```

Starts a new Blackjack game session.

---

# Docker

Docker is used to create a consistent development environment.

Benefits:

- Simplified setup
- Consistent environment across machines
- Easier deployment process

---

# Project Architecture

The application follows a layered architecture:

```
Controller
    |
    v
Service
    |
    v
Repository
    |
    v
Database
```

## Controller Layer

Handles HTTP requests and communication with clients.

## Service Layer

Contains business logic such as:

- Blackjack game logic
- User operations
- Balance management

## Repository Layer

Handles database communication using Spring Data JPA.

---

# Security Features

Implemented security features:

- JWT authentication
- BCrypt password hashing
- Protected API endpoints
- Role-based access control

Available roles:

```
USER
ADMIN
```

---

# Future Improvements

Planned features:

- Complete Blackjack rules engine
- Card animations
- Improved UI/UX
- Multiplayer support
- Leaderboard system
- Game history
- Automated testing
- CI/CD pipeline
- Production deployment using Docker

---

# Running the Project

The project is currently under development.

Requirements:

- Java 17+
- Maven
- Docker

Detailed setup instructions will be added as development continues.

---

# Author

**Maruf Mulk**

---

# Purpose

This project was created as a personal portfolio project to demonstrate skills in:

- Backend development
- Full-stack web development
- Spring Boot application design
- REST API development
- Database design
- Authentication and security
- Containerization with Docker
