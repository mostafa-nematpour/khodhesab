package ir.mostafa.nematpour.khodhesab.computing

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.navigation.fragment.DialogFragmentNavigatorDestinationBuilder
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

        /**
         * چرخ روی  افراد برای اضافه کردن
         * پول هایی که اول وسط گذاشته بودند
         * واضافه کردن آنها به لیست خرید
         *
         *
         */
        for (p in persons) {
            if (p.money != 0) {
                val persons0 = db.getPersons()
                spentList.add(Spent(-1, p.id, p.money, persons0))
            }
            val credits0 = mutableListOf<Credit>()

            for (p0 in persons) {
                credits0.add(Credit(-1, p0.id, 0))
            }
            val answersPerson = AnswersPerson(-1, p.id, credits0)

            answersPersons.add(answersPerson)
        }

//        Log.d("result", "result: \n\n\n ${spentList.toString()}")
        for (s in spentList) {
            //چرخش روی خرید ها
            val spent = if (db.getSpent(s.id) == null) s else db.getSpent(s.id)

            if (spent != null) {
                if (spent.list != null) {
                    money += spent.money
                    number++
                    val credit = spent.money / spent.list!!.size
                    Log.d("result", "\n\n\n\n    result: $credit")

                    for (ap in answersPersons) {
                        if (ap.personId == spent.buyerId) {
                            for (p in spent.list!!) {
                                for (c in ap.creditlist!!) {
                                    if (c.personId == p.id) {
                                        c.credit += credit
                                    }
                                }
                            }
                        }
//                        for (credit1 in ap.creditlist!!){
//                            if(credit1.personId)
//                        }
                    }

                } else {
                    /**
                     *
                     */
                }
            } else {
                /**
                 */
            }

            Log.d("result", "result: \n\n\n ${spent.toString()}")

        }


        for (ap in answersPersons) {
            for (c in ap.creditlist!!) {

            }

        }
        Log.d("answersPersons", "result: $answersPersons")


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