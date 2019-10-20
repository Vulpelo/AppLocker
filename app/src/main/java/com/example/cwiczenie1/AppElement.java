package com.example.cwiczenie1;

import com.example.cwiczenie1.database.ResetWhen;

import java.io.Serializable;

public class AppElement implements Serializable {
    public long id = 0;
    public String name;
    public boolean isProtected = false;
    public ResetWhen resetWhen = ResetWhen.SCREEN_OFF;
    public boolean enteredPass = false;

    public AppElement(String name) {
        this.name = name;
    }
}
