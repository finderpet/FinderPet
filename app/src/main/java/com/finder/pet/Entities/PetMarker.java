package com.finder.pet.Entities;

public class PetMarker {

    /**
     * Attributes
     */
    private String name; //Nombre de la mascota para adoptar
    private String type; //Pet type
    private String state; //Pet State
    private String phone; //Phone owner
    private String email; //Email owner
    private String image; //Pet image
    private double latitude; //Location latitude
    private double longitude; //Location longitude

    /**
     * Empty Constructor
     */
    public PetMarker() {
    }

    public PetMarker(String name, String type, String state, String phone, String email, String image, double latitude, double longitude) {
        this.name = name;
        this.type = type;
        this.state = state;
        this.phone = phone;
        this.email = email;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
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
