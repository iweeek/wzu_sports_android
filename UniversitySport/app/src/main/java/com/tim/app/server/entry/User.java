package com.tim.app.server.entry;


import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = 6187447685293862071L;
    private String uid;
    private String phone;
    private String username;
    private String password;
    private String headpicthumb;
    private String headpic;

    public User() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHeadpicthumb() {
        return headpicthumb;
    }

    public void setHeadpicthumb(String headpicthumb) {
        this.headpicthumb = headpicthumb;
    }

    public String getHeadpic() {
        return headpic;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }
}
