package ir.mostafa.nematpour.khodhesab.model

class AnswersPerson(
    val answerId: Int,
    val personId: Int,
    var creditlist: MutableList<Credit>?
) {
    override fun toString(): String {
        return "\nAnswersPerson(      \nanswerId=$answerId,       \npersonId=$personId,       \ncreditlist=$creditlist)"
    }

}