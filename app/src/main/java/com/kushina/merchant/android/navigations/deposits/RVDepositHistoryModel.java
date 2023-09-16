package com.kushina.merchant.android.navigations.deposits;

public class RVDepositHistoryModel {

    private String depositID;
    private String reference;
    private String statusID;
    private String status;
    private String depositMethodID;
    private String depositMethod;
    private String userID;
    private String amount;
    private String proofOfPaymentImage;
    private String date_submitted;
    private String firstname;
    private String lastname;
    private String fullname;
    private String email;
    private String mobile;

    public RVDepositHistoryModel(String depositID, String reference, String statusID, String status, String depositMethodID, String depositMethod, String userID, String amount, String proofOfPaymentImage, String date_submitted, String firstname, String lastname, String fullname, String email, String mobile) {
        this.depositID = depositID;
        this.reference = reference;
        this.statusID = statusID;
        this.status = status;
        this.depositMethodID = depositMethodID;
        this.depositMethod = depositMethod;
        this.userID = userID;
        this.amount = amount;
        this.proofOfPaymentImage = proofOfPaymentImage;
        this.date_submitted = date_submitted;
        this.firstname = firstname;
        this.lastname = lastname;
        this.fullname = fullname;
        this.email = email;
        this.mobile = mobile;
    }

    public String getDepositID() {
        return depositID;
    }

    public String getReference() {
        return reference;
    }

    public String getStatusID() {
        return statusID;
    }

    public String getStatus() {
        return status;
    }

    public String getDepositMethodID() {
        return depositMethodID;
    }

    public String getDepositMethod() {
        return depositMethod;
    }

    public String getUserID() {
        return userID;
    }

    public String getAmount() {
        return amount;
    }

    public String getProofOfPaymentImage() {
        return proofOfPaymentImage;
    }

    public String getDate_submitted() {
        return date_submitted;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }
}
