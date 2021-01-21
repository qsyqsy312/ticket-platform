package com.ticket.dao;

import com.ticket.dao.base.BaseDao;
import com.ticket.entity.Role;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends BaseDao<Role,String> {
}
