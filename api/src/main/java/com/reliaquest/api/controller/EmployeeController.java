package com.reliaquest.api.controller;

import com.reliaquest.api.dto.EmployeeDTO;
import com.reliaquest.api.exception.InvalidEmployeeException;
import com.reliaquest.api.exception.MockEmployeeServiceException;
import com.reliaquest.api.exception.TooManyMockEmployeeRequestsException;
import com.reliaquest.api.service.EmployeeService;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController implements IEmployeeController<EmployeeDTO, EmployeeDTO> {

    private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Gets the highest salary among all employees.
     *
     * @return the highest salary or 404 if none found
     */
    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        try {
            log.info("\"Received request for highest salary\"");
            return employeeService
                    .getHighestSalary()
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (TooManyMockEmployeeRequestsException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        } catch (MockEmployeeServiceException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Searches employees by name.
     *
     * @param searchString the name to search for
     * @return a list of matching employees
     */
    @Override
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByNameSearch(String searchString) {
        try {
            log.info("\"Received request for employee search\" searchString=\"{}\"", searchString);
            List<EmployeeDTO> employeeDTOS = employeeService.getByNameSearch(searchString);
            return ResponseEntity.ok(employeeDTOS);
        } catch (TooManyMockEmployeeRequestsException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        } catch (MockEmployeeServiceException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Gets the names of the top 10 highest earning employees.
     *
     * @return a list of employee names
     */
    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        try {
            log.info("\"Received request for top 10 earners\"");
            List<String> topTenEarningNames = employeeService.getTopTenHighestEarningNames();
            return ResponseEntity.ok(topTenEarningNames);
        } catch (TooManyMockEmployeeRequestsException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        } catch (MockEmployeeServiceException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Retrieves all employees.
     *
     * @return a list of all employees
     */
    @Override
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        try {
            log.info("\"Received request for all employees\"");
            List<EmployeeDTO> employeeDTOS = employeeService.getAll();
            return ResponseEntity.ok(employeeDTOS);
        } catch (TooManyMockEmployeeRequestsException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        } catch (MockEmployeeServiceException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Gets an employee by ID.
     *
     * @param id the employee ID
     * @return the employee details or 404 if not found
     */
    @Override
    public ResponseEntity<EmployeeDTO> getEmployeeById(String id) {
        try {
            log.info("\"Received request for employee by id\" employeeId=\"{}\"", id);
            return employeeService
                    .getById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (TooManyMockEmployeeRequestsException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        } catch (MockEmployeeServiceException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Creates a new employee.
     *
     * @param employeeInput the employee data
     * @return the created employee or error if invalid
     */
    @Override
    public ResponseEntity<EmployeeDTO> createEmployee(EmployeeDTO employeeInput) {
        try {
            log.info("\"Received request to create employee\"");
            return employeeService.create(employeeInput)
                    .map(ResponseEntity.status(HttpStatus.CREATED)::body)
                    .orElse(ResponseEntity.notFound().build());
        } catch (InvalidEmployeeException ie) {
            return ResponseEntity.badRequest().build();
        } catch (TooManyMockEmployeeRequestsException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        } catch (MockEmployeeServiceException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Deletes an employee by name.
     *
     * @param id the employee name
     * @return 204 No Content on success
     */
    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        try {
            log.info("\"Received request to delete employee\" employeeName=\"{}\"", id);
            employeeService.deleteByName(id);
            return ResponseEntity.noContent().build();
        } catch (TooManyMockEmployeeRequestsException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        } catch (MockEmployeeServiceException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
