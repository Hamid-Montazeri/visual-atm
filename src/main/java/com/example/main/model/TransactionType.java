package com.example.main.model;

public enum TransactionType {

    DEPOSIT("واریز"), WITHDRAW("برداشت");

    private final String value;

    TransactionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TransactionType getByValue(String value) {
        return switch (value) {
            case "واریز" -> DEPOSIT;
            case "برداشت" -> WITHDRAW;
            default -> null;
        };
    }

}
