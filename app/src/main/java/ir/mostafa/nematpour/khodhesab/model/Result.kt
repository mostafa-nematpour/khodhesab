package ir.mostafa.nematpour.khodhesab.model

data class Result(
    val id: Int = -1,
    val totalCost: Int,
    val totalPurchase: Int,
    val time: String,
    val date: String,
    val color: String
)