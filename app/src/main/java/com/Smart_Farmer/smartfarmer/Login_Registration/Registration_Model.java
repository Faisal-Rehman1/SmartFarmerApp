package com.Smart_Farmer.smartfarmer.Login_Registration;

public class Registration_Model {

    String email, username, joiningCat, userAddress, ProfileImageUri;

    public Registration_Model(String email, String username, String joiningCat, String userAddress, String ProfileImageUri) {
        this.email = email;
        this.username = username;
        this.joiningCat = joiningCat;
        this.userAddress = userAddress;
        this.ProfileImageUri = ProfileImageUri;
    }

    public String getProfileImageUri() {
        return ProfileImageUri;
    }

    public void setProfileImageUri(String profileImageUri) {
        ProfileImageUri = profileImageUri;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getjoiningCat() {
        return joiningCat;
    }

    public void setjoiningCat(String joiningCat) {
        this.joiningCat = joiningCat;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }
}
