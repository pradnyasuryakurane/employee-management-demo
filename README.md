# Employee Management Demo

[![CI](https://github.com/pradnyasuryakurane/employee-management-demo/actions/workflows/ci.yml/badge.svg)](https://github.com/pradnyasuryakurane/employee-management-demo/actions/workflows/ci.yml)

A comprehensive Spring Boot application for managing employee data with RESTful APIs, JPA, and modern development practices.

## Overview

This project demonstrates a full-stack employee management system built with Spring Boot 3, Java 21, and PostgreSQL. It includes features like CRUD operations, soft-delete with restore capability, comprehensive audit history, search, filtering, pagination, validation, and comprehensive testing.

## Key Features

### Soft Delete & Restore
- **Soft Delete**: Employee records are marked as `INACTIVE` instead of being permanently deleted, preserving data integrity
- **Restore Capability**: Soft-deleted employees can be restored back to `ACTIVE` status
- **Audit Trail**: All delete and restore operations are logged with timestamps and user information
- **Backward Compatible**: Existing delete endpoint behavior is preserved (now performs soft-delete)

### Comprehensive Audit History
- **Complete Tracking**: Records all CRUD operations (CREATE, UPDATE, DELETE, RESTORE)
- **Before/After Snapshots**: Stores JSON snapshots of entity state changes for complete traceability
- **Metadata Capture**: Tracks who performed the action, when it occurred, and operation descriptions
- **Dedicated Storage**: Separate `employee_audit` table with proper indexing for performance

### Enhanced Filtering
- **Include Inactive Parameter**: Optional `includeInactive` parameter controls visibility of soft-deleted records
- **Default Behavior**: `includeInactive=false` by default maintains backward compatibility
- **Flexible Integration**: Works seamlessly with existing filters (department, status, search)

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
GET /api/v1/employees?page=0&size=10&department=Engineering&status=ACTIVE&search=John&includeInactive=false
```

**Parameters:**
- `page` (optional): Page number for pagination (default: 0)
- `size` (optional): Page size for pagination (default: 10)
- `department` (optional): Filter by department
- `status` (optional): Filter by employee status (ACTIVE/INACTIVE)
- `search` (optional): Search in first name, last name, or email
- `includeInactive` (optional): Include soft-deleted employees (default: false)

**Curl Example:**
```bash
# Get active employees only (default behavior)
curl "http://localhost:8080/api/v1/employees?page=0&size=10&department=Engineering"

# Get all employees including soft-deleted ones
curl "http://localhost:8080/api/v1/employees?includeInactive=true"
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

#### Delete Employee (Soft Delete)
```http
DELETE /api/v1/employees/{id}
```

**Note:** This performs a soft delete - the employee record is marked as `INACTIVE` with `status=INACTIVE`, `deletedAt` timestamp, and `deletedBy` information. The record is preserved in the database and can be restored.

**Curl Example:**
```bash
curl -X DELETE http://localhost:8080/api/v1/employees/1
```

#### Restore Employee
```http
PUT /api/v1/employees/{id}/restore
```

**Description:** Restores a soft-deleted employee back to `ACTIVE` status. Returns 400 if the employee is not currently deleted.

**Curl Example:**
```bash
curl -X PUT http://localhost:8080/api/v1/employees/1/restore
```

**Response:**
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "status": "ACTIVE",
  "deletedAt": null,
  "deletedBy": null,
  "createdAt": "2023-01-01T10:00:00",
  "updatedAt": "2023-01-01T15:30:00"
}
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
  "updatedAt": "2023-01-01T10:00:00",
  "deletedAt": null,
  "deletedBy": null
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

## Audit History

The system maintains a comprehensive audit trail for all employee operations:

### Audit Types Tracked
- **CREATE**: New employee records
- **UPDATE**: Changes to existing employee data  
- **DELETE**: Soft delete operations
- **RESTORE**: Restore operations for soft-deleted employees

### Audit Data Structure
Each audit entry contains:
- **Employee ID**: Reference to the affected employee
- **Audit Type**: Type of operation performed
- **Performed By**: User who performed the action
- **Performed At**: Timestamp of the operation
- **Before Snapshot**: JSON representation of the entity state before the change
- **After Snapshot**: JSON representation of the entity state after the change
- **Description**: Human-readable description of the operation

### Database Schema
```sql
CREATE TABLE employee_audit (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    audit_type VARCHAR(20) NOT NULL,
    performed_by VARCHAR(100),
    performed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    before_snapshot TEXT,
    after_snapshot TEXT,
    description VARCHAR(500)
);
```

## Soft Delete Implementation

### Database Changes
The employee table includes additional columns for soft delete functionality:
```sql
ALTER TABLE employees ADD COLUMN deleted_at TIMESTAMP;
ALTER TABLE employees ADD COLUMN deleted_by VARCHAR(100);
```

### Behavior
- **List Operations**: By default, only active employees are returned
- **Get by ID**: Returns employee regardless of delete status  
- **Delete Operation**: Sets `status=INACTIVE`, records `deletedAt` and `deletedBy`
- **Restore Operation**: Reverts `status=ACTIVE`, clears `deletedAt` and `deletedBy`

### Examples

#### Soft Delete Flow
```bash
# 1. Delete an employee (soft delete)
curl -X DELETE http://localhost:8080/api/v1/employees/1

# 2. List employees (won't include deleted employee)  
curl "http://localhost:8080/api/v1/employees"

# 3. List all employees including deleted
curl "http://localhost:8080/api/v1/employees?includeInactive=true" 

# 4. Restore the deleted employee
curl -X PUT http://localhost:8080/api/v1/employees/1/restore
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
  // Get all employees with optional includeInactive parameter
  getAll: (params = {}) => axios.get(`${API_BASE_URL}/employees`, { params }),
  
  // Get employee by ID
  getById: (id) => axios.get(`${API_BASE_URL}/employees/${id}`),
  
  // Create new employee
  create: (data) => axios.post(`${API_BASE_URL}/employees`, data),
  
  // Update employee
  update: (id, data) => axios.put(`${API_BASE_URL}/employees/${id}`, data),
  
  // Soft delete employee
  delete: (id) => axios.delete(`${API_BASE_URL}/employees/${id}`),
  
  // Restore soft-deleted employee
  restore: (id) => axios.put(`${API_BASE_URL}/employees/${id}/restore`),
  
  // Export employees with includeInactive option
  export: (params = {}) => axios.get(`${API_BASE_URL}/employees/export`, { 
    params, 
    responseType: 'blob' 
  })
};

// Usage examples
employeeService.getAll({ includeInactive: true, department: 'Engineering' });
employeeService.restore(1);
```

#### Vue.js with Fetch
```javascript
const API_BASE_URL = 'http://localhost:8080/api/v1';

export default {
  async getEmployees(params = {}) {
    const query = new URLSearchParams(params).toString();
    const response = await fetch(`${API_BASE_URL}/employees?${query}`);
    return response.json();
  },
  
  async restoreEmployee(id) {
    const response = await fetch(`${API_BASE_URL}/employees/${id}/restore`, {
      method: 'PUT'
    });
    return response.json();
  },
  
  async deleteEmployee(id) {
    const response = await fetch(`${API_BASE_URL}/employees/${id}`, {
      method: 'DELETE'
    });
    return response.ok;
  }
}

// Usage examples
// Get active employees only
await getEmployees({ department: 'Engineering' });

// Get all employees including deleted
await getEmployees({ includeInactive: true });

// Restore employee
await restoreEmployee(1);
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
│   │   │       │   ├── Employee.java
│   │   │       │   ├── EmployeeAudit.java
│   │   │       │   └── AuditType.java
│   │   │       ├── exception/
│   │   │       ├── mapper/
│   │   │       ├── repository/
│   │   │       │   ├── EmployeeRepository.java
│   │   │       │   └── EmployeeAuditRepository.java
│   │   │       └── service/
│   │   │           ├── EmployeeService.java
│   │   │           ├── AuditService.java
│   │   │           └── impl/
│   │   │               ├── EmployeeServiceImpl.java
│   │   │               └── AuditServiceImpl.java
│   │   └── resources/
│   │       ├── db/migration/
│   │       │   ├── V1__initial_schema.sql
│   │       │   └── V2__add_soft_delete_and_audit.sql
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
