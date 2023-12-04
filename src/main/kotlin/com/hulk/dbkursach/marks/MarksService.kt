package com.hulk.dbkursach.marks

import com.hulk.dbkursach.create
import com.hulk.dbkursach.people.PeopleType
import com.hulk.dbkursach.tables.daos.MarkDao
import com.hulk.dbkursach.tables.pojos.Mark
import com.hulk.dbkursach.tables.references.GROUP
import com.hulk.dbkursach.tables.references.MARK
import com.hulk.dbkursach.tables.references.PEOPLE
import com.hulk.dbkursach.tables.references.SUBJECT
import org.jooq.impl.DSL.select
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MarksService(
    private val markDao: MarkDao
) {
    @Transactional
    fun createMark(request: CreateMarkRequest): Mark {
        if (!isStudentAndSubjectAndTeacherExist(request.studentId, request.subjectId, request.teacherId)) {
            throw RuntimeException("Can't set mark to student ${request.studentId}")
        }

        return markDao.create(Mark(
                studentId = request.studentId,
                subjectId = request.subjectId,
                teacherId = request.teacherId,
                value = request.value,
                year = request.year
        ))
    }

    @Transactional
    fun updateMark(mark: Mark): Mark {
        if ( !markDao.existsById(mark.id)) {
            throw RuntimeException("Can not update mark with id $${mark.id} to student with id ${mark.studentId} not exists")
        }
        markDao.update(mark)

        return mark;
    }

    private fun isStudentAndSubjectAndTeacherExist(studentId: Long, subjectId: Long, teacherId: Long): Boolean =
        markDao.ctx()
            .fetchExists(
                select(MARK.ID)
                    .from(MARK)
                    .innerJoin(SUBJECT).on(SUBJECT.ID.eq(MARK.SUBJECT_ID))
                    .innerJoin(PEOPLE).on(PEOPLE.ID.eq(MARK.STUDENT_ID)
                        .and(PEOPLE.TYPE.eq(PeopleType.STUDENT.name))
                    )
                    .innerJoin(PEOPLE).on(PEOPLE.ID.eq(MARK.TEACHER_ID)
                        .and(PEOPLE.TYPE.eq(PeopleType.TEACHER.name))
                    )
            )


    private fun getStudentGroupName(studentId: Long): String = markDao.ctx()
        .select(GROUP.NAME)
        .from(PEOPLE)
        .innerJoin(GROUP).on(PEOPLE.GROUP_ID.eq(GROUP.ID))
        .where(GROUP.ID.eq(studentId))
        .fetchOneInto(String::class.java)!!
}