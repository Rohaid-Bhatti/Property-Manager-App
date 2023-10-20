package com.hzm.sharedpreference.propertyWork.propertyDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PropertyEntity::class, LocationEntity::class], version = 1)
abstract class PropertyDatabase : RoomDatabase() {

    abstract fun propertyDao(): PropertyDAO

    companion object {
        @Volatile
        private var INSTANCE: PropertyDatabase? = null

        fun getDatabase(context: Context): PropertyDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        PropertyDatabase::class.java,
                        "propertyDB"
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }
}