package com.tim.app.server.entry;

import java.io.Serializable;

/**
 * @创建者 倪军
 * @创建时间 2017/8/8
 * @描述
 */

public class University implements Serializable {
    private static final long serialVersionUID = 6187447685293862071L;
    private  int id;
    private String name;


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


    @Override
    public String toString() {
        return "University{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

}
