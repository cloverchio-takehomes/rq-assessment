package com.reliaquest.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record MockEmployeeListResponseDTO(String status, @JsonProperty("data") List<MockEmployeeDTO> data) {
}
