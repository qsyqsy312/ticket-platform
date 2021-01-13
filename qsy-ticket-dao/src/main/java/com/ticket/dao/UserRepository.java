package com.ticket.dao;


import com.ticket.dao.base.BaseDao;
import com.ticket.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends BaseDao<User,String> {
}
