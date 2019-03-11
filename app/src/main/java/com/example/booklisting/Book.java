package com.example.booklisting;

public class Book {
    private String title;
    private String author;
    private String date;
    private String imageUrl;
    private Double averageRating;
    private String buyUrl;

    public Book(String title, String author, String date, String imageUrl, Double averageRating, String buyUrl) {
        this.title = title;
        this.author = author;
        this.date = date;
        this.imageUrl = imageUrl;
        this.averageRating = averageRating;
        this.buyUrl = buyUrl;
    }

    public Book(String title, String author, String date, String imageUrl, Double averageRating) {
        this.title = title;
        this.author = author;
        this.date = date;
        this.imageUrl = imageUrl;
        this.averageRating = averageRating;
    }

    public Book(String title, String author, String date, String imageUrl) {
        this.title = title;
        this.author = author;
        this.date = date;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public String getBuyUrl() {
        return buyUrl;
    }
}
