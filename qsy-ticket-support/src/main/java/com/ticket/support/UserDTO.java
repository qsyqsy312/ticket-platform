package com.ticket.support;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.ticket.support.base.BaseDTO;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class UserDTO extends BaseDTO {

    private String userName;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date registerTime;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }
}
