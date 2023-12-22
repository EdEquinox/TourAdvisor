package pt.isec.touradvisor.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import coil.compose.rememberImagePainter

data class Category (
    val nome: String? = null,
    val descricao: String? = null,
    val imagem: String? = null
) {
    override fun toString(): String {
        return "Categoria:( nome = $nome , descricao = $descricao, imagem = $imagem )"
    }

    @Composable
    fun ToImage() : Painter {
        val painter = rememberImagePainter(data = this.imagem)
        return painter
    }


}
