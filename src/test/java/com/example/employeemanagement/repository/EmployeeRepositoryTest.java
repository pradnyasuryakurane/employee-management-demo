package com.example.employeemanagement.repository;

import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.entity.EmployeeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee employee1;
    private Employee employee2;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();

        employee1 = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .hireDate(LocalDate.of(2020, 1, 1))
                .jobTitle("Engineer")
                .department("Engineering")
                .salary(BigDecimal.valueOf(75000))
                .status(EmployeeStatus.ACTIVE)
                .build();

        employee2 = Employee.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .hireDate(LocalDate.of(2021, 1, 1))
                .jobTitle("Manager")
                .department("HR")
                .salary(BigDecimal.valueOf(80000))
                .status(EmployeeStatus.INACTIVE)
                .build();

        employeeRepository.save(employee1);
        employeeRepository.save(employee2);
    }

    @Test
    void testFindByStatus() {
        List<Employee> activeEmployees = employeeRepository.findByStatus(EmployeeStatus.ACTIVE);
        List<Employee> inactiveEmployees = employeeRepository.findByStatus(EmployeeStatus.INACTIVE);

        assertThat(activeEmployees).hasSize(1);
        assertThat(activeEmployees.get(0).getFirstName()).isEqualTo("John");

        assertThat(inactiveEmployees).hasSize(1);
        assertThat(inactiveEmployees.get(0).getFirstName()).isEqualTo("Jane");
    }

    @Test
    void testFindByDepartmentIgnoreCase() {
        List<Employee> engineeringEmployees = employeeRepository.findByDepartmentIgnoreCase("engineering");
        List<Employee> hrEmployees = employeeRepository.findByDepartmentIgnoreCase("hr");

        assertThat(engineeringEmployees).hasSize(1);
        assertThat(hrEmployees).hasSize(1);
    }

    @Test
    void testFindByEmailIgnoreCase() {
        Employee found = employeeRepository.findByEmailIgnoreCase("JOHN.DOE@EXAMPLE.COM");

        assertThat(found).isNotNull();
        assertThat(found.getFirstName()).isEqualTo("John");
    }

    @Test
    void testFindByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase() {
        List<Employee> results = employeeRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase("john", "smith");

        assertThat(results).hasSize(2);
    }

}
