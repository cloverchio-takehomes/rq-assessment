package com.reliaquest.api.util;

import com.reliaquest.api.dto.EmployeeDTO;
import com.reliaquest.api.dto.MockEmployeeDTO;
import com.reliaquest.api.model.Employee;

public class EmployeeTransformer {

    public static Employee toEmployee(MockEmployeeDTO mockEmployeeDTO) {
        Employee employee = new Employee();
        employee.setId(mockEmployeeDTO.id());
        employee.setName(mockEmployeeDTO.name());
        employee.setAge(mockEmployeeDTO.age());
        employee.setSalary(mockEmployeeDTO.salary());
        employee.setTitle(mockEmployeeDTO.title());
        employee.setEmail(mockEmployeeDTO.email());
        return employee;
    }

    public static Employee toEmployee(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        employee.setId(employee.getId());
        employee.setName(employeeDTO.name());
        employee.setAge(employeeDTO.age());
        employee.setSalary(employeeDTO.salary());
        employee.setTitle(employeeDTO.title());
        employee.setEmail(employeeDTO.email());
        return employee;
    }

    public static EmployeeDTO toEmployeeDTO(Employee employee) {
        return new EmployeeDTO(
                employee.getId(),
                employee.getName(),
                employee.getSalary(),
                employee.getAge(),
                employee.getTitle(),
                employee.getEmail());
    }

    public static MockEmployeeDTO toMockEmployeeDTO(Employee employee) {
        return new MockEmployeeDTO(
                employee.getId(),
                employee.getName(),
                employee.getSalary(),
                employee.getAge(),
                employee.getTitle(),
                employee.getEmail());
    }
}
