package com.example.zyra;

public class Plants {

    private Integer ID;
    private String PlantTitle;
    private String PlantType;

    public Plants(Integer ID, String plantTitle, String plantType) {
        this.ID = ID;
        PlantTitle = plantTitle;
        PlantType = plantType;
    }

    public Plants(String plantTitle, String plantType) {
        PlantTitle = plantTitle;
        PlantType = plantType;
    }

    public Integer getID() {
        return ID;
    }

    public String getPlantTitle() {
        return PlantTitle;
    }

    public String getPlantType() {
        return PlantType;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public void setPlantTitle(String plantTitle) {
        PlantTitle = plantTitle;
    }

    public void setPlantType(String plantType) {
        PlantType = plantType;
    }
}
