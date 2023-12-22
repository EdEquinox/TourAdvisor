package pt.isec.touradvisor.data

import com.google.firebase.firestore.GeoPoint

data class POI(
    val name: String? = null,
    val description: String? = null,
    val geoPoint: GeoPoint? = null,
    val category: Category?,
    val image: String? = null
) {
    override fun toString(): String {
        return name ?: ""
    }
}