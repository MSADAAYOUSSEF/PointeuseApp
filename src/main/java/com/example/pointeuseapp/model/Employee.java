package com.example.pointeuseapp.model;

import java.io.Serializable;
import java.util.UUID;

public class Employee implements Serializable {
    private final UUID id;
    private final String name;

    public Employee(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    @Override
    public String toString() { return name; }
}