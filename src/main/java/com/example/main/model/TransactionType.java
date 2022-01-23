package com.example.main.model;

public enum TransactionType {


    DEPOSIT("واریز"), WITHDRAW("برداشت");

    private String value;

    TransactionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }



}
