package com.qianyishen.domain;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * user实体类
 * @author User
 */
public class User implements Serializable {

    private Integer id;
    private String username;
    private String password;
    private String telephone;
    private String email;
    private Timestamp createDate; //用户创建时间

    public Integer getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", telephone=" + telephone +
                ", email='" + email + '\'' +
                ", createDate=" + createDate +
                '}';
    }
}
