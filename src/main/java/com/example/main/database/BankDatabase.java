package com.example.main.database;

import com.example.main.model.Account;
import com.example.main.model.AccountOwner;
import com.example.main.model.Transaction;
import com.example.main.model.TransactionType;
import com.example.main.util.MyDialog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.example.main.util.Constants.*;


public class BankDatabase {

    private Connection connection;

    private static BankDatabase bankDatabase;

    private BankDatabase() {
        connection = connect();
        createTables();
    }

    // singleton
    public static BankDatabase getInstance() {

        synchronized (BankDatabase.class) {
            if (bankDatabase == null) {
                bankDatabase = new BankDatabase();
            }
        }

        return bankDatabase;
    }

    private Connection connect() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to the PostgreSQL server successfully.");
//            MyDialog.showDialog("Database Connection", "Connected to the 'PostgreSQL' server successfully");
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
//            MyDialog.showDialog("Database Connection", "Failed to Connected to the 'PostgreSQL' server");
        }
        return connection;
    }

    private void createTables() {
        try {
            connection.createStatement().execute(CREATE_TABLE_ACCOUNT);
            connection.createStatement().execute(CREATE_TABLE_ACCOUNT_OWNER);
            connection.createStatement().execute(CREATE_TABLE_TRANSACTION);

            MyDialog.showInfoDialog("Create Tables", "All Tables Created Successfully");
            System.out.println("All Tables Created Successfully");

        } catch (SQLException e) {
            MyDialog.showErrorDialog("Create Tables", "Failed to Create Tables");
            System.err.println("Failed to Create Tables");
        }

    }

    public long insertAccount(Account account) {
        String insert_account_query = String.format("INSERT INTO %s (number,balance,owner) VALUES (?,?,?)", TABLE_ACCOUNT);

        long genKey = 0;

        try {
            PreparedStatement account_pstmt = connection.prepareStatement(insert_account_query, Statement.RETURN_GENERATED_KEYS);

            account_pstmt.setString(1, account.getNumber());
            account_pstmt.setDouble(2, account.getBalance());
            account_pstmt.setString(3, account.getOwner().getName() + " " + account.getOwner().getFamily());

            int affectedAccountRows = account_pstmt.executeUpdate();

            if (affectedAccountRows > 0) {
                System.out.println("'Account' Inserted Successfully");
                // get the ID back
                try (ResultSet rs = account_pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        genKey = rs.getLong(1);
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
//            connection.close();

        } catch (SQLException ex) {
            MyDialog.showErrorDialog(
                    "Inserting 'Account' to database failed!", ex.getMessage());
        }

        return genKey;

    }

    public long insertAccountOwner(Account account, long accountId) {
        String INSERT_ACCOUNT_OWNER = String.format(
                "INSERT INTO %s (name,family,national_code,birth_date,account_id) VALUES (?,?,?,?,?)",
                TABLE_ACCOUNT_OWNER
        );

        long genKey = 0;

        try {
            PreparedStatement owner_pstmt = connection.prepareStatement(INSERT_ACCOUNT_OWNER, Statement.RETURN_GENERATED_KEYS);
            owner_pstmt.setString(1, account.getOwner().getName());
            owner_pstmt.setString(2, account.getOwner().getFamily());
            owner_pstmt.setString(3, account.getOwner().getNationalCode());
            owner_pstmt.setString(4, account.getOwner().getBirthDate());
            owner_pstmt.setLong(5, accountId);

            int affectedOwnerRows = owner_pstmt.executeUpdate();

            if (affectedOwnerRows > 0) {
                MyDialog.showInfoDialog("Successful Operation", "'Account Owner' Inserted Successfully");
                // get the ID back
                try (ResultSet rs = owner_pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        genKey = rs.getLong(1);
                    }
                } catch (SQLException ex) {
                    MyDialog.showErrorDialog("Error in inserting 'Account' Owner to database", ex.getMessage());
                }
            }

//            connection.close();

        } catch (SQLException ex) {
            MyDialog.showErrorDialog("Error!", ex.getMessage());
        }

        return genKey;
    }

    public long insertTransaction(Transaction transaction, long accountId) {
        String insert_trx_query = String.format(
                "INSERT INTO %s (number,date,amount,type,account_id) VALUES (?,?,?,?,?)",
                TABLE_TRANSACTION
        );

        long genKey = 0;

        try {
            PreparedStatement trxPs = connection.prepareStatement(insert_trx_query, Statement.RETURN_GENERATED_KEYS);

            trxPs.setInt(1, Integer.parseInt(transaction.getNumber()));
            trxPs.setString(2, transaction.getDate());
            trxPs.setDouble(3, transaction.getAmount());
            trxPs.setString(4, transaction.getTransactionType().getValue());
            trxPs.setLong(5, accountId);

            int affectedAccountRows = trxPs.executeUpdate();

            if (affectedAccountRows > 0) {
                MyDialog.showInfoDialog("Successful Operation", "'Transaction' Inserted Successfully");
                // get the ID back
                try (ResultSet rs = trxPs.getGeneratedKeys()) {
                    if (rs.next()) {
                        genKey = rs.getLong(1);
                    }
                } catch (SQLException ex) {
                    MyDialog.showErrorDialog("Error!", ex.getMessage());
                }
            }

//            connection.close();

        } catch (SQLException ex) {
            MyDialog.showErrorDialog("Error!", ex.getMessage());
        }

        return genKey;
    }

    public List<Transaction> getAllTransactions() {
        String query = String.format("SELECT * FROM %s", TABLE_TRANSACTION);
        List<Transaction> transactionList = new ArrayList<>();
        try {
            PreparedStatement pstmt = connection.prepareStatement(query);
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                //Display values
                String number = String.valueOf(resultSet.getInt("number"));
                String date = resultSet.getString("date");
                double amount = resultSet.getDouble("amount");
                String type = resultSet.getString("type");
                int accountId = resultSet.getInt("account_id");

                Transaction transaction = new Transaction(
                        number,
                        date,
                        amount,
                        type.equals("واریز") ? TransactionType.DEPOSIT : TransactionType.WITHDRAW
                );

                transactionList.add(transaction);


            }

//            connection.close();

        } catch (SQLException e) {
            MyDialog.showErrorDialog("Error!", e.getMessage());
        }

        return transactionList;
    }

    public void updateAccountOwner(AccountOwner owner, long accountId) {
        String ownerSql = String.format("UPDATE %s SET name = ? , family = ? , national_code = ? , birth_date = ? WHERE account_id = ?", TABLE_ACCOUNT_OWNER);
        String accountSql = String.format("UPDATE %s SET owner = ? WHERE id = ?", TABLE_ACCOUNT);

        int affectedrows = 0;

        try {
            PreparedStatement ownerPs = connection.prepareStatement(ownerSql);
            PreparedStatement accountPs = connection.prepareStatement(accountSql);

            ownerPs.setString(1, owner.getName());
            ownerPs.setString(2, owner.getFamily());
            ownerPs.setString(3, owner.getNationalCode());
            ownerPs.setString(4, owner.getBirthDate());
            ownerPs.setLong(5, accountId);

            accountPs.setString(1, owner.getName() + " " + owner.getFamily());
            accountPs.setLong(2, accountId);

            ownerPs.executeUpdate();
            accountPs.executeUpdate();

            MyDialog.showInfoDialog("Successful Operation", "Account updated successfully");

//            connection.close();

        } catch (SQLException ex) {
            MyDialog.showErrorDialog("Error!", ex.getMessage());
        }

    }

    public int updateAccountBalance(double amount, long id) {
        String SQL = String.format("UPDATE %s SET balance = ? WHERE id = ?", TABLE_ACCOUNT);

        int affectedrows = 0;

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setDouble(1, amount);
            pstmt.setLong(2, id);

            affectedrows = pstmt.executeUpdate();

            MyDialog.showInfoDialog("Insert Records", "Number of Affected Rows: " + affectedrows);

//            connection.close();

        } catch (SQLException ex) {
            MyDialog.showErrorDialog("Error!", ex.getMessage());
        }
        return affectedrows;
    }

    public long deleteAllTransactions() {
        String SQL = "DELETE FROM " + TABLE_TRANSACTION;
        try {
            PreparedStatement pstmt = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            int affectedRows = pstmt.executeUpdate();
            MyDialog.showInfoDialog("Delete Records", "Number of Deleted Rows: " + affectedRows);

//            connection.close();

            return affectedRows;

        } catch (SQLException ex) {
            MyDialog.showErrorDialog("Error!", ex.getMessage());
        }
        return 0;
    }

    public List<AccountOwner> getAllAccountOwners() {
        List<AccountOwner> accountOwners = new ArrayList<>();
        String SQL = "SELECT * from " + TABLE_ACCOUNT_OWNER;

        PreparedStatement pstmt;
        ResultSet resultSet;

        try {
            pstmt = connection.prepareStatement(SQL);
            resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                //Display values
                long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                String family = resultSet.getString("family");
                String nationalCode = resultSet.getString("national_code");
                String birthDate = resultSet.getString("birth_date");

                AccountOwner accountOwner = new AccountOwner(name, family, nationalCode, birthDate, null);
                accountOwners.add(accountOwner);
            }
//            connection.close();

        } catch (SQLException e) {
            MyDialog.showErrorDialog("Error!", e.getMessage());
        }

        return accountOwners;

    }

    public List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();

        String SQL = "SELECT * from " + TABLE_ACCOUNT;

        PreparedStatement pstmt;
        try {
            pstmt = connection.prepareStatement(SQL);
            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                //Display values
                long id = resultSet.getLong("id");
                String number = resultSet.getString("number");
                double balance = resultSet.getDouble("balance");
                String owner = resultSet.getString("owner");

                Account account = new Account();
                account.setNumber(number);
                account.setBalance(balance);
                accounts.add(account);
            }

//            connection.close();

        } catch (SQLException e) {
            MyDialog.showErrorDialog("Error!", e.getMessage());
        }

        return accounts;

    }

    public Account findAccountById(long id) {
        String SQL = String.format("SELECT * from %s WHERE id = ?", TABLE_ACCOUNT);

        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(SQL);
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
//                connection.close();
                return account;
            }
        } catch (SQLException throwable) {
            MyDialog.showErrorDialog("Error!", throwable.getMessage());
        }

        return null;

    }


}
