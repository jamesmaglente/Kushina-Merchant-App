package com.kushina.merchant.android.navigations.orders;

public class SpinnerStatusesModel {

    private String statusID;
    private String status;

    public SpinnerStatusesModel(String statusID, String status) {
        this.statusID = statusID;
        this.status = status;
    }

    public String getStatusID() {
        return statusID;
    }

    public String getStatus() {
        return status;
    }
}
