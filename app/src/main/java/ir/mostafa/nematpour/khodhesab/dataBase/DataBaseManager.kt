package ir.mostafa.nematpour.khodhesab.dataBase

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import ir.mostafa.nematpour.khodhesab.model.Part
import ir.mostafa.nematpour.khodhesab.model.Person
import ir.mostafa.nematpour.khodhesab.model.Spent

class DataBaseManager(
    context: Context?,
    name: String? = "khodHesab.db",
    factory: SQLiteDatabase.CursorFactory? = null,
    version: Int = 1
) : SQLiteOpenHelper(context, name, factory, version) {

    private val personsTable = "Persons"
    private val personId = "id_person"
    private val personName = "name"
    private val personMoney = "money"
    private val personImageLink = "imageLink"
    /**/
    private val spentTable = "Spent"
    private val spentId = "id_spent"
    /**/
    private val time = "Timestamp"
    private val about = "about"
    /**/
    private val tableListOfParts = "SpentListOfParts"
    private val dSpentId = "spentId"

    override fun onCreate(db: SQLiteDatabase?) {
        val persons =
            "CREATE TABLE $personsTable ( $personId INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, $personName VARCHAR(30), $personMoney INTEGER, $personImageLink VARCHAR(60));"
        val spent =
            "CREATE TABLE $spentTable ( $spentId INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, $personId INTEGER, $personMoney INTEGER, $time INTEGER, $about TEXT);"
        val cQuery3 =
            "CREATE TABLE $tableListOfParts ( $spentId INTEGER, $personId INTEGER );"
        db?.execSQL(persons)
        db?.execSQL(spent)
        db?.execSQL(cQuery3)

        Log.d("db", "onCreate")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    fun insertParts(part: Part) {
        val idb = this.writableDatabase
        val icv = ContentValues()
        icv.put(personId, part.personId)
        icv.put(dSpentId, part.spentId)
        idb.insert(tableListOfParts, null, icv)
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
            "SELECT * FROM $tableListOfParts WHERE $dSpentId = $i"

        //SELECT * FROM SpentListOfParts WHERE spentId = 4
        val cursor: Cursor = gdb.rawQuery(gQuery, null)

        if (cursor.moveToFirst()) {
            do {

                val person = this.getPerson(cursor.getString(2))
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


    fun getSpents(): MutableList<Spent> {
        val items = mutableListOf<Spent>()
        val gdb = this.readableDatabase
        val cursor: Cursor = gdb.query(
            spentTable,
            arrayOf(spentId, personId, personMoney,  time, about),
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
//        val gQuery = "SELECT * FROM $personsTable"
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
            udb.execSQL("DELETE FROM $tableListOfParts WHERE spentId = ${spent.id}")

            for (p in spent.list!!) {
                if (p.flag)
                    this.insertParts(Part(0, spent.id, p.id))
            }


        }
        Log.i("Mahdi", spent.money.toString())
    }
/*
     fun deletePerson(dprs: Person): Boolean {
         val ddb = this.writableDatabase
         val dResult = ddb.delete(
             personsTable,
             "$personId=?",
             arrayOf(java.lang.String.valueOf(dprs.pID))
         ).toLong()
         Log.i("Mahdi", "deletePerson Method")
         return if (dResult == 0L) false else true
     }*/

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