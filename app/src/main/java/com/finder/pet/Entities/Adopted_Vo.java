package com.finder.pet.Entities;

import java.io.Serializable;

public class Adopted_Vo implements Serializable {

    /**
     * Attributes
     */
    private String keyPost; // Key post in firebase
    private String idUser; // id firebase current user
    private String date; //Fecha de registro de la mascota
    private String name; //Nombre de la mascota para adoptar
    private String email; //Correo de quién perdió la mascota
    private String type; //Tipo de mascota para adoptar
    private String age; //Edad de la mascota para adoptar
    private String breed; //Raza de la mascota para adoptar
    private String sterilized; //Mascota esterilizada si, no
    private String vaccines; //Vacunas que tiene las mascota
    private String observations; //Descrición de la mascota
    private String phone; //Teléfono de contacto de quién entrega la mascota
    private String image1; //Foto 1 de la mascota para adoptar
    private String image2; //Foto 1 de la mascota para adoptar
    private String image3; //Foto 1 de la mascota para adoptar
    private String location; //Dirección o lugar donde se esta la mascota
    private double latitude; //Location latitude
    private double longitude; //Location longitude

    /**
     * Empty Constructor
     */
    public Adopted_Vo() {
    }

    public Adopted_Vo(String idUser, String date, String name, String email, String type, String age, String breed,
                      String sterilized, String vaccines, String observations, String phone,
                      String image1, String image2, String image3, String location,
                      double latitude, double longitude) {
        this.idUser = idUser;
        this.date = date;
        this.name = name;
        this.email = email;
        this.type = type;
        this.age = age;
        this.breed = breed;
        this.sterilized = sterilized;
        this.vaccines = vaccines;
        this.observations = observations;
        this.phone = phone;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    //Getters and Setters
    public String getKeyPost() {
        return keyPost;
    }
    public void setKeyPost(String keyPost) {
        this.keyPost = keyPost;
    }
    public String getIdUser() {
        return idUser;
    }
    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getAge() {
        return age;
    }
    public void setAge(String age) {
        this.age = age;
    }
    public String getBreed() {
        return breed;
    }
    public void setBreed(String breed) {
        this.breed = breed;
    }
    public String getSterilized() {
        return sterilized;
    }
    public void setSterilized(String sterilized) {
        this.sterilized = sterilized;
    }
    public String getVaccines() {
        return vaccines;
    }
    public void setVaccines(String vaccines) {
        this.vaccines = vaccines;
    }
    public String getObservations() {
        return observations;
    }
    public void setObservations(String observations) {
        this.observations = observations;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getImage1() {
        return image1;
    }
    public void setImage1(String image1) {
        this.image1 = image1;
    }
    public String getImage2() {
        return image2;
    }
    public void setImage2(String image2) {
        this.image2 = image2;
    }
    public String getImage3() {
        return image3;
    }
    public void setImage3(String image3) {
        this.image3 = image3;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
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

