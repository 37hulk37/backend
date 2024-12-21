package com.hulk.university.users.teachers

import com.hulk.university.enums.UserType
import com.hulk.university.tables.daos.UserDao
import com.hulk.university.tables.references.MARK
import com.hulk.university.tables.references.USER
import com.hulk.university.users.UserHandler
import com.hulk.university.users.UserStatistics
import org.jooq.impl.DSL.avg
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.temporal.ChronoField

@Component
class TeacherHandler(
   private val userDao: UserDao,
): UserHandler {

    override fun getUserType(): UserType = UserType.Teacher

    override fun getAverageMarks(from: LocalDateTime, to: LocalDateTime): List<UserStatistics> = userDao.ctx()
        .select(USER.ID, USER.FIRST_NAME, USER.LAST_NAME, avg(MARK.VALUE).`as`("averageMark"))
        .from(USER)
        .join(MARK).on(MARK.TEACHER_ID.eq(USER.ID))
        .where(MARK.YEAR.ge(from.get(ChronoField.YEAR))
            .and(MARK.YEAR.le(to.get(ChronoField.YEAR)))
        )
        .groupBy(USER.ID)
        .orderBy(USER.ID)
        .fetchInto(UserStatistics::class.java)
}