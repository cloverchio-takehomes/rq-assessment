package com.reliaquest.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EmployeeDTO(String id, String name, Integer salary, Integer age, String title, String email) {}
