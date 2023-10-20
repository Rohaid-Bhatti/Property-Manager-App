package com.hzm.sharedpreference.propertyWork.propertyDB

import androidx.lifecycle.LiveData
import androidx.room.*
import com.hzm.sharedpreference.dataClass.PropertyListItem

@Dao
interface PropertyDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperty(property: PropertyEntity): Long

    @Delete
    suspend fun deleteProperty(property: PropertyEntity)

    @Query("SELECT * FROM property_table")
    fun getAllProperties(): LiveData<List<PropertyEntity>>

    @Query("SELECT * FROM property_table WHERE userEmail = :userEmail")
    fun getPropertiesByUser(userEmail: String): LiveData<List<PropertyEntity>>

    @Query("SELECT * FROM property_table INNER JOIN location_table ON property_table.houseId = location_table.houseId")
    fun getAllPropertiesWithLocations(): List<PropertyListItem>

    // Code Checking
    @Insert
    suspend fun insertLocation(location: LocationEntity)

    @Query("SELECT * FROM location_table WHERE locationId = :locationId")
    suspend fun getLocationById(locationId: Long): LocationEntity?

    @Query("SELECT * FROM location_table")
    fun getAllLocations(): LiveData<List<LocationEntity>>

    @Query("DELETE FROM location_table WHERE houseId = :houseId")
    suspend fun deleteLocationByHouseId(houseId: Long)

    // for updating
    @Query("SELECT * FROM property_table WHERE houseId = :houseId")
    fun getPropertyById(houseId: Long): PropertyEntity?

    @Query("SELECT * FROM location_table WHERE houseId = :houseId")
    fun getLocationByHouseId(houseId: Long): LocationEntity?

    @Update
    suspend fun updateProperty(property: PropertyEntity)

    @Update
    suspend fun updateLocation(location: LocationEntity)

    // for filtering data from database
    @Query(
        "SELECT * FROM property_table INNER JOIN location_table ON property_table.houseId = location_table.houseId " +
                "WHERE location_table.address LIKE '%' || :query || '%' OR " +
                "location_table.city LIKE '%' || :query || '%' OR " +
                "property_table.type LIKE '%' || :query || '%' OR " +
                "property_table.interior LIKE '%' || :query || '%' OR " +
                "property_table.size LIKE '%' || :query || '%'"
    )
    fun getPropertiesWithLocationsByQuery(query: String): List<PropertyListItem>

    @Query("SELECT * FROM property_table INNER JOIN location_table ON property_table.houseId = location_table.houseId " +
            "WHERE property_table.interior LIKE '%' || :filterCriteria || '%'")
    fun getPropertiesWithLocationsByFurnished(filterCriteria: String): List<PropertyListItem>

    @Query("SELECT * FROM property_table INNER JOIN location_table ON property_table.houseId = location_table.houseId " +
            "WHERE property_table.type LIKE '%' || :filterCriteria || '%'")
    fun getPropertiesWithLocationsByType(filterCriteria: String): List<PropertyListItem>
}