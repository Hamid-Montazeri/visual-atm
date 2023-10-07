package com.example.main.controller;

import com.example.main.database.DatabaseHelper;
import com.example.main.database.impl.AccountDao;
import com.example.main.database.impl.AccountOwnerDao;
import com.example.main.database.impl.TransactionDao;
import com.example.main.model.Account;
import com.example.main.model.AccountOwner;
import com.example.main.model.Transaction;
import com.example.main.model.TransactionType;
import com.example.main.util.DateConverter;
import com.example.main.util.MyDialog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import static com.example.main.util.MyDialog.showErrorDialog;

public class MainController implements Initializable {

    private AccountDao accountDao;
    private AccountOwnerDao accountOwnerDao;
    private TransactionDao transactionDao;
    Account account;
    long accountId;

    @FXML
    private TextField tfName;
    @FXML
    private TextField tfFamily;
    @FXML
    private TextField tfNationalCode;
    @FXML
    private DatePicker dp;
    @FXML
    private TextField tfEditAccountId;
    @FXML
    private TextField tfEditName;
    @FXML
    private TextField tfEditFamily;
    @FXML
    private TextField tfEditNationalCode;
    @FXML
    private TextField tfDepositAmount;
    @FXML
    private Label lblBalance;
    @FXML
    private TextField tfWithdrawAmount;
    @FXML
    private TextField tfDepositAccountId;
    @FXML
    private TextField tfWithdrawAccountId;
    @FXML
    private DatePicker editDp;
    @FXML
    private TableView<Transaction> tblShowTrx;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        accountDao = new AccountDao();
        accountOwnerDao = new AccountOwnerDao();
        transactionDao = new TransactionDao();


        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
        Connection connection = databaseHelper.getConnection();
        if (connection != null) {
            databaseHelper.createTables(connection);
        }
    }

    @FXML
    protected void openAccount() {
        AccountOwner accountOwner = createAccountOwner();
        if (accountOwner == null) return;

        Account account = new Account(accountOwner, 0);
        accountId = accountDao.save(account);

        accountOwner.setAccounts(List.of(account));
        accountOwnerDao.save(accountOwner);
    }

    private AccountOwner createAccountOwner() {
        AccountOwner accountOwner = new AccountOwner();
        accountOwner.setName(tfName.getText().trim());
        accountOwner.setFamily(tfFamily.getText().trim());
        accountOwner.setBirthDate(
                dp.getValue() == null ? String.valueOf(LocalDate.now()) : dp.getValue().toString()
        );
        try {
            accountOwner.setNationalCode(tfNationalCode.getText().trim());
        } catch (Exception e) {
            showErrorDialog("Error!", e.getMessage());
            return null;
        }
        return accountOwner;
    }

    @FXML
    protected void editAccountOwner() {
        try {
            AccountOwner newAccountOwner = new AccountOwner();
            newAccountOwner.setName(tfEditName.getText().trim());
            newAccountOwner.setFamily(tfEditFamily.getText().trim());
            newAccountOwner.setBirthDate(editDp.getValue() == null ? String.valueOf(LocalDate.now()) : editDp.getValue().toString());
            newAccountOwner.setNationalCode(tfEditNationalCode.getText().trim());

            long accountId = Long.parseLong(tfEditAccountId.getText().trim());

            accountOwnerDao.update(newAccountOwner, accountId);
        } catch (Exception e) {
            showErrorDialog("Error!", e.getMessage());
        }
    }

    @FXML
    private void deposit() {
        String trxNumber = String.valueOf(new Random().nextInt(1000, 9999));
        String trxDate = DateConverter.getPersianDate();
        double amount = Double.parseDouble(tfDepositAmount.getText().trim());

        Transaction transaction = new Transaction(trxNumber, trxDate, amount, TransactionType.DEPOSIT, account);

        // insert transaction to database
        long generatedTransactionId = transactionDao.save(transaction);

        // get previous balance from database
        double prevBalance = accountDao.findAccountById(Long.parseLong(tfDepositAccountId.getText().trim()))
                .getBalance();

        // update account balance in database
        accountDao.updateAccountBalance(
                prevBalance + amount,
                Long.parseLong(tfDepositAccountId.getText().trim())
        );
    }

    @FXML
    private void withdraw() {
        // get previous balance from database
        double prevBalance = accountDao.findAccountById(Long.parseLong(tfWithdrawAccountId.getText().trim()))
                .getBalance();

        double withdrawAmount = Double.parseDouble(tfWithdrawAmount.getText().trim());

        if (prevBalance - withdrawAmount <= 200000) {
            showErrorDialog("Error!", "Entered amount is more than your balance! (Min balance is 200,000 IRR)");
            return;
        }

        Transaction transaction = new Transaction(
                String.valueOf(new Random().nextInt(1000, 9999)),
                DateConverter.getPersianDate(),
                Double.parseDouble(tfWithdrawAmount.getText().trim()),
                TransactionType.WITHDRAW,
                account);

        // insert transaction to database
        transactionDao.save(transaction);

        // update account balance in database
        accountDao.updateAccountBalance(
                prevBalance - withdrawAmount,
                Long.parseLong(tfWithdrawAccountId.getText().trim())
        );

    }

    @FXML
    private void deleteAllTransactions() {
        tblShowTrx.getItems().clear();
        transactionDao.deleteAll();
    }

    @FXML
    protected void showAllTransactions() {
        ObservableList<Transaction> data = FXCollections.observableArrayList(transactionDao.getAll());
        tblShowTrx.setItems(data);

        TableColumn<Transaction, String> numberCol = new TableColumn<>("شناسه تراکنش");
        TableColumn<Transaction, String> dateCol = new TableColumn<>("تاریخ");
        TableColumn<Transaction, Double> amountCol = new TableColumn<>("مبلغ");
        TableColumn<Transaction, Object> typeCol = new TableColumn<>("نوع تراکنش");

        numberCol.setCellValueFactory(new PropertyValueFactory<>("number"));
        dateCol.setCellValueFactory((new PropertyValueFactory<>("date")));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("transactionType"));


        tblShowTrx.getColumns().setAll(numberCol, dateCol, amountCol, typeCol);

        if (data.isEmpty()) {
            showErrorDialog("Error!", "There is No Transactions in Database!");
        }
    }

    @FXML
    protected void onDepositTabSelected() {
        try {
            if (anyAccountExists()) {
                double balance = accountDao.getAll().get(0).getBalance();
                DecimalFormat format = new DecimalFormat("###,###");
                String formattedBalance = format.format(balance);
                lblBalance.setText("موجودی حساب: " + formattedBalance + " ريال");
            }
        } catch (SQLException e) {
            MyDialog.showErrorDialog("Error", e.getMessage());
        }
    }

    private boolean anyAccountExists() throws SQLException {
        List<Account> accounts = accountDao.getAll();
        ;
        List<AccountOwner> allAccountOwners = accountOwnerDao.getAll();
        ;
        return !accounts.isEmpty() || !allAccountOwners.isEmpty();
    }

}