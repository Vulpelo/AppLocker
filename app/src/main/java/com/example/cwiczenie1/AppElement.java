package com.example.cwiczenie1;

import java.io.Serializable;

public class AppElement implements Serializable {
    public long id = 0;
    public String name;
    public boolean isProtected = false;

    public AppElement(String name) {
        this.name = name;
    }
}
