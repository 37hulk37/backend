package com.hulk.university.users.students

import com.hulk.university.enums.UserType
import com.hulk.university.tables.daos.GroupDao
import com.hulk.university.tables.daos.UserDao
import com.hulk.university.tables.references.GROUP
import com.hulk.university.tables.references.MARK
import com.hulk.university.tables.references.SUBJECT
import com.hulk.university.tables.references.USER
import com.hulk.university.users.UserHandler
import com.hulk.university.users.UserStatistics
import org.jooq.impl.DSL.avg
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoField

@Component
class StudentHandler(
    private val userDao: UserDao,
    private val groupDao: GroupDao,
): UserHandler {

    override fun getUserType(): UserType = UserType.Student

    override fun getAverageMarks(from: Instant, to: Instant): List<UserStatistics> = userDao.ctx()
        .select(USER.FIRST_NAME, USER.LAST_NAME, avg(MARK.VALUE))
        .from(USER)
        .innerJoin(MARK).on(MARK.STUDENT_ID.eq(USER.ID))
        .innerJoin(SUBJECT).on(MARK.SUBJECT_ID.eq(SUBJECT.ID))
        .where(
            USER.TYPE.eq(UserType.Student)
            .and(MARK.YEAR.ge(from.get(ChronoField.YEAR)))
            .and(MARK.YEAR.le(to.get(ChronoField.YEAR)))
        )
        .fetchInto(UserStatistics::class.java)

    override fun getGroupName(groupId: Long?): String = groupDao.ctx()
        .select(GROUP.NAME)
        .from(GROUP)
        .where(GROUP.ID.eq(groupId))
        .fetchOneInto(String::class.java)!!

}