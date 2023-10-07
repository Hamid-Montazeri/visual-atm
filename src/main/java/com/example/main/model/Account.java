package com.example.main.model;

import com.example.main.util.DateConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.example.main.database.DatabaseUtils.MIN_BALANCE;


public class Account {
    private Long id;
    private String number;
    private double balance;
    private AccountOwner owner;
    private List<Transaction> transactionList;

    public Account() {
    }

    public Account(AccountOwner owner, double balance) {
        this.number = String.valueOf(new Random().nextInt(100000, 200000));
        this.balance = balance <= 0 ? MIN_BALANCE : balance;
        this.owner = owner;
        this.transactionList = new ArrayList<>();
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

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public AccountOwner getOwner() {
        return owner;
    }

    public void setOwner(AccountOwner owner) {
        this.owner = owner;
    }

    public List<Transaction> getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    public void withdraw(double amount) {
        if (this.balance - amount < MIN_BALANCE) {
            throw new RuntimeException("Entered amount is more than your balance! (Min balance is " + MIN_BALANCE + ")");
        }

        setBalance(balance - amount);

        Transaction transaction = new Transaction(
                String.valueOf(new Random().nextInt(1000, 9999)),
                DateConverter.getPersianDate(),
                amount,
                TransactionType.WITHDRAW,
                this);

        transactionList.add(transaction);
    }

    public void deposit(double amount) {
        setBalance(this.balance + amount);

        Transaction transaction = new Transaction(
                String.valueOf(new Random().nextInt(1000, 9999)),
                DateConverter.getPersianDate(),
                amount,
                TransactionType.DEPOSIT,
                this);

        transactionList.add(transaction);
    }

}
