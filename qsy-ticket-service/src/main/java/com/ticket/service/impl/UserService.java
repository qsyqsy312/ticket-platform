package com.ticket.service.impl;

import com.google.common.collect.BoundType;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.ticket.entity.User;
import com.ticket.service.IUserService;
import com.ticket.service.base.StandardShardingService;
import com.ticket.support.UserDTO;
import com.ticket.support.base.BaseDTO;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingValue;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.*;

@Service
public class UserService extends StandardShardingService<User, String> implements IUserService {


    @Override
    public User toEntity(BaseDTO dto, User entity) {
        UserDTO userDTO = (UserDTO) dto;
        entity.setUserName(userDTO.getUserName());
        entity.setRegisterTime(userDTO.getRegisterTime());
        return entity;
    }

    @Override
    public Object toDTO(User entity,Object ... data) {
        UserDTO dto = new UserDTO();
        dto.setUserName(entity.getUserName());
        dto.setId(entity.getId());
        return dto;
    }

    @Override
    public void customIDGenerator(User user) {
        user.setId(UUID.randomUUID().toString().replaceAll("-", "")+"-"+new DateTime(user.getRegisterTime()).toString("yyyyMMddHHmmss"));
    }

    @Override
    public Specification<User> getSpecification(Map<String, Object> queryParam) {
        Specification<User> querySpec = new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                if (queryParam.get("staffName") != null) {
                    Join<Object, Object> staff = root.join("staff");
                    predicate.getExpressions().add(criteriaBuilder.equal(staff.get("name"), queryParam.get("staffName")));
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

    @Override
    public void createTable(User user) {
        Map<String,Collection<Comparable>> shardingValuesMap = Maps.newHashMap();
        shardingValuesMap.put("id", Arrays.asList(user.getId()));
        shardingValuesMap.put("registerTime", Arrays.asList(user.getCreateTime()));
        Map<String,Range<Comparable>> rangeValuesMap = Maps.newHashMap();
        ComplexKeysShardingValue shardingValue = new ComplexKeysShardingValue<>(User.TABLE_NAME, shardingValuesMap, rangeValuesMap);
        Collection<String> tableNames = algorithm.doSharding(this.tableRule.getActualTableNames("db0"), shardingValue);
        EntityManager entityManager = baseDao.getEntityManager();
        for(String tableName:tableNames){
            entityManager.createNativeQuery("CREATE TABLE IF NOT EXISTS " + tableName + " LIKE " + baseDao.getTableName()).executeUpdate();
        }
    }

    @Override
    public void createTable(Map<String, Object> queryParam) {
        EntityManager entityManager = baseDao.getEntityManager();
        LocalDate startTime = LocalDate.parse((String) queryParam.get("startTime"));
        LocalDate endTime = LocalDate.parse((String) queryParam.get("endTime"));
        Map<String,Collection<Date>> shardingValuesMap = Maps.newHashMap();
        Map<String,Range<Date>> rangeValuesMap = Maps.newHashMap();
        rangeValuesMap.put("registerTime", Range.range(startTime.toDate(), BoundType.CLOSED, endTime.toDate(), BoundType.CLOSED));
        ComplexKeysShardingValue shardingValue = new ComplexKeysShardingValue<>(User.TABLE_NAME, shardingValuesMap, rangeValuesMap);

        Collection<String> tableNames = algorithm.doSharding(this.tableRule.getActualTableNames("db0"), shardingValue);
        for (String tableName : tableNames) {
            entityManager.createNativeQuery("CREATE TABLE IF NOT EXISTS " + tableName + " LIKE " + baseDao.getTableName()).executeUpdate();
        }
    }


}
