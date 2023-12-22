package pt.isec.touradvisor.data

data class Localizacao (
    val name: String,
    val description: String,
    val image: String,
    val latitude: Double,
    val longitude: Double,
    val idUser: String
) {
    constructor() : this("", "", "", 0.0, 0.0, "")
    override fun toString(): String {
        return name
    }

}