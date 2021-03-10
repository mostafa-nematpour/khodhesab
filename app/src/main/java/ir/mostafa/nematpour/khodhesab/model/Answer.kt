package ir.mostafa.nematpour.khodhesab.model

class Answer(
    val id: Int,
    val totalExpenses: String,
    val totalSpent: Int,
    val time: String,
    var palist: MutableList<AnswersPerson>?,
    val about: String=""
) {
}