package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.RentalBookingDao
import com.example.data.local.RentalBookingEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RentalBookingDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var rentalBookingDao: RentalBookingDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        rentalBookingDao = database.rentalBookingDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndRetrieveRentalBooking() = runBlocking {
        val booking = RentalBookingEntity(
            pickupLocation = "MG Road Wholesale Mandi, Gate 2",
            dropOffLocation = "Indiranagar 100ft Road, Metro Pillar 42",
            vehicleType = "Tata Ace",
            scheduledTime = "Instant (Within 15 mins)",
            status = "CONFIRMED",
            bookingReference = "TMP-992104",
            totalFare = 450.0,
            distanceKm = 8.5
        )

        val id = rentalBookingDao.insertBooking(booking)
        assertTrue(id > 0)

        val retrieved = rentalBookingDao.getBookingById(id)
        assertNotNull(retrieved)
        assertEquals("MG Road Wholesale Mandi, Gate 2", retrieved?.pickupLocation)
        assertEquals("Indiranagar 100ft Road, Metro Pillar 42", retrieved?.dropOffLocation)
        assertEquals("Tata Ace", retrieved?.vehicleType)
        assertEquals("Instant (Within 15 mins)", retrieved?.scheduledTime)
        assertEquals("CONFIRMED", retrieved?.status)
        assertEquals(450.0, retrieved?.totalFare ?: 0.0, 0.01)
    }

    @Test
    fun getAllBookingsFlow() = runBlocking {
        val booking1 = RentalBookingEntity(
            pickupLocation = "Point A",
            dropOffLocation = "Point B",
            vehicleType = "3-Wheeler Tempo",
            scheduledTime = "Today, 3:00 PM"
        )
        val booking2 = RentalBookingEntity(
            pickupLocation = "Point C",
            dropOffLocation = "Point D",
            vehicleType = "Pickup 8ft",
            scheduledTime = "Tomorrow, 9:00 AM"
        )

        rentalBookingDao.insert(booking1)
        rentalBookingDao.insert(booking2)

        val allBookings = rentalBookingDao.getAllBookings().first()
        assertEquals(2, allBookings.size)
    }

    @Test
    fun updateAndStatusChange() = runBlocking {
        val booking = RentalBookingEntity(
            pickupLocation = "Warehouse 4",
            dropOffLocation = "Retail Store 12",
            vehicleType = "EV Mini Tempo",
            scheduledTime = "Instant",
            status = "CONFIRMED"
        )
        val id = rentalBookingDao.insertBooking(booking)

        rentalBookingDao.updateBookingStatus(id, "IN_TRANSIT")
        val updated = rentalBookingDao.getBookingById(id)
        assertEquals("IN_TRANSIT", updated?.status)
    }

    @Test
    fun deleteRentalBooking() = runBlocking {
        val booking = RentalBookingEntity(
            pickupLocation = "Location X",
            dropOffLocation = "Location Y",
            vehicleType = "Tata 407",
            scheduledTime = "Next Monday, 8:00 AM"
        )
        val id = rentalBookingDao.insertBooking(booking)
        assertNotNull(rentalBookingDao.getBookingById(id))

        rentalBookingDao.deleteBookingById(id)
        val deleted = rentalBookingDao.getBookingById(id)
        assertNull(deleted)
    }
}
