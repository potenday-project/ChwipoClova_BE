package com.chwipoClova.qa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QQa is a Querydsl query type for Qa
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QQa extends EntityPathBase<Qa> {

    private static final long serialVersionUID = 24461021L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QQa qa = new QQa("qa");

    public final StringPath aiAnswer = createString("aiAnswer");

    public final StringPath answer = createString("answer");

    public final com.chwipoClova.interview.entity.QInterview interview;

    public final DateTimePath<java.util.Date> modifyDate = createDateTime("modifyDate", java.util.Date.class);

    public final NumberPath<Long> qaId = createNumber("qaId", Long.class);

    public final StringPath question = createString("question");

    public final DateTimePath<java.util.Date> regDate = createDateTime("regDate", java.util.Date.class);

    public QQa(String variable) {
        this(Qa.class, forVariable(variable), INITS);
    }

    public QQa(Path<? extends Qa> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QQa(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QQa(PathMetadata metadata, PathInits inits) {
        this(Qa.class, metadata, inits);
    }

    public QQa(Class<? extends Qa> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.interview = inits.isInitialized("interview") ? new com.chwipoClova.interview.entity.QInterview(forProperty("interview"), inits.get("interview")) : null;
    }

}

