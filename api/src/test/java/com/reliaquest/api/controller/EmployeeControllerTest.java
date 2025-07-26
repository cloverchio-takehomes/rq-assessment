package com.reliaquest.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import com.reliaquest.api.dto.EmployeeDTO;
import com.reliaquest.api.exception.InvalidEmployeeException;
import com.reliaquest.api.exception.MockEmployeeServiceException;
import com.reliaquest.api.service.EmployeeService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

class EmployeeControllerTest {

    private static final String ID = "id";
    private static final String NAME = "Chris";
    private static final int SALARY = 1000;
    private static final int AGE = 0;
    private static final String TITLE = "title";
    private static final String EMAIL = "email@email.com";

    @Mock
    private EmployeeService employeeService;

    private EmployeeController employeeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        employeeController = new EmployeeController(employeeService);
    }

    @Test
    void testGetHighestSalaryOfEmployees_found() {
        when(employeeService.getHighestSalary()).thenReturn(Optional.of(120000));

        ResponseEntity<Integer> response = employeeController.getHighestSalaryOfEmployees();
        assertEquals(200, response.getStatusCode().value());
        assertEquals(120000, response.getBody());
    }

    @Test
    void testGetHighestSalaryOfEmployees_notFound() {
        when(employeeService.getHighestSalary()).thenReturn(Optional.empty());

        ResponseEntity<Integer> response = employeeController.getHighestSalaryOfEmployees();
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void testGetHighestSalaryOfEmployees_internalServerError() {
        when(employeeService.getHighestSalary()).thenThrow(new MockEmployeeServiceException("", 500));

        ResponseEntity<Integer> response = employeeController.getHighestSalaryOfEmployees();
        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void testGetEmployeesByNameSearch_success() {
        EmployeeDTO employeeDTO = new EmployeeDTO(ID, NAME, SALARY, AGE, TITLE, EMAIL);
        when(employeeService.getByNameSearch(NAME)).thenReturn(List.of(employeeDTO));

        ResponseEntity<List<EmployeeDTO>> response = employeeController.getEmployeesByNameSearch(NAME);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals(NAME, response.getBody().get(0).name());
    }

    @Test
    void testGetEmployeesByNameSearch_internalServerError() {
        when(employeeService.getByNameSearch(NAME)).thenThrow(new MockEmployeeServiceException("", 500));

        ResponseEntity<List<EmployeeDTO>> response = employeeController.getEmployeesByNameSearch(NAME);
        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNames() {
        when(employeeService.getTopTenHighestEarningNames()).thenReturn(List.of("A", "B", "C"));

        ResponseEntity<List<String>> response = employeeController.getTopTenHighestEarningEmployeeNames();
        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().contains("B"));
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNames_internalServerError() {
        when(employeeService.getTopTenHighestEarningNames()).thenThrow(new MockEmployeeServiceException("", 500));

        ResponseEntity<List<String>> response = employeeController.getTopTenHighestEarningEmployeeNames();
        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void testGetAllEmployees_success() {
        EmployeeDTO employeeDTO = new EmployeeDTO(ID, NAME, SALARY, AGE, TITLE, EMAIL);
        when(employeeService.getAll()).thenReturn(List.of(employeeDTO));

        ResponseEntity<List<EmployeeDTO>> response = employeeController.getAllEmployees();
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testGetAllEmployees_internalServerError() {
        when(employeeService.getAll()).thenThrow(new MockEmployeeServiceException("", 500));

        ResponseEntity<List<EmployeeDTO>> response = employeeController.getAllEmployees();
        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void testGetEmployeeById_found() {
        EmployeeDTO employeeDTO = new EmployeeDTO(ID, NAME, SALARY, AGE, TITLE, EMAIL);
        when(employeeService.getById(NAME)).thenReturn(Optional.of(employeeDTO));

        ResponseEntity<EmployeeDTO> response = employeeController.getEmployeeById(NAME);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(NAME, response.getBody().name());
    }

    @Test
    void testGetEmployeeById_notFound() {
        when(employeeService.getById("xyz")).thenReturn(Optional.empty());

        ResponseEntity<EmployeeDTO> response = employeeController.getEmployeeById("xyz");
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void testGetEmployeeById_internalServerError() {
        when(employeeService.getById(NAME)).thenThrow(new MockEmployeeServiceException("", 500));

        ResponseEntity<EmployeeDTO> response = employeeController.getEmployeeById(NAME);
        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void testCreateEmployee_success() {
        EmployeeDTO employeeDTO = new EmployeeDTO(ID, NAME, SALARY, AGE, TITLE, EMAIL);
        when(employeeService.create(employeeDTO)).thenReturn(Optional.of(employeeDTO));

        ResponseEntity<EmployeeDTO> response = employeeController.createEmployee(employeeDTO);
        assertEquals(201, response.getStatusCode().value());
    }

    @Test
    void testCreateEmployee_invalid() {
        EmployeeDTO employeeDTO = new EmployeeDTO(ID, NAME, SALARY, AGE, TITLE, EMAIL);
        doThrow(new InvalidEmployeeException("Invalid", 400))
                .when(employeeService)
                .create(employeeDTO);

        ResponseEntity<EmployeeDTO> response = employeeController.createEmployee(employeeDTO);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void testCreateEmployee_internalServerError() {
        EmployeeDTO employeeDTO = new EmployeeDTO(ID, NAME, SALARY, AGE, TITLE, EMAIL);
        doThrow(new MockEmployeeServiceException("", 500)).when(employeeService).create(any(EmployeeDTO.class));

        ResponseEntity<EmployeeDTO> response = employeeController.createEmployee(employeeDTO);
        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void testDeleteEmployeeById_success() {
        ResponseEntity<String> response = employeeController.deleteEmployeeById(NAME);
        assertEquals(204, response.getStatusCode().value());
        verify(employeeService).deleteByName(NAME);
    }

    @Test
    void testDeleteEmployeeById_internalServerError() {
        doThrow(new MockEmployeeServiceException("", 500)).when(employeeService).deleteByName(NAME);

        ResponseEntity<String> response = employeeController.deleteEmployeeById(NAME);
        assertEquals(500, response.getStatusCode().value());
    }
}
