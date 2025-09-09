package com.example.employeemanagement.controller;

import com.example.employeemanagement.dto.EmployeeCreateRequest;
import com.example.employeemanagement.dto.EmployeeResponse;
import com.example.employeemanagement.dto.EmployeeUpdateRequest;
import com.example.employeemanagement.dto.PagedResponse;
import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.entity.EmployeeStatus;
import com.example.employeemanagement.mapper.EmployeeMapper;
import com.example.employeemanagement.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.opencsv.CSVWriter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Tag(name = "Employee Management", description = "APIs for managing employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeMapper employeeMapper;

    @PostMapping
    @Operation(summary = "Create a new employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Employee created"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeCreateRequest request) {
        Employee employee = employeeService.createEmployee(request);
        EmployeeResponse response = employeeMapper.toResponse(employee);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(employee.getId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all employees with pagination and filtering")
    @ApiResponse(responseCode = "200", description = "List of employees")
    public ResponseEntity<PagedResponse<EmployeeResponse>> getAllEmployees(
            Pageable pageable,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) EmployeeStatus status,
            @RequestParam(required = false) String search) {
        Page<Employee> employees = employeeService.getAllEmployees(pageable, department, status, search);
        Page<EmployeeResponse> responses = employees.map(employeeMapper::toResponse);
        PagedResponse<EmployeeResponse> pagedResponse = PagedResponse.of(responses);
        return ResponseEntity.ok(pagedResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee found"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        Employee employee = employeeService.getEmployeeById(id);
        EmployeeResponse response = employeeMapper.toResponse(employee);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update employee fully")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee updated"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<EmployeeResponse> updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeUpdateRequest request) {
        Employee employee = employeeService.updateEmployee(id, request);
        EmployeeResponse response = employeeMapper.toResponse(employee);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update employee partially")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee updated"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<EmployeeResponse> partialUpdateEmployee(@PathVariable Long id, @RequestBody EmployeeUpdateRequest request) {
        Employee employee = employeeService.partialUpdateEmployee(id, request);
        EmployeeResponse response = employeeMapper.toResponse(employee);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete employee (soft delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Employee deleted"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/export")
    @Operation(summary = "Export employees to CSV")
    @ApiResponse(responseCode = "200", description = "CSV file")
    public ResponseEntity<byte[]> exportEmployees(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) EmployeeStatus status,
            @RequestParam(required = false) String search) {
        List<Employee> employees = employeeService.getAllEmployees(Pageable.unpaged(), department, status, search).getContent();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(outputStream))) {
            // Header
            writer.writeNext(new String[]{"ID", "First Name", "Last Name", "Email", "Phone", "Date of Birth", "Hire Date", "Job Title", "Department", "Salary", "Status", "Created At", "Updated At"});

            // Data
            for (Employee emp : employees) {
                writer.writeNext(new String[]{
                        emp.getId().toString(),
                        emp.getFirstName(),
                        emp.getLastName(),
                        emp.getEmail(),
                        emp.getPhone() != null ? emp.getPhone() : "",
                        emp.getDateOfBirth() != null ? emp.getDateOfBirth().toString() : "",
                        emp.getHireDate().toString(),
                        emp.getJobTitle(),
                        emp.getDepartment(),
                        emp.getSalary().toString(),
                        emp.getStatus().toString(),
                        emp.getCreatedAt().toString(),
                        emp.getUpdatedAt().toString()
                });
            }
        } catch (Exception e) {
            throw new RuntimeException("Error generating CSV", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "employees.csv");

        return ResponseEntity.ok()
                .headers(headers)
                .body(outputStream.toByteArray());
    }

}
