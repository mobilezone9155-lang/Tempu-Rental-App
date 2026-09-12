package com.example.data.repository

import com.example.data.local.BookingDao
import com.example.data.local.BookingEntity
import kotlinx.coroutines.flow.Flow

class BookingRepository(private val dao: BookingDao) {
    val allBookings: Flow<List<BookingEntity>> = dao.getAllBookings()
    val activeBooking: Flow<BookingEntity?> = dao.getActiveBooking()

    suspend fun getBookingById(id: Long): BookingEntity? = dao.getBookingById(id)

    suspend fun createBooking(booking: BookingEntity): Long = dao.insertBooking(booking)

    suspend fun updateStatus(id: Long, status: String) = dao.updateBookingStatus(id, status)

    suspend fun cancelBooking(id: Long) = dao.updateBookingStatus(id, "CANCELLED")

    suspend fun deleteBooking(id: Long) = dao.deleteBookingById(id)
}
