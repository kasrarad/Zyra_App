package com.example.zyra;

public class UserInfoDB {

    private Integer ID;
    private String userID;
    private String Name;
    private String Username;
    private String Password;

    public UserInfoDB(Integer ID, String userID, String name, String username, String password) {
        this.ID = ID;
        this.userID = userID;
        Name = name;
        Username = username;
        Password = password;
    }

    public UserInfoDB(String userID, String name, String username, String password) {
        this.userID = userID;
        Name = name;
        Username = username;
        Password = password;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
