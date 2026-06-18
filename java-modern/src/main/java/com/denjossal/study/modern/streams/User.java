package com.denjossal.study.modern.streams;

import java.util.UUID;

public record User(String userId, String fullName, int age) {

    @Override
    public String toString() {
        return String.format("UserId: %s | Full Name: %s | Age: %d", userId, fullName, age);
    }
}
