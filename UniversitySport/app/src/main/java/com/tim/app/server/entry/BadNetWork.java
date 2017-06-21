package com.tim.app.server.entry;

import java.io.Serializable;

/**
 * Created by nimon on 2017/6/21.
 */

public class BadNetWork implements Serializable {
    private String message;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "BadNetWork{" +
                "message='" + message + '\'' +
                '}';
    }
}
