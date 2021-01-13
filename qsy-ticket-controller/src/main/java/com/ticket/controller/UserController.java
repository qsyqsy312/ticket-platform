package com.ticket.controller;


import com.ticket.service.IUserService;
import com.ticket.support.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("user")
public class UserController {

    @Autowired
    private IUserService userService;


    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ResponseBody
    public Object save(@RequestBody UserDTO userDTO) throws Exception{
        return userService.save(userDTO);
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Object update(@RequestBody UserDTO userDTO) throws Exception{
        return userService.update(userDTO);
    }

    @RequestMapping(value = "query", method = RequestMethod.GET)
    @ResponseBody
    public Object query(@RequestParam Map<String, Object> queryParam) {
        return userService.list(queryParam, Sort.unsorted());
    }


    @RequestMapping(value = "page", method = RequestMethod.GET)
    @ResponseBody
    public Object page(@RequestParam Map<String, Object> queryParam) {
        PageRequest pageRequest = PageRequest.of(Integer.valueOf(queryParam.get("page").toString()), Integer.valueOf(queryParam.get("size").toString()));
        queryParam.clear();
        return userService.page(queryParam, pageRequest);
    }


}
