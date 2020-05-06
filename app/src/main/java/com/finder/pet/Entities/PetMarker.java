package com.finder.pet.Entities;

public class PetMarker {

    /**
     * Attributes
     */
    private String name; //Nombre de la mascota para adoptar
    private String state; //Correo de quién perdió la mascota
    private double latitude; //Location latitude
    private double longitude; //Location longitude

    /**
     * Empty Constructor
     */
    public PetMarker() {
    }

    public PetMarker(String name, String state, double latitude, double longitude) {
        this.name = name;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
