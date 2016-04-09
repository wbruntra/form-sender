package com.example.william.formsender;

import android.app.Application;

/**
 * Created by william on 4/9/16.
 */
public class User extends Application {
    private String name = "none";
    private String userId = "none";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
