package pt.isec.touradvisor.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import coil.compose.rememberImagePainter
import com.google.firebase.firestore.GeoPoint
data class Local (
    val name: String? = null,
    val description: String? = null,
    val image: String? = null,
    val geoPoint: GeoPoint? = null
) {
    override fun toString(): String {
        return name ?: ""
    }

    @Composable
    fun toImage(): Painter {
        return rememberImagePainter(data = image)
    }

}