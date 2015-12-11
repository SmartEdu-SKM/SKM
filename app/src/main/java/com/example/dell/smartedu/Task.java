package com.example.dell.smartedu;

/**
 * Created by Dell on 9/12/2015.
 */
public class Task {
    private String title;
    private String description;
    private int id;

    public Task(){}

    public Task(int id, String ttl, String dsc) {
        this.id = id;
        this.title = ttl;
        this.description = dsc;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public void setId(int i) {
        id = i;
    }

    public void setDescription(String str) {
        description = str;
    }

    public void setTitle(String str) {
        title = str;
    }
}
