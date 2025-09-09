package com.example.employeemanagement.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.*;

import com.example.employeemanagement.entity.EmployeeStatus;

import lombok.Data;

@Data
public class EmployeeCreateRequest {

    @NotBlank
    @Size(max = 50)
    private String firstName;

    @NotBlank
    @Size(max = 50)
    private String lastName;

    @NotBlank
    @Email
    private String email;

    private String phone;

    private LocalDate dateOfBirth;

    @NotNull
    private LocalDate hireDate;

    @NotBlank
    private String jobTitle;

    @NotBlank
    private String department;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal salary;

    @NotNull
    private EmployeeStatus status;

}
