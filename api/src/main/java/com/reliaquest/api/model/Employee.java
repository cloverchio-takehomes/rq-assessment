package com.reliaquest.api.model;

import lombok.Data;

@Data
public class Employee {
    private String id;
    private String name;
    private Integer age;
    private Integer salary;
    private String title;
    private String email;
}
