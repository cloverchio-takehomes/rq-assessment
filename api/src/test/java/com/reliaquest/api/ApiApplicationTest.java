package com.reliaquest.api;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.reliaquest.api.dto.EmployeeDTO;
import com.reliaquest.api.exception.InvalidEmployeeException;
import com.reliaquest.api.exception.MockEmployeeServiceException;
import com.reliaquest.api.service.EmployeeService;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ApiApplicationTest {

    private static final String URL = "/api/v1/employee";
    private static final String ID = "id";
    private static final String NAME = "Chris";
    private static final int SALARY = 1000;
    private static final int AGE = 0;
    private static final String TITLE = "title";
    private static final String EMAIL = "email@email.com";
    private static final String CONTENT =
            """
                {
                    "id": "id",
                    "name": "Chris",
                    "salary": 1000,
                    "age": 0,
                    "title": "title",
                    "email": "email"
                }
            """;

    @Resource
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Test
    void testGetHighestSalary_success() throws Exception {
        when(employeeService.getHighestSalary()).thenReturn(Optional.of(150000));

        mockMvc.perform(get(URL + "/highestSalary"))
                .andExpect(status().isOk())
                .andExpect(content().string("150000"));
    }

    @Test
    void testGetHighestSalary_notFound() throws Exception {
        when(employeeService.getHighestSalary()).thenReturn(Optional.empty());

        mockMvc.perform(get(URL + "/highestSalary")).andExpect(status().isNotFound());
    }

    @Test
    void testGetHighestSalary_internalServerError() throws Exception {
        when(employeeService.getHighestSalary()).thenThrow(new MockEmployeeServiceException(500));

        mockMvc.perform(get(URL + "/highestSalary")).andExpect(status().isInternalServerError());
    }

    @Test
    void testGetEmployeesByNameSearch_success() throws Exception {
        EmployeeDTO employeeDTO = new EmployeeDTO(ID, NAME, SALARY, AGE, TITLE, EMAIL);
        List<EmployeeDTO> mockList = List.of(employeeDTO);
        when(employeeService.getByNameSearch(NAME)).thenReturn(mockList);

        mockMvc.perform(get(URL + "/search/" + NAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(NAME));
    }

    @Test
    void testGetEmployeesByNameSearch_internalServerError() throws Exception {
        when(employeeService.getByNameSearch(NAME)).thenThrow(new MockEmployeeServiceException(500));

        mockMvc.perform(get(URL + "/search/" + NAME)).andExpect(status().isInternalServerError());
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNames_success() throws Exception {
        List<String> mockNames = List.of("X", "Y", "Z");
        when(employeeService.getTopTenHighestEarningNames()).thenReturn(mockNames);

        mockMvc.perform(get(URL + "/topTenHighestEarningEmployeeNames"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("X"))
                .andExpect(jsonPath("$[1]").value("Y"))
                .andExpect(jsonPath("$[2]").value("Z"))
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNames_internalServerError() throws Exception {
        when(employeeService.getTopTenHighestEarningNames()).thenThrow(new MockEmployeeServiceException(500));

        mockMvc.perform(get(URL + "/topTenHighestEarningEmployeeNames")).andExpect(status().isInternalServerError());
    }

    @Test
    void testGetAllEmployees() throws Exception {
        EmployeeDTO employeeDTO = new EmployeeDTO(ID, NAME, SALARY, AGE, TITLE, EMAIL);
        List<EmployeeDTO> mockEmployees = List.of(employeeDTO);
        when(employeeService.getAll()).thenReturn(mockEmployees);

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(NAME))
                .andExpect(jsonPath("$[0].salary").value(SALARY));
    }

    @Test
    void testGetAllEmployees_internalServerError() throws Exception {
        when(employeeService.getAll()).thenThrow(new MockEmployeeServiceException(500));

        mockMvc.perform(get(URL)).andExpect(status().isInternalServerError());
    }

    @Test
    void testGetEmployeeById_found() throws Exception {
        EmployeeDTO employeeDTO = new EmployeeDTO(ID, NAME, SALARY, AGE, TITLE, EMAIL);
        when(employeeService.getById(ID)).thenReturn(Optional.of(employeeDTO));

        mockMvc.perform(get(URL + "/" + ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(NAME))
                .andExpect(jsonPath("$.salary").value(SALARY));
    }

    @Test
    void testGetEmployeeById_notFound() throws Exception {
        when(employeeService.getById(ID)).thenReturn(Optional.empty());

        mockMvc.perform(get(URL + "/" + ID)).andExpect(status().isNotFound());
    }

    @Test
    void testGetEmployeeById_internalServerError() throws Exception {
        when(employeeService.getById(ID)).thenThrow(new MockEmployeeServiceException(500));

        mockMvc.perform(get(URL + "/" + ID)).andExpect(status().isInternalServerError());
    }

    @Test
    void testCreateEmployee_success() throws Exception {
        EmployeeDTO employeeDTO = new EmployeeDTO(ID, NAME, SALARY, AGE, TITLE, EMAIL);
        when(employeeService.create(any(EmployeeDTO.class))).thenReturn(Optional.of(employeeDTO));
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(CONTENT))
                .andExpect(status().isCreated());

        verify(employeeService).create(Mockito.any(EmployeeDTO.class));
    }

    @Test
    void testCreateEmployee_invalidEmployee() throws Exception {
        doThrow(new InvalidEmployeeException("Invalid Employee", 400))
                .when(employeeService)
                .create(Mockito.any(EmployeeDTO.class));

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(CONTENT))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateEmployee_internalServerError() throws Exception {
        doThrow(new MockEmployeeServiceException(500))
                .when(employeeService)
                .create(Mockito.any(EmployeeDTO.class));

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(CONTENT))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testDeleteEmployeeById_success() throws Exception {
        mockMvc.perform(delete(URL + "/" + NAME)).andExpect(status().isNoContent());

        verify(employeeService).deleteByName(NAME);
    }

    @Test
    void testDeleteEmployeeById_internalServerError() throws Exception {
        doThrow(new MockEmployeeServiceException(500))
                .when(employeeService)
                .deleteByName(NAME);

        mockMvc.perform(delete(URL + "/" + NAME)).andExpect(status().isInternalServerError());
    }
}
