package com.kushina.merchant.android.navigations.withdrawals;

public class RVWithdrawHistoryModel {

    private String withdrawID;
    private String codeID;
    private String userID;
    private String customerName;
    private String customerNumber;
    private String statusID;
    private String status;
    private String withdrawMethodID;
    private String withdrawMethod;
    private String accountName;
    private String accountNumber;
    private String amount;
    private String dateCreated;

    public RVWithdrawHistoryModel(String withdrawID, String codeID, String userID, String customerName, String customerNumber, String statusID, String status, String withdrawMethodID, String withdrawMethod, String accountName, String accountNumber, String amount, String dateCreated) {
        this.withdrawID = withdrawID;
        this.codeID = codeID;
        this.userID = userID;
        this.customerName = customerName;
        this.customerNumber = customerNumber;
        this.statusID = statusID;
        this.status = status;
        this.withdrawMethodID = withdrawMethodID;
        this.withdrawMethod = withdrawMethod;
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.dateCreated = dateCreated;
    }

    public String getWithdrawID() {
        return withdrawID;
    }

    public String getCodeID() {
        return codeID;
    }

    public String getUserID() {
        return userID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public String getStatusID() {
        return statusID;
    }

    public String getStatus() {
        return status;
    }

    public String getWithdrawMethodID() {
        return withdrawMethodID;
    }

    public String getWithdrawMethod() {
        return withdrawMethod;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAmount() {
        return amount;
    }

    public String getDateCreated() {
        return dateCreated;
    }
}
