package com.example.employeemanagement.service.impl;

import com.example.employeemanagement.dto.EmployeeCreateRequest;
import com.example.employeemanagement.dto.EmployeeUpdateRequest;
import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.entity.EmployeeStatus;
import com.example.employeemanagement.exception.EmployeeNotFoundException;
import com.example.employeemanagement.exception.EmailAlreadyExistsException;
import com.example.employeemanagement.mapper.EmployeeMapper;
import com.example.employeemanagement.repository.EmployeeRepository;
import com.example.employeemanagement.service.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private EmployeeCreateRequest createRequest;
    private EmployeeUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        employee = Employee.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .hireDate(LocalDate.of(2020, 1, 1))
                .jobTitle("Engineer")
                .department("Engineering")
                .salary(BigDecimal.valueOf(75000))
                .status(EmployeeStatus.ACTIVE)
                .build();

        createRequest = EmployeeCreateRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .hireDate(LocalDate.of(2020, 1, 1))
                .jobTitle("Engineer")
                .department("Engineering")
                .salary(BigDecimal.valueOf(75000))
                .status(EmployeeStatus.ACTIVE)
                .build();

        updateRequest = EmployeeUpdateRequest.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .build();
    }

    @Test
    void testCreateEmployee() {
        when(employeeRepository.findByEmailIgnoreCase(createRequest.getEmail())).thenReturn(null);
        when(employeeMapper.toEntity(createRequest)).thenReturn(employee);
        when(employeeRepository.save(employee)).thenReturn(employee);

        Employee result = employeeService.createEmployee(createRequest);

        assertEquals(employee, result);
        verify(employeeRepository).findByEmailIgnoreCase(createRequest.getEmail());
        verify(employeeMapper).toEntity(createRequest);
        verify(employeeRepository).save(employee);
    }

    @Test
    void testCreateEmployeeEmailExists() {
        when(employeeRepository.findByEmailIgnoreCase(createRequest.getEmail())).thenReturn(employee);

        assertThrows(EmailAlreadyExistsException.class, () -> employeeService.createEmployee(createRequest));
    }

    @Test
    void testGetEmployeeById() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        Employee result = employeeService.getEmployeeById(1L);

        assertEquals(employee, result);
    }

    @Test
    void testGetEmployeeByIdNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.getEmployeeById(1L));
    }

    @Test
    void testGetAllEmployees() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Employee> page = new PageImpl<>(List.of(employee));
        when(employeeRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<Employee> result = employeeService.getAllEmployees(pageable, null, null, null);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testUpdateEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.findByEmailIgnoreCase(updateRequest.getEmail())).thenReturn(null);
        when(employeeRepository.save(employee)).thenReturn(employee);

        Employee result = employeeService.updateEmployee(1L, updateRequest);

        assertEquals(employee, result);
        verify(employeeMapper).updateEntityFromRequest(updateRequest, employee);
    }

    @Test
    void testUpdateEmployeeNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.updateEmployee(1L, updateRequest));
    }

    @Test
    void testUpdateEmployeeEmailExists() {
        Employee existing = Employee.builder().id(2L).email("jane.doe@example.com").build();
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.findByEmailIgnoreCase(updateRequest.getEmail())).thenReturn(existing);

        assertThrows(EmailAlreadyExistsException.class, () -> employeeService.updateEmployee(1L, updateRequest));
    }

    @Test
    void testPartialUpdateEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.findByEmailIgnoreCase(updateRequest.getEmail())).thenReturn(null);
        when(employeeRepository.save(employee)).thenReturn(employee);

        Employee result = employeeService.partialUpdateEmployee(1L, updateRequest);

        assertEquals(employee, result);
        verify(employeeMapper).partialUpdateEntityFromRequest(updateRequest, employee);
    }

    @Test
    void testDeleteEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(employee)).thenReturn(employee);

        employeeService.deleteEmployee(1L);

        assertEquals(EmployeeStatus.INACTIVE, employee.getStatus());
        assertNotNull(employee.getDeletedAt());
        assertEquals("system", employee.getDeletedBy());
        verify(employeeRepository).save(employee);
        verify(auditService).auditEmployeeDelete(any(Employee.class), eq("system"));
    }

    @Test
    void testDeleteEmployeeNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.deleteEmployee(1L));
    }

    @Test
    void testRestoreEmployee() {
        // Setup employee as deleted
        employee.setStatus(EmployeeStatus.INACTIVE);
        employee.setDeletedAt(java.time.Instant.now());
        employee.setDeletedBy("system");
        
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(employee)).thenReturn(employee);

        Employee result = employeeService.restoreEmployee(1L);

        assertEquals(EmployeeStatus.ACTIVE, employee.getStatus());
        assertNull(employee.getDeletedAt());
        assertNull(employee.getDeletedBy());
        verify(employeeRepository).save(employee);
        verify(auditService).auditEmployeeRestore(any(Employee.class), eq(employee), eq("system"));
        assertEquals(employee, result);
    }

    @Test
    void testRestoreEmployeeNotDeleted() {
        // Employee is not deleted (deletedAt is null)
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        assertThrows(IllegalStateException.class, () -> employeeService.restoreEmployee(1L));
    }

    @Test
    void testRestoreEmployeeNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.restoreEmployee(1L));
    }

}
