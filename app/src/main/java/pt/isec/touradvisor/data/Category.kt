package pt.isec.touradvisor.data

data class Category (
    val name: String,
    val description: String,
    val image: String)
{
    override fun toString(): String {
        return name
    }
}