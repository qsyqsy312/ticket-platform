package com.ticket.service.base;


import com.ticket.entity.base.BaseModel;
import com.ticket.support.base.BaseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 定义增删改查常用方法
 *
 * @param <T>
 * @param <ID>
 */
public interface IBaseService<T extends BaseModel, ID extends Serializable> {

    Specification<T> getSpecification(Map<String, Object> queryParam);

    Object save(BaseDTO dto) throws Exception;

    Object update(BaseDTO dto) throws Exception;

    void deleteByIds(Iterable<ID> ids) throws Exception;

    T findOneById(ID id);

    List<Object> list(Map<String, Object> queryParam, Sort sort);

    Page<Object> page(Map<String, Object> queryParam, Pageable pageable);
}
