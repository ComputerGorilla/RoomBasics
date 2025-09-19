package com.example.roombasics.data.repository

import com.example.roombasics.data.database.UserDao
import com.example.roombasics.data.database.entities.User
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {
    fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()

    suspend fun insertUser(user: User) = userDao.insertUser(user)

    suspend fun updateUser(user: User) = userDao.updateUser(user)

    suspend fun deleteUser(user: User) = userDao.deleteUser(user)

    fun getUserById(userId: Int): User? = getUserById(userId)
}