package com.ticket.entity;


import com.ticket.entity.base.BaseModel;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = User.TABLE_NAME)
public class User extends BaseModel {

    public static final String TABLE_NAME = "sys_user";

    @Column
    private String userName;

    @Column
    private String password;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date registerTime;



    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }
}
