package com.example.employeemanagement.controller;

import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.entity.EmployeeStatus;
import com.example.employeemanagement.repository.EmployeeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
        employee = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .hireDate(LocalDate.of(2020, 1, 1))
                .jobTitle("Engineer")
                .department("Engineering")
                .salary(BigDecimal.valueOf(75000))
                .status(EmployeeStatus.ACTIVE)
                .build();
        employee = employeeRepository.save(employee);
    }

    @Test
    void testCreateEmployee() throws Exception {
        String json = """
                {
                    "firstName": "Jane",
                    "lastName": "Smith",
                    "email": "jane.smith@example.com",
                    "hireDate": "2021-01-01",
                    "jobTitle": "Manager",
                    "department": "HR",
                    "salary": 80000,
                    "status": "ACTIVE"
                }
                """;

        mockMvc.perform(post("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.email").value("jane.smith@example.com"));
    }

    @Test
    void testCreateEmployeeValidationError() throws Exception {
        String json = """
                {
                    "firstName": "",
                    "lastName": "Smith",
                    "email": "invalid-email",
                    "hireDate": "2021-01-01",
                    "jobTitle": "Manager",
                    "department": "HR",
                    "salary": 80000,
                    "status": "ACTIVE"
                }
                """;

        mockMvc.perform(post("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.firstName").exists());
    }

    @Test
    void testGetEmployeeById() throws Exception {
        mockMvc.perform(get("/api/v1/employees/{id}", employee.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void testGetEmployeeByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/employees/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Employee not found with id: 999"));
    }

    @Test
    void testGetAllEmployees() throws Exception {
        mockMvc.perform(get("/api/v1/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void testUpdateEmployee() throws Exception {
        String json = """
                {
                    "firstName": "John",
                    "lastName": "Updated",
                    "email": "john.updated@example.com",
                    "hireDate": "2020-01-01",
                    "jobTitle": "Senior Engineer",
                    "department": "Engineering",
                    "salary": 85000,
                    "status": "ACTIVE"
                }
                """;

        mockMvc.perform(put("/api/v1/employees/{id}", employee.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Updated"));
    }

    @Test
    void testPartialUpdateEmployee() throws Exception {
        String json = """
                {
                    "lastName": "PartiallyUpdated"
                }
                """;

        mockMvc.perform(patch("/api/v1/employees/{id}", employee.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("PartiallyUpdated"));
    }

    @Test
    void testDeleteEmployee() throws Exception {
        mockMvc.perform(delete("/api/v1/employees/{id}", employee.getId()))
                .andExpect(status().isNoContent());

        // Verify soft delete
        Employee deleted = employeeRepository.findById(employee.getId()).orElseThrow();
        assert deleted.getStatus().equals(EmployeeStatus.INACTIVE);
    }

}
