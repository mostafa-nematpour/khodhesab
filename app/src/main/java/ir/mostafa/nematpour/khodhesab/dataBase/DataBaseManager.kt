package ir.mostafa.nematpour.khodhesab.dataBase

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import ir.mostafa.nematpour.khodhesab.model.*

class DataBaseManager(
    context: Context?,
    name: String? = "khodHesab.db",
    factory: SQLiteDatabase.CursorFactory? = null,
    version: Int = 1
) : SQLiteOpenHelper(context, name, factory, version) {

    /* personsTable */
    private val personsTable = "Persons"
    private val personId = "id_person"
    private val personName = "name"
    private val personMoney = "money"
    private val personImageLink = "imageLink"

    /* spentTable */
    private val spentTable = "Spent"
    private val spentId = "id_spent"

    /* personsInSpentTable */
    private val personsInSpentTable = "PersonsInSpent"

    /* answersTable */
    private val answersTable = "Answers"
    private val answerId = "id_answer"
    private val totalExpenses = "total_expenses"
    private val totalSpent = "total_spent"

    /* answersPersonTable */
    private val answersPersonTable = "Answers_Person"

    /* creditsTable */
    private val creditsTable = "Answers_Person_Credits"
    private val credit = "credit"

    /**/
    private val time = "Timestamp"
    private val about = "about"

    override fun onCreate(db: SQLiteDatabase?) {
        val persons =
            "CREATE TABLE $personsTable ( $personId INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, $personName VARCHAR(30), $personMoney INTEGER, $personImageLink VARCHAR(60));"
        val spent =
            "CREATE TABLE $spentTable ( $spentId INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, $personId INTEGER, $personMoney INTEGER, $time INTEGER, $about TEXT);"
        val personsInSpent =
            "CREATE TABLE $personsInSpentTable ( $spentId INTEGER, $personId INTEGER );"
        val answers =
            "CREATE TABLE $answersTable ( $answerId INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, $totalExpenses INTEGER, $totalSpent INTEGER, $time INTEGER);"
        val answersPerson =
            "CREATE TABLE $answersPersonTable ( $answerId INTEGER, $personId INTEGER);"
        val credits =
            "CREATE TABLE $creditsTable ( $answerId INTEGER, $personId INTEGER, $credit INTEGER);"

        db?.execSQL(persons)
        db?.execSQL(spent)
        db?.execSQL(personsInSpent)
        db?.execSQL(answers)
        db?.execSQL(answersPerson)
        db?.execSQL(credits)

        Log.d("db", "onCreate:  Table Was made")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    fun insertParts(part: Part) {
        val idb = this.writableDatabase
        val icv = ContentValues()
        icv.put(personId, part.personId)
        icv.put(spentId, part.spentId)
        idb.insert(personsInSpentTable, null, icv)
        idb.close()
    }

    fun insertSpent(spent: Spent) {
        val idb = this.writableDatabase
        val icv = ContentValues()
        icv.put(personId, spent.buyerId)
        icv.put(personMoney, spent.money)
        icv.put(time, spent.time)
        icv.put(about, spent.about)
        idb.insert(spentTable, null, icv)
        idb.close()
    }

    fun insertPerson(person: Person) {
        val idb = this.writableDatabase
        val icv = ContentValues()
        icv.put(personName, person.name)
        icv.put(personMoney, person.money)
        icv.put(personImageLink, person.imageLink)
        idb.insert(personsTable, null, icv)
        idb.close()
    }

    private fun getPartBySpentId(i: Int): MutableList<Person> {
        val items = mutableListOf<Person>()
        val gdb = this.readableDatabase
        val gQuery =
            "SELECT * FROM $personsInSpentTable WHERE $spentId = $i"

        val cursor: Cursor = gdb.rawQuery(gQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val person = this.getPerson(cursor.getString(1))
                if (person != null) {
                    items.add(person)
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        return items
    }


    fun getPerson(gID: String): Person? {
        var gPrs: Person? = null
        val gdb = this.readableDatabase
        val gQuery =
            "SELECT * FROM $personsTable WHERE $personId=$gID"
        val gCur: Cursor = gdb.rawQuery(gQuery, null)
        if (gCur.moveToFirst()) {
            gPrs = Person(
                gCur.getString(0).toInt(),
                gCur.getString(1),
                gCur.getString(2).toInt(),
                gCur.getString(3)
            )
        }
        gCur.close()
        return gPrs
    }


    fun getSpent(gID: Int): Spent? {
        var gPrs: Spent? = null
        val gdb = this.readableDatabase
        val gQuery =
            "SELECT * FROM $spentTable WHERE $spentId=$gID"
        val cursor: Cursor = gdb.rawQuery(gQuery, null)
        if (cursor.moveToFirst()) {
            gPrs = Spent(
                cursor.getString(0).toInt(),
                cursor.getString(1).toInt(),
                cursor.getString(2).toInt(),
                this.getPartBySpentId(cursor.getString(0).toInt()),
                cursor.getString(3),
                cursor.getString(4)
            )
        }
        cursor.close()
        return gPrs
    }

    fun getAnswers(): MutableList<Answer> {
        val items = mutableListOf<Answer>()

        val gdb = this.readableDatabase
        val cursor: Cursor = gdb.query(
            answersTable,
            arrayOf(answerId, totalExpenses, totalSpent, time),
            null,
            null,
            null,
            null,
            null
        )
        if (cursor.moveToFirst()) {
            do {
                val answer =
                    Answer(
                        cursor.getString(0).toInt(),
                        cursor.getString(1),
                        cursor.getString(2).toInt(),
                        cursor.getString(3),
                        null
                    )

                val answersPersons = mutableListOf<AnswersPerson>()
                val cursor2: Cursor = gdb.query(
                    answersPersonTable,
                    arrayOf(answerId, personId),
                    "$answerId = ${cursor.getString(0).toInt()}",
                    null,
                    null,
                    null,
                    null
                )
                if (cursor2.moveToFirst()) {
                    do {
                        val answersPerson =
                            AnswersPerson(
                                cursor2.getString(0).toInt(),
                                cursor2.getString(1).toInt(),
                                null
                            )
                        answersPersons.add(answersPerson)
                    } while (cursor2.moveToNext())
                }
                cursor2.close()


                answer.palist = answersPersons
                items.add(answer)

            } while (cursor.moveToNext())
        }
        cursor.close()



        items.reverse()
        return items
    }


    fun insertAnswer(answer: Answer) {
        val idb = this.writableDatabase
        val icv = ContentValues()
        icv.put(totalExpenses, answer.totalExpenses)
        icv.put(totalSpent, answer.totalSpent)
        icv.put(time, answer.time)
        idb.insert(answersTable, null, icv)
        val q = "SELECT last_insert_rowid() as $answerId FROM $answersTable"
        val cursor: Cursor =
            idb.rawQuery(q, null)
        cursor.moveToFirst()

        val lastId: Int = cursor.getInt(cursor.getColumnIndex(answerId))
        cursor.close()

        if (answer.palist != null) {
            for (l in answer.palist!!) {
                this.insertAnswerPerson(AnswersPerson(lastId, l.personId, l.creditlist))
            }
        }
    }


    fun insertAnswerPerson(answersPerson: AnswersPerson) {
        val idb = this.writableDatabase
        val icv = ContentValues()
        icv.put(answerId, answersPerson.answerId)
        icv.put(personId, answersPerson.personId)
        idb.insert(answersPersonTable, null, icv)
        idb.close()
        Log.d("insertCredit", "insertCredit: insertAnswerPerson")

        if (answersPerson.creditlist != null) {
            for (c in answersPerson.creditlist!!) {

                this.insertCredit(answersPerson.answerId,c)
            }
        }
    }

    fun insertCredit(answer_Id:Int,credit: Credit) {
        Log.d("insertCredit", "insertCredit: ")
        val idb = this.writableDatabase
        val icv = ContentValues()
        icv.put(answerId, answer_Id)
        icv.put(personId, credit.personId)
        icv.put(this.credit, credit.credit)
        idb.insert(creditsTable, null, icv)
        idb.close()
    }

    fun getSpents(): MutableList<Spent> {
        val items = mutableListOf<Spent>()
        val gdb = this.readableDatabase
        val cursor: Cursor = gdb.query(
            spentTable,
            arrayOf(spentId, personId, personMoney, time, about),
            null,
            null,
            null,
            null,
            null
        )
        if (cursor.moveToFirst()) {
            do {
                val spent =
                    Spent(
                        cursor.getString(0).toInt(),
                        cursor.getString(1).toInt(),
                        cursor.getString(2).toInt(),
                        null,
                        cursor.getString(3),
                        cursor.getString(4)
                    )
                items.add(spent)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return items
    }

    fun getPersons(): MutableList<Person> {
        val items = mutableListOf<Person>()
        val gdb = this.readableDatabase
        val cursor: Cursor = gdb.query(
            personsTable,
            arrayOf(personId, personName, personMoney, personImageLink),
            null,
            null,
            null,
            null,
            null
        )
        if (cursor.moveToFirst()) {
            do {
                val person =
                    Person(
                        cursor.getString(0).toInt(),
                        cursor.getString(1),
                        cursor.getString(2).toInt(),
                        cursor.getString(3)
                    )
                items.add(person)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return items
    }

    fun updateSpent(spent: Spent) {
        if (spent.list != null && spent.list!!.size > 0) {
            val udb = this.writableDatabase
            val ucv = ContentValues()
            ucv.put(spentId, spent.buyerId)
            ucv.put(personMoney, spent.money)
            ucv.put(about, spent.about)
            ucv.put(time, spent.time)
            ucv.put(about, spent.about)
            udb.update(
                this.spentTable,
                ucv,
                "$personId = ?",
                arrayOf(java.lang.String.valueOf(spent.id))
            )
            udb.execSQL("DELETE FROM $personsInSpentTable WHERE $spentId = ${spent.id}")

            for (p in spent.list!!) {
                if (p.flag)
                    this.insertParts(Part(spent.id, p.id))
            }
        }
        Log.i("Mahdi", spent.money.toString())
    }


    fun personCount(): Int {
        val gQuery = "SELECT * FROM $personsTable"
        val gdb = this.readableDatabase
        val gCur: Cursor = gdb.rawQuery(gQuery, null)
        val a = gCur.count
        gCur.close()
        return a
    }

    fun deleteAll() {
        val query = "DELETE FROM $personsTable"
        val query1 = "DELETE FROM sqlite_sequence WHERE name='$personsTable'"
        val queryS = "DELETE FROM $spentTable"
        val queryS1 = "DELETE FROM sqlite_sequence WHERE name='$spentTable'"

        val gdb = this.writableDatabase
        gdb.execSQL(query)
        gdb.execSQL(query1)
        gdb.execSQL(queryS)
        gdb.execSQL(queryS1)
    }
}