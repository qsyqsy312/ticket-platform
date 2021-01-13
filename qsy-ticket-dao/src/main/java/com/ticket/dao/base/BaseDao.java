package com.ticket.dao.base;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ticket.entity.base.BaseModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@NoRepositoryBean
public interface BaseDao<T extends BaseModel, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    Class<T> getDomainClazz();

    JPAQueryFactory getJPAQueryFactory();

    String getTableName();

    T customSave(T t);

    T customUpdate(T t);

    <S extends T> Iterable<S> batchCustomSave(Iterable<S> var1);

    void customDeleteById(ID id);


    T findOneById(ID id);

    /**
     * 返回出entityManager自定义查询
     *
     * @return
     */
    EntityManager getEntityManager();


    /**
     * 构造基础表查询条件
     *
     * @param queryParam
     * @return
     */
    Specification<T> getBaseSpecification(Map<String, Object> queryParam);


    /**
     * 原生sql返回实体
     *
     * @param sql
     * @param params
     * @return
     */
    List<T> nativeSQLQuery(String sql, Map<String, Object> params);


    /**
     * 原生sql返回实体(分页)
     *
     * @param sql
     * @param params
     * @return
     */
    Page<T> nativeSQLQuery(String sql, String countSql, Map<String, Object> params, Pageable pageable);

    /**
     * 原生sql返回指定DTO类型
     *
     * @param sql
     * @param params
     * @param cls
     * @return
     */
    List nativeSQLQuery(String sql, Map<String, Object> params, Class<?> cls);


    /**
     * 原生sql返回指定DTO类型(分页)
     *
     * @param sql
     * @param params
     * @param cls
     * @return
     */
    Page nativeSQLQuery(String sql, String countSql, Map<String, Object> params, Class<?> cls, Pageable pageable);

    /**
     * 原生sql返回Map
     *
     * @param sql
     * @param params
     * @return
     */
    List<Map<String, Object>> nativeSQLQueryToMap(String sql, Map<String, Object> params);
}
