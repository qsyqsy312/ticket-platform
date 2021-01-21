package com.ticket.entity.base;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QBaseModel is a Querydsl query type for BaseModel
 */
@Generated("com.querydsl.codegen.SupertypeSerializer")
public class QBaseModel extends EntityPathBase<BaseModel> {

    private static final long serialVersionUID = -620649167L;

    public static final QBaseModel baseModel = new QBaseModel("baseModel");

    public final DateTimePath<java.util.Date> createTime = createDateTime("createTime", java.util.Date.class);

    public final StringPath id = createString("id");

    public final DateTimePath<java.util.Date> lastModifyTime = createDateTime("lastModifyTime", java.util.Date.class);

    public QBaseModel(String variable) {
        super(BaseModel.class, forVariable(variable));
    }

    public QBaseModel(Path<? extends BaseModel> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBaseModel(PathMetadata metadata) {
        super(BaseModel.class, metadata);
    }

}

