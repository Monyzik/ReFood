package com.example.refood;

public class Post {
    private String author, title, text, image;

    Post(String author, String title, String text, String image) {
        this.author = author;
        this.title = title;
        this.text = text;
        this.image = image;
    }

    public void editText(String newText) {
        this.text = newText;
    }

    public void editTitle(String newTitle) {
        this.title = newTitle;
    }

    public String getAuthor() {
        return author;
    }

    public String getImage() {
        return image;
    }

    public String getText() {
        return text;
    }

    public String getTitle() {
        return title;
    }
}
