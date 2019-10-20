package com.example.cwiczenie1;

import android.graphics.drawable.Drawable;

import com.example.cwiczenie1.database.ResetWhen;

import java.io.Serializable;

public class AppElement implements Serializable {
    Drawable appImage = null;

    public long id = 0;
    public String name = "";
    public String appName = "Hej";
    public boolean isProtected = false;
    public ResetWhen resetWhen = ResetWhen.SCREEN_OFF;
    public boolean enteredPass = false;

    public AppElement(String name) {
        this.name = name;
    }
}
