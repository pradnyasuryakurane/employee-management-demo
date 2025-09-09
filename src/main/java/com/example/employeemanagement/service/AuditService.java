package com.example.employeemanagement.service;

import com.example.employeemanagement.entity.AuditType;
import com.example.employeemanagement.entity.Employee;

public interface AuditService {

    void auditEmployeeAction(Employee beforeEmployee, Employee afterEmployee, AuditType auditType, String performedBy);

    void auditEmployeeCreate(Employee employee, String performedBy);

    void auditEmployeeDelete(Employee employee, String performedBy);

    void auditEmployeeRestore(Employee beforeEmployee, Employee afterEmployee, String performedBy);

}