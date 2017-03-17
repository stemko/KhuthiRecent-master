package ru.solandme.simpleblog;

public class Product {

    private String ProductName, ProductDescription, imageURL, ProductPrice;

    public Product() {
    }

    public Product(String ProductName, String ProductDescription, String imageURL, String ProductPrice) {
        this.ProductName = ProductName;
        this.ProductDescription = ProductDescription;
        this.imageURL = imageURL;
        this.ProductPrice = ProductPrice;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String ProductName) {
        this.ProductName = ProductName;
    }

    public String getProductDescription() {
        return ProductDescription;
    }

    public void setProductDescription(String ProductDescription) {
        this.ProductDescription = ProductDescription;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getProductPrice() {
        return ProductPrice;
    }

    public void setProductPrice(String ProductPrice) {
        this.ProductPrice = ProductPrice;
    }
}
