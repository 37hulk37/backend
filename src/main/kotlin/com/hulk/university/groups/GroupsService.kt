package com.hulk.university.groups

import com.hulk.university.create
import com.hulk.university.exceptions.NotFoundException
import com.hulk.university.tables.daos.GroupDao
import com.hulk.university.tables.pojos.Group
import com.hulk.university.tables.references.GROUP
import com.hulk.university.tables.references.MARK
import com.hulk.university.tables.references.USER
import org.jooq.impl.DSL
import org.jooq.impl.DSL.select
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GroupsService(
    private val groupDao: GroupDao
) {

    @Transactional
    fun createGroup(groupName: String): Group {
        if (isGroupAlreadyExists(groupName)) {
            throw NotFoundException("Group with name $groupName already exists")
        }
        return groupDao.create(Group(name = groupName))
    }

    @Transactional
    fun updateGroup(group: Group): Group {
        if ( !groupDao.existsById(group.id!!)) {
            throw NotFoundException("Teacher with id ${group.id} not exists")
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
        if (!groupDao.existsById(id)) {
            throw NotFoundException("Group with id $id not exists")
        }
        groupDao.deleteById(id)
    }

    private fun isGroupAlreadyExists(groupName: String) = groupDao.ctx()
        .fetchExists(
            select(GROUP)
                .where(GROUP.NAME.eq(groupName))
        )
}