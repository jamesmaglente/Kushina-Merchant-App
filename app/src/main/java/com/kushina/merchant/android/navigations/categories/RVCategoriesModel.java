package com.kushina.merchant.android.navigations.categories;

public class RVCategoriesModel {

    private String categoryID;
    private String description;
    private String status;
    private String dateCreated;

    public RVCategoriesModel(String categoryID, String description, String status, String dateCreated) {
        this.categoryID = categoryID;
        this.description = description;
        this.status = status;
        this.dateCreated = dateCreated;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getDateCreated() {
        return dateCreated;
    }
}
