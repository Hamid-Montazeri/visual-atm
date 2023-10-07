package com.example.main.model;

public class Transaction {

    private Long id;
    private String number;
    private String date;
    private double amount;
    private TransactionType transactionType;
    private Account account;

    public Transaction() {
    }

    public Transaction(String number, String date, double amount, TransactionType transactionType, Account account) {
        this.number = number;
        this.date = date;
        this.amount = amount;
        this.transactionType = transactionType;
        this.account = account;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
