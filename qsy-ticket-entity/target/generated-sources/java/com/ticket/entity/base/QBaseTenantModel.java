package com.ticket.entity.base;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QBaseTenantModel is a Querydsl query type for BaseTenantModel
 */
@Generated("com.querydsl.codegen.SupertypeSerializer")
public class QBaseTenantModel extends EntityPathBase<BaseTenantModel> {

    private static final long serialVersionUID = 1094516903L;

    public static final QBaseTenantModel baseTenantModel = new QBaseTenantModel("baseTenantModel");

    public final StringPath tenantId = createString("tenantId");

    public QBaseTenantModel(String variable) {
        super(BaseTenantModel.class, forVariable(variable));
    }

    public QBaseTenantModel(Path<? extends BaseTenantModel> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBaseTenantModel(PathMetadata metadata) {
        super(BaseTenantModel.class, metadata);
    }

}

