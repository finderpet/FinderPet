package com.finder.pet.Entities;

import java.io.Serializable;

public class ReportProblem implements Serializable {

    private String title;
    private String email;
    private String description;
    private String image;

    public ReportProblem() {
    }

    public ReportProblem(String title, String email, String description, String image) {
        this.title = title;
        this.email = email;
        this.description = description;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
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
}
