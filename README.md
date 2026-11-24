# Training Institute Course and Batch Management System

An institute operations portal for managing courses, trainers, students, batches, enrolments, seat capacity validation, and attendance overview.

## Tech Stack

- **Frontend:** Angular 20.x (Standalone Components, Reactive Forms, Bootstrap 5.x, SCSS)
- **Backend:** Java 21, Spring Boot 3.5.x REST API, Spring Data JPA, Maven Wrapper (`mvnw.cmd` / `mvnw`)
- **Database:** MySQL 8.x (InnoDB, `institute_db`)
- **Testing:** JUnit 5, Mockito, Spring Boot Test (H2 for isolated tests), Karma + Jasmine (`ChromeHeadless`)

## Project Structure

```text
training-institute-management/
├── frontend/       # Angular 20 standalone SPA
├── backend/        # Spring Boot 3.5 REST API
├── database/       # MySQL 8 schema, sample seed data, and ER diagram
├── documentation/  # API, database, and test documentation
└── postman/        # Postman API collection
```

## Prerequisites (Windows)

- **JDK 21+** (`java -version`)
- **Node.js 20+ & npm** (`node -v`, `npm -v`)
- **MySQL Server 8.x** (`mysql --version`)

## Environment Variables

### Backend (`backend/.env.example`)
Copy `backend/.env.example` to `backend/.env` (or export these variables in your PowerShell session before starting Spring Boot):

```properties
DB_HOST=localhost
DB_PORT=3306
DB_NAME=institute_db
DB_USERNAME=root
DB_PASSWORD=your_mysql_password
SERVER_PORT=8080
CORS_ALLOWED_ORIGINS=http://localhost:4200
```

### Frontend (`frontend/.env.example`)
Reference configuration for the API base URL:

```properties
API_BASE_URL=http://localhost:8080/api/v1
APP_TITLE=Training Institute Management Portal
```

## Running on Windows (PowerShell)

### 1. Initialize MySQL Database

```powershell
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS institute_db;"
Get-Content database/schema.sql | mysql -u root -p institute_db
Get-Content database/sample-data.sql | mysql -u root -p institute_db
```

### 2. Start Spring Boot Backend (`http://localhost:8080`)

```powershell
cd backend
$env:DB_PASSWORD="your_mysql_password"
.\mvnw.cmd spring-boot:run
```

Verify the API is live:

```powershell
curl.exe -s http://localhost:8080/api/v1/dashboard/summary
```

### 3. Start Angular Frontend (`http://localhost:4200`)

```powershell
cd frontend
npm.cmd install
npm.cmd start
```

Open `http://localhost:4200` in your browser.

## Running Tests & Builds

### Backend Tests (52 tests on H2)

```powershell
cd backend
.\mvnw.cmd test
```

### Frontend Build & Unit Tests

```powershell
cd frontend
npm.cmd run build
npm.cmd test -- --watch=false --browsers=ChromeHeadless
```

## Documentation & Postman

- **REST API Reference:** [documentation/API_DOCUMENTATION.md](documentation/API_DOCUMENTATION.md)
- **Database Design & ER Diagram:** [documentation/DATABASE_DESIGN.md](documentation/DATABASE_DESIGN.md)
- **Test Report:** [documentation/TEST_REPORT.md](documentation/TEST_REPORT.md)
- **Postman Collection:** Import [postman/institute-management.postman_collection.json](postman/institute-management.postman_collection.json) into Postman (`baseUrl` defaults to `http://localhost:8080/api/v1`).
