package com.petsadoption.Objects;

import java.util.Objects;

public class Application {
    String uid;
    String applicantUid;
    String petUid;
    String firstName;
    String lastName;
    String email;
    String mobile;
    String address;
    String isPetOwner;
    String isHomeOwner;
    String hasYard;
    String hasChildren;
    String petNames;
    String petBreeds;
    String petDisposition;
    String vetMobile;
    String isYardFenced;
    String childrenAges;
    int status;
    long timestamp;

    public Application() {
    }

    public Application(String uid, String applicantUid, String petUid, String firstName, String lastName, String email, String mobile, String address, String isPetOwner, String isHomeOwner, String hasYard, String hasChildren, String petNames, String petBreeds, String petDisposition, String vetMobile, String isYardFenced, String childrenAges, int status, long timestamp) {
        this.uid = uid;
        this.applicantUid = applicantUid;
        this.petUid = petUid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobile = mobile;
        this.address = address;
        this.isPetOwner = isPetOwner;
        this.isHomeOwner = isHomeOwner;
        this.hasYard = hasYard;
        this.hasChildren = hasChildren;
        this.petNames = petNames;
        this.petBreeds = petBreeds;
        this.petDisposition = petDisposition;
        this.vetMobile = vetMobile;
        this.isYardFenced = isYardFenced;
        this.childrenAges = childrenAges;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getApplicantUid() {
        return applicantUid;
    }

    public void setApplicantUid(String applicantUid) {
        this.applicantUid = applicantUid;
    }

    public String getPetUid() {
        return petUid;
    }

    public void setPetUid(String petUid) {
        this.petUid = petUid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIsPetOwner() {
        return isPetOwner;
    }

    public void setIsPetOwner(String isPetOwner) {
        this.isPetOwner = isPetOwner;
    }

    public String getIsHomeOwner() {
        return isHomeOwner;
    }

    public void setIsHomeOwner(String isHomeOwner) {
        this.isHomeOwner = isHomeOwner;
    }

    public String getHasYard() {
        return hasYard;
    }

    public void setHasYard(String hasYard) {
        this.hasYard = hasYard;
    }

    public String getHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(String hasChildren) {
        this.hasChildren = hasChildren;
    }

    public String getPetNames() {
        return petNames;
    }

    public void setPetNames(String petNames) {
        this.petNames = petNames;
    }

    public String getPetBreeds() {
        return petBreeds;
    }

    public void setPetBreeds(String petBreeds) {
        this.petBreeds = petBreeds;
    }

    public String getPetDisposition() {
        return petDisposition;
    }

    public void setPetDisposition(String petDisposition) {
        this.petDisposition = petDisposition;
    }

    public String getVetMobile() {
        return vetMobile;
    }

    public void setVetMobile(String vetMobile) {
        this.vetMobile = vetMobile;
    }

    public String getIsYardFenced() {
        return isYardFenced;
    }

    public void setIsYardFenced(String isYardFenced) {
        this.isYardFenced = isYardFenced;
    }

    public String getChildrenAges() {
        return childrenAges;
    }

    public void setChildrenAges(String childrenAges) {
        this.childrenAges = childrenAges;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}