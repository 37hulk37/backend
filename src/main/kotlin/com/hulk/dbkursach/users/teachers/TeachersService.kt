package com.hulk.dbkursach.users.teachers

import com.hulk.dbkursach.create
import com.hulk.dbkursach.users.CreateUserRequest
import com.hulk.dbkursach.users.UserStatistics
import com.hulk.dbkursach.enums.UserType
import com.hulk.dbkursach.tables.daos.UserDao
import com.hulk.dbkursach.tables.pojos.User
import com.hulk.dbkursach.tables.references.MARK
import com.hulk.dbkursach.tables.references.USER
import org.jooq.impl.DSL.avg
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TeachersService(
    private val teachersDao: UserDao
) {
    @Transactional
    fun createTeacher(request: CreateUserRequest): TeacherInfo {
        val teacher = teachersDao.create(User(
                firstName = request.firstName,
                lastName = request.lastName,
                patherName = request.patherName,
                groupId = request.groupId,
                type = UserType.Student
        ))

        return TeacherInfo(teacher)
    }

    @Transactional
    fun updateTeacher(teacher: User): TeacherInfo {
        if ( !teachersDao.existsById(teacher.id!!)) {
            throw RuntimeException("Teacher with id ${teacher.id} not exists")
        }
        teachersDao.update(teacher)

        return TeacherInfo(teacher)
    }

    fun getAverageMarks(from: Int, until: Int): List<UserStatistics> = teachersDao.ctx()
        .select(USER.FIRST_NAME, USER.LAST_NAME, avg(MARK.VALUE))
        .from(USER)
        .innerJoin(MARK).on(MARK.STUDENT_ID.eq(USER.ID))
        .where(
            MARK.YEAR.ge(from)
            .and(MARK.YEAR.le(until))
        )
        .fetch { UserStatistics(it.value1()!!, it.value2()!!, it.value3()!!.toDouble()) }


    fun deleteTeacher(id: Long) {
        if ( !teachersDao.existsById(id)) {
            throw RuntimeException("Teacher with id $id not exists")
        }

        teachersDao.deleteById(id)
    }
}