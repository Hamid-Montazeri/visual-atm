package com.example.main.database;

import com.example.main.model.Account;
import com.example.main.model.AccountOwner;
import com.example.main.model.Transaction;
import com.example.main.util.MyDialog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.example.main.database.DatabaseUtils.*;


public class DatabaseHelper {

    private Connection connection;

    private static DatabaseHelper databaseHelper;

    // singleton
    public static DatabaseHelper getInstance() {
        try {
            if (databaseHelper == null) {
                databaseHelper = new DatabaseHelper();
            } else if (databaseHelper.getConnection().isClosed()) {
                databaseHelper = new DatabaseHelper();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return databaseHelper;
    }

    private DatabaseHelper() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public void createTables(Connection connection) {
        try {
            boolean accountTableCreated = connection.createStatement().execute(CREATE_TABLE_ACCOUNT);
            boolean ownerTableCreated = connection.createStatement().execute(CREATE_TABLE_ACCOUNT_OWNER);
            boolean transactionTableCreated = connection.createStatement().execute(CREATE_TABLE_TRANSACTION);

            if (accountTableCreated && ownerTableCreated && transactionTableCreated) {
                MyDialog.showInfoDialog("Create Tables", "All Tables Created Successfully");
                System.out.println("All Tables Created Successfully");
            }

        } catch (SQLException e) {
            MyDialog.showErrorDialog("Create Tables", "Failed to Create Tables");
            System.err.println("Failed to Create Tables");
        }

    }

    public long insertAccount(Account account) {
        String insert_account_query = String.format("INSERT INTO %s (number,balance,owner) VALUES (?,?,?)", TABLE_ACCOUNT);

        long genKey = 0;

        try {
            PreparedStatement ps = connection.prepareStatement(insert_account_query, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, account.getNumber());
            ps.setDouble(2, account.getBalance());
            ps.setString(3, account.getOwner().getName() + " " + account.getOwner().getFamily());

            int affectedAccountRows = ps.executeUpdate();

            if (affectedAccountRows > 0) {
                // get the ID back
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    genKey = rs.getLong(1);
                }
            }

        } catch (SQLException ex) {
            MyDialog.showErrorDialog("Inserting 'Account' to database failed!", ex.getMessage());
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
                ResultSet rs = owner_pstmt.getGeneratedKeys();
                if (rs.next()) {
                    genKey = rs.getLong(1);
                }
            }

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
                ResultSet rs = trxPs.getGeneratedKeys();
                if (rs.next()) {
                    genKey = rs.getLong(1);
                }

            }

        } catch (SQLException ex) {
            MyDialog.showErrorDialog("Error!", ex.getMessage());
        }

        return genKey;
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
        } catch (SQLException ex) {
            MyDialog.showErrorDialog("Error!", ex.getMessage());
        }

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

        } catch (SQLException e) {
            MyDialog.showErrorDialog("Error!", e.getMessage());
        }

        return accounts;

    }


    public Connection getConnection() {
        return connection;
    }

}
