package com.reliaquest.api.service;

import com.reliaquest.api.client.MockEmployeeClient;
import com.reliaquest.api.dto.*;
import com.reliaquest.api.exception.InvalidEmployeeException;
import com.reliaquest.api.exception.MockEmployeeServiceException;
import com.reliaquest.api.exception.TooManyMockEmployeeRequestsException;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.util.EmployeeTransformer;
import feign.FeignException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    private final MockEmployeeClient mockEmployeeClient;

    public EmployeeService(MockEmployeeClient mockEmployeeClient) {
        this.mockEmployeeClient = mockEmployeeClient;
    }

    /**
     * Finds the highest employee Salary.
     *
     * @return An optional representing the highest employee salary.
     * @throws MockEmployeeServiceException if there are issues with the upstream service.
     */
    public Optional<Integer> getHighestSalary() throws MockEmployeeServiceException {
        return getEmployeeStream().map(Employee::getSalary).max(Integer::compare);
    }

    /**
     * Finds employees using a given search string.
     * <p>
     * I recognize that there's more cases to be handled here and that
     * under normal circumstances .equals is not sufficient...
     *
     * @param searchString used to find employees.
     * @return a list of {@link EmployeeDTO}.
     * @throws MockEmployeeServiceException if there are issues with the upstream service.
     */
    public List<EmployeeDTO> getByNameSearch(String searchString) throws MockEmployeeServiceException {
        return getEmployeeStream()
                .filter(employee -> employee.getName().equals(searchString))
                .map(EmployeeTransformer::toEmployeeDTO)
                .toList();
    }

    /**
     * Finds the top 10 highest earning employees.
     *
     * @return a list of 10 employee names representing the top earners.
     * @throws MockEmployeeServiceException if there are issues with the upstream service.
     */
    public List<String> getTopTenHighestEarningNames() throws MockEmployeeServiceException {
        return getEmployeeStream()
                .sorted(Comparator.comparingInt(Employee::getSalary).reversed())
                .limit(10)
                .map(Employee::getName)
                .toList();
    }

    /**
     * Retrieves a list of all employees.
     *
     * @return a list of {@link EmployeeDTO}
     * @throws MockEmployeeServiceException if there are issues with the upstream service.
     */
    public List<EmployeeDTO> getAll() throws MockEmployeeServiceException {
        try {
            return getEmployeeStream().map(EmployeeTransformer::toEmployeeDTO).toList();
        } catch (FeignException e) {
            log.error("\"There was an issue retrieving employees\" errorMessage=\"{}\"", e.getMessage());
            throw handleFeignException(e);
        }
    }

    /**
     * Finds employees by their associated id.
     *
     * @param id in which to find an employee by.
     * @return an optional of the corresponding {@link EmployeeDTO}.
     * @throws MockEmployeeServiceException if there are issues with the upstream service.
     */
    public Optional<EmployeeDTO> getById(String id) throws MockEmployeeServiceException {
        try {
            return Optional.ofNullable(mockEmployeeClient.getMockEmployeeById(id))
                    .map(MockEmployeeResponseDTO::data)
                    .map(EmployeeTransformer::toEmployee)
                    .map(EmployeeTransformer::toEmployeeDTO);
        } catch (FeignException e) {
            log.error(
                    "\"Could not retrieve employee by id\" employeeId=\"{}\": errorMessage=\"{}\"", id, e.getMessage());
            throw handleFeignException(e);
        }
    }

    /**
     * Creates a new employee.
     *
     * @param employeeDTO represents the new employee to be created.
     * @return optional of {@link EmployeeDTO} representing the created employee.
     * @throws MockEmployeeServiceException if there are issues with the upstream service.
     */
    public Optional<EmployeeDTO> create(EmployeeDTO employeeDTO) throws MockEmployeeServiceException {
        Employee employee = EmployeeTransformer.toEmployee(employeeDTO);
        MockEmployeeDTO mockEmployeeDTO = EmployeeTransformer.toMockEmployeeDTO(employee);
        try {
            return Optional.ofNullable(
                            mockEmployeeClient.createMockEmployee(MockEmployeeCreateRequestDTO.from(mockEmployeeDTO)))
                    .map(MockEmployeeResponseDTO::data)
                    .map(EmployeeTransformer::toEmployee)
                    .map(EmployeeTransformer::toEmployeeDTO);
        } catch (FeignException e) {
            log.error("\"Could not create employee\" errorMessage=\"{}\"", e.getMessage());
            throw handleFeignException(e);
        }
    }

    /**
     * Deletes an employee by their corresponding name.
     *
     * @param name the name of the employee to delete.
     * @throws MockEmployeeServiceException if there are issues with the upstream service.
     */
    public void deleteByName(String name) throws MockEmployeeServiceException {
        try {
            mockEmployeeClient.deleteMockEmployeeByName(new MockEmployeeDeleteRequestDTO(name));
        } catch (FeignException e) {
            log.error("\"Could not delete employee\" employeeName=\"{}\" errorMessage=\"{}\"", name, e.getMessage());
            throw handleFeignException(e);
        }
    }

    private Stream<Employee> getEmployeeStream() {
        try {
            return mockEmployeeClient.getAllMockEmployees().data().stream().map(EmployeeTransformer::toEmployee);
        } catch (FeignException e) {
            throw handleFeignException(e);
        }
    }

    private RuntimeException handleFeignException(FeignException e) {
        HttpStatus status = HttpStatus.resolve(e.status());
        if (status == HttpStatus.BAD_REQUEST) {
            return new InvalidEmployeeException(e.contentUTF8(), status.value());
        }
        if (status == HttpStatus.TOO_MANY_REQUESTS) {
            return new TooManyMockEmployeeRequestsException(status.value());
        }
        if (status != null && status.is5xxServerError()) {
            return new MockEmployeeServiceException(status.value());
        }
        throw e;
    }
}
