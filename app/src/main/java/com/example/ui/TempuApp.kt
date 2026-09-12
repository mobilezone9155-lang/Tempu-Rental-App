package com.example.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.MainActivity
import com.example.R
import com.example.ui.components.BookingSuccessDialog
import com.example.ui.components.InvoiceDialog
import com.example.ui.screens.BookingScreen
import com.example.ui.screens.BookingsListScreen
import com.example.ui.screens.RateCardScreen
import com.example.ui.screens.TrackingScreen
import com.example.ui.theme.MyApplicationTheme

@Composable
fun TempuApp(viewModel: TempoViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val activeBooking by viewModel.activeBooking.collectAsStateWithLifecycle()
    val invoiceBooking by viewModel.selectedBookingForInvoice.collectAsStateWithLifecycle()
    val successBooking by viewModel.showBookingSuccessDialog.collectAsStateWithLifecycle()
    var showSupportDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TempuTopBar(
                activeBookingCount = if (activeBooking != null) 1 else 0,
                onSupportClick = { showSupportDialog = true }
            )
        },
        bottomBar = {
            TempuBottomBar(
                currentTab = currentTab,
                hasActiveBooking = activeBooking != null,
                onTabSelected = { viewModel.setTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                TempoTab.BOOK -> BookingScreen(viewModel = viewModel)
                TempoTab.TRACKING -> TrackingScreen(viewModel = viewModel)
                TempoTab.BOOKINGS -> BookingsListScreen(viewModel = viewModel)
                TempoTab.RATE_CARD -> RateCardScreen(viewModel = viewModel)
            }
        }
    }

    // Invoice Modal Dialog
    invoiceBooking?.let { booking ->
        InvoiceDialog(
            booking = booking,
            onDismiss = { viewModel.dismissInvoice() }
        )
    }

    // Booking Success Celebratory Dialog
    successBooking?.let { booking ->
        BookingSuccessDialog(
            booking = booking,
            onTrackNow = {
                viewModel.dismissBookingSuccess()
                viewModel.setTab(TempoTab.TRACKING)
            },
            onDismiss = { viewModel.dismissBookingSuccess() }
        )
    }

    // Support Hotline Dialog
    if (showSupportDialog) {
        SupportHotlineDialog(onDismiss = { showSupportDialog = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TempuTopBar(
    activeBookingCount: Int,
    onSupportClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = "Tempu Logo",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Tempu",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 19.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Rental",
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "Instant Mini-Truck & Goods Logistics",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (activeBookingCount > 0) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFDCFCE7),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF16A34A))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "1 Active",
                                color = Color(0xFF15803D),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onSupportClick,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .testTag("support_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.HeadsetMic,
                        contentDescription = "Logistics Support",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun TempuBottomBar(
    currentTab: TempoTab,
    hasActiveBooking: Boolean,
    onTabSelected: (TempoTab) -> Unit
) {
    NavigationBar(
        modifier = Modifier.testTag("tempu_bottom_nav"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        NavigationBarItem(
            selected = currentTab == TempoTab.BOOK,
            onClick = { onTabSelected(TempoTab.BOOK) },
            icon = {
                Icon(Icons.Default.LocalShipping, contentDescription = "Book Tempo")
            },
            label = { Text("Book", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_book")
        )

        NavigationBarItem(
            selected = currentTab == TempoTab.TRACKING,
            onClick = { onTabSelected(TempoTab.TRACKING) },
            icon = {
                if (hasActiveBooking) {
                    BadgedBox(badge = { Badge { Text("1") } }) {
                        Icon(Icons.Default.NearMe, contentDescription = "Live Tracking")
                    }
                } else {
                    Icon(Icons.Default.NearMe, contentDescription = "Live Tracking")
                }
            },
            label = { Text("Live Track", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_tracking")
        )

        NavigationBarItem(
            selected = currentTab == TempoTab.BOOKINGS,
            onClick = { onTabSelected(TempoTab.BOOKINGS) },
            icon = {
                Icon(Icons.Default.ReceiptLong, contentDescription = "Trips History")
            },
            label = { Text("Trips", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_bookings")
        )

        NavigationBarItem(
            selected = currentTab == TempoTab.RATE_CARD,
            onClick = { onTabSelected(TempoTab.RATE_CARD) },
            icon = {
                Icon(Icons.Default.TableChart, contentDescription = "Tariff Rate Card")
            },
            label = { Text("Rate Card", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_rate_card")
        )
    }
}

@Composable
private fun SupportHotlineDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.HeadsetMic,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "24x7 Logistics Support",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Need help with your tempo booking, goods tracking, driver coordination, or billing queries? Our dispatch desk is always ready.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:18002008367")
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Call Toll-Free: 1800-200-TEMPU")
                }

                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Close")
                }
            }
        }
    }
}
