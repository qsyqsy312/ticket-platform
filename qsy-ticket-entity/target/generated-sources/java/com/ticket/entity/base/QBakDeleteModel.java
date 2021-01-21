package com.ticket.entity.base;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QBakDeleteModel is a Querydsl query type for BakDeleteModel
 */
@Generated("com.querydsl.codegen.SupertypeSerializer")
public class QBakDeleteModel extends EntityPathBase<BakDeleteModel> {

    private static final long serialVersionUID = -171586087L;

    public static final QBakDeleteModel bakDeleteModel = new QBakDeleteModel("bakDeleteModel");

    public final QBaseModel _super = new QBaseModel(this);

    //inherited
    public final DateTimePath<java.util.Date> createTime = _super.createTime;

    public final BooleanPath deleteStatus = createBoolean("deleteStatus");

    public final DateTimePath<java.util.Date> deleteTime = createDateTime("deleteTime", java.util.Date.class);

    //inherited
    public final StringPath id = _super.id;

    //inherited
    public final DateTimePath<java.util.Date> lastModifyTime = _super.lastModifyTime;

    public QBakDeleteModel(String variable) {
        super(BakDeleteModel.class, forVariable(variable));
    }

    public QBakDeleteModel(Path<? extends BakDeleteModel> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBakDeleteModel(PathMetadata metadata) {
        super(BakDeleteModel.class, metadata);
    }

}

