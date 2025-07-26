package com.reliaquest.api.client;

import com.reliaquest.api.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "mockEmployeeClient", url = "${mock.employee.service.url}")
public interface MockEmployeeClient {

    @GetMapping("/v1/employee")
    MockEmployeeListResponseDTO getAllMockEmployees();

    @GetMapping("/v1/employee/{id}")
    MockEmployeeResponseDTO getMockEmployeeById(@PathVariable("id") String id);

    @PostMapping("/v1/employee")
    MockEmployeeResponseDTO createMockEmployee(@RequestBody MockEmployeeCreateRequestDTO mockEmployeeCreateRequestDTO);

    @DeleteMapping("/v1/employee")
    void deleteMockEmployeeByName(@RequestBody MockEmployeeDeleteRequestDTO mockEmployeeDeleteRequestDTO);
}
