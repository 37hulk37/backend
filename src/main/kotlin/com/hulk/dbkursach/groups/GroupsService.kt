package com.hulk.dbkursach.groups

import com.hulk.dbkursach.create
import com.hulk.dbkursach.tables.daos.GroupDao
import com.hulk.dbkursach.tables.pojos.Group
import com.hulk.dbkursach.tables.references.GROUP
import com.hulk.dbkursach.tables.references.MARK
import com.hulk.dbkursach.tables.references.USER
import org.jooq.impl.DSL
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GroupsService(
    private val groupDao: GroupDao
) {
    @Transactional
    fun createGroup(groupname: String): Group =
        groupDao.create(Group(name = groupname))

    @Transactional
    fun updateGroup(group: Group): Group {
        if ( !groupDao.existsById(group.id!!)) {
            throw RuntimeException("Teacher with id ${group.id} not exists")
        }
        groupDao.update(group)
        return group
    }

    fun getAverageMarks(from: Int, until: Int): List<GroupStatistics> = groupDao.ctx()
        .select(GROUP.NAME, DSL.avg(MARK.VALUE))
        .from(GROUP)
        .innerJoin(USER).on(USER.GROUP_ID.eq(GROUP.ID))
        .innerJoin(MARK).on(MARK.STUDENT_ID.eq(USER.ID))
        .where(MARK.YEAR.ge(from)
                .and(MARK.YEAR.le(until))
        )
        .fetch { GroupStatistics(it.value1()!!, it.value2()!!.toDouble()) }

    fun deleteGroup(id: Long) {
        if ( !groupDao.existsById(id)) {
            throw RuntimeException("Teacher with id $id not exists")
        }

        groupDao.deleteById(id)
    }
}