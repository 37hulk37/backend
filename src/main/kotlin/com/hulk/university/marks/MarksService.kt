package com.hulk.university.marks

import com.hulk.university.create
import com.hulk.university.createOrUpdate
import com.hulk.university.exceptions.ApplicationException
import com.hulk.university.exceptions.NotFoundException
import com.hulk.university.subjects.SubjectService
import com.hulk.university.tables.daos.MarkDao
import com.hulk.university.tables.daos.SubjectDao
import com.hulk.university.tables.pojos.Mark
import com.hulk.university.tables.references.GROUP
import com.hulk.university.tables.references.USER
import com.hulk.university.users.UserService
import org.jooq.impl.DSL.select
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MarksService(
    private val markDao: MarkDao,
    private val userService: UserService,
    private val subjectService: SubjectService,
) {

    @Transactional
    fun createMark(request: CreateMarkRequest): Mark {
        if (!userService.isUserAlreadyExists(request.studentId)) {
            throw NotFoundException("Student with id ${request.studentId} not exists")
        }
        if (!userService.isUserAlreadyExists(request.teacherId)) {
            throw NotFoundException("Teacher with id ${request.studentId} not exists")
        }
        if (!subjectService.isSubjectAlreadyExists(request.subjectId)) {
            throw NotFoundException("Subject with id ${request.subjectId} not exists")
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
            throw ApplicationException("Can not update mark with id ${mark.id} to student with id ${mark.studentId} not exists")
        }
        markDao.update(mark)
        return mark
    }

}