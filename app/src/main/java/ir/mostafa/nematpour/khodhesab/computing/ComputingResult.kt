package ir.mostafa.nematpour.khodhesab.computing

import android.annotation.SuppressLint
import android.content.Context
import ir.mostafa.nematpour.khodhesab.dataBase.DataBaseManager
import ir.mostafa.nematpour.khodhesab.model.CPerson
import ir.mostafa.nematpour.khodhesab.model.CTable
import ir.mostafa.nematpour.khodhesab.model.Spent
import ir.mostafa.nematpour.khodhesab.model.Result
import java.lang.Exception
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ComputingResult(val spentList: MutableList<Spent>, val context: Context?) {


    @SuppressLint("SimpleDateFormat")
    fun result(): Result {
        val db = DataBaseManager(context)
        val list = db.getSpents()
        var money = 0
        var number = 0
        var currentDate = ""
        var currentTime = ""

        val pList = mutableListOf<CPerson>()
        val persons = db.getPersons()

        val table = mutableListOf<CTable>()
        for (p in persons) {
            table.add(CTable(0, p.id))
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
                    s.money + "\n\n" + pList.toString() + "\n\n" + spent?.list!! + "\n\n" +pList

        }




        try {
            val calendar = Calendar.getInstance()
            currentDate +=
                DateFormat.getDateInstance(DateFormat.YEAR_FIELD).format(calendar.time)
            val format = SimpleDateFormat("HH:mm")
            currentTime = format.format(calendar.time)

        } catch (e: Exception) {
        }

        return Result(-1, money, number, currentTime, currentDate, "")

    }
}