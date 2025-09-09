package com.example.employeemanagement.repository;

import com.example.employeemanagement.entity.EmployeeAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmployeeAuditRepository extends JpaRepository<EmployeeAudit, Long> {

    List<EmployeeAudit> findByEmployeeIdOrderByPerformedAtDesc(Long employeeId);

}