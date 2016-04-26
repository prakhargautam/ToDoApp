package com.example.prakhargautam.todoapp;

import java.io.Serializable;

/**
 * Created by prakhargautam on 26/04/16.
 */
public class Tag implements Serializable{
    int id;
    String name;
    int defaultPriority;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDefaultPriority() {
        return defaultPriority;
    }

    public void setDefaultPriority(int defaultPriority) {
        this.defaultPriority = defaultPriority;
    }
}
