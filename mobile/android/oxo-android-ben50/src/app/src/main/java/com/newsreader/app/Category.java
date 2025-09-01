package com.newsreader.app;

public class Category {
    private String name;
    private String description;
    private int articleCount;

    public Category(String name, String description, int articleCount) {
        this.name = name;
        this.description = description;
        this.articleCount = articleCount;
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

    public int getArticleCount() {
        return articleCount;
    }

    public void setArticleCount(int articleCount) {
        this.articleCount = articleCount;
    }
}
