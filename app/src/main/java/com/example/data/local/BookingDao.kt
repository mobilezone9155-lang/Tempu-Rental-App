package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {
    @Query("SELECT * FROM tempo_bookings ORDER BY createdAt DESC")
    fun getAllBookings(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM tempo_bookings WHERE status NOT IN ('DELIVERED', 'CANCELLED') ORDER BY createdAt DESC LIMIT 1")
    fun getActiveBooking(): Flow<BookingEntity?>

    @Query("SELECT * FROM tempo_bookings WHERE id = :id")
    suspend fun getBookingById(id: Long): BookingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity): Long

    @androidx.room.Update
    suspend fun updateBooking(booking: BookingEntity)

    @androidx.room.Delete
    suspend fun deleteBooking(booking: BookingEntity)

    @Query("UPDATE tempo_bookings SET status = :status WHERE id = :id")
    suspend fun updateBookingStatus(id: Long, status: String)

    @Query("DELETE FROM tempo_bookings WHERE id = :id")
    suspend fun deleteBookingById(id: Long)

    @Query("DELETE FROM tempo_bookings")
    suspend fun clearAll()
}
