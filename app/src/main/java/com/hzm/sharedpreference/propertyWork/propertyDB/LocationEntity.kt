package com.hzm.sharedpreference.propertyWork.propertyDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location_table")
data class LocationEntity(

    @PrimaryKey(autoGenerate = true)
    val locationId : Long,
    val address : String,
    val city : String,
    val houseId : Long,
)
