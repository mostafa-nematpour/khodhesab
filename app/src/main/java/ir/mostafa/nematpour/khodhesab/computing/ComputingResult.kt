package ir.mostafa.nematpour.khodhesab.computing

import android.annotation.SuppressLint
import android.content.Context
import ir.mostafa.nematpour.khodhesab.dataBase.DataBaseManager
import ir.mostafa.nematpour.khodhesab.model.*
import java.lang.Exception
import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ComputingResult(val spentList: MutableList<Spent>, val context: Context?) {


    @SuppressLint("SimpleDateFormat")
    fun result() {
        val db = DataBaseManager(context)
        var money = 0
        var number = 0

        val persons = db.getPersons()
        val spentList = db.getSpents()

        val credits = mutableListOf<Credit>()
        val answersPersons = mutableListOf<AnswersPerson>()

        for (p in persons) {
            credits.add(Credit(0, p.id, 0))
            answersPersons.add(AnswersPerson(-1, p.id, null))
        }




        for (s in spentList) {
            //چرخش روی خرید ها

            money += s.money
            number++
            /*currentDate += "\n\n" + db.getPerson(s.buyerId.toString())?.name + "\n\n" +s.money + "\n\n" + db.getSpent(
                s.id
            )?.list.toString() + "\n\n"*/
            val spent = db.getSpent(
                s.id
            )


        }


        var currentTime = "";
        try {
            val timestamp = Timestamp(System.currentTimeMillis())
            currentTime = timestamp.time.toString()
        } catch (e: Exception) {
        }


        val answer = Answer(-1, money.toString(), number, currentTime, answersPersons)
        db.insertAnswer(answer)

    }
}