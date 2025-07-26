package com.reliaquest.api.dto;

public record MockEmployeeCreateRequestDTO(
        String name,
        Integer salary,
        Integer age,
        String title
) {
    public static MockEmployeeCreateRequestDTO from(MockEmployeeDTO mockEmployeeDTO) {
        return new MockEmployeeCreateRequestDTO(
                mockEmployeeDTO.name(),
                mockEmployeeDTO.salary(),
                mockEmployeeDTO.age(),
                mockEmployeeDTO.title()
        );
    }
}

