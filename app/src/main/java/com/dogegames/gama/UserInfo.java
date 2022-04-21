package com.dogegames.gama;

public class UserInfo implements java.io.Serializable {
    String userName;
    int ownConsoleCount;
    int ownTitleCount;
    int totalPrice;

    UserInfo() {
        this.userName = "Unknown";
        this.ownConsoleCount=0;
        this.ownTitleCount=0;
        this.totalPrice=0;
    }

    UserInfo(String userName) {
        this.userName = userName;
        this.ownConsoleCount=0;
        this.ownTitleCount=0;
        this.totalPrice=0;
    }

    UserInfo(String userName, int ownConsoleCount, int ownTitleCount, int totalPrice) {
        this.userName = userName;
        this.ownTitleCount = ownTitleCount;
        this.totalPrice = totalPrice;
        this.ownConsoleCount=ownConsoleCount;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setOwnTitleCount(int ownTitleCount) {
        this.ownTitleCount = ownTitleCount;
    }

    public int getOwnTitleCount() {
        return ownTitleCount;
    }

    public void setOwnConsoleCount(int ownConsoleCount) {
        this.ownConsoleCount = ownConsoleCount;
    }

    public int getOwnConsoleCount() {
        return ownConsoleCount;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public String toString(){
        return "("+userName+","+ownConsoleCount+","+ownTitleCount+","+totalPrice+")";
    }
}
