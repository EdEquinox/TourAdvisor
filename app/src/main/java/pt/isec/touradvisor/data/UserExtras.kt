package pt.isec.touradvisor.data

data class UserExtras(
    val profilePic : String? = null,
    val name : String? = null,
    val banner : String? = null
) {
    override fun toString(): String {
        return name ?: ""
    }
}