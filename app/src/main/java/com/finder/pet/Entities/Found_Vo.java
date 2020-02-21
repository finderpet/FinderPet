package com.finder.pet.Entities;

import java.io.Serializable;

public class Found_Vo implements Serializable {


//    private String found_kind; //Tipo de mascota
//    private String found_description; //Descrición de la mascota
//    private String found_phone; //Teléfono de contacto de quién encontró la mascota
//    private String found_image; //Foto de la mascota encontrada
//    private String found_address; //Dirección o lugar donde fue encontrada la mascota
//
//    /**
//     * Empty Constructor
//     */
//    public Found_Vo(){
//
//    }
//
//    /**
//     * Getters y setters Constructor
//     * @param found_kind
//     * @param found_description
//     * @param found_phone
//     * @param found_image
//     * @param found_address
//     */
//    public Found_Vo(String found_kind, String found_description, String found_phone, String found_image, String found_address) {
//        this.found_kind = found_kind;
//        this.found_description = found_description;
//        this.found_phone = found_phone;
//        this.found_image = found_image;
//        this.found_address = found_address;
//    }
//
//    public String getFound_kind() {
//        return found_kind;
//    }
//
//    public void setFound_kind(String found_kind) {
//        this.found_kind = found_kind;
//    }
//
//    public String getFound_description() {
//        return found_description;
//    }
//
//    public void setFound_description(String found_description) {
//        this.found_description = found_description;
//    }
//
//    public String getFound_phone() {
//        return found_phone;
//    }
//
//    public void setFound_phone(String found_phone) {
//        this.found_phone = found_phone;
//    }
//
//    public String getFound_image() {
//        return found_image;
//    }
//
//    public void setFound_image(String found_image) {
//        this.found_image = found_image;
//    }
//
//    public String getFound_address() {
//        return found_address;
//    }
//
//    public void setFound_address(String found_address) {
//        this.found_address = found_address;
//    }
    //::: DE ACA EN ADELANTE ES EL CODIGO PARA SIMULAR LOS CARDVIEW CON LA BASE DE DATOS QUE TENEMOS ::://

    private String name;
    private String description;
    private String image;
    private String type;
    private String time;
    private String price;


    public Found_Vo(){

    }

    public Found_Vo(String name, String description, String image, String type, String time, String price) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.type = type;
        this.time = time;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
