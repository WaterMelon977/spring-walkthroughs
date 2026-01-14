package com.example.cors;

public record Greeting(long id, String content) {

    public Greeting() {
        this(-1, "");
    }
}