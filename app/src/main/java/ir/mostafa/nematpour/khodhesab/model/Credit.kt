package ir.mostafa.nematpour.khodhesab.model

class Credit(
    val answerId: Int,
    val personId: Int,
    var credit: Long
) {
    override fun toString(): String {
        return "\n  Credit(answerId=$answerId, personId=$personId, credit=$credit)\n"
    }
}