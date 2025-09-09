package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.EmployeeCreateRequest;
import com.example.employeemanagement.dto.EmployeeUpdateRequest;
import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.entity.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {

    Employee createEmployee(EmployeeCreateRequest request);

    Employee getEmployeeById(Long id);

    Page<Employee> getAllEmployees(Pageable pageable, String department, EmployeeStatus status, String search);

    Page<Employee> getAllEmployees(Pageable pageable, String department, EmployeeStatus status, String search, boolean includeInactive);

    Employee updateEmployee(Long id, EmployeeUpdateRequest request);

    Employee partialUpdateEmployee(Long id, EmployeeUpdateRequest request);

    void deleteEmployee(Long id);

    Employee restoreEmployee(Long id);

}
