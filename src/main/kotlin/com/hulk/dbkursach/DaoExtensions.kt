package com.hulk.dbkursach

import org.jooq.Condition
import org.jooq.UpdatableRecord
import org.jooq.impl.DAOImpl

fun <R: UpdatableRecord<R>, P, T> DAOImpl<R, P, T>.create(entityObj: P): P =
    this.ctx()
        .insertInto(this.table)
        .set(this.ctx().newRecord(this.table, entityObj))
        .returning()
        .fetchOne(this.mapper())!!
