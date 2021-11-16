package com.company;


public class Player {

    private String name;

    public Player(String name) { //конструктор для создания экземпляра
        this.name = name;
    }

    public Player() {

    }

    public String getName() { //getter
        return name;
    }
}
