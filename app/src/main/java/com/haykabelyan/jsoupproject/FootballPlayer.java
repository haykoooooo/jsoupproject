package com.haykabelyan.jsoupproject;

public class FootballPlayer {
    private String number, position, fullName;

    public FootballPlayer(String number, String position, String fullName) {
        this.number = number;
        this.position = position;
        this.fullName = fullName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return number + " " + position + " " + fullName;
    }
}