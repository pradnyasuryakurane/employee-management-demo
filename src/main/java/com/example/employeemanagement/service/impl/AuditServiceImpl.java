package com.example.employeemanagement.service.impl;

import com.example.employeemanagement.entity.AuditType;
import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.entity.EmployeeAudit;
import com.example.employeemanagement.repository.EmployeeAuditRepository;
import com.example.employeemanagement.service.AuditService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditServiceImpl implements AuditService {

    private final EmployeeAuditRepository auditRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void auditEmployeeAction(Employee beforeEmployee, Employee afterEmployee, AuditType auditType, String performedBy) {
        try {
            String beforeSnapshot = beforeEmployee != null ? objectMapper.writeValueAsString(createAuditSnapshot(beforeEmployee)) : null;
            String afterSnapshot = afterEmployee != null ? objectMapper.writeValueAsString(createAuditSnapshot(afterEmployee)) : null;

            EmployeeAudit audit = EmployeeAudit.builder()
                    .employeeId(afterEmployee != null ? afterEmployee.getId() : beforeEmployee.getId())
                    .auditType(auditType)
                    .performedBy(performedBy)
                    .beforeSnapshot(beforeSnapshot)
                    .afterSnapshot(afterSnapshot)
                    .description(generateDescription(auditType, beforeEmployee, afterEmployee))
                    .build();

            auditRepository.save(audit);
            log.debug("Audit record created: type={}, employeeId={}, performedBy={}", 
                     auditType, audit.getEmployeeId(), performedBy);
        } catch (JsonProcessingException e) {
            log.error("Error serializing employee data for audit", e);
            // We'll still create an audit record without snapshots
            EmployeeAudit audit = EmployeeAudit.builder()
                    .employeeId(afterEmployee != null ? afterEmployee.getId() : beforeEmployee.getId())
                    .auditType(auditType)
                    .performedBy(performedBy)
                    .description("Error serializing snapshots: " + generateDescription(auditType, beforeEmployee, afterEmployee))
                    .build();
            auditRepository.save(audit);
        }
    }

    @Override
    @Transactional
    public void auditEmployeeCreate(Employee employee, String performedBy) {
        auditEmployeeAction(null, employee, AuditType.CREATE, performedBy);
    }

    @Override
    @Transactional
    public void auditEmployeeDelete(Employee employee, String performedBy) {
        auditEmployeeAction(employee, employee, AuditType.DELETE, performedBy);
    }

    @Override
    @Transactional
    public void auditEmployeeRestore(Employee beforeEmployee, Employee afterEmployee, String performedBy) {
        auditEmployeeAction(beforeEmployee, afterEmployee, AuditType.RESTORE, performedBy);
    }

    private EmployeeAuditSnapshot createAuditSnapshot(Employee employee) {
        return EmployeeAuditSnapshot.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .dateOfBirth(employee.getDateOfBirth())
                .hireDate(employee.getHireDate())
                .jobTitle(employee.getJobTitle())
                .department(employee.getDepartment())
                .salary(employee.getSalary())
                .status(employee.getStatus())
                .deletedAt(employee.getDeletedAt())
                .deletedBy(employee.getDeletedBy())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }

    private String generateDescription(AuditType auditType, Employee beforeEmployee, Employee afterEmployee) {
        switch (auditType) {
            case CREATE:
                return "Employee created: " + afterEmployee.getFirstName() + " " + afterEmployee.getLastName();
            case UPDATE:
                return "Employee updated: " + afterEmployee.getFirstName() + " " + afterEmployee.getLastName();
            case DELETE:
                return "Employee soft deleted: " + afterEmployee.getFirstName() + " " + afterEmployee.getLastName();
            case RESTORE:
                return "Employee restored: " + afterEmployee.getFirstName() + " " + afterEmployee.getLastName();
            default:
                return "Unknown audit action";
        }
    }

}