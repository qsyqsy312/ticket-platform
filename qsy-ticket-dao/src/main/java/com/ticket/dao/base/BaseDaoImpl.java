package com.ticket.dao.base;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ticket.entity.base.BakDeleteModel;
import com.ticket.entity.base.BaseModel;
import com.ticket.entity.base.BaseTenantModel;
import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class BaseDaoImpl<T extends BaseModel, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BaseDao<T, ID> {

    private static final int BATCH_SIZE = 500;

    private final EntityManager em;

    private final JPAQueryFactory jpaQueryFactory;

    private final String tableName;



    @SuppressWarnings("all")
    public BaseDaoImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager em) {
        super(entityInformation, em);
        this.em = em;
        //获取对应表的名字
        EntityManagerFactory entityManagerFactory = em.getEntityManagerFactory();
        SessionFactoryImpl sessionFactory = (SessionFactoryImpl)entityManagerFactory.unwrap(SessionFactory.class);
        MetamodelImplementor metamodel = sessionFactory.getMetamodel();
        SingleTableEntityPersister entityPersister = (SingleTableEntityPersister)metamodel.entityPersister(getDomainClass());
        tableName = entityPersister.getTableName();
        jpaQueryFactory = new JPAQueryFactory(em);
    }


    @Override
    public String getTableName() {
        return tableName;
    }


    @Override
    public Class<T> getDomainClazz() {
        return super.getDomainClass();
    }

    @Override
    public JPAQueryFactory getJPAQueryFactory() {
        return jpaQueryFactory;
    }

    private void setParameters(Map<String, Object> parameters, Query query) {
        if (parameters != null && !parameters.isEmpty()) {
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public T customSave(T t) {
        em.persist(t);
        return t;
    }


    @Override
    public T customUpdate(T t) {
        em.merge(t);
        return t;
    }

    @Override
    public <S extends T> Iterable<S> batchCustomSave(Iterable<S> var1) {
        List<S> list = new ArrayList<>(BATCH_SIZE);
        int num = 0;
        while (var1.iterator().hasNext()) {
            S s = var1.iterator().next();
            list.add(s);
            num++;
            em.persist(s);
            if (num % BATCH_SIZE == 0) {
                em.flush();
            }
        }
        return list;
    }

    @Override
    public void customDeleteById(ID id) {
        T t = findById(id).orElseThrow(() -> new RuntimeException("删除的数据不存在！"));
        if (t instanceof BakDeleteModel) {
            ((BakDeleteModel) t).setDeleteStatus(true);
            ((BakDeleteModel) t).setDeleteTime(new Date());
            em.merge(t);
        } else {
            delete(t);
        }
    }


    @Override
    public T findOneById(ID id) {
        return em.find(getDomainClass(), id);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }


    @Override
    public Specification<T> getBaseSpecification(Map<String, Object> queryParam) {
        Class<T> domainClass = getDomainClass();
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                CriteriaQuery<?> baseSearch = query.where();
                if (BakDeleteModel.class.isAssignableFrom(domainClass)) {
                    baseSearch.where(criteriaBuilder.equal(root.get("deleteStatus"), Boolean.FALSE)).getRestriction();
                }
                if (BaseTenantModel.class.isAssignableFrom(domainClass)) {
                    baseSearch.where(criteriaBuilder.equal(root.get("tenantId"), queryParam.get("tenantId"))).getRestriction();
                }
                return baseSearch.getRestriction();
            }
        };
    }

    @Override
    public List<T> nativeSQLQuery(String sql, Map<String, Object> params) {
        Query nativeQuery = em.createNativeQuery(sql, getDomainClass());
        setParameters(params, nativeQuery);
        return nativeQuery.getResultList();
    }


    @Override
    public Page<T> nativeSQLQuery(String sql, String countSql, Map<String, Object> params, Pageable pageable) {
        Query nativeQuery = em.createNativeQuery(sql, getDomainClass());
        setParameters(params, nativeQuery);
        List<T> resultList = nativeQuery.setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize()).getResultList();

        Query nativeCountQuery = em.createNativeQuery(countSql);
        setParameters(params, nativeCountQuery);
        Number total = (Number) nativeCountQuery.getSingleResult();
        return new PageImpl<T>(resultList, pageable, total.longValue());
    }

    @Override
    public Page nativeSQLQuery(String sql, String countSql, Map<String, Object> params, Class<?> cls, Pageable pageable) {
        Query nativeQuery = em.createNativeQuery(sql);
        nativeQuery.unwrap(NativeQueryImpl.class).setResultTransformer(new AliasToBeanResultTransformer(cls));
        setParameters(params, nativeQuery);
        List resultList = nativeQuery.setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize()).getResultList();

        Query nativeCountQuery = em.createNativeQuery(countSql);
        setParameters(params, nativeCountQuery);
        Number total = (Number) nativeCountQuery.getSingleResult();
        return new PageImpl<>(resultList, pageable, total.longValue());
    }

    @Override
    public List nativeSQLQuery(String sql, Map<String, Object> params, Class<?> cls) {
        Query nativeQuery = em.createNativeQuery(sql);
        nativeQuery.unwrap(NativeQueryImpl.class).setResultTransformer(new AliasToBeanResultTransformer(cls));
        setParameters(params, nativeQuery);
        return nativeQuery.getResultList();
    }

    @Override
    public List<Map<String, Object>> nativeSQLQueryToMap(String sql, Map<String, Object> params) {
        Query nativeQuery = em.createNativeQuery(sql);
        nativeQuery.unwrap(NativeQueryImpl.class).setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        setParameters(params, nativeQuery);
        return nativeQuery.getResultList();
    }
}
