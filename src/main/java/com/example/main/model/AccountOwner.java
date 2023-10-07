package com.example.main.model;

import java.util.List;

public class AccountOwner {

    private Long id;
    private String name;
    private String family;
    private String nationalCode;
    private String birthDate;
    private List<Account> accounts;

    public AccountOwner() {
    }

    public AccountOwner(String name, String family, String nationalCode, String birthDate, List<Account> accounts) {
        this.name = name;
        this.family = family;
        this.nationalCode = nationalCode;
        this.birthDate = birthDate;
        this.accounts = accounts;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getNationalCode() {
        return nationalCode;
    }

    public void setNationalCode(String nationalCode) {
        this.nationalCode = nationalCode;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
}
