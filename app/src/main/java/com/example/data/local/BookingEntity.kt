package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tempo_bookings")
data class BookingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookingReference: String,
    val vehicleId: String,
    val vehicleName: String,
    val vehicleVernacular: String,
    val pickupAddress: String,
    val dropAddress: String,
    val distanceKm: Double,
    val goodsCategory: String,
    val helperCount: Int,
    val baseFare: Double,
    val distanceFare: Double,
    val helperFee: Double,
    val insuranceFee: Double,
    val discountAmount: Double,
    val totalFare: Double,
    val status: String, // CONFIRMED, REACHED_PICKUP, LOADING, IN_TRANSIT, DELIVERED, CANCELLED
    val driverName: String,
    val driverPhone: String,
    val vehiclePlate: String,
    val driverRating: Double,
    val otp: String,
    val scheduledTime: String,
    val createdAt: Long = System.currentTimeMillis()
)

val BookingEntity.pickupLocation: String get() = pickupAddress
val BookingEntity.dropOffLocation: String get() = dropAddress
val BookingEntity.vehicleType: String get() = vehicleName

fun BookingEntity.toRentalBookingEntity(): RentalBookingEntity = RentalBookingEntity(
    id = id,
    pickupLocation = pickupAddress,
    dropOffLocation = dropAddress,
    vehicleType = vehicleName,
    scheduledTime = scheduledTime,
    status = status,
    bookingReference = bookingReference,
    driverName = driverName,
    driverPhone = driverPhone,
    vehiclePlate = vehiclePlate,
    goodsCategory = goodsCategory,
    distanceKm = distanceKm,
    helperCount = helperCount,
    totalFare = totalFare,
    otp = otp,
    createdAt = createdAt
)

fun RentalBookingEntity.toBookingEntity(): BookingEntity = BookingEntity(
    id = id,
    bookingReference = if (bookingReference.isNotEmpty()) bookingReference else "TMP-${System.currentTimeMillis().toString().takeLast(6)}",
    vehicleId = vehicleType.lowercase().replace(" ", "_"),
    vehicleName = vehicleType,
    vehicleVernacular = vehicleType,
    pickupAddress = pickupLocation,
    dropAddress = dropOffLocation,
    distanceKm = distanceKm,
    goodsCategory = goodsCategory,
    helperCount = helperCount,
    baseFare = totalFare * 0.4,
    distanceFare = totalFare * 0.6,
    helperFee = 0.0,
    insuranceFee = 0.0,
    discountAmount = 0.0,
    totalFare = totalFare,
    status = status,
    driverName = driverName,
    driverPhone = driverPhone,
    vehiclePlate = vehiclePlate,
    driverRating = 4.8,
    otp = otp,
    scheduledTime = scheduledTime,
    createdAt = createdAt
)
