package com.hulk.university.subjects

import com.hulk.university.create
import com.hulk.university.exceptions.NotFoundException
import com.hulk.university.tables.daos.SubjectDao
import com.hulk.university.tables.pojos.Subject
import com.hulk.university.tables.references.MARK
import com.hulk.university.tables.references.SUBJECT
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
        if (isSubjectNameAlreadyExists(request.name)) {
            throw NotFoundException("Subject name ${request.name} not unique")
        }

        return subjectDao.create(Subject(name = request.name))
    }

    @Transactional
    fun updateSubject(subject: Subject): Subject {
        if ( !subjectDao.existsById(subject.id)) {
            throw NotFoundException("Subject with id ${subject.id} not exists")
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
        .fetchInto(SubjectStatistics::class.java)


    fun deleteSubject(id: Long) {
        if ( !subjectDao.existsById(id)) {
            throw NotFoundException("Subject with id $id not exists")
        }

        subjectDao.deleteById(id)
    }

    fun isSubjectAlreadyExists(subjectId: Long): Boolean = subjectDao.existsById(subjectId)

    fun isSubjectNameAlreadyExists(name: String): Boolean = subjectDao.ctx()
        .fetchExists(
            select(SUBJECT)
                .from(SUBJECT)
                .where(SUBJECT.NAME.eq(name))
        )

}