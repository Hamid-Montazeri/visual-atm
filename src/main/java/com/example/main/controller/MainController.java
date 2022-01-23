package com.example.main.controller;

import com.example.main.database.BankDatabase;
import com.example.main.model.Account;
import com.example.main.model.AccountOwner;
import com.example.main.model.Transaction;
import com.example.main.model.TransactionType;
import com.example.main.util.DateConverter;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import static com.example.main.util.MyDialog.showErrorDialog;

public class MainController implements Initializable {

    private BankDatabase bankDatabase;
    private ObservableList<Transaction> list;
    private long ACCOUNT_ID;

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

//    @FXML
//    private TableColumn<Transaction, String> trxNumber;
//    @FXML
//    private TableColumn<Transaction, String> trxDate;
//    @FXML
//    private TableColumn<Transaction, Double> trxAmount;
//    @FXML
//    private TableColumn<Transaction, String> trxType;
//    @FXML
//    private TableColumn<Transaction, Integer> trxAccountId;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bankDatabase = BankDatabase.getInstance();

//        initTable();


    }

    private void initTable() {
        list = FXCollections.observableArrayList(bankDatabase.getAllTransactions());
        tblShowTrx.setItems(list);

        TableColumn<Transaction, String> numberCol = new TableColumn<>("شناسه تراکنش");
        TableColumn<Transaction, String> dateCol = new TableColumn<>("تاریخ");
        TableColumn<Transaction, Double> amountCol = new TableColumn<>("مبلغ");
        TableColumn<Transaction, Object> typeCol = new TableColumn<>("نوع تراکنش");

        numberCol.setCellValueFactory(new PropertyValueFactory<>("number"));
        dateCol.setCellValueFactory((new PropertyValueFactory<>("date")));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("transactionType"));

        tblShowTrx.getColumns().setAll(numberCol, dateCol, amountCol, typeCol);

    }

    @FXML
    protected void openAccount() {
        String ownerName = tfName.getText().trim();
        String ownerFamily = tfFamily.getText().trim();
        String ownerNationalCode = tfNationalCode.getText().trim();
        if (dp.getValue() == null) {
            dp.setValue(LocalDate.now());
        }
        String date = dp.getValue().toString();

        AccountOwner owner = new AccountOwner();
        owner.setName(ownerName);
        owner.setFamily(ownerFamily);
        try {
            owner.setNationalCode(ownerNationalCode);
        } catch (Exception e) {
            showErrorDialog("Error!", e.getMessage());
            return;
        }
        owner.setBirthDate(date);

        Account account = new Account(owner);
        ACCOUNT_ID = bankDatabase.insertAccount(account);
        bankDatabase.insertAccountOwner(account, ACCOUNT_ID);
    }

    @FXML
    protected void editAccountOwner() {
        String ownerName = tfEditName.getText().trim();
        String ownerFamily = tfEditFamily.getText().trim();
        String ownerNationalCode = tfEditNationalCode.getText().trim();
        if (editDp.getValue() == null) {
            editDp.setValue(LocalDate.now());
        }
        String date = editDp.getValue().toString();
        AccountOwner owner = new AccountOwner();
        owner.setName(ownerName);
        owner.setFamily(ownerFamily);
        try {
            owner.setNationalCode(ownerNationalCode);
        } catch (Exception e) {
            showErrorDialog("Error!", e.getMessage());
            return;
        }
        owner.setBirthDate(date);
        long accountId = Long.parseLong(tfEditAccountId.getText().trim());

        bankDatabase.updateAccountOwner(owner, accountId);

    }

    @FXML
    private void deposit() {
        Transaction transaction = new Transaction(
                String.valueOf(new Random().nextInt(1000, 9999)),
                DateConverter.getPersianDate(),
                Double.parseDouble(tfDepositAmount.getText().trim()),
                TransactionType.DEPOSIT
        );
        // insert transaction to database
        bankDatabase.insertTransaction(
                transaction,
                Long.parseLong(tfDepositAccountId.getText().trim())
        );

        // get previous balance from database
        double prevBalance = bankDatabase.findAccountById(Long.parseLong(tfDepositAccountId.getText().trim()))
                .getBalance();

        double depositAmount = Double.parseDouble(tfDepositAmount.getText().trim());

        // update account balance in database
        bankDatabase.updateAccountBalance(
                prevBalance + depositAmount,
                Long.parseLong(tfDepositAccountId.getText().trim())
        );
    }

    @FXML
    private void withdraw() {

        // get previous balance from database
        double prevBalance = bankDatabase.findAccountById(Long.parseLong(tfWithdrawAccountId.getText().trim()))
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
                TransactionType.WITHDRAW
        );

        // insert transaction to database
        bankDatabase.insertTransaction(
                transaction,
                Long.parseLong(tfWithdrawAccountId.getText().trim())
        );

        // update account balance in database
        bankDatabase.updateAccountBalance(
                prevBalance + withdrawAmount,
                Long.parseLong(tfWithdrawAccountId.getText().trim())
        );

    }

    @FXML
    private void deleteAllTransactions() {
        tblShowTrx.getItems().clear();
        bankDatabase.deleteAllTransactions();
    }

    @FXML
    protected void showAllTransactions() {
        initTable();

        if (list.size() == 0) {
            showErrorDialog("Error!", "There is No Transactions in Database!");
        }
    }

    @FXML
    protected void onDepositTabSelected() {
        if (anyAccountExists()) {
            double balance = bankDatabase.getAllAccounts().get(0).getBalance();
            DecimalFormat format = new DecimalFormat("###,###");
            String formattedBalance = format.format(balance);
            lblBalance.setText("موجودی حساب: " + formattedBalance + " ريال");
        }
    }

    private boolean anyAccountExists() {
        List<Account> allAccounts = bankDatabase.getAllAccounts();
        List<AccountOwner> allAccountOwners = bankDatabase.getAllAccountOwners();
        return allAccounts.size() > 0 || allAccountOwners.size() > 0;
    }


}