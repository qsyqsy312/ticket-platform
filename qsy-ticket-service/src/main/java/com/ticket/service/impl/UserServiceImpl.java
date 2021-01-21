package com.ticket.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ticket.dao.RoleRepository;
import com.ticket.entity.Role;
import com.ticket.entity.User;
import com.ticket.service.IUserService;
import com.ticket.service.base.BaseService;
import com.ticket.support.dto.UserDTO;
import com.ticket.support.dto.base.BaseDTO;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

@Service
public class UserServiceImpl extends BaseService<User, String> implements IUserService, UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    private RoleRepository roleRepository;


    private static class UserInfo extends org.springframework.security.core.userdetails.User {
        private String id;

        public UserInfo(String id,String username, String password, Collection<? extends GrantedAuthority> authorities) {
            super(username, password, authorities);
            this.id = id;
        }

        public UserInfo(String id,String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
            super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Map<String, Object> map = Maps.newHashMap();
        map.put("userName", username);
        User user = baseDao.findOne(getSpecification(map)).orElseThrow(() -> new UsernameNotFoundException("用户名或者密码错误！"));
        Set<Role> roles = user.getRoles();
        //TODO:角色
        UserInfo userInfo = new UserInfo(user.getId(), user.getUserName(), user.getPassword(), Lists.newArrayList());
        return userInfo;
    }

    @Override
    public User toEntity(BaseDTO dto, User entity) {
        UserDTO userDTO = (UserDTO) dto;
        entity.setUserName(userDTO.getUserName());
        entity.setRegisterTime(new Date());
        entity.setEnabled(userDTO.isEnabled());
        entity.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        return entity;
    }

    @Override
    public Object toDTO(User entity, Object... data) {
        UserDTO dto = new UserDTO();
        dto.setUserName(entity.getUserName());
        dto.setId(entity.getId());
        dto.setRegisterTime(entity.getRegisterTime());
        dto.setEnabled(entity.isEnabled());
        return dto;
    }

    @Override
    public Specification<User> getSpecification(Map<String, Object> queryParam) {
        Specification<User> querySpec = new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                if (queryParam.get("username") != null) {
                    predicate.getExpressions().add(criteriaBuilder.equal(root.get("userName"), queryParam.get("userName")));
                }
                if (queryParam.get("startTime") != null && queryParam.get("endTime") != null) {
                    LocalDate startTime = LocalDate.parse((String) queryParam.get("startTime"));
                    LocalDate endTime = LocalDate.parse((String) queryParam.get("endTime"));
                    predicate.getExpressions().add(criteriaBuilder.greaterThanOrEqualTo(root.get("registerTime").as(Date.class), startTime.toDate()));
                    predicate.getExpressions().add(criteriaBuilder.lessThanOrEqualTo(root.get("registerTime").as(Date.class), endTime.toDate()));
                }
                return predicate;
            }
        };
        return baseDao.getBaseSpecification(queryParam).and(querySpec);
    }


}
