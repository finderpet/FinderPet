package com.finder.pet.Entities;

public class Lost_Vo {

    private String name;
    private String description;
    private String image;
    private String type;
    private String time;
    private String price;


    public Lost_Vo(){

    }

    public Lost_Vo(String name, String description, String image, String type, String time, String price) {
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
