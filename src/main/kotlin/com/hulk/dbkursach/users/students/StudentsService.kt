package com.hulk.dbkursach.USER.students

import com.hulk.dbkursach.create
import com.hulk.dbkursach.enums.UserType
import com.hulk.dbkursach.users.CreateUserRequest
import com.hulk.dbkursach.users.UserStatistics
import com.hulk.dbkursach.tables.daos.GroupDao
import com.hulk.dbkursach.tables.daos.UserDao
import com.hulk.dbkursach.tables.pojos.User
import com.hulk.dbkursach.tables.references.GROUP
import com.hulk.dbkursach.tables.references.MARK
import com.hulk.dbkursach.tables.references.SUBJECT
import com.hulk.dbkursach.tables.references.USER
import org.jooq.impl.DSL.avg
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StudentsService(
    private val studentDao: UserDao,
    private val groupDao: GroupDao
) {
    @Transactional
    fun createStudent(request: CreateUserRequest): StudentInfo {
        val student = studentDao.create(User(
                firstName = request.firstName,
                lastName = request.lastName,
                patherName = request.patherName,
                groupId = request.groupId,
                type = UserType.Student
        ))

        return StudentInfo(student, getStudentGroupName(student.groupId!!))
    }

    @Transactional
    fun updateStudent(student: User): StudentInfo {
        if ( !studentDao.existsById(student.id!!)) {
            throw RuntimeException("Student with id ${student.id} not exists")
        }
        studentDao.update(student)

        return StudentInfo(student, getStudentGroupName(student.id))
    }

    fun getAverageMarks(from: Int, until: Int): List<UserStatistics> = studentDao.ctx()
        .select(USER.FIRST_NAME, USER.LAST_NAME, avg(MARK.VALUE))
        .from(USER)
        .innerJoin(MARK).on(MARK.STUDENT_ID.eq(USER.ID))
        .innerJoin(SUBJECT).on(MARK.SUBJECT_ID.eq(SUBJECT.ID))
        .where(USER.TYPE.eq(UserType.Student)
            .and(MARK.YEAR.ge(from))
            .and(MARK.YEAR.le(until))
        )
        .fetch { UserStatistics(it.value1()!!, it.value2()!!, it.value3()!!.toDouble()) }


    fun deleteStudent(id: Long) {
        if ( !studentDao.existsById(id)) {
            throw RuntimeException("Student with id $id not exists")
        }

        studentDao.deleteById(id)
    }

    private fun getStudentGroupName(groupId: Long): String = groupDao.ctx()
        .select(GROUP.NAME)
        .from(GROUP)
            .where(GROUP.ID.eq(groupId))
        .fetchOneInto(String::class.java)!!
}
