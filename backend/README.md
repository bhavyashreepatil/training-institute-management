# Institute Management Backend

Spring Boot 3.5.x REST API for the Training Institute Course and Batch Management System.

## Environment Variables

Configure the following environment variables (see `.env.example`):

- `DB_HOST` (default: `localhost`)
- `DB_PORT` (default: `3306`)
- `DB_NAME` (default: `institute_db`)
- `DB_USERNAME` (default: `root`)
- `DB_PASSWORD`
- `SERVER_PORT` (default: `8080`)
- `CORS_ALLOWED_ORIGINS` (default: `http://localhost:4200,http://127.0.0.1:4200`)

## Commands (Windows PowerShell)

```powershell
.\mvnw.cmd clean compile
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```
