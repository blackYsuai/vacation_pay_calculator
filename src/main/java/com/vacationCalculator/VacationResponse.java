package com.vacationCalculator;

public class VacationResponse {
    private String message;
    private double amount;

    public VacationResponse(String message, double amount) {
        this.message = message;
        this.amount = amount;
    }

    public String getMessage() {
        return message;
    }

    public double getAmount() {
        return amount;
    }
}
