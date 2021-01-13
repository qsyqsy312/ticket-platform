package com.ticket.entity.base;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseModel implements Serializable {

    /**
     * id由代码控制
     */
    @Id
    private String id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(
            name = "createTime"
    )
    @CreatedDate
    private Date createTime;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(
            name = "lastModifyTime"
    )
    @LastModifiedDate
    private Date lastModifyTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseModel baseModel = (BaseModel) o;

        return id.equals(baseModel.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
