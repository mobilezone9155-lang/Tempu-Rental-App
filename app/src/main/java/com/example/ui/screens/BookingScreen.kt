package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.outlined.ElectricCar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.FleetData
import com.example.data.model.TempoVehicle
import com.example.ui.BookingFormState
import com.example.ui.FareCalculation
import com.example.ui.TempoViewModel
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BookingScreen(
    viewModel: TempoViewModel,
    modifier: Modifier = Modifier
) {
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val fares = viewModel.calculateFares()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("booking_screen_list"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Hero Banner Header
        item {
            HeroHeaderCard()
        }

        // Fleet Vehicle Selector
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Choose Your Tempo",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "5 Models Available",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
            FleetCarousel(
                selectedVehicle = formState.selectedVehicle,
                onSelectVehicle = { viewModel.selectVehicle(it) }
            )
        }

        // Pickup & Drop Location Section
        item {
            Spacer(modifier = Modifier.height(14.dp))
            RouteLocationCard(
                pickupAddress = formState.pickupAddress,
                dropAddress = formState.dropAddress,
                onPickupChange = { viewModel.updatePickupAddress(it) },
                onDropChange = { viewModel.updateDropAddress(it) }
            )
        }

        // Trip Distance Slider
        item {
            Spacer(modifier = Modifier.height(12.dp))
            DistanceEstimatorCard(
                distanceKm = formState.distanceKm,
                onDistanceChange = { viewModel.updateDistance(it) }
            )
        }

        // Cargo & Goods Category
        item {
            Spacer(modifier = Modifier.height(12.dp))
            GoodsCategoryCard(
                selectedCategory = formState.selectedGoodsCategory,
                onSelectCategory = { viewModel.selectGoodsCategory(it) }
            )
        }

        // Labor / Helper Selection
        item {
            Spacer(modifier = Modifier.height(12.dp))
            HelperSelectionCard(
                selectedCount = formState.helperCount,
                onSelectCount = { viewModel.setHelperCount(it) }
            )
        }

        // Transit Insurance & Booking Schedule
        item {
            Spacer(modifier = Modifier.height(12.dp))
            AddonsAndScheduleCard(
                formState = formState,
                onToggleInsurance = { viewModel.toggleInsurance(it) },
                onToggleSchedule = { isSched, text -> viewModel.setScheduled(isSched, text) }
            )
        }

        // Promo Code Card
        item {
            Spacer(modifier = Modifier.height(12.dp))
            PromoCodeCard(
                formState = formState,
                onPromoCodeChange = { viewModel.updatePromoCode(it) },
                onApply = { viewModel.applyCoupon() },
                onRemove = { viewModel.removeCoupon() }
            )
        }

        // Fare Summary & Book Button
        item {
            Spacer(modifier = Modifier.height(14.dp))
            FareSummaryCard(
                vehicle = formState.selectedVehicle,
                fares = fares,
                formState = formState,
                onBookNow = { viewModel.bookTempo() }
            )
        }
    }
}

