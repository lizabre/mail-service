# mail-service
Web project for a 'Web Technologies (Bachelor)' course 
by - Uliana Yeshchenko (5662382) and Yelyzaveta Bredikhina (5662340)

## Prerequisites

| Tool | Version |
|------|---------|
| Java | 21      |
| Node.js | 22.12   |

## Getting Started

### 1. Start the server
```bash
cd server
link the project with grandle and run the application
```
Server runs on **http://localhost:8080**

### 2. Start the client
```bash
cd client
npm install
npm run start
```
Client runs on **http://localhost:4200**

## Default Credentials

The application seeds sample data on startup. You can log in with any of the following accounts:

| Name | Email | Password |
|------|-------|----------|
| Alice Smith | alice@test.com | Password1! |
| Bob Jones | bob@test.com | Password1! |
| Carol White | carol@test.com | Password1! |
| Dave Brown | dave@test.com | Password1! |

## Configuration

No `.env` file is required. All configuration values have defaults built into `application.properties` and the application works out of the box.

> **Note:** This is an educational project. Default credentials and secrets are intentionally included for ease of setup and grading.

## Tech Stack

- **Client:** Angular 2+, TypeScript, Angular Material
- **Server:** Spring Boot, Kotlin, REST API
- **Database:** H2 (in-memory), JPA/Hibernate
- **Auth:** JWT (JSON Web Tokens)
