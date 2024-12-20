package com.hulk.university

import org.jooq.UpdatableRecord
import org.jooq.impl.DAOImpl

fun <R: UpdatableRecord<R>, P, T> DAOImpl<R, P, T>.create(entityObj: P): P =
    this.ctx()
        .insertInto(this.table)
        .set(this.ctx().newRecord(this.table, entityObj))
        .returning()
        .fetchOne(this.mapper())!!

fun <R: UpdatableRecord<R>, P, T> DAOImpl<R, P, T>.createOrUpdate(entityObj: P): P {
    val rec = this.ctx().newRecord(this.table, entityObj)
    return this.ctx()
        .insertInto(this.table)
        .set(rec)
        .onDuplicateKeyUpdate()
        .set(rec)
        .returning()
        .fetchOne(this.mapper())!!
}
