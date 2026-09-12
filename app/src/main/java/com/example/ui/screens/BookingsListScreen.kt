package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.BookingEntity
import com.example.ui.TempoTab
import com.example.ui.TempoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BookingsListScreen(
    viewModel: TempoViewModel,
    modifier: Modifier = Modifier
) {
    val bookings by viewModel.allBookings.collectAsStateWithLifecycle()
    var filterStatus by remember { mutableStateOf("ALL") }

    val filteredBookings = when (filterStatus) {
        "ACTIVE" -> bookings.filter { it.status !in listOf("DELIVERED", "CANCELLED") }
        "COMPLETED" -> bookings.filter { it.status == "DELIVERED" }
        "CANCELLED" -> bookings.filter { it.status == "CANCELLED" }
        else -> bookings
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("bookings_list_screen")
    ) {
        // Filter Chips Row
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filters = listOf(
                Pair("ALL", "All Trips (${bookings.size})"),
                Pair("ACTIVE", "Active (${bookings.count { it.status !in listOf("DELIVERED", "CANCELLED") }})"),
                Pair("COMPLETED", "Delivered (${bookings.count { it.status == "DELIVERED" }})"),
                Pair("CANCELLED", "Cancelled (${bookings.count { it.status == "CANCELLED" }})")
            )
            items(filters) { (key, label) ->
                FilterChip(
                    selected = filterStatus == key,
                    onClick = { filterStatus = key },
                    label = { Text(label, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.testTag("filter_chip_$key")
                )
            }
        }

        if (filteredBookings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No Bookings Found",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Your booking records and receipts will appear here.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.setTab(TempoTab.BOOK) },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Book a Tempo")
                    }
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp, top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredBookings, key = { it.id }) { booking ->
                    BookingHistoryItem(
                        booking = booking,
                        onViewInvoice = { viewModel.viewInvoice(booking) },
                        onRebook = { viewModel.rebookTrip(booking) },
                        onTrackTrip = { viewModel.setTab(TempoTab.TRACKING) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BookingHistoryItem(
    booking: BookingEntity,
    onViewInvoice: () -> Unit,
    onRebook: () -> Unit,
    onTrackTrip: () -> Unit
) {
    val isActive = booking.status !in listOf("DELIVERED", "CANCELLED")

    val (statusLabel, statusColor, statusBg) = when (booking.status) {
        "CONFIRMED" -> Triple("DRIVER ASSIGNED", Color(0xFF1D4ED8), Color(0xFFDBEAFE))
        "REACHED_PICKUP" -> Triple("AT PICKUP", Color(0xFFB45309), Color(0xFFFEF3C7))
        "LOADING" -> Triple("LOADING GOODS", Color(0xFFC2410C), Color(0xFFFFEDD5))
        "IN_TRANSIT" -> Triple("IN TRANSIT", Color(0xFF0369A1), Color(0xFFE0F2FE))
        "DELIVERED" -> Triple("DELIVERED", Color(0xFF15803D), Color(0xFFDCFCE7))
        "CANCELLED" -> Triple("CANCELLED", Color(0xFFB91C1C), Color(0xFFFEE2E2))
        else -> Triple(booking.status, MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
    }

    val dateFormatted = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(booking.createdAt))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("booking_item_${booking.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = booking.vehicleName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "${booking.bookingReference} • $dateFormatted",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusBg
                ) {
                    Text(
                        text = statusLabel,
                        color = statusColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(10.dp))

            // Route addresses
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF16A34A))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = booking.pickupAddress,
                        fontSize = 12.sp,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = booking.dropAddress,
                        fontSize = 12.sp,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Price & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Fare",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "₹${String.format(Locale.getDefault(), "%.1f", booking.totalFare)}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (isActive) {
                        Button(
                            onClick = onTrackTrip,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("track_trip_btn_${booking.id}")
                        ) {
                            Icon(Icons.Default.NearMe, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Track Live", fontSize = 11.sp)
                        }
                    }

                    OutlinedButton(
                        onClick = onViewInvoice,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("history_invoice_btn_${booking.id}")
                    ) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Invoice", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = onRebook,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("rebook_btn_${booking.id}")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Rebook", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
