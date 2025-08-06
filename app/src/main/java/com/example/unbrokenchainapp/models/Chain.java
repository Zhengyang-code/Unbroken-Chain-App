package com.example.unbrokenchainapp.models;

import java.util.Date;

public class Chain {
    private long id;
    private String name;
    private String description;
    private Date createdAt;
    private boolean isActive;

    public Chain() {
        this.createdAt = new Date();
        this.isActive = true;
    }

    public Chain(String name, String description) {
        this.name = name;
        this.description = description;
        this.createdAt = new Date();
        this.isActive = true;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
} 