package pt.isec.touradvisor.data

data class POI(
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val category: Category?,
    val image: String,
    val idUser: String
) {
    constructor() : this("", "", 2.0, 0.0, null, "", "")
    override fun toString(): String {
        return name
    }
}