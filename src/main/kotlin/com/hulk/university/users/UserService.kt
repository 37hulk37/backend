package com.hulk.university.users

import com.hulk.university.create
import com.hulk.university.createOrUpdate
import com.hulk.university.enums.UserType
import com.hulk.university.exceptions.ApplicationException
import com.hulk.university.exceptions.NotFoundException
import com.hulk.university.groups.GroupsService
import com.hulk.university.setIfNotNull
import com.hulk.university.tables.daos.UserDao
import com.hulk.university.tables.pojos.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class UserService(
    private val userDao: UserDao,
    private val groupsService: GroupsService,
    private val userHandlers: List<UserHandler>
) {

    private val userHandlersByType: Map<UserType, UserHandler> by lazy {
        userHandlers.associateBy { it.getUserType() }
    }

    @Transactional
    fun createUser(request: CreateUserRequest): UserInfo {
        val user = userDao.create(User(
            firstName = request.firstName(),
            lastName = request.lastName(),
            patherName = request.patherName(),
            groupId = request.groupId(),
            type = request.userType()
        ))
        return transformToInfo(user)
    }

    @Transactional
    fun updateUser(userId: Long, request: UpdateUserRequest): UserInfo {
        val user = userDao.findById(userId) ?:
            throw NotFoundException("User with id $userId not found")

        setIfNotNull(request.firstName()) { user.firstName = request.firstName() }
        setIfNotNull(request.lastName()) { user.lastName = request.lastName() }
        setIfNotNull(request.patherName()) { user.patherName = request.patherName() }
        setIfNotNull(request.groupId()) { user.groupId = request.groupId() }
        val updatedUser = userDao.createOrUpdate(user)

        return transformToInfo(updatedUser)
    }

    @Transactional(readOnly = true)
    fun getUser(id: Long): UserInfo {
        val user = userDao.findById(id) ?:
            throw NotFoundException("User with id $id not found")

        return transformToInfo(user)
    }


    @Transactional(readOnly = true)
    fun getUsers(userType: UserType): List<UserInfo> =
        userDao.fetchByType(userType)
            .map(::transformToInfo)


    @Transactional
    fun deleteUser(userId: Long) {
        if (!userDao.existsById(userId)) {
            throw NotFoundException("User with id $userId not exists")
        }
        userDao.deleteById(userId)
    }

    fun isUserExists(userId: Long, type: UserType): Boolean =
        userDao.findById(userId)?.takeIf { it.type == type } != null


    fun getAverageMarks(userType: UserType, from: Instant, to: Instant): List<UserStatistics> {
        val handler = userHandlersByType[userType] ?:
            throw ApplicationException("Unknown user type: $userType")

        return handler.getAverageMarks(from, to)
    }

    private fun transformToInfo(user: User): UserInfo {
        val groupName = user.groupId?.let { groupsService.getGroup(user.groupId)?.name }
            ?: ""
        return UserInfo(user, groupName)
    }

}