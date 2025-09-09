package com.example.employeemanagement.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.*;

import com.example.employeemanagement.entity.EmployeeStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeUpdateRequest {

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @Email
    private String email;

    private String phone;

    private LocalDate dateOfBirth;

    private LocalDate hireDate;

    private String jobTitle;

    private String department;

    @DecimalMin("0.0")
    private BigDecimal salary;

    private EmployeeStatus status;

}
