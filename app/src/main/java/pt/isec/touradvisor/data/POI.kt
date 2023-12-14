package pt.isec.touradvisor.data

data class POI(
    val id: Int,
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val category: String,
    val image: String
) 