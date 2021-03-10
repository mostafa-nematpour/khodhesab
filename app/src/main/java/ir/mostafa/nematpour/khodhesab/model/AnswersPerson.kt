package ir.mostafa.nematpour.khodhesab.model

class AnswersPerson(
    val answerId: Int,
    val personId: Int,
    var creditlist: MutableList<Credit>?
) {
}