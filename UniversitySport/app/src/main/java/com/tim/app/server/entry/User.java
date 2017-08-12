package com.tim.app.server.entry;


import java.io.Serializable;
import java.util.Arrays;

public class User implements Serializable {

    private static final long serialVersionUID = 6187447685293862071L;
    public static final String USER = "user";
    public static final String USER_SHARED_PREFERENCE = "user_shared_preference";


    private int uid;
    private String phone;
    private String username;
    private String password;
    private String headpicthumb;
    private String headpic;
    private String[] roles;
    private Long expiredDate;
    private String token;
    private Student student;


    @Override
    public String toString() {
        return "User{" +
                "uid=" + uid +
                ", phone='" + phone + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", headpicthumb='" + headpicthumb + '\'' +
                ", headpic='" + headpic + '\'' +
                ", roles=" + Arrays.toString(roles) +
                ", expiredDate=" + expiredDate +
                ", token='" + token + '\'' +
                ", student=" + student +
                '}';
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
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

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public Long getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Long expiredDate) {
        this.expiredDate = expiredDate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
