package com.atta.myapp.model;

public class Order {


    String orderId, user, email, orderImage, orderTitle, orderDescription;


    public Order() {
    }

    public Order(String user, String email, String orderImage, String orderTitle, String orderDescription) {

        this.user = user;
        this.email = email;
        this.orderImage = orderImage;
        this.orderTitle = orderTitle;
        this.orderDescription = orderDescription;
    }


    public Order(String orderId, String user, String email, String orderImage, String orderTitle, String orderDescription) {
        this.orderId = orderId;
        this.user = user;
        this.email = email;
        this.orderImage = orderImage;
        this.orderTitle = orderTitle;
        this.orderDescription = orderDescription;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setUser(String user) {
        user = user;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setOrderImage(String orderImage) {
        this.orderImage = orderImage;
    }

    public void setOrderTitle(String orderTitle) {
        this.orderTitle = orderTitle;
    }

    public void setOrderDescription(String orderDescription) {
        this.orderDescription = orderDescription;
    }

    public String getUser() {

        return user;
    }

    public String getEmail() {
        return email;
    }

    public String getOrderImage() {
        return orderImage;
    }

    public String getOrderTitle() {
        return orderTitle;
    }

    public String getOrderDescription() {
        return orderDescription;
    }
}
