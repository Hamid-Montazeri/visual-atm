package com.example.main.model;

import com.example.main.util.DateConverter;
import com.example.main.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Account {

    private String number;
    private double balance;
    private AccountOwner owner;
    private List<Transaction> transactionList;

    public Account() {
    }

    public Account(AccountOwner owner) {
        this.number = String.valueOf(new Random().nextInt(100000, 200000));
        this.balance = Constants.MIN_BALANCE;
        this.owner = owner;
        this.transactionList = new ArrayList<>();
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

    public void withdraw(double amount) throws Exception {
        if (getBalance() - amount < Constants.MIN_BALANCE) {
            throw new Exception("Entered amount is more than your balance! (Min balance is 20$)");
        }

        setBalance(balance - amount);

        Transaction transaction = new Transaction(
                String.valueOf(new Random().nextInt(1000, 9999)),
                DateConverter.getPersianDate(),
                amount,
                TransactionType.WITHDRAW
        );

        transactionList.add(transaction);
    }

    public void deposit(double amount) {
        setBalance(getBalance() + amount);

        Transaction transaction = new Transaction(
                String.valueOf(new Random().nextInt(1000, 9999)),
                DateConverter.getPersianDate(),
                amount,
                TransactionType.DEPOSIT
        );

        transactionList.add(transaction);
    }

    @Override
    public String toString() {
        return String.format(Constants.DASH_LINE + "Account Number: %s\nOwner: %s\nAccount Balance: %s$\nMin Balance: %s$" + Constants.DASH_LINE, number, owner, balance, Constants.MIN_BALANCE);
    }


}
