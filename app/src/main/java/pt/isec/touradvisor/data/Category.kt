package pt.isec.touradvisor.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import coil.compose.rememberImagePainter

data class Category(
    val nome: String? = null,
    val descricao: String? = null,
    val imagem: String? = null,
    val user : String? = null
) {
    override fun toString(): String {
        return "Category:( name = $nome , description = $descricao, image = $imagem )"
    }

    @Composable
    fun toImage(): Painter {
        return rememberImagePainter(data = imagem)
    }


}
