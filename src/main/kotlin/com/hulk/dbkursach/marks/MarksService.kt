package com.hulk.dbkursach.marks

import com.hulk.dbkursach.create
import com.hulk.dbkursach.enums.UserType
import com.hulk.dbkursach.tables.daos.MarkDao
import com.hulk.dbkursach.tables.pojos.Mark
import com.hulk.dbkursach.tables.references.GROUP
import com.hulk.dbkursach.tables.references.MARK
import com.hulk.dbkursach.tables.references.SUBJECT
import com.hulk.dbkursach.tables.references.USER
import org.jooq.Condition
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
                    .innerJoin(USER).on(USER.ID.eq(MARK.STUDENT_ID)
                        .and(USER.TYPE.eq(UserType.Student))
                    )
                    .innerJoin(USER).on(USER.ID.eq(MARK.TEACHER_ID)
                        .and(USER.TYPE.eq(UserType.Teacher))
                    )
            )


    private fun getStudentGroupName(studentId: Long): String = markDao.ctx()
        .select(GROUP.NAME)
        .from(USER)
        .innerJoin(GROUP).on(USER.GROUP_ID.eq(GROUP.ID))
        .where(GROUP.ID.eq(studentId))
        .fetchOneInto(String::class.java)!!
}