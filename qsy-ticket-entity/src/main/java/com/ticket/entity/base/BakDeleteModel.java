package com.ticket.entity.base;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@MappedSuperclass
public class BakDeleteModel extends BaseModel {

    @Column(name = "deleteStatus",columnDefinition = "bit not null default 0")
    private Boolean deleteStatus = false;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date deleteTime;



    public Date getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }

    public Boolean getDeleteStatus() {
        return deleteStatus;
    }

    public void setDeleteStatus(Boolean deleteStatus) {
        this.deleteStatus = deleteStatus;
    }
}
