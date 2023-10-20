package com.hzm.sharedpreference.roomDb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class Contact(

    @PrimaryKey(autoGenerate = true)
    val id : Long,
    val name : String,
    val email : String,
    val password : String,
    val number : String,
)
