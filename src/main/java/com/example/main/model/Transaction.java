package com.example.main.model;

public class Transaction {

    private String number;
    private String date;
    private double amount;
    private TransactionType transactionType;

    public Transaction() {
    }

    public Transaction(String number, String date, double amount, TransactionType transactionType) {
        this.number = number;
        this.date = date;
        this.amount = amount;
        this.transactionType = transactionType;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    @Override
    public String toString() {
        return String.format("Transaction ID: %s\nTransaction Type: %s\nAmount: %s$\nDate: %s", number, transactionType, amount, date);
    }
}
