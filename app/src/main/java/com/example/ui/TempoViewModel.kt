package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.BookingEntity
import com.example.data.model.FleetData
import com.example.data.model.TempoVehicle
import com.example.data.repository.BookingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

enum class TempoTab {
    BOOK,
    TRACKING,
    BOOKINGS,
    RATE_CARD
}

data class BookingFormState(
    val selectedVehicle: TempoVehicle = FleetData.vehicles[1], // Default: Tata Ace
    val pickupAddress: String = "MG Road Wholesale Mandi, Gate 2",
    val dropAddress: String = "Greenfield Residences, Phase 4, Tower C",
    val distanceKm: Double = 8.5,
    val selectedGoodsCategory: String = "Furniture & Appliances",
    val helperCount: Int = 1, // 0, 1, 2
    val withInsurance: Boolean = true,
    val promoCode: String = "",
    val appliedCoupon: String? = null,
    val couponDiscount: Double = 0.0,
    val couponMessage: String? = null,
    val isScheduled: Boolean = false,
    val scheduledTime: String = "Instant (Driver arriving in 10 mins)"
)

class TempoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BookingRepository

    init {
        val db = AppDatabase.getInstance(application)
        repository = BookingRepository(db.bookingDao())
        seedSampleBookingsIfEmpty()
    }

    val allBookings: StateFlow<List<BookingEntity>> = repository.allBookings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeBooking: StateFlow<BookingEntity?> = repository.activeBooking
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _currentTab = MutableStateFlow(TempoTab.BOOK)
    val currentTab: StateFlow<TempoTab> = _currentTab.asStateFlow()

    private val _formState = MutableStateFlow(BookingFormState())
    val formState: StateFlow<BookingFormState> = _formState.asStateFlow()

    private val _selectedBookingForInvoice = MutableStateFlow<BookingEntity?>(null)
    val selectedBookingForInvoice: StateFlow<BookingEntity?> = _selectedBookingForInvoice.asStateFlow()

    private val _showBookingSuccessDialog = MutableStateFlow<BookingEntity?>(null)
    val showBookingSuccessDialog: StateFlow<BookingEntity?> = _showBookingSuccessDialog.asStateFlow()

    fun setTab(tab: TempoTab) {
        _currentTab.value = tab
    }

    fun selectVehicle(vehicle: TempoVehicle) {
        _formState.update { it.copy(selectedVehicle = vehicle) }
    }

    fun updatePickupAddress(address: String) {
        _formState.update { it.copy(pickupAddress = address) }
    }

    fun updateDropAddress(address: String) {
        _formState.update { it.copy(dropAddress = address) }
    }

    fun updateDistance(km: Double) {
        _formState.update { it.copy(distanceKm = km.coerceIn(1.0, 100.0)) }
    }

    fun selectGoodsCategory(category: String) {
        _formState.update { it.copy(selectedGoodsCategory = category) }
    }

    fun setHelperCount(count: Int) {
        _formState.update { it.copy(helperCount = count) }
    }

    fun toggleInsurance(enabled: Boolean) {
        _formState.update { it.copy(withInsurance = enabled) }
    }

    fun updatePromoCode(code: String) {
        _formState.update { it.copy(promoCode = code) }
    }

    fun applyCoupon() {
        val code = _formState.value.promoCode.trim().uppercase()
        if (code == "TEMPU50") {
            _formState.update {
                it.copy(
                    appliedCoupon = code,
                    couponDiscount = 50.0,
                    couponMessage = "₹50 flat discount applied!"
                )
            }
        } else if (code == "FIRSTTRIP") {
            val discount = 100.0
            _formState.update {
                it.copy(
                    appliedCoupon = code,
                    couponDiscount = discount,
                    couponMessage = "Special ₹100 discount applied!"
                )
            }
        } else {
            _formState.update {
                it.copy(
                    appliedCoupon = null,
                    couponDiscount = 0.0,
                    couponMessage = "Invalid promo code. Try TEMPU50 or FIRSTTRIP"
                )
            }
        }
    }

    fun removeCoupon() {
        _formState.update {
            it.copy(
                appliedCoupon = null,
                couponDiscount = 0.0,
                promoCode = "",
                couponMessage = null
            )
        }
    }

    fun setScheduled(scheduled: Boolean, timeText: String) {
        _formState.update {
            it.copy(
                isScheduled = scheduled,
                scheduledTime = timeText
            )
        }
    }

    fun calculateFares(): FareCalculation {
        val form = _formState.value
        val vehicle = form.selectedVehicle
        val baseFare = vehicle.baseFare
        val extraKm = (form.distanceKm - 2.0).coerceAtLeast(0.0)
        val distanceFare = extraKm * vehicle.ratePerKm
        val helperFee = when (form.helperCount) {
            1 -> 150.0
            2 -> 280.0
            else -> 0.0
        }
        val insuranceFee = if (form.withInsurance) 19.0 else 0.0
        val subtotal = baseFare + distanceFare + helperFee + insuranceFee
        val discount = form.couponDiscount.coerceAtMost(subtotal - 50.0)
        val taxes = (subtotal - discount) * 0.05
        val total = ((subtotal - discount) + taxes).coerceAtLeast(50.0)

        return FareCalculation(
            baseFare = baseFare,
            distanceFare = distanceFare,
            helperFee = helperFee,
            insuranceFee = insuranceFee,
            discount = discount,
            taxAmount = taxes,
            totalFare = total
        )
    }

    fun bookTempo() {
        viewModelScope.launch {
            val form = _formState.value
            val fares = calculateFares()
            val driverNames = listOf("Manoj Kumar", "Surendra Yadav", "Vikram Singh", "Rafiq Khan", "Rakesh Sharma")
            val assignedDriver = driverNames.random()
            val phoneDigits = Random.nextInt(10000000, 99999999)
            val driverPhone = "+91 98$phoneDigits"
            val randomNum = Random.nextInt(1000, 9999)
            val plate = "DL 1L BA $randomNum"
            val otpCode = Random.nextInt(1000, 9999).toString()
            val ref = "TMP-${Random.nextInt(10000, 99999)}"

            val newBooking = BookingEntity(
                bookingReference = ref,
                vehicleId = form.selectedVehicle.id,
                vehicleName = form.selectedVehicle.name,
                vehicleVernacular = form.selectedVehicle.vernacularName,
                pickupAddress = form.pickupAddress,
                dropAddress = form.dropAddress,
                distanceKm = form.distanceKm,
                goodsCategory = form.selectedGoodsCategory,
                helperCount = form.helperCount,
                baseFare = fares.baseFare,
                distanceFare = fares.distanceFare,
                helperFee = fares.helperFee,
                insuranceFee = fares.insuranceFee,
                discountAmount = fares.discount,
                totalFare = fares.totalFare,
                status = "CONFIRMED",
                driverName = assignedDriver,
                driverPhone = driverPhone,
                vehiclePlate = plate,
                driverRating = 4.85,
                otp = otpCode,
                scheduledTime = if (form.isScheduled) form.scheduledTime else "Instant (Arriving in ${form.selectedVehicle.estimatedArrivalMins}m)",
                createdAt = System.currentTimeMillis()
            )

            val id = repository.createBooking(newBooking)
            val created = newBooking.copy(id = id)
            _showBookingSuccessDialog.value = created
            _currentTab.value = TempoTab.TRACKING
        }
    }

    fun dismissBookingSuccess() {
        _showBookingSuccessDialog.value = null
    }

    fun advanceTripStage(booking: BookingEntity) {
        viewModelScope.launch {
            val nextStatus = when (booking.status) {
                "CONFIRMED" -> "REACHED_PICKUP"
                "REACHED_PICKUP" -> "LOADING"
                "LOADING" -> "IN_TRANSIT"
                "IN_TRANSIT" -> "DELIVERED"
                else -> booking.status
            }
            repository.updateStatus(booking.id, nextStatus)
        }
    }

    fun cancelActiveTrip(bookingId: Long) {
        viewModelScope.launch {
            repository.cancelBooking(bookingId)
        }
    }

    fun viewInvoice(booking: BookingEntity) {
        _selectedBookingForInvoice.value = booking
    }

    fun dismissInvoice() {
        _selectedBookingForInvoice.value = null
    }

    fun rebookTrip(booking: BookingEntity) {
        val matchVehicle = FleetData.vehicles.find { it.id == booking.vehicleId } ?: FleetData.vehicles[1]
        _formState.update {
            it.copy(
                selectedVehicle = matchVehicle,
                pickupAddress = booking.pickupAddress,
                dropAddress = booking.dropAddress,
                distanceKm = booking.distanceKm,
                selectedGoodsCategory = booking.goodsCategory,
                helperCount = booking.helperCount
            )
        }
        _currentTab.value = TempoTab.BOOK
    }

    private fun seedSampleBookingsIfEmpty() {
        viewModelScope.launch {
            // Seed a completed past trip if database is brand new so user immediately sees rich history
            val existing = repository.getBookingById(1)
            if (existing == null) {
                val pastBooking = BookingEntity(
                    id = 1,
                    bookingReference = "TMP-38102",
                    vehicleId = "tata_ace",
                    vehicleName = "Tata Ace",
                    vehicleVernacular = "Chhota Haathi",
                    pickupAddress = "Sector 18 Electronics Market",
                    dropAddress = "DLF Cyber City, Tower 9B",
                    distanceKm = 12.0,
                    goodsCategory = "Carton Boxes & Packages",
                    helperCount = 1,
                    baseFare = 290.0,
                    distanceFare = 220.0,
                    helperFee = 150.0,
                    insuranceFee = 19.0,
                    discountAmount = 50.0,
                    totalFare = 660.45,
                    status = "DELIVERED",
                    driverName = "Manoj Yadav",
                    driverPhone = "+91 98123 45678",
                    vehiclePlate = "DL 1L AE 2049",
                    driverRating = 4.9,
                    otp = "7392",
                    scheduledTime = "Delivered Yesterday",
                    createdAt = System.currentTimeMillis() - 86400000L
                )
                repository.createBooking(pastBooking)
            }
        }
    }
}

data class FareCalculation(
    val baseFare: Double,
    val distanceFare: Double,
    val helperFee: Double,
    val insuranceFee: Double,
    val discount: Double,
    val taxAmount: Double,
    val totalFare: Double
)
