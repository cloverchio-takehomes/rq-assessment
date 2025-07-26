package com.reliaquest.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.reliaquest.api.client.MockEmployeeClient;
import com.reliaquest.api.dto.*;
import com.reliaquest.api.exception.MockEmployeeServiceException;
import feign.FeignException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class EmployeeServiceTest {

    private static final String ID = "id";
    private static final int AGE = 0;
    private static final String TITLE = "title";
    private static final String EMAIL = "email@email.com";

    @Mock
    private MockEmployeeClient mockEmployeeClient;

    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        employeeService = new EmployeeService(mockEmployeeClient);
    }

    @Test
    void testGetHighestSalary() {
        MockEmployeeDTO mockEmployeeA = new MockEmployeeDTO(ID, "A", 100, AGE, TITLE, EMAIL);
        MockEmployeeDTO mockEmployeeB = new MockEmployeeDTO(ID, "B", 200, AGE, TITLE, EMAIL);

        List<MockEmployeeDTO> mockEmployees = List.of(mockEmployeeA, mockEmployeeB);
        when(mockEmployeeClient.getAllMockEmployees()).thenReturn(new MockEmployeeListResponseDTO(null, mockEmployees));

        Optional<Integer> result = employeeService.getHighestSalary();
        assertTrue(result.isPresent());
        assertEquals(200, result.get());
    }

    @Test
    void testGetByNameSearch_exactMatch() {
        MockEmployeeDTO mockEmployeeA = new MockEmployeeDTO(ID, "A", 100, AGE, TITLE, EMAIL);
        MockEmployeeDTO mockEmployeeB = new MockEmployeeDTO(ID, "B", 200, AGE, TITLE, EMAIL);

        List<MockEmployeeDTO> mockEmployees = List.of(mockEmployeeA, mockEmployeeB);
        when(mockEmployeeClient.getAllMockEmployees()).thenReturn(new MockEmployeeListResponseDTO(null, mockEmployees));

        List<EmployeeDTO> result = employeeService.getByNameSearch("A");
        assertEquals(1, result.size());
        assertEquals("A", result.get(0).name());
    }

    @Test
    void testGetByNameSearch_noMatch() {
        MockEmployeeDTO mockEmployeeA = new MockEmployeeDTO(ID, "A", 100, AGE, TITLE, EMAIL);
        MockEmployeeDTO mockEmployeeB = new MockEmployeeDTO(ID, "B", 200, AGE, TITLE, EMAIL);

        List<MockEmployeeDTO> mockEmployees = List.of(mockEmployeeA, mockEmployeeB);
        when(mockEmployeeClient.getAllMockEmployees()).thenReturn(new MockEmployeeListResponseDTO(null, mockEmployees));

        List<EmployeeDTO> result = employeeService.getByNameSearch("C");
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetTopTenHighestEarningNames() {
        MockEmployeeDTO mockEmployee1 = new MockEmployeeDTO(ID, "1", 100, AGE, TITLE, EMAIL);
        MockEmployeeDTO mockEmployee2 = new MockEmployeeDTO(ID, "2", 200, AGE, TITLE, EMAIL);
        MockEmployeeDTO mockEmployee3 = new MockEmployeeDTO(ID, "3", 300, AGE, TITLE, EMAIL);
        MockEmployeeDTO mockEmployee4 = new MockEmployeeDTO(ID, "4", 400, AGE, TITLE, EMAIL);
        MockEmployeeDTO mockEmployee5 = new MockEmployeeDTO(ID, "5", 500, AGE, TITLE, EMAIL);
        MockEmployeeDTO mockEmployee6 = new MockEmployeeDTO(ID, "6", 600, AGE, TITLE, EMAIL);
        MockEmployeeDTO mockEmployee7 = new MockEmployeeDTO(ID, "7", 700, AGE, TITLE, EMAIL);
        MockEmployeeDTO mockEmployee8 = new MockEmployeeDTO(ID, "8", 800, AGE, TITLE, EMAIL);
        MockEmployeeDTO mockEmployee9 = new MockEmployeeDTO(ID, "9", 900, AGE, TITLE, EMAIL);
        MockEmployeeDTO mockEmployee10 = new MockEmployeeDTO(ID, "10", 1000, AGE, TITLE, EMAIL);
        MockEmployeeDTO mockEmployee0 = new MockEmployeeDTO(ID, "0", 5, AGE, TITLE, EMAIL);

        List<MockEmployeeDTO> mockEmployees = List.of(
                mockEmployee1,
                mockEmployee2,
                mockEmployee3,
                mockEmployee4,
                mockEmployee5,
                mockEmployee6,
                mockEmployee7,
                mockEmployee8,
                mockEmployee9,
                mockEmployee10,
                mockEmployee0);

        when(mockEmployeeClient.getAllMockEmployees()).thenReturn(new MockEmployeeListResponseDTO(null, mockEmployees));

        List<String> result = employeeService.getTopTenHighestEarningNames();
        assertFalse(result.contains("0"));
    }

    @Test
    void testGetTopTenHighestEarningNames_order() {
        MockEmployeeDTO mockEmployeeA = new MockEmployeeDTO(ID, "A", 100, AGE, TITLE, EMAIL);
        MockEmployeeDTO mockEmployeeB = new MockEmployeeDTO(ID, "B", 200, AGE, TITLE, EMAIL);
        MockEmployeeDTO mockEmployeeC = new MockEmployeeDTO(ID, "C", 300, AGE, TITLE, EMAIL);

        List<MockEmployeeDTO> mockEmployees = List.of(mockEmployeeA, mockEmployeeB, mockEmployeeC);
        when(mockEmployeeClient.getAllMockEmployees()).thenReturn(new MockEmployeeListResponseDTO(null, mockEmployees));

        List<String> result = employeeService.getTopTenHighestEarningNames();
        assertEquals(List.of("C", "B", "A"), result);
    }

    @Test
    void testGetById_found() {
        MockEmployeeDTO mockEmployee = new MockEmployeeDTO(ID, "A", 100, AGE, TITLE, EMAIL);
        when(mockEmployeeClient.getMockEmployeeById(ID)).thenReturn(new MockEmployeeResponseDTO(null, mockEmployee));

        Optional<EmployeeDTO> result = employeeService.getById(ID);
        assertTrue(result.isPresent());
        assertEquals("A", result.get().name());
    }

    @Test
    void testGetById_notFound() {
        when(mockEmployeeClient.getMockEmployeeById("A")).thenReturn(new MockEmployeeResponseDTO(null, null));
        Optional<EmployeeDTO> result = employeeService.getById("A");
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetById_mockServiceError() {
        FeignException fe = FeignException.errorStatus(
                "GET",
                feign.Response.builder()
                        .status(500)
                        .reason("")
                        .request(mock(feign.Request.class))
                        .build());

        when(mockEmployeeClient.getMockEmployeeById("A")).thenThrow(fe);
        assertThrows(MockEmployeeServiceException.class, () -> employeeService.getById("A"));
    }

    @Test
    void testGetAll() {
        MockEmployeeDTO mockEmployeeA = new MockEmployeeDTO(ID, "A", 100, AGE, TITLE, EMAIL);
        MockEmployeeDTO mockEmployeeB = new MockEmployeeDTO(ID, "B", 200, AGE, TITLE, EMAIL);
        MockEmployeeDTO mockEmployeeC = new MockEmployeeDTO(ID, "C", 300, AGE, TITLE, EMAIL);

        List<MockEmployeeDTO> mockEmployees = List.of(mockEmployeeA, mockEmployeeB, mockEmployeeC);
        when(mockEmployeeClient.getAllMockEmployees()).thenReturn(new MockEmployeeListResponseDTO(null, mockEmployees));

        List<EmployeeDTO> result = employeeService.getAll();
        assertEquals(3, result.size());
    }

    @Test
    void testGetAll_mockServiceError() {
        FeignException fe = FeignException.errorStatus(
                "GET",
                feign.Response.builder()
                        .status(500)
                        .reason("")
                        .request(mock(feign.Request.class))
                        .build());

        when(mockEmployeeClient.getAllMockEmployees()).thenThrow(fe);
        assertThrows(MockEmployeeServiceException.class, () -> employeeService.getAll());
    }

    @Test
    void testCreate() {
        EmployeeDTO employeeDTO = new EmployeeDTO(ID, "A", 100, AGE, TITLE, EMAIL);
        employeeService.create(employeeDTO);
        verify(mockEmployeeClient).createMockEmployee(any(MockEmployeeCreateRequestDTO.class));
    }

    @Test
    void testCreate_mockServiceError() {
        FeignException fe = FeignException.errorStatus(
                "GET",
                feign.Response.builder()
                        .status(500)
                        .reason("")
                        .request(mock(feign.Request.class))
                        .build());

        EmployeeDTO employeeDTO = new EmployeeDTO(ID, "A", 100, AGE, TITLE, EMAIL);
        doThrow(fe).when(mockEmployeeClient).createMockEmployee(any(MockEmployeeCreateRequestDTO.class));
        assertThrows(MockEmployeeServiceException.class, () -> employeeService.create(employeeDTO));
    }

    @Test
    void testDeleteByName() {
        employeeService.deleteByName("A");
        verify(mockEmployeeClient).deleteMockEmployeeByName(any(MockEmployeeDeleteRequestDTO.class));
    }

    @Test
    void testDeleteByName_mockServiceError() {
        FeignException fe = FeignException.errorStatus(
                "GET",
                feign.Response.builder()
                        .status(500)
                        .reason("")
                        .request(mock(feign.Request.class))
                        .build());

        doThrow(fe).when(mockEmployeeClient).deleteMockEmployeeByName(any(MockEmployeeDeleteRequestDTO.class));
        assertThrows(MockEmployeeServiceException.class, () -> employeeService.deleteByName("A"));
    }
}
