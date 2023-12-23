package pt.isec.touradvisor.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import coil.compose.rememberImagePainter
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

    @Composable
    fun toImage() : Painter {
        val painter = rememberImagePainter(data = this.image)
        return painter
    }
}