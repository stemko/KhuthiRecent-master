package ru.solandme.simpleblog;

public class Blog {

    private String Companytitle, CompanyContacts, imageURL, username;

    public Blog() {
    }

    public Blog(String title, String description, String imageURL, String username) {
        this.Companytitle = Companytitle;
        this.CompanyContacts = CompanyContacts;
        this.imageURL = imageURL;
        this.username = username;
    }

    public String getTitle() {
        return Companytitle;
    }

    public void setTitle(String title) {
        this.Companytitle = Companytitle;
    }

    public String getDescription() {
        return CompanyContacts;
    }

    public void setDescription(String description) {
        this.CompanyContacts = CompanyContacts;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
