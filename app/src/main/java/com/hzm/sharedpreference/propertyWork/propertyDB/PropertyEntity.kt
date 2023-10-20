package com.hzm.sharedpreference.propertyWork.propertyDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "property_table")
data class PropertyEntity(
    @PrimaryKey(autoGenerate = true)
    val houseId : Long,
    val numberOfRoom : String,
    val numberOfBedroom : String,
    val numberOfBathroom : String,
    val floor : String,
    val type : String,
    val interior : String,
    val size : String,
    val userEmail : String,
)
