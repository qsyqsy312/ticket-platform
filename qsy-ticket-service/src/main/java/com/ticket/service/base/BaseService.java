package com.ticket.service.base;


import com.ticket.dao.base.BaseDao;
import com.ticket.entity.base.BaseModel;
import com.ticket.support.dto.base.BaseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * 普通CRUD实现类
 * @param <T>
 * @param <ID>
 */
public abstract class BaseService<T extends BaseModel, ID extends Serializable> implements IBaseService<T, ID>,IDataTransform<T,ID>{


    protected BaseDao<T, ID> baseDao;


    @Autowired
    public void setBaseDao(BaseDao<T, ID> baseDao) {
        this.baseDao = baseDao;
    }



    @Override
    public Specification<T> getSpecification(Map<String, Object> queryParam) {
        return baseDao.getBaseSpecification(queryParam);
    }


    //默认实现，UUID
    @Override
    public void customIDGenerator(T t){
        if (t.getId() == null) {
            t.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        }
    }




    @Override
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
    public Object save(BaseDTO dto) throws Exception{
        T t = toEntity(dto,baseDao.getDomainClazz().newInstance());
        customIDGenerator(t);

        return toDTO(baseDao.customSave(t));
    }


    @Override
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
    public Object update(BaseDTO dto) {
        T one = baseDao.findOneById((ID) dto.getId());
        T t = toEntity(dto,one);
        return toDTO(baseDao.customUpdate(t));
    }

    @Override
    @Transactional(readOnly = true)
    public T findOneById(ID id) {
        return baseDao.findById(id).orElseThrow(()->new RuntimeException("查询数据不存在！"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIds(Iterable<ID> ids) {
        if (ids != null) {
            while (ids.iterator().hasNext()) {
                baseDao.customDeleteById(ids.iterator().next());
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object> list(Map<String, Object> queryParam, Sort sort) {
        return baseDao.findAll(getSpecification(queryParam), sort).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Object> page(Map<String, Object> queryParam, Pageable pageable) {
        Page<T> page = baseDao.findAll(getSpecification(queryParam), pageable);
        return page.map(this::toDTO);
    }
}
