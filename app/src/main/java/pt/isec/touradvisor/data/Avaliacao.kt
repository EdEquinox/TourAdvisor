package pt.isec.touradvisor.data

data class Avaliacao(
    var comment : String,
    var rating : Int,
    var user : String,
    var poi : String
) {
}