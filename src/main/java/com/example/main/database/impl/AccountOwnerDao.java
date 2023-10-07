package com.example.main.database.impl;

import com.example.main.database.Dao;
import com.example.main.database.DatabaseHelper;
import com.example.main.model.AccountOwner;
import com.example.main.util.MyDialog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.example.main.database.DatabaseUtils.TABLE_ACCOUNT;
import static com.example.main.database.DatabaseUtils.TABLE_ACCOUNT_OWNER;

public class AccountOwnerDao implements Dao<AccountOwner> {

    @Override
    public long save(AccountOwner accountOwner) {
        long genKey = 0;

        try {
            Connection connection = DatabaseHelper.getInstance().getConnection();
            if (connection == null || connection.isClosed()) {
                return 0;
            }
            String query = String.format("INSERT INTO %s (name,family,national_code,birth_date) VALUES (?,?,?,?)", TABLE_ACCOUNT_OWNER);
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, accountOwner.getName());
            ps.setString(2, accountOwner.getFamily());
            ps.setString(3, accountOwner.getNationalCode());
            ps.setString(4, accountOwner.getBirthDate());

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
    public AccountOwner getById(long id) {
        try {
            Connection connection = DatabaseHelper.getInstance().getConnection();
            if (connection == null || connection.isClosed()) {
                return null;
            }
            String query = String.format("SELECT * from %s WHERE id = ?", TABLE_ACCOUNT_OWNER);
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setLong(1, id);
            ResultSet resultSet = ps.executeQuery();

            AccountOwner accountOwner = new AccountOwner();

            if (resultSet.first()) {
                accountOwner.setName(resultSet.getString("name"));
                accountOwner.setFamily(resultSet.getString("family"));
                accountOwner.setNationalCode(resultSet.getString("national_code"));
                accountOwner.setBirthDate(resultSet.getString("birth_date"));
//                accountOwner.setAccounts();
            }
            return accountOwner;
        } catch (SQLException e) {
            MyDialog.showErrorDialog("Error", e.getMessage());
        }
        return null;
    }

    @Override
    public void update(AccountOwner accountOwner, long id) {
        AccountOwner existingAccountOwner = getById(id);

        existingAccountOwner.setName(accountOwner.getName());
        existingAccountOwner.setFamily(accountOwner.getFamily());
        existingAccountOwner.setNationalCode(accountOwner.getNationalCode());
        existingAccountOwner.setBirthDate(accountOwner.getBirthDate());
        existingAccountOwner.setAccounts(accountOwner.getAccounts());

        save(existingAccountOwner);
    }

    @Override
    public void deleteById(long id) {
        try {
            Connection connection = DatabaseHelper.getInstance().getConnection();
            if (connection == null || connection.isClosed()) {
                return;
            }
            String query = String.format("DELETE FROM %S WHERE id = ?", TABLE_ACCOUNT_OWNER);
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setLong(1, id);
            boolean executed = ps.execute();
            System.out.println("Delete Account Owner from db with id " + id + ": " + executed);
        } catch (SQLException e) {
            MyDialog.showErrorDialog("Error", e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        String SQL = String.format("DELETE FROM %s", TABLE_ACCOUNT_OWNER);
        try {
            Connection connection = DatabaseHelper.getInstance().getConnection();
            connection.createStatement().execute(SQL);
        } catch (SQLException ex) {
            MyDialog.showErrorDialog("Error!", ex.getMessage());
        }
    }

    @Override
    public List<AccountOwner> getAll() {
        List<AccountOwner> accountOwners = new ArrayList<>();

        try {
            Connection connection = DatabaseHelper.getInstance().getConnection();

            if (connection == null || connection.isClosed()) {
                return null;
            }

            String query = String.format("SELECT * FROM %s", TABLE_ACCOUNT);
            ResultSet resultSet = connection.createStatement().executeQuery(query);

            while (resultSet.next()) {
                AccountOwner accountOwner = new AccountOwner();
                accountOwner.setName(resultSet.getString("name"));
                accountOwner.setFamily(resultSet.getString("family"));
                accountOwner.setNationalCode(resultSet.getString("national_code"));
                accountOwner.setBirthDate(resultSet.getString("birth_date"));
//                accountOwner.setAccounts();
                accountOwners.add(accountOwner);
            }
        } catch (SQLException e) {
            MyDialog.showErrorDialog("Error", e.getMessage());
        }

        return accountOwners;
    }
}
