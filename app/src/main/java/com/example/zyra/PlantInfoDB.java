package com.example.zyra;

public class PlantInfoDB {

    private Integer ID;
    private String userID;
    private String nameBySpecies;
    private String nameByUser;
    private String temperature;
    private String moisture;
    private String previousMoisturesLevel;
    private String image;
    private String wiki;
    private Integer syncstatus;

    public PlantInfoDB(Integer ID, String userID, String nameBySpecies, String nameByUser, String temperature, String moisture, String previousMoisturesLevel, String image, String wiki, Integer syncstatus) {
        this.ID = ID;
        this.userID = userID;
        this.nameBySpecies = nameBySpecies;
        this.nameByUser = nameByUser;
        this.temperature = temperature;
        this.moisture = moisture;
        this.previousMoisturesLevel = previousMoisturesLevel;
        this.image = image;
        this.wiki = wiki;
        this.syncstatus = syncstatus;
    }

    public PlantInfoDB(String userID, String nameBySpecies, String nameByUser, String temperature, String moisture, String previousMoisturesLevel, String image, String wiki, Integer syncstatus) {
        this.userID = userID;
        this.nameBySpecies = nameBySpecies;
        this.nameByUser = nameByUser;
        this.temperature = temperature;
        this.moisture = moisture;
        this.previousMoisturesLevel = previousMoisturesLevel;
        this.image = image;
        this.wiki = wiki;
        this.syncstatus = syncstatus;
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

    public String getNameBySpecies() {
        return nameBySpecies;
    }

    public void setNameBySpecies(String nameBySpecies) {
        this.nameBySpecies = nameBySpecies;
    }

    public String getNameByUser() {
        return nameByUser;
    }

    public void setNameByUser(String nameByUser) {
        this.nameByUser = nameByUser;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getMoisture() {
        return moisture;
    }

    public void setMoisture(String moisture) {
        this.moisture = moisture;
    }

    public String getPreviousMoisturesLevel() {
        return previousMoisturesLevel;
    }

    public void setPreviousMoisturesLevel(String previousMoisturesLevel) {
        this.previousMoisturesLevel = previousMoisturesLevel;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getWiki() {
        return wiki;
    }

    public void setWiki(String wiki) {
        this.wiki = wiki;
    }

    public Integer getSyncstatus() {
        return syncstatus;
    }

    public void setSyncstatus(Integer syncstatus) {
        this.syncstatus = syncstatus;
    }
}
