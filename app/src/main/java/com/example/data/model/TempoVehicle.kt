package com.example.data.model

data class TempoVehicle(
    val id: String,
    val name: String,
    val vernacularName: String,
    val capacityKg: Int,
    val dimensions: String,
    val baseFare: Double, // Includes first 2 km
    val ratePerKm: Double,
    val tag: String,
    val isPopular: Boolean = false,
    val estimatedArrivalMins: Int = 12,
    val description: String,
    val idealFor: List<String>
)

object FleetData {
    val vehicles = listOf(
        TempoVehicle(
            id = "3w_tempo",
            name = "3-Wheeler Tempo",
            vernacularName = "Auto Loader / Ape",
            capacityKg = 500,
            dimensions = "5.5ft x 4.5ft x 4ft",
            baseFare = 180.0,
            ratePerKm = 16.0,
            tag = "Most Economical",
            isPopular = false,
            estimatedArrivalMins = 8,
            description = "Compact 3-wheeler goods carrier. Perfect for crowded market lanes and fast intra-city drops.",
            idealFor = listOf("Small Furniture", "Cartons & Crates", "Home Appliances", "Retail Deliveries")
        ),
        TempoVehicle(
            id = "tata_ace",
            name = "Tata Ace",
            vernacularName = "Chhota Haathi",
            capacityKg = 850,
            dimensions = "7ft x 4.8ft x 4.8ft",
            baseFare = 290.0,
            ratePerKm = 22.0,
            tag = "Most Popular",
            isPopular = true,
            estimatedArrivalMins = 12,
            description = "India's favorite 4-wheel mini truck. Ideal for 1 BHK house shifting, wholesale stocks, and mid-sized cargo.",
            idealFor = listOf("1 BHK Shifting", "Electronics & TVs", "Wholesale Groceries", "Commercial Freight")
        ),
        TempoVehicle(
            id = "pickup_8ft",
            name = "Pickup 8ft",
            vernacularName = "Bolero Maxi Truck",
            capacityKg = 1300,
            dimensions = "8.2ft x 5.2ft x 5ft",
            baseFare = 390.0,
            ratePerKm = 28.0,
            tag = "Heavy Payload",
            isPopular = false,
            estimatedArrivalMins = 15,
            description = "Heavy-duty pickup truck with reinforced suspension for machinery, pipes, furniture sets, and 2 BHK relocation.",
            idealFor = listOf("2 BHK Shifting", "Plywood & Hardware", "Industrial Machines", "Event Stages")
        ),
        TempoVehicle(
            id = "ev_loader",
            name = "EV Mini Tempo",
            vernacularName = "Electric Cargo",
            capacityKg = 600,
            dimensions = "6ft x 4.2ft x 4.2ft",
            baseFare = 160.0,
            ratePerKm = 14.0,
            tag = "Eco Green 🌱",
            isPopular = false,
            estimatedArrivalMins = 10,
            description = "Zero-emission electric cargo tempo for modern clean green urban deliveries at subsidized green rates.",
            idealFor = listOf("E-Commerce Delivery", "Produce & Farm Veggies", "Fragile Glassware", "City Centre Drops")
        ),
        TempoVehicle(
            id = "tata_407",
            name = "Tata 407 (14ft)",
            vernacularName = "Large Canter",
            capacityKg = 2500,
            dimensions = "14ft x 6.5ft x 6.5ft",
            baseFare = 650.0,
            ratePerKm = 38.0,
            tag = "Large Cargo",
            isPopular = false,
            estimatedArrivalMins = 20,
            description = "Spacious closed/open-body truck designed for full home moving (3 BHK+), factory dispatches, and warehouse distribution.",
            idealFor = listOf("3+ BHK Villa Moving", "Industrial Machinery", "Warehouse Shifting", "Pallet Cargo")
        )
    )
}
