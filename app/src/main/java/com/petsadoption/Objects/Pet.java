package com.petsadoption.Objects;

public class Pet {
    String uid;
    String animal;
    String photo;
    String name;
    String breed;
    String ageYears;
    String ageMonths;
    String sex;
    String weight;
    String location;
    String personality;
    String healthNotes;
    String fileName;

    public Pet() {
    }

    public Pet(String uid, String animal, String photo, String name, String breed, String ageYears, String ageMonths, String sex, String weight, String location, String personality, String healthNotes, String fileName) {
        this.uid = uid;
        this.animal = animal;
        this.photo = photo;
        this.name = name;
        this.breed = breed;
        this.ageYears = ageYears;
        this.ageMonths = ageMonths;
        this.sex = sex;
        this.weight = weight;
        this.location = location;
        this.personality = personality;
        this.healthNotes = healthNotes;
        this.fileName = fileName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAnimal() {
        return animal;
    }

    public void setAnimal(String animal) {
        this.animal = animal;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getAgeYears() {
        return ageYears;
    }

    public void setAgeYears(String ageYears) {
        this.ageYears = ageYears;
    }

    public String getAgeMonths() {
        return ageMonths;
    }

    public void setAgeMonths(String ageMonths) {
        this.ageMonths = ageMonths;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPersonality() {
        return personality;
    }

    public void setPersonality(String personality) {
        this.personality = personality;
    }

    public String getHealthNotes() {
        return healthNotes;
    }

    public void setHealthNotes(String healthNotes) {
        this.healthNotes = healthNotes;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
