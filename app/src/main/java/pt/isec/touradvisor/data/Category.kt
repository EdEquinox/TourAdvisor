package pt.isec.touradvisor.data

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import coil.compose.rememberImagePainter

data class Category (
    val nome: String? = null,
    val descricao: String? = null,
    val imagem: String? = null
) {
    override fun toString(): String {
        return nome ?: ""
    }

    @Composable
    fun ToImage() : Painter {
        val painter = rememberImagePainter(data = this.imagem)
        return painter
    }


}
