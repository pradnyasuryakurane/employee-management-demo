package com.example.employeemanagement.service.impl;

import com.example.employeemanagement.entity.EmployeeStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class EmployeeAuditSnapshot {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private LocalDate hireDate;
    private String jobTitle;
    private String department;
    private BigDecimal salary;
    private EmployeeStatus status;
    private Instant deletedAt;
    private String deletedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}