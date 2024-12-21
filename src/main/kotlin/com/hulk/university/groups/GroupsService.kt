package com.hulk.university.groups

import com.hulk.university.create
import com.hulk.university.createOrUpdate
import com.hulk.university.exceptions.NotFoundException
import com.hulk.university.tables.daos.GroupDao
import com.hulk.university.tables.pojos.Group
import com.hulk.university.tables.references.GROUP
import com.hulk.university.tables.references.MARK
import com.hulk.university.tables.references.USER
import org.jooq.impl.DSL
import org.jooq.impl.DSL.selectFrom
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.temporal.ChronoField

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
    fun updateGroup(groupId: Long, groupName: String): Group {
        val group = groupDao.findById(groupId) ?:
            throw NotFoundException("Teacher with id $groupId not exists")

        group.name = groupName
        return groupDao.createOrUpdate(group)
    }

    fun getGroups(): List<Group> = groupDao.findAll()


    fun getGroup(groupId: Long?): Group? =
        groupDao.findById(groupId)


    fun getAverageMarks(from: LocalDateTime, to: LocalDateTime): List<GroupStatistics> = groupDao.ctx()
        .select(GROUP.ID, GROUP.NAME, DSL.avg(MARK.VALUE).`as`("averageMark"))
        .from(GROUP)
        .innerJoin(USER).on(USER.GROUP_ID.eq(GROUP.ID))
        .innerJoin(MARK).on(MARK.STUDENT_ID.eq(USER.ID))
        .where(MARK.YEAR.ge(from.get(ChronoField.YEAR))
            .and(MARK.YEAR.le(to.get(ChronoField.YEAR)))
        )
        .groupBy(GROUP.ID)
        .fetchInto(GroupStatistics::class.java)


    @Transactional
    fun deleteGroup(id: Long) {
        if (!groupDao.existsById(id)) {
            throw NotFoundException("Group with id $id not exists")
        }
        groupDao.deleteById(id)
    }


    private fun isGroupAlreadyExists(groupName: String) = groupDao.ctx()
        .fetchExists(
            selectFrom(GROUP)
                .where(GROUP.NAME.eq(groupName))
        )
}