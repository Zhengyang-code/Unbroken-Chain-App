package com.example.unbrokenchainapp.models;

import java.util.Date;

public class ChainEntry {
    private long id;
    private long chainId;
    private Date date;
    private boolean completed;

    public ChainEntry() {
    }

    public ChainEntry(long chainId, Date date, boolean completed) {
        this.chainId = chainId;
        this.date = date;
        this.completed = completed;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getChainId() {
        return chainId;
    }

    public void setChainId(long chainId) {
        this.chainId = chainId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
} 