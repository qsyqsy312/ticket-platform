package com.ticket.service.impl;

import com.ticket.entity.Role;
import com.ticket.service.IRoleService;
import com.ticket.service.base.BaseService;
import com.ticket.support.dto.base.BaseDTO;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Map;

@Service
public class RoleServiceImpl extends BaseService<Role,String> implements IRoleService {






    @Override
    public Role toEntity(BaseDTO dto, Role entity) {
        return null;
    }

    @Override
    public Object toDTO(Role entity, Object... data) {
        return null;
    }



    @Override
    public Specification<Role> getSpecification(Map<String, Object> queryParam) {
        Specification<Role> querySpec = new Specification<Role>() {
            @Override
            public Predicate toPredicate(Root<Role> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                if (queryParam.get("roleCode") != null) {
                    predicate.getExpressions().add(criteriaBuilder.like(root.get("roleCode"), "%"+queryParam.get("roleCode")+"%"));
                }
                if (queryParam.get("roleName") != null) {
                    predicate.getExpressions().add(criteriaBuilder.like(root.get("roleName"), "%"+queryParam.get("roleName")+"%"));
                }
                return predicate;
            }
        };
        return baseDao.getBaseSpecification(queryParam).and(querySpec);
    }
}
