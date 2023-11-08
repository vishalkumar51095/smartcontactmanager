package com.smart.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//'smartcontact.hibernate_sequence' doesn't exist AUTO-> IDENTITY
    private int id;

    @NotBlank(message = "Name field is required !!")
    @Size(min = 2,max = 20,message = "min 2 and max 20 characters are allowed !!")
    private String name;
    @Column(unique = true)
    private String email;
    private String password;
    private boolean enabled;
    private String role;
    private String imageUrl;
    @Column(length = 500)
    private String about;




    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,mappedBy = "user")
    private List<Contact> contacts=new ArrayList<>();


    public User(int id, String name, String email, String password, boolean enabled, String role, String imageUrl, String about, List<Contact> contacts) {
        super();
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
        this.role = role;
        this.imageUrl = imageUrl;
        this.about = about;
        this.contacts = contacts;
    }

    public User() {
        super();
        //default Constructor
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }
}
