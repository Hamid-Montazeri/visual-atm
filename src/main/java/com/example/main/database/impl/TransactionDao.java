package com.example.main.database.impl;

import com.example.main.database.Dao;
import com.example.main.database.DatabaseHelper;
import com.example.main.model.Account;
import com.example.main.model.Transaction;
import com.example.main.model.TransactionType;
import com.example.main.util.MyDialog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.example.main.database.DatabaseUtils.TABLE_ACCOUNT;
import static com.example.main.database.DatabaseUtils.TABLE_TRANSACTION;

public class TransactionDao implements Dao<Transaction> {

    @Override
    public long save(Transaction transaction) {
        long genKey = 0;

        try {
            Connection connection = DatabaseHelper.getInstance().getConnection();

            if (connection == null || connection.isClosed()) {
                return 0;
            }

            String query = String.format("INSERT INTO %s (number,date,amount,type,account_id) VALUES (?,?,?,?,?)", TABLE_TRANSACTION);
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            ps.setInt(1, Integer.parseInt(transaction.getNumber()));
            ps.setString(2, transaction.getDate());
            ps.setDouble(3, transaction.getAmount());
            ps.setString(4, transaction.getTransactionType().getValue());
//        ps.setLong(5, );

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
    public Transaction getById(long id) {
        try {
            Connection connection = DatabaseHelper.getInstance().getConnection();
            if (connection == null || connection.isClosed()) {
                return null;
            }
            String query = String.format("SELECT * from %s WHERE id = ?", TABLE_ACCOUNT);
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setLong(1, id);
            ResultSet resultSet = ps.executeQuery();

            Transaction transaction = new Transaction();

            if (resultSet.first()) {
                transaction.setNumber(resultSet.getString("number"));
                transaction.setTransactionType(TransactionType.getByValue(resultSet.getString("type")));
                transaction.setDate(resultSet.getString("date"));
                transaction.setAmount(Double.parseDouble(resultSet.getString("amount")));
//                transaction.setAccount();
            }
            return transaction;
        } catch (SQLException e) {
            MyDialog.showErrorDialog("Error", e.getMessage());
        }
        return null;
    }

    @Override
    public void update(Transaction transaction, long id) {
        Transaction existingTransaction = getById(id);

        existingTransaction.setNumber(transaction.getNumber());
        existingTransaction.setAccount(transaction.getAccount());
        existingTransaction.setDate(transaction.getDate());
        existingTransaction.setAmount(transaction.getAmount());
        existingTransaction.setTransactionType(transaction.getTransactionType());

        save(existingTransaction);
    }

    @Override
    public void deleteById(long id) {
        try {
            Connection connection = DatabaseHelper.getInstance().getConnection();
            if (connection == null || connection.isClosed()) {
                return;
            }
            String query = String.format("DELETE FROM %S WHERE id = ?", TABLE_TRANSACTION);
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setLong(1, id);
            boolean executed = ps.execute();
            System.out.println("Delete Transaction from db with id " + id + ": " + executed);
        } catch (SQLException e) {
            MyDialog.showErrorDialog("Error", e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        String SQL = String.format("DELETE FROM %s", TABLE_TRANSACTION);
        try {
            Connection connection = DatabaseHelper.getInstance().getConnection();
            connection.createStatement().execute(SQL);
        } catch (SQLException ex) {
            MyDialog.showErrorDialog("Error!", ex.getMessage());
        }
    }

    @Override
    public List<Transaction> getAll() {
        String query = String.format("SELECT * FROM %s", TABLE_TRANSACTION);
        List<Transaction> transactionList = new ArrayList<>();

        try {
            Connection connection = DatabaseHelper.getInstance().getConnection();
            PreparedStatement pstmt = connection.prepareStatement(query);
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                //Display values
                String number = String.valueOf(resultSet.getInt("number"));
                String date = resultSet.getString("date");
                double amount = resultSet.getDouble("amount");
                String type = resultSet.getString("type");
                int accountId = resultSet.getInt("account_id");
                Account account = new AccountDao().getById(accountId);

                Transaction transaction = new Transaction(
                        number,
                        date,
                        amount,
                        type.trim().equals("واریز") ? TransactionType.DEPOSIT : TransactionType.WITHDRAW,
                        account);

                transactionList.add(transaction);
            }

        } catch (SQLException e) {
            MyDialog.showErrorDialog("Error!", e.getMessage());
        }

        return transactionList;
    }
}
