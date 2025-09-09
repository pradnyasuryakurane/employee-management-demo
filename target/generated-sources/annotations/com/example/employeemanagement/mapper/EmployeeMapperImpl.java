package com.example.employeemanagement.mapper;

import com.example.employeemanagement.dto.EmployeeCreateRequest;
import com.example.employeemanagement.dto.EmployeeResponse;
import com.example.employeemanagement.dto.EmployeeUpdateRequest;
import com.example.employeemanagement.entity.Employee;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-09T09:31:34+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.16 (Eclipse Adoptium)"
)
@Component
public class EmployeeMapperImpl implements EmployeeMapper {

    @Override
    public Employee toEntity(EmployeeCreateRequest request) {
        if ( request == null ) {
            return null;
        }

        Employee.EmployeeBuilder employee = Employee.builder();

        employee.firstName( request.getFirstName() );
        employee.lastName( request.getLastName() );
        employee.email( request.getEmail() );
        employee.phone( request.getPhone() );
        employee.dateOfBirth( request.getDateOfBirth() );
        employee.hireDate( request.getHireDate() );
        employee.jobTitle( request.getJobTitle() );
        employee.department( request.getDepartment() );
        employee.salary( request.getSalary() );
        employee.status( request.getStatus() );

        return employee.build();
    }

    @Override
    public EmployeeResponse toResponse(Employee entity) {
        if ( entity == null ) {
            return null;
        }

        EmployeeResponse employeeResponse = new EmployeeResponse();

        employeeResponse.setId( entity.getId() );
        employeeResponse.setFirstName( entity.getFirstName() );
        employeeResponse.setLastName( entity.getLastName() );
        employeeResponse.setEmail( entity.getEmail() );
        employeeResponse.setPhone( entity.getPhone() );
        employeeResponse.setDateOfBirth( entity.getDateOfBirth() );
        employeeResponse.setHireDate( entity.getHireDate() );
        employeeResponse.setJobTitle( entity.getJobTitle() );
        employeeResponse.setDepartment( entity.getDepartment() );
        employeeResponse.setSalary( entity.getSalary() );
        employeeResponse.setStatus( entity.getStatus() );
        employeeResponse.setCreatedAt( entity.getCreatedAt() );
        employeeResponse.setUpdatedAt( entity.getUpdatedAt() );

        return employeeResponse;
    }

    @Override
    public void updateEntityFromRequest(EmployeeUpdateRequest request, Employee employee) {
        if ( request == null ) {
            return;
        }

        if ( request.getFirstName() != null ) {
            employee.setFirstName( request.getFirstName() );
        }
        if ( request.getLastName() != null ) {
            employee.setLastName( request.getLastName() );
        }
        if ( request.getEmail() != null ) {
            employee.setEmail( request.getEmail() );
        }
        if ( request.getPhone() != null ) {
            employee.setPhone( request.getPhone() );
        }
        if ( request.getDateOfBirth() != null ) {
            employee.setDateOfBirth( request.getDateOfBirth() );
        }
        if ( request.getHireDate() != null ) {
            employee.setHireDate( request.getHireDate() );
        }
        if ( request.getJobTitle() != null ) {
            employee.setJobTitle( request.getJobTitle() );
        }
        if ( request.getDepartment() != null ) {
            employee.setDepartment( request.getDepartment() );
        }
        if ( request.getSalary() != null ) {
            employee.setSalary( request.getSalary() );
        }
        if ( request.getStatus() != null ) {
            employee.setStatus( request.getStatus() );
        }
    }

    @Override
    public void partialUpdateEntityFromRequest(EmployeeUpdateRequest request, Employee employee) {
        if ( request == null ) {
            return;
        }

        if ( request.getFirstName() != null ) {
            employee.setFirstName( request.getFirstName() );
        }
        if ( request.getLastName() != null ) {
            employee.setLastName( request.getLastName() );
        }
        if ( request.getEmail() != null ) {
            employee.setEmail( request.getEmail() );
        }
        if ( request.getPhone() != null ) {
            employee.setPhone( request.getPhone() );
        }
        if ( request.getDateOfBirth() != null ) {
            employee.setDateOfBirth( request.getDateOfBirth() );
        }
        if ( request.getHireDate() != null ) {
            employee.setHireDate( request.getHireDate() );
        }
        if ( request.getJobTitle() != null ) {
            employee.setJobTitle( request.getJobTitle() );
        }
        if ( request.getDepartment() != null ) {
            employee.setDepartment( request.getDepartment() );
        }
        if ( request.getSalary() != null ) {
            employee.setSalary( request.getSalary() );
        }
        if ( request.getStatus() != null ) {
            employee.setStatus( request.getStatus() );
        }
    }
}
