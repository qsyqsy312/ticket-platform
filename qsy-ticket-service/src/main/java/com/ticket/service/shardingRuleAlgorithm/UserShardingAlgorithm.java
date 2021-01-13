package com.ticket.service.shardingRuleAlgorithm;


import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.ticket.entity.User;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingValue;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.*;

@Component
public class UserShardingAlgorithm implements ComplexKeysShardingAlgorithm<Comparable<?>> {


    private DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMM");

    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, ComplexKeysShardingValue<Comparable<?>> shardingValue) {
        Map columnNameAndShardingValuesMap = shardingValue.getColumnNameAndShardingValuesMap();
        Map<String, Range<Comparable<?>>> columnNameAndRangeValuesMap = shardingValue.getColumnNameAndRangeValuesMap();
        //EQ IN
        if (MapUtils.isNotEmpty(columnNameAndShardingValuesMap)) {
            Collection<Date> registerTimes = (Collection<Date>) columnNameAndShardingValuesMap.get("registerTime");
            Collection<String> ids = (Collection<String>) columnNameAndShardingValuesMap.get("id");

            if (CollectionUtils.isNotEmpty(ids)) {
                //处理id逻辑
                return getTableNameById(availableTargetNames, ids);
            } else {
                Assert.notEmpty(registerTimes, "查询条件至少包含主键或者时间任意一个！");
                return getTableNameByTime(availableTargetNames, registerTimes);
            }
        }
        // >= <= between
        if (MapUtils.isNotEmpty(columnNameAndRangeValuesMap)) {
            List<String> tableNames = Lists.newArrayList();
            Range<Comparable<?>> valueRange = columnNameAndRangeValuesMap.get("registerTime");
            if (valueRange.hasLowerBound() && valueRange.hasUpperBound()) {
                DateTime startTime = new DateTime(valueRange.lowerEndpoint());
                DateTime endTime = new DateTime(valueRange.upperEndpoint());
                if (Period.fieldDifference(startTime.toLocalDateTime(), endTime.toLocalDateTime()).getMonths() > 1) {
                    throw new RuntimeException("查询日期不得超过两个月");
                }
                while (startTime.compareTo(endTime)<=0){
                    tableNames.add(User.TABLE_NAME+"_"+startTime.toString(formatter));
                    startTime = startTime.plusMonths(1);
                }
            }

            if (!tableNames.isEmpty()) {
                return tableNames;
            }
        }
        throw new RuntimeException("分表错误！表名称不存在！");
    }


    private Collection<String> getTableNameByTime(Collection<String> availableTargetNames, Collection<Date> dates) {
        List<String> tableNames = Lists.newArrayList();
        for (Date createTime : dates) {
            String dateStr = new DateTime(createTime).toString(formatter);
            String tableName = User.TABLE_NAME + "_" + dateStr;
            tableNames.add(tableName);
        }
        return tableNames;
    }


    private Collection<String> getTableNameById(Collection<String> availableTargetNames, Collection<String> ids) {
        List<String> tableNames = Lists.newArrayList();
        for(String id:ids){
            String dateStr = id.split("-")[1];
            DateTime dateTime = DateTime.parse(dateStr, DateTimeFormat.forPattern("yyyyMMddHHmmss"));
            String tableName = User.TABLE_NAME + "_" + dateTime.toString(formatter);
            tableNames.add(tableName);
        }
        return tableNames;
    }
}
