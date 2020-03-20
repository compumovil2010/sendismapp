package com.example.sendismapp.logic;

public class Route {
    private String name;
    private double distance;//in m
    private int difficulty;
    public Route(String name,double distance,int difficulty){
        this.difficulty=difficulty;
        this.distance=distance;
        this.name=name;
    }

    public String getName() {
        return name;
    }

    public double getDistance() {
        return distance;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
}
