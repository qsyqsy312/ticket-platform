package com.ticket.service.base;

import com.ticket.entity.base.BaseModel;
import com.ticket.support.dto.base.BaseDTO;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.core.rule.TableRule;
import org.apache.shardingsphere.core.strategy.route.complex.ComplexShardingStrategy;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 水平分表
 *
 * @param <T>
 * @param <ID>
 */
public abstract class StandardShardingService<T extends BaseModel, ID extends Serializable> extends BaseService<T, ID> implements IShardingService<T, ID> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StandardShardingService.class);


    protected ComplexKeysShardingAlgorithm algorithm;
    protected TableRule tableRule;

    @Autowired
    protected ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        ShardingDataSource bean = null;
        try {
            bean = applicationContext.getBean(ShardingDataSource.class);
        } catch (Exception e) {
            LOGGER.info("sharding-jdbc not configured");
            return;
        }
        Collection<TableRule> tableRules = bean.getRuntimeContext().getRule().getTableRules();
        for (TableRule tableRule : tableRules) {
            if (baseDao.getTableName().equals(tableRule.getLogicTable())) {
                this.tableRule = tableRule;
                break;
            }
        }
        Assert.notNull(tableRule, "分表规则未配置！");
        //反射取出分片算法实现
        ComplexShardingStrategy tableShardingStrategy = (ComplexShardingStrategy) tableRule.getTableShardingStrategy();
        Field shardingAlgorithm = ReflectionUtils.findField(ComplexShardingStrategy.class, "shardingAlgorithm");
        ReflectionUtils.makeAccessible(shardingAlgorithm);
        this.algorithm = (ComplexKeysShardingAlgorithm) ReflectionUtils.getField(shardingAlgorithm, tableShardingStrategy);
    }

    @Override
    public Object save(BaseDTO dto) throws Exception {
        T t = toEntity(dto, baseDao.getDomainClazz().newInstance());
        customIDGenerator(t);
        createTable(t);
        return toDTO(baseDao.customSave(t));
    }

    @Override
    public Object update(BaseDTO dto) {
        T one = baseDao.findOneById((ID) dto.getId());
        T t = toEntity(dto, one);
        createTable(t);
        return toDTO(baseDao.customUpdate(t));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public T findOneById(ID id) {
        return super.findOneById(id);
    }

    @Override
    public void deleteByIds(Iterable<ID> ids) {
        super.deleteByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Object> list(Map<String, Object> queryParam, Sort sort) {
        createTable(queryParam);
        return super.list(queryParam, sort);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Page<Object> page(Map<String, Object> queryParam, Pageable pageable) {
        createTable(queryParam);
        return super.page(queryParam, pageable);
    }


}
