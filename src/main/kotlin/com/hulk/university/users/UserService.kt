package com.hulk.university.users

import com.hulk.university.create
import com.hulk.university.enums.UserType
import com.hulk.university.tables.daos.UserDao
import com.hulk.university.tables.pojos.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class UserService(
    private val userDao: UserDao,
    private val userHandlers: List<UserHandler>
) {

    private val userHandlersByType: Map<UserType, UserHandler> by lazy {
        userHandlers.associateBy { it.getUserType() }
    }

    @Transactional
    fun createUser(request: CreateUserRequest): UserInfo {
        val student = userDao.create(User(
            firstName = request.firstName(),
            lastName = request.lastName(),
            patherName = request.patherName(),
            groupId = request.groupId(),
            type = request.userType()
        ))

        val handler = userHandlersByType[request.userType()] ?:
            throw RuntimeException("Unknown user type: ${request.userType()}")

        return UserInfo(student, handler.getGroupName(request.groupId()))
    }

    @Transactional
    fun updateUser(user: User): UserInfo {
        if ( !isUserAlreadyExists(user.id!!)) {
            throw RuntimeException("User with id ${user.id} not exists")
        }
        userDao.update(user)

        val handler = userHandlersByType[user.type] ?:
            throw RuntimeException("Unknown user type: ${user.type}")
        return UserInfo(user, handler.getGroupName(user.groupId!!))
    }

    @Transactional(readOnly = true)
    fun getUser(id: Long): UserInfo {
        val user = userDao.findById(id) ?:
            throw RuntimeException("User with id $id not found")

        val handler = userHandlersByType[user.type] ?:
            throw RuntimeException("Unknown user type: ${user.type}")
        return UserInfo(user, handler.getGroupName(user.groupId!!))
    }

    @Transactional(readOnly = true)
    fun getUsers(userType: UserType): List<UserInfo> {
        val handler = userHandlersByType[userType] ?:
            throw RuntimeException("Unknown user type: $userType")

        return userDao.fetchByType(userType)
            .associateBy { it.groupId }
            .map { (groupId, user) ->
                UserInfo(user, handler.getGroupName(groupId))
            }
    }

    @Transactional
    fun deleteUser(userId: Long) {
        if (!userDao.existsById(userId)) {
            throw RuntimeException("User with id $userId not exists")
        }
        userDao.deleteById(userId)
    }

    fun isUserAlreadyExists(userId: Long): Boolean = userDao.existsById(userId)

    fun getAverageMarks(userType: UserType, from: Instant, to: Instant): List<UserStatistics> {
        val handler = userHandlersByType[userType] ?:
            throw RuntimeException("Unknown user type: $userType")

        return handler.getAverageMarks(from, to)
    }

}