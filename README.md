# Employee Management Demo

[![CI](https://github.com/pradnyasuryakurane/employee-management-demo/actions/workflows/ci.yml/badge.svg)](https://github.com/pradnyasuryakurane/employee-management-demo/actions/workflows/ci.yml)

A comprehensive Spring Boot application for managing employee data with RESTful APIs, JPA, and modern development practices.

## Overview

This project demonstrates a full-stack employee management system built with Spring Boot 3, Java 21, and PostgreSQL. It includes features like CRUD operations, search, filtering, pagination, validation, and comprehensive testing.

## Prerequisites

- **Java**: 21 or higher
- **Maven**: 3.9.1 or higher
- **Docker**: For containerized deployment (optional)

## Local Development

### Setup

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd employee-management-demo
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the application in development mode:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

The application will start on `http://localhost:8080`

### Database

In development mode, the application uses H2 in-memory database.

- **H2 Console**: http://localhost:8080/h2-console
- **JDBC URL**: jdbc:h2:mem:testdb
- **Username**: sa
- **Password**: (leave empty)

### API Documentation

- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs

## Docker Deployment

### Using Docker Compose

1. Build and run the services:
   ```bash
   docker-compose up --build
   ```

2. The application will be available at `http://localhost:8080`
3. PostgreSQL will be available at `localhost:5432`

### Environment Variables

Copy `.env.example` to `.env` and update the values:

```bash
cp .env.example .env
```

## API Documentation

### Base URL
`http://localhost:8080/api/v1/employees`

### Authentication
Currently, all endpoints are open. Security configuration is scaffolded for future JWT implementation.

### Endpoints

#### Create Employee
```http
POST /api/v1/employees
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phone": "+1-555-0123",
  "dateOfBirth": "1990-01-01",
  "hireDate": "2020-01-01",
  "jobTitle": "Software Engineer",
  "department": "Engineering",
  "salary": 75000.00,
  "status": "ACTIVE"
}
```

**Curl Example:**
```bash
curl -X POST http://localhost:8080/api/v1/employees \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "hireDate": "2020-01-01",
    "jobTitle": "Software Engineer",
    "department": "Engineering",
    "salary": 75000,
    "status": "ACTIVE"
  }'
```

#### Get All Employees
```http
GET /api/v1/employees?page=0&size=10&department=Engineering&status=ACTIVE&search=John
```

**Curl Example:**
```bash
curl "http://localhost:8080/api/v1/employees?page=0&size=10&department=Engineering"
```

#### Get Employee by ID
```http
GET /api/v1/employees/{id}
```

**Curl Example:**
```bash
curl http://localhost:8080/api/v1/employees/1
```

#### Update Employee (Full)
```http
PUT /api/v1/employees/{id}
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Smith",
  "email": "john.smith@example.com",
  "hireDate": "2020-01-01",
  "jobTitle": "Senior Engineer",
  "department": "Engineering",
  "salary": 85000,
  "status": "ACTIVE"
}
```

**Curl Example:**
```bash
curl -X PUT http://localhost:8080/api/v1/employees/1 \
  -H "Content-Type: application/json" \
  -d '{"firstName": "John", "lastName": "Smith", "email": "john.smith@example.com"}'
```

#### Update Employee (Partial)
```http
PATCH /api/v1/employees/{id}
Content-Type: application/json

{
  "jobTitle": "Lead Engineer",
  "salary": 95000
}
```

**Curl Example:**
```bash
curl -X PATCH http://localhost:8080/api/v1/employees/1 \
  -H "Content-Type: application/json" \
  -d '{"jobTitle": "Lead Engineer"}'
```

#### Delete Employee
```http
DELETE /api/v1/employees/{id}
```

**Curl Example:**
```bash
curl -X DELETE http://localhost:8080/api/v1/employees/1
```

### Response Format

#### Success Response
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phone": "+1-555-0123",
  "dateOfBirth": "1990-01-01",
  "hireDate": "2020-01-01",
  "jobTitle": "Software Engineer",
  "department": "Engineering",
  "salary": 75000.00,
  "status": "ACTIVE",
  "createdAt": "2023-01-01T10:00:00",
  "updatedAt": "2023-01-01T10:00:00"
}
```

#### Paged Response
```json
{
  "content": [...],
  "page": 0,
  "size": 10,
  "totalElements": 100,
  "totalPages": 10,
  "first": true,
  "last": false
}
```

#### Error Response
```json
{
  "timestamp": "2023-01-01T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/employees",
  "validationErrors": {
    "email": "Email should be valid",
    "firstName": "First name is required"
  }
}
```

## Frontend Integration Recommendations

### Recommended Frameworks
- **React**: With hooks and axios for API calls
- **Vue.js**: With Vue 3 Composition API
- **Angular**: With HttpClient

### Example Frontend Integration

#### React with Axios
```javascript
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/v1';

export const employeeService = {
  getAll: (params) => axios.get(`${API_BASE_URL}/employees`, { params }),
  create: (data) => axios.post(`${API_BASE_URL}/employees`, data),
  update: (id, data) => axios.put(`${API_BASE_URL}/employees/${id}`, data),
  delete: (id) => axios.delete(`${API_BASE_URL}/employees/${id}`)
};
```

#### Vue.js with Fetch
```javascript
const API_BASE_URL = 'http://localhost:8080/api/v1';

export default {
  async getEmployees(params = {}) {
    const query = new URLSearchParams(params).toString();
    const response = await fetch(`${API_BASE_URL}/employees?${query}`);
    return response.json();
  }
}
```

### CORS Configuration
The backend is configured to allow requests from `http://localhost:3000` for frontend development.

## Testing

### Unit Tests
```bash
mvn test -Dtest=*Test
```

### Integration Tests
```bash
mvn test -Dtest=*IntegrationTest
```

### Test Coverage
Run with JaCoCo:
```bash
mvn clean test jacoco:report
```

## Project Structure

```
employee-management-demo/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/employeemanagement/
│   │   │       ├── config/
│   │   │       ├── controller/
│   │   │       ├── dto/
│   │   │       ├── entity/
│   │   │       ├── exception/
│   │   │       ├── mapper/
│   │   │       ├── repository/
│   │   │       └── service/
│   │   └── resources/
│   │       ├── db/migration/
│   │       └── application*.properties
│   └── test/
│       └── java/
│           └── com/example/employeemanagement/
├── Dockerfile
├── docker-compose.yml
├── .env.example
└── README.md
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the MIT License.
