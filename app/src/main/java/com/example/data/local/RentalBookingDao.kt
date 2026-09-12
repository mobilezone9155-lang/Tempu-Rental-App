package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for managing rental booking records in the Room database.
 */
@Dao
interface RentalBookingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: RentalBookingEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(booking: RentalBookingEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(bookings: List<RentalBookingEntity>)

    @Update
    suspend fun updateBooking(booking: RentalBookingEntity)

    @Update
    suspend fun update(booking: RentalBookingEntity)

    @Query("UPDATE rental_bookings SET status = :status WHERE id = :id")
    suspend fun updateBookingStatus(id: Long, status: String)

    @Delete
    suspend fun deleteBooking(booking: RentalBookingEntity)

    @Delete
    suspend fun delete(booking: RentalBookingEntity)

    @Query("DELETE FROM rental_bookings WHERE id = :id")
    suspend fun deleteBookingById(id: Long)

    @Query("DELETE FROM rental_bookings")
    suspend fun deleteAllBookings()

    @Query("SELECT * FROM rental_bookings WHERE id = :id")
    suspend fun getBookingById(id: Long): RentalBookingEntity?

    @Query("SELECT * FROM rental_bookings WHERE id = :id")
    fun getBookingByIdFlow(id: Long): Flow<RentalBookingEntity?>

    @Query("SELECT * FROM rental_bookings ORDER BY created_at DESC")
    fun getAllBookings(): Flow<List<RentalBookingEntity>>

    @Query("SELECT * FROM rental_bookings ORDER BY created_at DESC")
    suspend fun getAllBookingsList(): List<RentalBookingEntity>

    @Query("SELECT * FROM rental_bookings WHERE vehicle_type = :vehicleType ORDER BY created_at DESC")
    fun getBookingsByVehicleType(vehicleType: String): Flow<List<RentalBookingEntity>>

    @Query("SELECT * FROM rental_bookings WHERE status = :status ORDER BY created_at DESC")
    fun getBookingsByStatus(status: String): Flow<List<RentalBookingEntity>>

    @Query("SELECT * FROM rental_bookings WHERE status NOT IN ('DELIVERED', 'CANCELLED') ORDER BY created_at DESC")
    fun getActiveBookings(): Flow<List<RentalBookingEntity>>

    @Query("SELECT * FROM rental_bookings WHERE pickup_location LIKE '%' || :location || '%' OR drop_off_location LIKE '%' || :location || '%' ORDER BY created_at DESC")
    fun searchByLocation(location: String): Flow<List<RentalBookingEntity>>
}
