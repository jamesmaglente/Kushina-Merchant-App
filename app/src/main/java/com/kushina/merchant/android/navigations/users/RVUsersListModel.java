package com.kushina.merchant.android.navigations.users;

public class RVUsersListModel {

    private String userID;
    private String codeID;
    private String userGroupID;
    private String groupName;
    private String memberShipTypeID;
    private String membershipType;
    private String referByID;
    private String code;
    private String availableECashClaims;
    private String username;
    private String ecash;
    private String toque;
    private String email;
    private String mobile;
    private String firstname;
    private String middlename;
    private String lastname;
    private String gender;
    private String age;
    private String profilePicture;
    private String status;
    private String dateCreated;

    public RVUsersListModel(String userID, String codeID, String userGroupID, String groupName, String memberShipTypeID, String membershipType, String referByID, String code, String availableECashClaims, String username, String ecash, String toque, String email, String mobile, String firstname, String middlename, String lastname, String gender, String age, String profilePicture, String status, String dateCreated) {
        this.userID = userID;
        this.codeID = codeID;
        this.userGroupID = userGroupID;
        this.groupName = groupName;
        this.memberShipTypeID = memberShipTypeID;
        this.membershipType = membershipType;
        this.referByID = referByID;
        this.code = code;
        this.availableECashClaims = availableECashClaims;
        this.username = username;
        this.ecash = ecash;
        this.toque = toque;
        this.email = email;
        this.mobile = mobile;
        this.firstname = firstname;
        this.middlename = middlename;
        this.lastname = lastname;
        this.gender = gender;
        this.age = age;
        this.profilePicture = profilePicture;
        this.status = status;
        this.dateCreated = dateCreated;
    }

    public String getUserID() {
        return userID;
    }

    public String getCodeID() {
        return codeID;
    }

    public String getUserGroupID() {
        return userGroupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getMemberShipTypeID() {
        return memberShipTypeID;
    }

    public String getMembershipType() {
        return membershipType;
    }

    public String getReferByID() {
        return referByID;
    }

    public String getCode() {
        return code;
    }

    public String getAvailableECashClaims() {
        return availableECashClaims;
    }

    public String getUsername() {
        return username;
    }

    public String getEcash() {
        return ecash;
    }

    public String getToque() {
        return toque;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public String getLastname() {
        return lastname;
    }

    public String getGender() {
        return gender;
    }

    public String getAge() {
        return age;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public String getStatus() {
        return status;
    }

    public String getDateCreated() {
        return dateCreated;
    }
}
