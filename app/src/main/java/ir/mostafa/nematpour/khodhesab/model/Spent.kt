package ir.mostafa.nematpour.khodhesab.model

// خرج کرد
data class Spent(
    val id: Int,
    val buyerId: Int,
    val money: Int,
    var list: MutableList<Person>?,
    val time: String,
    val about: String
) {
    override fun toString(): String {
        return "id: $id {\n      buyerId: $buyerId\n" +
                "      money: $money\n" +
                "      list: $list}"
    }
}