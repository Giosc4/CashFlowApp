package com.example.cashflow.dataClass;
public class Planning {
    private int id;
    private int templateId;
    private String repetition;
    private String endDate;

    // Constructors, Getters, Setters
    public Planning(int id, int templateId, String repetition, String endDate) {
        this.id = id;
        this.templateId = templateId;
        this.repetition = repetition;
        this.endDate = endDate;
    }
}
