package com.ticket.service;


import com.ticket.entity.User;
import com.ticket.service.base.IShardingService;

public interface IUserService extends IShardingService<User,String> {

}
