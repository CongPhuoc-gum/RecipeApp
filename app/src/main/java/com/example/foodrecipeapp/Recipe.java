package com.example.foodrecipeapp;

public class Recipe {
    private String recipeName;
    private String description;
    private String ingredients;
    private String steps;
    private String imageUrl;

    // Constructor để khởi tạo đối tượng
    public Recipe(String recipeName, String description, String ingredients, String steps, String imageUrl, String cookingTime, String servings, String country, String s) {
        this.recipeName = recipeName;
        this.description = description;
        this.ingredients = ingredients;
        this.steps = steps;
        this.imageUrl = imageUrl;
    }

    // Getters và Setters
    public String getRecipeName() { return recipeName; }
    public String getDescription() { return description; }
    public String getIngredients() { return ingredients; }
    public String getSteps() { return steps; }
    public String getImageUrl() { return imageUrl; }

    public void setRecipeName(String recipeName) { this.recipeName = recipeName; }
    public void setDescription(String description) { this.description = description; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }
    public void setSteps(String steps) { this.steps = steps; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
