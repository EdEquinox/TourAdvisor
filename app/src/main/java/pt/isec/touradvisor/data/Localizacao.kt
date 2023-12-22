package pt.isec.touradvisor.data

import com.google.firebase.firestore.GeoPoint
data class Localizacao (
    val name: String? = null,
    val description: String? = null,
    val image: String? = null,
    val geoPoint: GeoPoint? = null
) {
    override fun toString(): String {
        return name ?: ""
    }

}