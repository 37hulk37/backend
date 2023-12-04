package com.hulk.dbkursach.people.students

import com.hulk.dbkursach.create
import com.hulk.dbkursach.people.*
import com.hulk.dbkursach.tables.daos.GroupDao
import com.hulk.dbkursach.tables.daos.PeopleDao
import com.hulk.dbkursach.tables.pojos.People
import com.hulk.dbkursach.tables.references.GROUP
import com.hulk.dbkursach.tables.references.MARK
import com.hulk.dbkursach.tables.references.PEOPLE
import com.hulk.dbkursach.tables.references.SUBJECT
import org.jooq.impl.DSL.avg
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StudentsService(
    private val studentDao: PeopleDao,
    private val groupDao: GroupDao
) {
    @Transactional
    fun createStudent(request: CreatePeopleRequest): StudentInfo {
        val student = studentDao.create(People(
                firstName = request.firstName,
                lastName = request.lastName,
                patherName = request.patherName,
                groupId = request.groupId,
                type = PeopleType.STUDENT.name
        ))

        return StudentInfo(student, getStudentGroupName(student.groupId!!))
    }

    @Transactional
    fun updateStudent(student: People): StudentInfo {
        if ( !studentDao.existsById(student.id!!)) {
            throw RuntimeException("Student with id ${student.id} not exists")
        }
        studentDao.update(student)

        return StudentInfo(student, getStudentGroupName(student.id))
    }

    fun getAverageMarks(from: Int, until: Int): List<PeopleStatistics> = studentDao.ctx()
        .select(PEOPLE.FIRST_NAME, PEOPLE.LAST_NAME, avg(MARK.VALUE))
        .from(PEOPLE)
        .innerJoin(MARK).on(MARK.STUDENT_ID.eq(PEOPLE.ID))
        .innerJoin(SUBJECT).on(MARK.SUBJECT_ID.eq(SUBJECT.ID))
        .where(PEOPLE.TYPE.eq(PeopleType.STUDENT.name)
            .and(MARK.YEAR.ge(from))
            .and(MARK.YEAR.le(until))
        )
        .fetch { PeopleStatistics(it.value1()!!, it.value2()!!, it.value3()!!.toDouble()) }


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
