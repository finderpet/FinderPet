package com.finder.pet.Entities;

import java.io.Serializable;

public class Adopted_Vo implements Serializable {

    /**
     * Attributes
     */
    private String name; //Nombre de la mascota para adoptar
    private String email; //Correo de quién perdió la mascota
    private String type; //Tipo de mascota para adoptar
    private String observations; //Descrición de la mascota
    private String phone; //Teléfono de contacto de quién entrega la mascota
    private String image1; //Foto 1 de la mascota para adoptar
    private String image2; //Foto 1 de la mascota para adoptar
    private String image3; //Foto 1 de la mascota para adoptar
    private String location; //Dirección o lugar donde se esta la mascota

    /**
     * Empty Constructor
     */
    public Adopted_Vo() {
    }

    public Adopted_Vo(String name, String email, String type, String observations, String phone, String image1, String image2, String image3, String location) {
        this.name = name;
        this.email = email;
        this.type = type;
        this.observations = observations;
        this.phone = phone;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.location = location;
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
}

