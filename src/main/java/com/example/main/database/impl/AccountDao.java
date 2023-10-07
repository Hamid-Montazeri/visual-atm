package com.example.main.database.impl;

import com.example.main.database.Dao;
import com.example.main.database.DatabaseHelper;
import com.example.main.model.Account;
import com.example.main.util.MyDialog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.example.main.database.DatabaseUtils.TABLE_ACCOUNT;

public class AccountDao implements Dao<Account> {

    @Override
    public long save(Account account) {
        long genKey = 0;

        try {
            Connection connection = DatabaseHelper.getInstance().getConnection();

            if (connection == null || connection.isClosed()) {
                return 0;
            }

            String query = String.format("INSERT INTO %s (number,balance,owner) VALUES (?,?,?)", TABLE_ACCOUNT);
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, account.getNumber());
            ps.setDouble(2, account.getBalance());
            ps.setString(3, account.getOwner().getName() + " " + account.getOwner().getFamily());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                ResultSet resultSet = ps.getGeneratedKeys();
                if (resultSet.first()) {
                    genKey = resultSet.getLong(1);
                }
            }
        } catch (SQLException e) {
            MyDialog.showErrorDialog("Error", e.getMessage());
        }

        return genKey;
    }

    @Override
    public Account getById(long id) {
        try {
            Connection connection = DatabaseHelper.getInstance().getConnection();
            if (connection == null || connection.isClosed()) {
                return null;
            }
            String query = String.format("SELECT * from %s WHERE id = ?", TABLE_ACCOUNT);
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setLong(1, id);
            ResultSet resultSet = ps.executeQuery();

            Account account = new Account();

            if (resultSet.first()) {
                account.setNumber(resultSet.getString("number"));
                account.setBalance(resultSet.getDouble("balance"));
                //            account.setTransactionList();
                //            account.setOwner(resultSet.getString("owner"));
            }
            return account;
        } catch (SQLException e) {
            MyDialog.showErrorDialog("Error", e.getMessage());
        }
        return null;
    }

    @Override
    public void update(Account account, long id) {
        Account existingAccount = getById(id);

        existingAccount.setNumber(account.getNumber());
        existingAccount.setBalance(account.getBalance());
        existingAccount.setOwner(account.getOwner());
        existingAccount.setTransactionList(account.getTransactionList());

        save(existingAccount);
    }

    @Override
    public void deleteById(long id) {
        try {
            Connection connection = DatabaseHelper.getInstance().getConnection();
            if (connection == null || connection.isClosed()) {
                return;
            }
            String query = String.format("DELETE FROM %S WHERE id = ?", TABLE_ACCOUNT);
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setLong(1, id);
            boolean executed = ps.execute();
            System.out.println("Delete Account from db with id " + id + ": " + executed);
        } catch (SQLException e) {
            MyDialog.showErrorDialog("Error", e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        String SQL = String.format("DELETE FROM %s", TABLE_ACCOUNT);
        try {
            Connection connection = DatabaseHelper.getInstance().getConnection();
            connection.createStatement().execute(SQL);
        } catch (SQLException ex) {
            MyDialog.showErrorDialog("Error!", ex.getMessage());
        }
    }

    @Override
    public List<Account> getAll() {
        List<Account> accounts = new ArrayList<>();

        try {
            Connection connection = DatabaseHelper.getInstance().getConnection();

            if (connection == null || connection.isClosed()) {
                return null;
            }

            String query = String.format("SELECT * FROM %s", TABLE_ACCOUNT);
            ResultSet resultSet = connection.createStatement().executeQuery(query);

            while (resultSet.next()) {
                Account account = new Account();
                account.setNumber(resultSet.getString("number"));
                account.setBalance(resultSet.getDouble("balance"));
                accounts.add(account);
            }
        } catch (SQLException e) {
            MyDialog.showErrorDialog("Error", e.getMessage());
        }

        return accounts;
    }

    public Account findAccountById(long id) {
        String SQL = String.format("SELECT * from %s WHERE id = ?", TABLE_ACCOUNT);

        try {
            Connection connection = DatabaseHelper.getInstance().getConnection();
            PreparedStatement pstmt = connection.prepareStatement(SQL);
            pstmt.setLong(1, id);

            ResultSet resultSet = pstmt.executeQuery();
            Account account;
            if (resultSet.next()) {
                //Display values
                String number = resultSet.getString("number");
                double balance = resultSet.getDouble("balance");
                String owner = resultSet.getString("owner");

                account = new Account();
                account.setNumber(number);
                account.setBalance(balance);
//                account.setOwner();
//                account.setTransactionList();

                MyDialog.showInfoDialog("Successful Operation", "Account found with id: " + id);

                return account;
            }
        } catch (SQLException throwable) {
            MyDialog.showErrorDialog("Error!", throwable.getMessage());
        }
        return null;
    }

    public int updateAccountBalance(double amount, long id) {
        String SQL = String.format("UPDATE %s SET balance = ? WHERE id = ?", TABLE_ACCOUNT);

        int affectedrows = 0;

        try {
            Connection connection = DatabaseHelper.getInstance().getConnection();
            PreparedStatement pstmt = connection.prepareStatement(SQL);
            pstmt.setDouble(1, amount);
            pstmt.setLong(2, id);

            affectedrows = pstmt.executeUpdate();

            MyDialog.showInfoDialog("Insert Records", "Number of Affected Rows: " + affectedrows);

        } catch (SQLException e) {
            MyDialog.showErrorDialog("Error!", e.getMessage());
        }

        return affectedrows;
    }

}
