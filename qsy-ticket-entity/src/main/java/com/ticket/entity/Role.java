package com.ticket.entity;

import com.ticket.entity.base.BaseModel;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = Role.TABLE_NAME)
public class Role extends BaseModel {
    public static final String TABLE_NAME = "sys_role";

    @Column
    private String roleCode;

    @Column
    private String roleName;

    @ManyToMany(cascade = CascadeType.REFRESH,mappedBy = "roles")
    private Set<User> users;


    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
