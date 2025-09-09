package com.example.employeemanagement.mapper;

import com.example.employeemanagement.dto.EmployeeCreateRequest;
import com.example.employeemanagement.dto.EmployeeResponse;
import com.example.employeemanagement.dto.EmployeeUpdateRequest;
import com.example.employeemanagement.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmployeeMapper {

    Employee toEntity(EmployeeCreateRequest request);

    EmployeeResponse toResponse(Employee entity);

    void updateEntityFromRequest(EmployeeUpdateRequest request, @MappingTarget Employee employee);

    void partialUpdateEntityFromRequest(EmployeeUpdateRequest request, @MappingTarget Employee employee);

}
