package com.finder.pet.Entities;

import java.io.Serializable;

public class Found_Vo implements Serializable {

    private String found_kind; //Tipo de mascota
    private String found_description; //Descrición de la mascota
    private String found_phone; //Teléfono de contacto de quién encontró la mascota
    private String found_image; //Foto de la mascota encontrada
    private String found_address; //Dirección o lugar donde fue encontrada la mascota

    /**
     * Empty Constructor
     */
    public Found_Vo(){

    }

    /**
     * Getters y setters Constructor
     * @param found_kind
     * @param found_description
     * @param found_phone
     * @param found_image
     * @param found_address
     */
    public Found_Vo(String found_kind, String found_description, String found_phone, String found_image, String found_address) {
        this.found_kind = found_kind;
        this.found_description = found_description;
        this.found_phone = found_phone;
        this.found_image = found_image;
        this.found_address = found_address;
    }

    public String getFound_kind() {
        return found_kind;
    }

    public void setFound_kind(String found_kind) {
        this.found_kind = found_kind;
    }

    public String getFound_description() {
        return found_description;
    }

    public void setFound_description(String found_description) {
        this.found_description = found_description;
    }

    public String getFound_phone() {
        return found_phone;
    }

    public void setFound_phone(String found_phone) {
        this.found_phone = found_phone;
    }

    public String getFound_image() {
        return found_image;
    }

    public void setFound_image(String found_image) {
        this.found_image = found_image;
    }

    public String getFound_address() {
        return found_address;
    }

    public void setFound_address(String found_address) {
        this.found_address = found_address;
    }
}
