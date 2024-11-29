package com.example.foodrecipeapp;

public class Recipe {
    private String recipeName;
    private String description;
    private String ingredients;
    private String steps;
    private String imageUrl;
    private String cookingTime;
    private String servings;
    private String country;
    private String userEmail;

    public Recipe() {

    }

    // Constructor có tham số
    public Recipe(String recipeName, String description, String ingredients, String steps, String imageUrl,
                  String cookingTime, String servings, String country, String userEmail) {
        this.recipeName = recipeName;
        this.description = description;
        this.ingredients = ingredients;
        this.steps = steps;
        this.imageUrl = imageUrl;
        this.cookingTime = cookingTime;
        this.servings = servings;
        this.country = country;
        this.userEmail = userEmail;
    }

    // Getter và setter (nếu cần)
    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(String cookingTime) {
        this.cookingTime = cookingTime;
    }

    public String getServings() {
        return servings;
    }

    public void setServings(String servings) {
        this.servings = servings;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}

