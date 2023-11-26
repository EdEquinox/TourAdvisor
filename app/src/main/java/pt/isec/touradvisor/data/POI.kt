package pt.isec.touradvisor.data

data class POI(
    val id: Int,
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val category: String,
    val rating: Float,
    val image: String,
    val address: String,
    val website: String,
    val phone: String,
    val email: String,
    val openingHours: String,
    val price: String,
    val duration: String,
    val distance: String,
    val isFavorite: Boolean
) 