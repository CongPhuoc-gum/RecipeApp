package com.example.foodrecipeapp;

public class Recipe {
    private String recipeId;
    private String name;
    private String description;
    private String ingredients;
    private String steps;
    private String imageUrl;
    private String cookingTime;
    private String servings;
    private String country;
    private String userName;
    private String userUid;

    // Constructor không tham số
    public Recipe() {
        // Constructor mặc định cần thiết cho Firebase
    }

    // Constructor có tham số bao gồm recipeId
    public Recipe(String recipeId, String name, String description, String ingredients, String steps,
                  String imageUrl, String cookingTime, String servings, String country,
                  String userName, String userUid) {
        this.recipeId = recipeId;
        this.name = name;
        this.description = description;
        this.ingredients = ingredients;
        this.steps = steps;
        this.imageUrl = imageUrl;
        this.cookingTime = cookingTime;
        this.servings = servings;
        this.country = country;
        this.userName = userName;
        this.userUid = userUid;
    }

    // Các phương thức getter và setter cho các trường
    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }
}
