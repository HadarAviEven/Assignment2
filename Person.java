package com.hadar.assignment2;

public class Person {
    private String name;
    private String gender;
    private String street;
    private String country;
    private String postcode;

    public Person(String name, String gender, String street, String country, String postcode) {
        this.name = name;
        this.gender = gender;
        this.street = street;
        this.country = country;
        this.postcode = postcode;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getStreet() {
        return street;
    }

    public String getCountry() {
        return country;
    }

    public String getPostcode() {
        return postcode;
    }
}
