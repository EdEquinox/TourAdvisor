package pt.isec.touradvisor.data

data class Category (
    val name: String,
    val description: String,
    val image: String,
    val idUser: String
)
{
    constructor() : this("", "", "", "")
    override fun toString(): String {
        return name
    }
}