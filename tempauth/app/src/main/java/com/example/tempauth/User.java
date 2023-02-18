package com.example.tempauth;

public class User
{
    boolean twoFA;
    boolean newU;
    String scrtK="";
    //int backup1, backup2, backup3;

    public User(boolean twoFA, boolean newU, String scrtK)
    {
        this.twoFA = twoFA;
        this.newU = newU;
        this.scrtK = scrtK;
    }

    public User() {}

    public boolean isTwoFA() {
        return twoFA;
    }

    public void setTwoFA(boolean twoFA) {
        this.twoFA = twoFA;
    }

    public boolean isNewU() {
        return newU;
    }

    public void setNewU(boolean newU) {
        this.newU = newU;
    }

    public String getScrtK() {
        return scrtK;
    }

    public void setScrtK(String scrtK) {
        this.scrtK = scrtK;
    }

}
