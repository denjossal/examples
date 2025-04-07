package com.co.study.examples;

import java.util.UUID;

public class User {
    private final String userId;
    private final String fullName;
    private final int age;

    // Constructor explícito para evitar generar usuarios desde dentro
    public User(String userId, String fullName, int age) {
        this.userId = userId;
        this.fullName = fullName;
        this.age = age;
    }

    public String getFullName() {
        return fullName;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return String.format("UserId: %s | Full Name: %s | Age: %d", userId, fullName, age);
    }
}