package com.example.main.util;

public class Constants {

    public static final double MIN_BALANCE = 200000;
    public static final String DASH_LINE = "\n------------------------------\n";

    // database
    public static final String URL = "jdbc:postgresql://localhost:5432/atm_db";
    public static final String USER = "postgres";
    public static final String PASSWORD = "123456";
    // tables
    public static final String TABLE_ACCOUNT = "account";
    public static final String TABLE_ACCOUNT_OWNER = "account_owner";
    public static final String TABLE_TRANSACTION = "transaction";

    // queries
    public static final String CREATE_TABLE_ACCOUNT = String.format(
            "CREATE TABLE IF NOT EXISTS %s (id serial primary key, number varchar,  balance double precision, owner varchar)",
            TABLE_ACCOUNT
    );
    public static final String CREATE_TABLE_ACCOUNT_OWNER = String.format(
            "CREATE TABLE IF NOT EXISTS %s (id serial primary key, name varchar, family varchar, national_code varchar, birth_date varchar, account_id int)",
            TABLE_ACCOUNT_OWNER
    );
    public static final String CREATE_TABLE_TRANSACTION = String.format(
            "CREATE TABLE IF NOT EXISTS %s (id serial primary key, number int, date varchar, amount double precision, type varchar, account_id int)",
            TABLE_TRANSACTION
    );


}
