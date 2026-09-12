package com.example.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room database entity representing a vehicle rental booking.
 *
 * Contains all core routing and schedule metadata:
 * - [pickupLocation]: Starting pickup point or address
 * - [dropOffLocation]: Final drop-off point or address
 * - [vehicleType]: Category/model of the rental vehicle (e.g., "Tata Ace", "3-Wheeler", "Pickup 8ft")
 * - [scheduledTime]: Scheduled departure/pickup time (e.g., "Instant", "Today, 4:00 PM")
 */
@Entity(tableName = "rental_bookings")
data class RentalBookingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "pickup_location")
    val pickupLocation: String,

    @ColumnInfo(name = "drop_off_location")
    val dropOffLocation: String,

    @ColumnInfo(name = "vehicle_type")
    val vehicleType: String,

    @ColumnInfo(name = "scheduled_time")
    val scheduledTime: String,

    @ColumnInfo(name = "status")
    val status: String = "CONFIRMED", // CONFIRMED, REACHED_PICKUP, LOADING, IN_TRANSIT, DELIVERED, CANCELLED

    @ColumnInfo(name = "booking_reference")
    val bookingReference: String = "",

    @ColumnInfo(name = "customer_name")
    val customerName: String = "",

    @ColumnInfo(name = "customer_phone")
    val customerPhone: String = "",

    @ColumnInfo(name = "driver_name")
    val driverName: String = "",

    @ColumnInfo(name = "driver_phone")
    val driverPhone: String = "",

    @ColumnInfo(name = "vehicle_plate")
    val vehiclePlate: String = "",

    @ColumnInfo(name = "goods_category")
    val goodsCategory: String = "General Goods",

    @ColumnInfo(name = "distance_km")
    val distanceKm: Double = 0.0,

    @ColumnInfo(name = "helper_count")
    val helperCount: Int = 0,

    @ColumnInfo(name = "total_fare")
    val totalFare: Double = 0.0,

    @ColumnInfo(name = "otp")
    val otp: String = "",

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)

typealias RentalBooking = RentalBookingEntity
