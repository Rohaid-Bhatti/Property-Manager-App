package com.hzm.sharedpreference.roomDb

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ContactDAO {

    @Insert
    suspend fun insertUser(contact: Contact)

    @Update
    suspend fun updateUser(contact: Contact)

    @Delete
    suspend fun deleteUser(contact: Contact)

    @Query("SELECT * FROM user_table")
    fun getUser() : LiveData<List<Contact>>

    @Query("SELECT * FROM user_table WHERE email = :email AND password = :password")
    suspend fun getUserByEmailAndPassword(email: String, password: String): Contact?

    @Query("SELECT * FROM user_table WHERE email = :email")
    suspend fun getUserByEmail(email: String): Contact?

    @Query("SELECT * FROM user_table WHERE number = :number")
    suspend fun getUserByNumber(number: String): Contact?
}