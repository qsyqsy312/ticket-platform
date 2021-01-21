package com.ticket.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -572256063L;

    public static final QUser user = new QUser("user");

    public final com.ticket.entity.base.QBaseModel _super = new com.ticket.entity.base.QBaseModel(this);

    //inherited
    public final DateTimePath<java.util.Date> createTime = _super.createTime;

    public final BooleanPath enabled = createBoolean("enabled");

    //inherited
    public final StringPath id = _super.id;

    //inherited
    public final DateTimePath<java.util.Date> lastModifyTime = _super.lastModifyTime;

    public final StringPath password = createString("password");

    public final DateTimePath<java.util.Date> registerTime = createDateTime("registerTime", java.util.Date.class);

    public final SetPath<Role, QRole> roles = this.<Role, QRole>createSet("roles", Role.class, QRole.class, PathInits.DIRECT2);

    public final StringPath userName = createString("userName");

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

