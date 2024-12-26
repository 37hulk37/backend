package com.hulk.university.subjects

import com.hulk.university.create
import com.hulk.university.createOrUpdate
import com.hulk.university.exceptions.NotFoundException
import com.hulk.university.setIfNotNull
import com.hulk.university.tables.daos.SubjectDao
import com.hulk.university.tables.pojos.Subject
import com.hulk.university.tables.references.MARK
import com.hulk.university.tables.references.SUBJECT
import org.jooq.impl.DSL.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoField

@Service
class SubjectService(
    private val subjectDao: SubjectDao
) {
    @Transactional
    fun createSubject(subjectName: String): Subject {
        if (isSubjectNameAlreadyExists(subjectName)) {
            throw NotFoundException("Subject name $subjectName not unique")
        }
        return subjectDao.create(Subject(name = subjectName))
    }

    @Transactional
    fun updateSubject(subjectId: Long, subjectName: String): Subject {
        val subject = subjectDao.findById(subjectId) ?:
            throw NotFoundException("Subject with id $subjectId not found")

        setIfNotNull(subjectName) { subject.name = subjectName }
        return subjectDao.createOrUpdate(subject)
    }

    fun getSubjects(): List<Subject> = subjectDao.findAll()

    fun getSubject(subjectId: Long) =
        subjectDao.findById(subjectId) ?:
            throw NotFoundException("Subject with id $subjectId not found")

    fun getAverageMarks(from: Instant, until: Instant): List<SubjectStatistics> = subjectDao.ctx()
        .select(SUBJECT.ID, SUBJECT.NAME, avg(MARK.VALUE).`as`("averageMark"))
        .from(SUBJECT)
        .innerJoin(MARK).on(MARK.SUBJECT_ID.eq(SUBJECT.ID))
        .where(MARK.YEAR.ge(from.get(ChronoField.YEAR))
            .and(MARK.YEAR.le(until.get(ChronoField.YEAR)))
        )
        .groupBy(SUBJECT.ID)
        .fetchInto(SubjectStatistics::class.java)


    @Transactional
    fun deleteSubject(id: Long) {
        if ( !subjectDao.existsById(id)) {
            throw NotFoundException("Subject with id $id not exists")
        }
        subjectDao.deleteById(id)
    }

    fun isSubjectExists(subjectId: Long): Boolean = subjectDao.existsById(subjectId)

    private fun isSubjectNameAlreadyExists(name: String): Boolean = subjectDao.ctx()
        .fetchExists(
            selectFrom(SUBJECT)
                .where(SUBJECT.NAME.eq(name))
        )

}