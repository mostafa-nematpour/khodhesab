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
        val list = db.getSpents()
        var money = 0
        var number = 0
        var currentDate = ""
        var currentTime = ""

        val answersPersons= mutableListOf<AnswersPerson>()
        val pList = mutableListOf<CPerson>()
        val persons = db.getPersons()

        val table = mutableListOf<CTable>()
        for (p in persons) {
            table.add(CTable(0, p.id))
            answersPersons.add(AnswersPerson(-1,p.id,null))
        }
        for (p in persons) {
            pList.add(CPerson(p.id, table))
        }



        for (s in list) {
            //چرخش روی خرید ها

            money += s.money
            number++
            /*currentDate += "\n\n" + db.getPerson(s.buyerId.toString())?.name + "\n\n" +s.money + "\n\n" + db.getSpent(
                s.id
            )?.list.toString() + "\n\n"*/
            val spent = db.getSpent(
                s.id
            )

            for (l in spent?.list!!) {

                currentDate += "\n\n${spent.list!!.size}"
                // چرخ روی مشترکین خرید
                for (pl in pList) {
                    // برای \یدا کردن مشترک خرید روی لیست افراد ۲
                    if (pl.person == l.id) {
                        for (t in pl.list!!) {
                            if (s.buyerId == t.personId) {
                                t.money -= (s.money)
                                break
                            }

                        }
                        break
                    }
                }
            }
            currentDate += "\n\n" + db.getPerson(s.buyerId.toString())?.name + "\n\n" +
                    s.money + "\n\n" + pList.toString() + "\n\n" + spent?.list!! + "\n\n" + pList

        }




        try {
            val timestamp = Timestamp(System.currentTimeMillis())
            currentTime = timestamp.time.toString()
        } catch (e: Exception) {
        }


        val answer = Answer(-1, money.toString(), number, currentTime, answersPersons)
        db.insertAnswer(answer)

    }
}