package com.finder.pet.Entities;

import java.io.Serializable;

public class Advert implements Serializable {
    /**
     * Attributes
     */
    private String name; //Nombre del anuncio
    private String description; //Descripcion del anuncio
    private String phone; // Telefono del anuncio
    private String urlPage; //URL de la pagina del anunciante
    private String image; //Foto del servicio o negocio

    /**
     * Empty Constructor
     */
    public Advert() {
    }

    public Advert(String name, String description, String phone, String urlPage, String image) {
        this.name = name;
        this.description = description;
        this.phone = phone;
        this.urlPage = urlPage;
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
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getUrlPage() {
        return urlPage;
    }
    public void setUrlPage(String urlPage) {
        this.urlPage = urlPage;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
}
