package com.atta.myapp.model;

public class User {

    String userName, email, image;

    public User(String userName, String email, String image) {
        this.userName = userName;
        this.email = email;
        this.image = image;
    }

    public User() {
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getImage() {
        return image;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