@Composable
private fun HeroHeaderCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hero_tempo_banner),
                    contentDescription = "Tempo Rental Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Gradient scrim overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xDD0F172A))
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp)
                ) {
                    Text(
                        text = "Instant Tempu Rental",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "Verified Drivers • Transparent Rates • Rapid Arrival",
                        color = Color(0xFFFFD180),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun FleetCarousel(
    selectedVehicle: TempoVehicle,
    onSelectVehicle: (TempoVehicle) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(FleetData.vehicles, key = { it.id }) { vehicle ->
            val isSelected = vehicle.id == selectedVehicle.id
            Card(
                modifier = Modifier
                    .width(190.dp)
                    .clickable { onSelectVehicle(vehicle) }
                    .testTag("vehicle_card_${vehicle.id}")
                    .border(
                        width = if (isSelected) 2.5.dp else 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                    else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Badge row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (vehicle.isPopular) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
                        ) {
                            Text(
                                text = vehicle.tag,
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = vehicle.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = vehicle.vernacularName,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Specs pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Up to ${vehicle.capacityKg} kg",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Bed: ${vehicle.dimensions}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "₹${vehicle.baseFare.toInt()}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 17.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "first 2 km",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                        Text(
                            text = "~${vehicle.estimatedArrivalMins}m away",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF16A34A)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RouteLocationCard(
    pickupAddress: String,
    dropAddress: String,
    onPickupChange: (String) -> Unit,
    onDropChange: (String) -> Unit
) {
    val pickupPresets = listOf(
        "MG Road Wholesale Mandi, Gate 2",
        "Industrial Area Phase 2, Shed 40",
        "City Railway Freight Terminal",
        "Electronics Central Market, Shop 14",
        "Home: Flat 302, Palm Heights"
    )
    val dropPresets = listOf(
        "Greenfield Residences, Phase 4",
        "South Express Logistics Park",
        "Commercial Complex, Ring Road",
        "City Supermarket Warehouse",
        "Apex Business Park, Tower C"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Pickup & Drop Locations",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Pickup Row
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF16A34A).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF16A34A))
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = pickupAddress,
                        onValueChange = onPickupChange,
                        label = { Text("Pickup Address / Mandi / Shop") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pickup_address_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = false,
                        maxLines = 2
                    )
                }
            }

            // Quick Pickup Presets
            LazyRow(
                modifier = Modifier.padding(top = 6.dp, start = 34.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(pickupPresets) { preset ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.clickable { onPickupChange(preset) }
                    ) {
                        Text(
                            text = preset.split(",").first(),
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Drop Row
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = dropAddress,
                        onValueChange = onDropChange,
                        label = { Text("Delivery Destination") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("drop_address_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = false,
                        maxLines = 2
                    )
                }
            }

            // Quick Drop Presets
            LazyRow(
                modifier = Modifier.padding(top = 6.dp, start = 34.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(dropPresets) { preset ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.clickable { onDropChange(preset) }
                    ) {
                        Text(
                            text = preset.split(",").first(),
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DistanceEstimatorCard(
    distanceKm: Double,
    onDistanceChange: (Double) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Trip Distance",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "${String.format(Locale.getDefault(), "%.1f", distanceKm)} km (~${(distanceKm * 3.5).toInt() + 10} mins)",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Slider(
                value = distanceKm.toFloat(),
                onValueChange = { onDistanceChange(it.toDouble()) },
                valueRange = 1f..60f,
                modifier = Modifier.testTag("distance_slider")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(3.0, 8.0, 15.0, 25.0, 45.0).forEach { km ->
                    val isSelected = Math.abs(distanceKm - km) < 1.0
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.clickable { onDistanceChange(km) }
                    ) {
                        Text(
                            text = "${km.toInt()} km",
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GoodsCategoryCard(
    selectedCategory: String,
    onSelectCategory: (String) -> Unit
) {
    val categories = listOf(
        "🪑 Furniture & Appliances",
        "📦 Carton Boxes & Packages",
        "🧱 Hardware & Building Material",
        "🥬 Mandi Produce & Groceries",
        "⚙️ Machinery & Tools",
        "⚡ Electronics & Displays"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Type of Goods / Cargo",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectCategory(cat) },
                        label = { Text(cat, fontSize = 12.sp) },
                        leadingIcon = if (isSelected) {
                            {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.testTag("goods_chip_${cat.take(5)}")
                    )
                }
            }
        }
    }
}

@Composable
private fun HelperSelectionCard(
    selectedCount: Int,
    onSelectCount: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Loading / Unloading Labor",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Floor to floor service",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val helperOptions = listOf(
                    Triple(0, "Driver Only", "Self load/unload (Free)"),
                    Triple(1, "1 Helper", "+₹150 for assistance"),
                    Triple(2, "2 Helpers", "+₹280 for heavy shifting")
                )

                helperOptions.forEach { (count, title, sub) ->
                    val isSelected = selectedCount == count
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSelectCount(count) }
                            .testTag("helper_opt_$count")
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = sub,
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                lineHeight = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddonsAndScheduleCard(
    formState: BookingFormState,
    onToggleInsurance: (Boolean) -> Unit,
    onToggleSchedule: (Boolean, String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Insurance Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = Color(0xFF16A34A),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Cargo Transit Protection",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Covers damage & theft up to ₹50,000 for just ₹19",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                Switch(
                    checked = formState.withInsurance,
                    onCheckedChange = onToggleInsurance,
                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF16A34A)),
                    modifier = Modifier.testTag("insurance_switch")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(10.dp))

            // Schedule Options
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Booking Timing",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = if (formState.isScheduled) formState.scheduledTime else "Instant (Driver dispatched in ~10 mins)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Row {
                    OutlinedButton(
                        onClick = { onToggleSchedule(false, "Instant") },
                        shape = RoundedCornerShape(8.dp),
                        colors = if (!formState.isScheduled) ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ) else ButtonDefaults.outlinedButtonColors(),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("Now", fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    OutlinedButton(
                        onClick = { onToggleSchedule(true, "Scheduled for Today 4:00 PM") },
                        shape = RoundedCornerShape(8.dp),
                        colors = if (formState.isScheduled) ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ) else ButtonDefaults.outlinedButtonColors(),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("Later", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun PromoCodeCard(
    formState: BookingFormState,
    onPromoCodeChange: (String) -> Unit,
    onApply: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Discount,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Apply Promo Coupon",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (formState.appliedCoupon != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFDCFCE7), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${formState.appliedCoupon} Applied!",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF14532D),
                            fontSize = 13.sp
                        )
                        Text(
                            text = formState.couponMessage ?: "Savings applied",
                            fontSize = 11.sp,
                            color = Color(0xFF166534)
                        )
                    }
                    OutlinedButton(
                        onClick = onRemove,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("Remove", fontSize = 11.sp, color = Color.Red)
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = formState.promoCode,
                        onValueChange = onPromoCodeChange,
                        placeholder = { Text("Use TEMPU50 or FIRSTTRIP", fontSize = 12.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("promo_code_input"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onApply,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("apply_promo_btn")
                    ) {
                        Text("Apply", fontSize = 12.sp)
                    }
                }
                if (formState.couponMessage != null) {
                    Text(
                        text = formState.couponMessage,
                        fontSize = 11.sp,
                        color = Color.Red,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FareSummaryCard(
    vehicle: TempoVehicle,
    fares: FareCalculation,
    formState: BookingFormState,
    onBookNow: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("fare_summary_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Estimated Total Fare",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "₹${String.format(Locale.getDefault(), "%.1f", fares.totalFare)}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 28.sp,
                        color = Color(0xFFFFB74D)
                    )
                }
                OutlinedButton(
                    onClick = { isExpanded = !isExpanded },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(if (isExpanded) "Hide Details" else "View Details", fontSize = 11.sp)
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(8.dp))

                    FareRow("Base Fare (incl. 2 km)", "₹${vehicle.baseFare.toInt()}")
                    val extraKm = (formState.distanceKm - 2.0).coerceAtLeast(0.0)
                    FareRow(
                        "Distance (${String.format(Locale.getDefault(), "%.1f", extraKm)} km × ₹${vehicle.ratePerKm.toInt()})",
                        "₹${String.format(Locale.getDefault(), "%.1f", fares.distanceFare)}"
                    )
                    if (fares.helperFee > 0) {
                        FareRow("Helper Assistance (${formState.helperCount} helper)", "₹${fares.helperFee.toInt()}")
                    }
                    if (fares.insuranceFee > 0) {
                        FareRow("Transit Protection", "₹${fares.insuranceFee.toInt()}")
                    }
                    if (fares.discount > 0) {
                        FareRow("Coupon Discount", "-₹${String.format(Locale.getDefault(), "%.1f", fares.discount)}", Color(0xFF4ADE80))
                    }
                    FareRow("GST & Logistics Platform Fee (5%)", "₹${String.format(Locale.getDefault(), "%.1f", fares.taxAmount)}")
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onBookNow,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("book_tempo_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.LocalShipping,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Book ${vehicle.name} (${vehicle.vernacularName})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color(0xFF4ADE80),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Pay cash or UPI directly to driver after delivery",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun FareRow(label: String, value: String, color: Color = Color.White.copy(alpha = 0.9f)) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
        Text(text = value, color = color, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
