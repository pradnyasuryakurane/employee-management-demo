package com.example.employeemanagement.service.impl;

import com.example.employeemanagement.dto.EmployeeCreateRequest;
import com.example.employeemanagement.dto.EmployeeUpdateRequest;
import com.example.employeemanagement.entity.AuditType;
import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.entity.EmployeeStatus;
import com.example.employeemanagement.exception.EmployeeNotFoundException;
import com.example.employeemanagement.exception.EmailAlreadyExistsException;
import com.example.employeemanagement.mapper.EmployeeMapper;
import com.example.employeemanagement.repository.EmployeeRepository;
import com.example.employeemanagement.service.AuditService;
import com.example.employeemanagement.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final AuditService auditService;

    @Override
    @Transactional
    public Employee createEmployee(EmployeeCreateRequest request) {
        if (employeeRepository.findByEmailIgnoreCase(request.getEmail()) != null) {
            throw new EmailAlreadyExistsException("Email already exists: " + request.getEmail());
        }
        Employee employee = employeeMapper.toEntity(request);
        Employee savedEmployee = employeeRepository.save(employee);
        
        // Audit the creation
        auditService.auditEmployeeCreate(savedEmployee, getCurrentUser());
        
        return savedEmployee;
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));
    }

    @Override
    public Page<Employee> getAllEmployees(Pageable pageable, String department, EmployeeStatus status, String search) {
        return getAllEmployees(pageable, department, status, search, false);
    }

    @Override
    public Page<Employee> getAllEmployees(Pageable pageable, String department, EmployeeStatus status, String search, boolean includeInactive) {
        Specification<Employee> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (department != null) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("department")), department.toLowerCase()));
            }
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            if (!includeInactive) {
                // Only show active employees (not soft-deleted) by default
                predicates.add(criteriaBuilder.isNull(root.get("deletedAt")));
            }
            if (search != null) {
                Predicate firstName = criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + search.toLowerCase() + "%");
                Predicate lastName = criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + search.toLowerCase() + "%");
                Predicate email = criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + search.toLowerCase() + "%");
                predicates.add(criteriaBuilder.or(firstName, lastName, email));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        return employeeRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional
    public Employee updateEmployee(Long id, EmployeeUpdateRequest request) {
        Employee beforeEmployee = getEmployeeById(id);
        if (request.getEmail() != null && !beforeEmployee.getEmail().equalsIgnoreCase(request.getEmail()) && employeeRepository.findByEmailIgnoreCase(request.getEmail()) != null) {
            throw new EmailAlreadyExistsException("Email already exists: " + request.getEmail());
        }
        
        // Create a copy for audit purposes
        Employee beforeCopy = createEmployeeCopy(beforeEmployee);
        
        employeeMapper.updateEntityFromRequest(request, beforeEmployee);
        Employee savedEmployee = employeeRepository.save(beforeEmployee);
        
        // Audit the update
        auditService.auditEmployeeAction(beforeCopy, savedEmployee, AuditType.UPDATE, getCurrentUser());
        
        return savedEmployee;
    }

    @Override
    @Transactional
    public Employee partialUpdateEmployee(Long id, EmployeeUpdateRequest request) {
        Employee beforeEmployee = getEmployeeById(id);
        if (request.getEmail() != null && !beforeEmployee.getEmail().equalsIgnoreCase(request.getEmail()) && employeeRepository.findByEmailIgnoreCase(request.getEmail()) != null) {
            throw new EmailAlreadyExistsException("Email already exists: " + request.getEmail());
        }
        
        // Create a copy for audit purposes
        Employee beforeCopy = createEmployeeCopy(beforeEmployee);
        
        employeeMapper.partialUpdateEntityFromRequest(request, beforeEmployee);
        Employee savedEmployee = employeeRepository.save(beforeEmployee);
        
        // Audit the update
        auditService.auditEmployeeAction(beforeCopy, savedEmployee, AuditType.UPDATE, getCurrentUser());
        
        return savedEmployee;
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = getEmployeeById(id);
        
        // Create a copy for audit purposes (before soft-delete)
        Employee beforeCopy = createEmployeeCopy(employee);
        
        // Soft delete: set status to INACTIVE and record deletion metadata
        employee.setStatus(EmployeeStatus.INACTIVE);
        employee.setDeletedAt(Instant.now());
        employee.setDeletedBy(getCurrentUser());
        
        Employee savedEmployee = employeeRepository.save(employee);
        
        // Audit the deletion
        auditService.auditEmployeeDelete(beforeCopy, getCurrentUser());
    }

    @Override
    @Transactional
    public Employee restoreEmployee(Long id) {
        Employee employee = getEmployeeById(id);
        
        if (employee.getDeletedAt() == null) {
            throw new IllegalStateException("Employee is not deleted and cannot be restored: " + id);
        }
        
        // Create a copy for audit purposes (before restore)
        Employee beforeCopy = createEmployeeCopy(employee);
        
        // Restore: set status to ACTIVE and clear deletion metadata
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setDeletedAt(null);
        employee.setDeletedBy(null);
        
        Employee savedEmployee = employeeRepository.save(employee);
        
        // Audit the restoration
        auditService.auditEmployeeRestore(beforeCopy, savedEmployee, getCurrentUser());
        
        return savedEmployee;
    }

    private Employee createEmployeeCopy(Employee original) {
        return Employee.builder()
                .id(original.getId())
                .firstName(original.getFirstName())
                .lastName(original.getLastName())
                .email(original.getEmail())
                .phone(original.getPhone())
                .dateOfBirth(original.getDateOfBirth())
                .hireDate(original.getHireDate())
                .jobTitle(original.getJobTitle())
                .department(original.getDepartment())
                .salary(original.getSalary())
                .status(original.getStatus())
                .deletedAt(original.getDeletedAt())
                .deletedBy(original.getDeletedBy())
                .createdAt(original.getCreatedAt())
                .updatedAt(original.getUpdatedAt())
                .build();
    }

    private String getCurrentUser() {
        // For now, return "system" - in a real application, this would get the current authenticated user
        return "system";
    }

}