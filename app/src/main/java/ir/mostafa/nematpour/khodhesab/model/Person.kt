package ir.mostafa.nematpour.khodhesab.model

data class Person(
    val id: Int, val name: String, val money: Int, val imageLink: String, var flag: Boolean = true
){
    override fun toString(): String {
        return "id: $id / name: $name\n"
    }
}