package com.example.employeemanagement.config;

import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.entity.EmployeeStatus;
import com.example.employeemanagement.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;

    @Override
    public void run(String... args) throws Exception {
        if (employeeRepository.count() == 0) {
            List<Employee> employees = new ArrayList<>();
            Random random = new Random();
            String[] firstNames = {"John", "Jane", "Alice", "Bob", "Charlie", "Diana", "Eve", "Frank", "Grace", "Henry"};
            String[] lastNames = {"Doe", "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez"};
            String[] departments = {"Engineering", "HR", "Finance", "Marketing", "Sales", "Operations", "IT", "Legal", "Support", "R&D"};
            String[] jobTitles = {"Engineer", "Manager", "Analyst", "Developer", "Consultant", "Specialist", "Coordinator", "Director", "Assistant", "Lead"};

            for (int i = 1; i <= 100; i++) {
                String firstName = firstNames[random.nextInt(firstNames.length)];
                String lastName = lastNames[random.nextInt(lastNames.length)];
                String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + i + "@example.com";
                String department = departments[random.nextInt(departments.length)];
                String jobTitle = jobTitles[random.nextInt(jobTitles.length)] + " " + department;
                BigDecimal salary = BigDecimal.valueOf(50000 + random.nextInt(100000));
                LocalDate hireDate = LocalDate.of(2020 + random.nextInt(5), 1 + random.nextInt(12), 1 + random.nextInt(28));
                LocalDate dateOfBirth = LocalDate.of(1980 + random.nextInt(20), 1 + random.nextInt(12), 1 + random.nextInt(28));
                String phone = String.format("(%03d) %03d-%04d", random.nextInt(1000), random.nextInt(1000), random.nextInt(10000));

                Employee employee = Employee.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .email(email)
                        .phone(phone)
                        .dateOfBirth(dateOfBirth)
                        .hireDate(hireDate)
                        .jobTitle(jobTitle)
                        .department(department)
                        .salary(salary)
                        .status(EmployeeStatus.ACTIVE)
                        .build();

                employees.add(employee);
            }

            employeeRepository.saveAll(employees);
        }
    }

}
