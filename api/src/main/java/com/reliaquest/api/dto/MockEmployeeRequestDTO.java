package com.reliaquest.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MockEmployeeRequestDTO(String status, @JsonProperty("data") MockEmployeeDTO data) {}
