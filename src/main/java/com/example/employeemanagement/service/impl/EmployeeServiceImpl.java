package com.example.employeemanagement.service.impl;

import co    @Override
    @Transactional
    public Employee partialUpdateEmployee(Long id, EmployeeUpdateRequest request) {
        Employee employee = getEmployeeById(id);
        if (request.getEmail() != null && !employee.getEmail().equalsIgnoreCase(request.getEmail()) && employeeRepository.findByEmailIgnoreCase(request.getEmail()) != null) {
            throw new EmailAlreadyExistsException("Email already exists: " + request.getEmail());
        }
        employeeMapper.partialUpdateEntityFromRequest(request, employee);
        return employeeRepository.save(employee);
    }employeemanagement.dto.EmployeeCreateRequest;
import com.example.employeemanagement.dto.EmployeeUpdateRequest;
import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.entity.EmployeeStatus;
import com.example.employeemanagement.exception.EmployeeNotFoundException;
import com.example.employeemanagement.exception.EmailAlreadyExistsException;
import com.example.employeemanagement.mapper.EmployeeMapper;
import com.example.employeemanagement.repository.EmployeeRepository;
import com.example.employeemanagement.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    @Transactional
    public Employee createEmployee(EmployeeCreateRequest request) {
        if (employeeRepository.findByEmailIgnoreCase(request.getEmail()) != null) {
            throw new EmailAlreadyExistsException("Email already exists: " + request.getEmail());
        }
        Employee employee = employeeMapper.toEntity(request);
        return employeeRepository.save(employee);
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));
    }

    @Override
    public Page<Employee> getAllEmployees(Pageable pageable, String department, EmployeeStatus status, String search) {
        Specification<Employee> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (department != null) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("department")), department.toLowerCase()));
            }
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
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
        Employee employee = getEmployeeById(id);
        if (request.getEmail() != null && !employee.getEmail().equalsIgnoreCase(request.getEmail()) && employeeRepository.findByEmailIgnoreCase(request.getEmail()) != null) {
            throw new EmailAlreadyExistsException("Email already exists: " + request.getEmail());
        }
        employeeMapper.updateEntityFromRequest(request, employee);
        return employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public Employee partialUpdateEmployee(Long id, EmployeeUpdateRequest request) {
        Employee employee = getEmployeeById(id);
        if (request.getEmail() != null && !employee.getEmail().equalsIgnoreCase(request.getEmail()) && employeeRepository.findByEmailIgnoreCase(request.getEmail()) != null) {
            throw new RuntimeException("Email already exists");
        }
        employeeMapper.partialUpdateEntityFromRequest(request, employee);
        return employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = getEmployeeById(id);
        employee.setStatus(EmployeeStatus.INACTIVE);
        employeeRepository.save(employee);
    }

}
