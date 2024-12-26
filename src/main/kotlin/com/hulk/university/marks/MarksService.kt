package com.hulk.university.marks

import com.hulk.university.create
import com.hulk.university.createOrUpdate
import com.hulk.university.enums.UserType
import com.hulk.university.exceptions.NotFoundException
import com.hulk.university.retryabletasks.RetryableTaskService
import com.hulk.university.subjects.SubjectService
import com.hulk.university.tables.daos.MarkDao
import com.hulk.university.tables.pojos.Mark
import com.hulk.university.users.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MarksService(
    private val markDao: MarkDao,
    private val userService: UserService,
    private val subjectService: SubjectService,
    private val retryableTaskService: RetryableTaskService,
) {

    @Transactional
    fun createMark(request: CreateMarkRequest): Mark {
        if (!userService.isUserExists(request.studentId, UserType.Student)) {
            throw NotFoundException("Student with id ${request.studentId} not exists")
        }
        if (!userService.isUserExists(request.teacherId, UserType.Teacher)) {
            throw NotFoundException("Teacher with id ${request.studentId} not exists")
        }
        if (!subjectService.isSubjectExists(request.subjectId)) {
            throw NotFoundException("Subject with id ${request.subjectId} not exists")
        }

        val mark = markDao.create(Mark(
            studentId = request.studentId,
            subjectId = request.subjectId,
            teacherId = request.teacherId,
            value = request.value,
            year = request.year
        ))
        retryableTaskService.createTask(mark, Mark::class.simpleName!!)

        return mark
    }

    @Transactional
    fun updateMark(markId: Long, markValue: Int): Mark {
        val mark = markDao.findById(markId) ?:
            throw NotFoundException("Mark with id $markId not exists")

        mark.value = markValue
        return markDao.createOrUpdate(mark)
    }

    fun getStudentMarks(studentId: Long): List<Mark> =
        markDao.fetchByStudentId(studentId)

    @Transactional
    fun deleteMark(markId: Long) {
        if (!markDao.existsById(markId)) {
            throw NotFoundException("Mark with id $markId not found")
        }
        markDao.deleteById(markId)
    }

}