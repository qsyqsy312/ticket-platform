package com.ticket.entity;


import com.ticket.entity.base.BaseModel;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = User.TABLE_NAME)
public class User extends BaseModel {

    public static final String TABLE_NAME = "sys_user";

    @Column(unique = true)
    private String userName;

    @Column
    private String password;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date registerTime;


    @Column(columnDefinition = "bit not null default 1")
    private boolean enabled;


    @ManyToMany
    @JoinTable(name = "sys_user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }


    public String getPassword() {
        return password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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
