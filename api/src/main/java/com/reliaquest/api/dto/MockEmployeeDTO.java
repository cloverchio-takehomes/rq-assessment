package com.reliaquest.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MockEmployeeDTO(
        String id,
        @JsonProperty("employee_name") String name,
        @JsonProperty("employee_salary") Integer salary,
        @JsonProperty("employee_age") Integer age,
        @JsonProperty("employee_title") String title,
        @JsonProperty("employee_email") String email) {
}
