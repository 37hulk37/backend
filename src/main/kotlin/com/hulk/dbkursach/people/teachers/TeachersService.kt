package com.hulk.dbkursach.people.teachers

import com.hulk.dbkursach.create
import com.hulk.dbkursach.people.CreatePeopleRequest
import com.hulk.dbkursach.people.PeopleStatistics
import com.hulk.dbkursach.people.PeopleType
import com.hulk.dbkursach.tables.daos.PeopleDao
import com.hulk.dbkursach.tables.pojos.People
import com.hulk.dbkursach.tables.references.MARK
import com.hulk.dbkursach.tables.references.PEOPLE
import org.jooq.impl.DSL.avg
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TeachersService(
    private val teachersDao: PeopleDao
) {
    @Transactional
    fun createTeacher(request: CreatePeopleRequest): TeacherInfo {
        val teacher = teachersDao.create(People(
                firstName = request.firstName,
                lastName = request.lastName,
                patherName = request.patherName,
                groupId = request.groupId,
                type = PeopleType.STUDENT.name
            ))

        return TeacherInfo(teacher)
    }

    @Transactional
    fun updateTeacher(teacher: People): TeacherInfo {
        if ( !teachersDao.existsById(teacher.id!!)) {
            throw RuntimeException("Teacher with id ${teacher.id} not exists")
        }
        teachersDao.update(teacher)

        return TeacherInfo(teacher)
    }

    fun getAverageMarks(from: Int, until: Int): List<PeopleStatistics> = teachersDao.ctx()
        .select(PEOPLE.FIRST_NAME, PEOPLE.LAST_NAME, avg(MARK.VALUE))
        .from(PEOPLE)
        .innerJoin(MARK).on(MARK.STUDENT_ID.eq(PEOPLE.ID))
        .where(
            MARK.YEAR.ge(from)
            .and(MARK.YEAR.le(until))
        )
        .fetch { PeopleStatistics(it.value1()!!, it.value2()!!, it.value3()!!.toDouble()) }


    fun deleteTeacher(id: Long) {
        if ( !teachersDao.existsById(id)) {
            throw RuntimeException("Teacher with id $id not exists")
        }

        teachersDao.deleteById(id)
    }
}