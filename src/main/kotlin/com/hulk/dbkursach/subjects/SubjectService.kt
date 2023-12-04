package com.hulk.dbkursach.subjects

import com.hulk.dbkursach.create
import com.hulk.dbkursach.tables.daos.SubjectDao
import com.hulk.dbkursach.tables.pojos.Subject
import com.hulk.dbkursach.tables.references.MARK
import com.hulk.dbkursach.tables.references.SUBJECT
import org.jooq.impl.DSL
import org.jooq.impl.DSL.avg
import org.jooq.impl.DSL.select
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SubjectService(
    private val subjectDao: SubjectDao
) {
    @Transactional
    fun createSubject(request: CreateSubjectRequest): Subject {
        if (isSubjectNameAlreadyTook(request.name)) {
            throw RuntimeException("Subject name ${request.name} not unique")
        }

        return subjectDao.create(Subject(name = request.name))
    }

    @Transactional
    fun updateSubject(subject: Subject): Subject {
        if ( !isSubjectExist(subject.id!!)) {
            throw RuntimeException("Subject with id ${subject.id} not exists")
        }
        subjectDao.update(subject)

        return subject
    }

    fun getAverageMarks(from: Int, until: Int): List<SubjectStatistics> = subjectDao.ctx()
            .select(SUBJECT.NAME, avg(MARK.VALUE))
            .from(SUBJECT)
            .innerJoin(MARK).on(MARK.SUBJECT_ID.eq(SUBJECT.ID))
            .where(MARK.YEAR.ge(from)
                .and(MARK.YEAR.le(until))
            )
            .fetch { SubjectStatistics(it.value1()!!, it.value2()!!.toDouble()) }


    fun deleteSubject(id: Long) {
        if ( !isSubjectExist(id)) {
            throw RuntimeException("Subject with id $id not exists")
        }

        subjectDao.deleteById(id)
    }

    private fun isSubjectNameAlreadyTook(name: String): Boolean = subjectDao.ctx()
        .fetchExists(
            select(SUBJECT)
                .from(SUBJECT)
                .where(SUBJECT.NAME.eq(name))
        )

    private fun isSubjectExist(id: Long): Boolean =
        subjectDao.existsById(id)
}