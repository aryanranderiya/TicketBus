package com.example.ticketbus.Model;

public class UserModel {

    public String Name, Phone, Email, Profile;

    public UserModel() {
    }

    public UserModel(String name, String phone, String email) {
        Name = name;
        Phone = phone;
        Email = email;
    }

    public UserModel(String name, String phone, String email, String profile) {
        Name = name;
        Phone = phone;
        Email = email;
        Profile = profile;
    }

    public String getProfile() {
        return Profile;
    }

    public void setProfile(String profile) {
        Profile = profile;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
