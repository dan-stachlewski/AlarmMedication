package com.example.dnafv.alarmmedication;

public class UserProfileModel {

    //Declare the Variables for the Model
    private int id;
    private String firstName;
    private String lastName;
    private String bloodGroup;
    private String contactNumber;
    private String DOB;
    private String emailAddress;
    private byte[] userProfileImage;

    //Create the Constructor using: right-click>Generate>Constructor>Choose Variables to use
    public UserProfileModel(int id,
                            String firstName,
                            String lastName,
                            String bloodGroup,
                            String contactNumber,
                            String DOB,
                            String emailAddress,
                            byte[] userProfileImage) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.bloodGroup = bloodGroup;
        this.contactNumber = contactNumber;
        this.DOB = DOB;
        this.emailAddress = emailAddress;
        this.userProfileImage = userProfileImage;
    }

    //Create the Getter & Setters using: right-click>Generate>Getters & Setters>Choose Variables to use

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public byte[] getUserProfileImage() {
        return userProfileImage;
    }

    public void setUserProfileImage(byte[] userProfileImage) {
        this.userProfileImage = userProfileImage;
    }
}
